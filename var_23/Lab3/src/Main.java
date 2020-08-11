import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;
import java.util.*;

public class Main extends Application {

    // The code executes here
    public static void main(String[] args) { launch(args); }

    static double getScreenHeight() { return 900; }

    static double getScreenWidth() { return 1600; }

    private double[] getPositions(double margin, int nodeQuantity, boolean isY) {
        double distance = (isY) ?
                (Main.getScreenHeight() - margin * 2) :
                (Main.getScreenWidth() - margin * 2);
        double[] list = new double[nodeQuantity];
        double currentPos = margin;
        int i = 0;
        int onOneSide = nodeQuantity / 4;
        int odd = nodeQuantity % 4;
        int firstOdd = (odd > 0) ? 1 : 0;
        int secondOdd = odd / 2;
        int thirdOdd = odd / 3;
        double step = distance / (onOneSide + firstOdd);
        int border = onOneSide + firstOdd;
        while (i < border) {
            if (isY) { currentPos += step; list[i] = currentPos; i++; }
            else { list[i] = currentPos; i++; }
        }
        step = distance / (onOneSide + secondOdd);
        border += onOneSide + secondOdd;
        while (i < border) {
            if (isY) { list[i] = currentPos; i++; }
            else { currentPos += step; list[i] = currentPos; i++; }
        }
        step = distance / (onOneSide + thirdOdd);
        border += onOneSide + thirdOdd;
        while (i < border) {
            if (isY) { currentPos -= step; list[i] = currentPos; i++; }
            else { list[i] = currentPos; i++; }
        }
        step = distance / onOneSide;
        border += onOneSide;
        while (i < border) {
            if (isY) { list[i] = currentPos; i++; }
            else { currentPos -= step; list[i] = currentPos; i++; }
        }
        return list;
    }

    //The matrix was generated with SciLab, gradebook number: 9423

    private final int[][] matrix = new int[][]{
            {1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0},
            {0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 1, 0, 0, 1, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 1, 1, 0, 1, 0, 0, 0, 0},
            {0, 1, 1, 1, 0, 0, 1, 0, 1, 0, 0, 0},
            {1, 0, 0, 1, 0, 0, 0, 1, 1, 1, 1, 0},
            {1, 0, 0, 0, 0, 1, 0, 1, 1, 0, 1, 1},
            {0, 1, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1},
            {1, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 0},
            {0, 1, 1, 0, 0, 0, 0, 1, 0, 1, 0, 1}
    };

    @Override
    public void start(Stage primaryStage) {
        // Set the label of the window
        primaryStage.setTitle("Новоутворений граф");

        // Initialize graphic objects
        Group root = new Group();
        Group condRoot = new Group();

        // Create instant objects
        double margin = 100;
        int nodeQuantity = matrix.length;
        double[] x = getPositions(margin, nodeQuantity, false);
        double[] y = getPositions(margin, nodeQuantity, true);
        int radius = 35;
        final double screenWidth = Main.getScreenWidth();
        final double screenHeight = Main.getScreenHeight();

        // Create an array of Nodes
        Node[] nodes = new Node[nodeQuantity];

        // Also it is needed to check double-links. If it exists, draw one of lines in some other way
        ArrayList<String> links = new ArrayList<>();
        boolean isDoubling = false;

        // Add necessary arrows and nodes
        for (int i = 0;  i < nodeQuantity; i++) {
            String label = String.valueOf(i + 1);
            Node node = new Node(label, x[i], y[i], radius);
            root.getChildren().add(node);
            nodes[i] = node;
            for (int j = 0; j < matrix[i].length; j++) {
                if (matrix[i][j] > 0) {
                    nodes[i].increasePositiveDegree();
                    if (matrix[j][i] > 0 && !links.contains(String.valueOf(j) + i) && j != i) {
                        links.add(String.valueOf(i) + j);
                        isDoubling = true;
                    }
                    root.getChildren().add(new Arrow(x[i], y[i], x[j], y[j], radius, isDoubling));
                    isDoubling = false;
                }
                if (matrix[j][i] > 0) nodes[i].increaseNegativeDegree();
            }
        }

        // Print table of nodes, degrees and semi-degrees
        System.out.println("Node\tDegree\tIN\t\tOUT");

        for (Node node : nodes) {
            System.out.println(node.getIndex() + "\t\t" + node.getDegree() + "\t\t" + node.getNegativeDegree() +
                    "\t\t" + node.getPositiveDegree());
        }

        // Print out ways length of 2 and 3
        Matrix test = new Matrix(matrix);
        System.out.println(test.getWays(2));
        System.out.println(test.getWays(3));

        System.out.println(test.matrixMultiplication(test));

        System.out.println("Matrix of reachability:\n" + test.getReachable().toString());
        System.out.println("Matrix of strong links:\n" + test.getStrongLinkedMatrix().toString());

        System.out.println("Strong linked elements");
        Map<Integer, List<Integer>> strongLinked = test.getStrongLinked();
        int strongLength = strongLinked.size();

        for (int i = 0; i < strongLength; i++) {
            List<Integer> row = strongLinked.get(i);
            System.out.print("K" + (i + 1) + ": ");
            row.forEach(el -> System.out.print((el + 1) + " "));
            System.out.println();
        }

        int[][] strongField = new int[strongLength][strongLength];
        for (int[] row : strongField) {
            Arrays.fill(row, 0);
        }

        for (int i = 0; i < strongLength; i++) {
            for (int j = 0; j < strongLength; j++) {
                if (i == j) continue;
                List<Integer> inputRow = strongLinked.get(i);
                List<Integer> outputRow = strongLinked.get(j);
                for (int in : inputRow) {
                    for (int out : outputRow) {
                        if (matrix[in][out] > 0) {
                            strongField[i][j] = 1;
                            break;
                        }
                    }
                }
            }
        }

        System.out.println("Condensed graph:\n" + new Matrix(strongField).toString());

        double[] condX = getPositions(margin, strongLength, false);
        double[] condY = getPositions(margin, strongLength, true);

        ArrayList<String> condLinks = new ArrayList<>();
        boolean isCondDoubling = false;


        for (int i = 0;  i < strongLength; i++) {
            String label = "K" + (i + 1);
            Node node = new Node(label, condX[i], condY[i], radius);
            condRoot.getChildren().add(node);
            for (int j = 0; j < strongLength; j++) {
                if (strongField[i][j] > 0) {
                    if (strongField[j][i] > 0 && !condLinks.contains(String.valueOf(j) + i) && j != i) {
                        condLinks.add(String.valueOf(i) + j);
                        isCondDoubling = true;
                    }
                    condRoot.getChildren().add(new Arrow(condX[i], condY[i], condX[j], condY[j], radius, isCondDoubling));
                    isCondDoubling = false;
                }
            }
        }

        /*
        // Graphic staff
        ToggleGroup toggle = new ToggleGroup();
        RadioButton basicGraphButton = new RadioButton("Звичайний граф");
        RadioButton condGraphButton = new RadioButton("Граф конденсації");

        basicGraphButton.setToggleGroup(toggle);
        condGraphButton.setToggleGroup(toggle);

        basicGraphButton.setLayoutX(margin / 5);
        basicGraphButton.setLayoutY(margin / 5);
        condGraphButton.setLayoutX(margin / 5);
        condGraphButton.setLayoutY(margin / 5 * 2);

        basicGraphButton.setOnAction(event -> {
            root.setVisible(true);
            condRoot.setVisible(false);
        });
        condGraphButton.setOnAction(event -> {
            root.setVisible(false);
            condRoot.setVisible(true);
        });

        root.setVisible(false);
        condRoot.setVisible(false);

        Group mainGroup = new Group();
        mainGroup.getChildren().addAll(root, condRoot, basicGraphButton, condGraphButton);

         */

        // Load the picture
        primaryStage.setScene(new Scene(root, screenWidth, screenHeight));
        primaryStage.show();
    }
}