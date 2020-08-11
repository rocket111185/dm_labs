import java.util.*;

public class Matrix {

    private final int[][] field;
    private final int length;

    Matrix(int[][] field) {
        this.field = field;
        this.length = field.length;
    }


    Matrix matrixMultiplication(Matrix other) {
        if (length != other.length)
            throw new IllegalArgumentException("Matrixes should be the same size!");
        int[][] result = new int[length][length];
        for (int i = 0; i < length; i++) {
            for (int j = 0; j < length; j++) {
                for (int k = 0; k < length; k++) {
                    result[i][j] += field[i][k] * other.field[k][j];
                }
            }
        }
        return new Matrix(result);
    }


    String getWays(int wayLength) {
        Map<Integer, List<Integer>> result = new HashMap<>();
        for (int step = 0; step < wayLength; step++) {
            Map<Integer, List<Integer>> currentResult = new HashMap<>();
            int index = 0;
            for (int i = 0; i < length; i++) {
                for (int j = 0; j < length; j++) {
                    if (i == j) continue;
                    if (field[i][j] == 1) {
                        if (step == 0) {
                            currentResult.put(index++, new ArrayList<>(Arrays.asList(i, j)));
                        } else {
                            for (int k = 0; k < result.size(); k++) {
                                List<Integer> row = new ArrayList<>(result.get(k));
                                if(row.get(row.size() - 1) == i) {
                                    row.add(j);
                                    currentResult.put(index++, row);
                                }
                            }
                        }
                    }
                }
            }
            result = new HashMap<>(currentResult);
        }
        StringBuilder str = new StringBuilder();
        str.append("Ways of ").append(wayLength).append("\n");
        for (int i = 0; i < result.size(); i++) {
            List<Integer> row = result.get(i);
            for (Integer el : row) {
                str.append(++el);
                str.append(", ");
            }
            str.delete(str.length() - 2, str.length() - 1);
            str.append("\n");
        }
        return str.toString();
    }

    // This method converts any values, greater than 0, to 1 in a matrix
    private Matrix booleanConversion() {
        int[][] result = new int[length][length];
        for (int i = 0; i < length; i++) {
            for (int j = 0; j < length; j++) {
                result[i][j] = (field[i][j] > 0) ? 1 : 0;
            }
        }
        return new Matrix(result);
    }

    // Yes! This is the matrix of reachability!
    Matrix getReachable() {
        int[][] result = new int[length][length];
        Matrix[] list = new Matrix[length];
        Matrix current = new Matrix(field);
        for (int i = 0; i < length; i++) {
            list[i] = current;
            current = new Matrix(current.matrixMultiplication(this).field);
        }
        for (Matrix matrix : list) {
            for (int i = 0; i < length; i++) {
                for (int j = 0; j < length; j++) {
                    result[i][j] += matrix.field[i][j];
                }
            }
        }
        return new Matrix(result).booleanConversion();
    }

    private int maxValue() {
        int max = field[0][0];
        for (int[] row : field) {
            for (int value : row) {
                if (value > max) max = value;
            }
        }
        return max;
    }


    Matrix getStrongLinkedMatrix() {
        int[][] result = new int[length][length];
        Matrix reach = getReachable();
        for (int i = 0; i < length; i++) {
            for (int j = 0; j < length; j++) {
                result[i][j] = (reach.field[i][j] > 0 && reach.field[j][i] > 0) ? 1 : 0;
            }
        }
        return new Matrix(result);
    }

    Map<Integer, List<Integer>> getStrongLinked() {
        Matrix initial = getStrongLinkedMatrix();
        // linked is a squared matrix of strong links, as you can see)
        Matrix linked = initial.matrixMultiplication(initial);
        // distinctElements contains elements, which have no input or output links
        ArrayList<Integer> distinctElements = new ArrayList<>();
        // currentGroup equals one component of graph
        List<Integer> currentGroup = new ArrayList<>();
        /* I have no idea how to create dynamic list of Lists better, than Map of Lists.
         * As you can guess, the keys of this Map are indexes actually.
         */
        Map<Integer, List<Integer>> result = new HashMap<>();
        int index = 0;

        // Find nodes, which are distinct elements (have only input/output links)
        int in = 0;
        int out = 0;
        for (int i = 0; i < length; i++) {
            for (int j = 0; j < length; j++) {
                if (field[i][j] > 0) out++;
                if (field[j][i] > 0) in++;
                if (in > 0 && out > 0) break;
            }
            if (out == 0 || in == 0) {
                distinctElements.add(i);
                result.put(index, new ArrayList<>(Collections.singletonList(i)));
                index++;
            }
            in = 0;
            out = 0;
        }

        /* Search for elements, which have connectivity with each other
         * and have the same number in the squared matrix of strong links.
         * We don't know, which number is common for one group, so we
         * should search through from 1 to maximum value in the matrix.
         */
        for (int i = 1; i <= linked.maxValue(); i++) {
            for (int j = 0; j < length; j++) {
                for (int k = 0; k < length; k++) {
                    if (linked.field[j][k] == linked.field[k][j] && linked.field[j][k] == i) {
                        if (!distinctElements.contains(j) && !distinctElements.contains(k)) {
                            if (!currentGroup.contains(j)) currentGroup.add(j);
                            if (!currentGroup.contains(k)) currentGroup.add(k);
                        }
                    }
                }
            }
            // If there are no links, which equal to current i, currentGroup is empty
            if (!currentGroup.isEmpty()) {
                result.put(index, new ArrayList<>(currentGroup));
                index++;
            }
            currentGroup.clear();
        }
        return result;
    }


    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        for (int[] ints : field) {
            for (int anInt : ints) {
                str.append(anInt).append(" ");
            }
            str.append("\n");
        }
        return str.toString();
    }

}
