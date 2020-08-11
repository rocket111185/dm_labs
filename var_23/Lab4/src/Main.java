import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;
import java.util.*;

public class Main extends Application {

    private final int[][] matrix = new int[][]{
            { 1, 1, 0, 1, 1, 1, 0, 0, 1, 0, 0, 1 },
            { 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0 },
            { 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0 },
            { 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 1 },
            { 0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 0 },
            { 0, 1, 0, 1, 1, 0, 0, 1, 0, 0, 0, 1 },
            { 0, 0, 0, 0, 0, 0, 1, 0, 0, 1, 1, 0 },
            { 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0 },
            { 1, 0, 1, 0, 1, 0, 1, 1, 0, 1, 0, 0 },
            { 1, 0, 1, 0, 0, 0, 1, 1, 0, 1, 0, 0 },
            { 0, 1, 1, 1, 0, 0, 1, 0, 0, 0, 1, 0 },
            { 1, 0, 1, 0, 0, 0, 0, 1, 1, 1, 1, 0 }

    };

    private int nodeQuantity = matrix.length;
    private Node[] nodes = new Node[nodeQuantity];
    private int activeNode = 0;
    private ArrayList<String> unvisited = new ArrayList<>();
    private ArrayList<Integer> BFSResult = new ArrayList<>();
    private LinkedList<Integer> queue = new LinkedList<>();
    private LinkedList<Integer> activeQueue = new LinkedList<>();
    private int nextNode = 0;
    private double margin = 150;
    private int radius = 35;

    private double[] x = getPositions(margin, nodeQuantity, false);
    private double[] y = getPositions(margin, nodeQuantity, true);

    private int[][] treeMatrix = new int[nodeQuantity][nodeQuantity];
    private Group treeGroup = new Group();

    // Colors for states of a node
    private String visited = "9FCA56";  // Green
    private String visiting = "55B5DB"; // Blue
    private String active = "BF4040";   // Red

    private LinkedList<Integer> related(int nodeIndex, boolean independentResult) {
        LinkedList<Integer> result = new LinkedList<>();
        for (int i = 0; i < nodeQuantity; i++) {
            if (matrix[nodeIndex][i] > 0 && (independentResult || unvisited.contains("" + i)) && i != nodeIndex) {
                result.add(i);
            }
        }
        return result;
    }

    private void changeActiveNode(int nodeIndex) {
        nodes[nodeIndex].setColor(active);
        unvisited.remove("" + nodeIndex);
        if (!BFSResult.contains(nodeIndex))
            BFSResult.add(nodeIndex);
    }

    private void changeActiveNode(int previous, int current) {
        activeNode = current;
        nodes[previous].setColor(visited);
        nodes[current].setColor(active);
    }

    private void visitNode(int nodeIndex) {
        nodes[nodeIndex].setColor(visiting);
        treeMatrix[activeNode][nodeIndex] = 1;
        treeGroup.getChildren().add(new Arrow(x[activeNode], y[activeNode], x[nodeIndex], y[nodeIndex], radius, false));
    }

    private void visitNodeFinal(int nodeIndex) {
        unvisited.remove("" + nodeIndex);
        if (!BFSResult.contains(nodeIndex))
            BFSResult.add(nodeIndex);
        nodes[nodeIndex].setColor(visited);
    }

    /*
     * actionNumber is responsible for current action.
     * It is needed for doing actions step-by-step.
     *
     * 0 - initialize queue of visiting
     * 1 - intend to visit neighbour node
     * 2 - visit neighbour node
     * 3 - change active node
     *
     * 69 - all nodes are visited, end BFS.
     */

    private int actionNumber = 0;

    private void BFS() {
        if (unvisited.isEmpty() && actionNumber != 69 && actionNumber != 4) {
            actionNumber = 69;
            for (int i = 0; i < nodeQuantity; i++)
                nodes[i].setColor(visited);
            return;
        }
        if (actionNumber == 0) {
            changeActiveNode(activeNode);
            queue = related(activeNode, false);
            if (queue.isEmpty()) {
                actionNumber = 3;
                return;
            }
            if (activeQueue.isEmpty())
                activeQueue = related(activeNode, true);
            actionNumber = 1;
        } else if (actionNumber == 1) {
            nextNode = queue.pop();
            visitNode(nextNode);
            actionNumber = 2;
        } else if (actionNumber == 2) {
            visitNodeFinal(nextNode);
            if (queue.isEmpty())
                actionNumber = 3;
            else
                actionNumber = 1;
        } else if (actionNumber == 3) {
            int nextActiveNode = activeQueue.pop();
            changeActiveNode(activeNode, nextActiveNode);
            actionNumber = 0;
        } else if (actionNumber == 69) {
            for (int i = 0; i < nodeQuantity; i++)
                System.out.print((i + 1) + " ");
            System.out.println();
            for (int i = 0; i < nodeQuantity; i++)
                System.out.print((BFSResult.get(i) + 1) + " ");
            System.out.println("\n\n");
            for (int[] row : treeMatrix) {
                for (int el : row)
                    System.out.print(el + " ");
                System.out.println();
            }
            actionNumber = 4;
        }
    }

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


    @Override
    public void start(Stage primaryStage) {
        // Set the label of the window
        primaryStage.setTitle("Лабораторна робота №4");

        // Initialize graphic objects
        Group graphNodes = new Group();
        Group graphArrows = new Group();

        // Create instant objects
        final double screenWidth = Main.getScreenWidth();
        final double screenHeight = Main.getScreenHeight();

        // Also it is needed to check double-links. If it exists, draw one of lines in some other way
        ArrayList<String> links = new ArrayList<>();
        boolean isDoubling = false;

        // Add necessary arrows and nodes
        for (int i = 0;  i < nodeQuantity; i++) {
            String label = String.valueOf(i + 1);
            nodes[i] = new Node(label, x[i], y[i], radius);
            graphNodes.getChildren().add(nodes[i]);
            treeGroup.getChildren().add(new Node(label, x[i], y[i], radius));
            for (int j = 0; j < matrix[i].length; j++) {
                if (matrix[i][j] > 0) {
                    if (matrix[j][i] > 0 && !links.contains(("" + j) + i) && j != i) {
                        links.add(("" + i) + j);
                        isDoubling = true;
                    }
                    graphArrows.getChildren().add(new Arrow(x[i], y[i], x[j], y[j], radius, isDoubling));
                    isDoubling = false;
                }
            }
        }


        Button button = new Button("halt()");
        button.setMinWidth(75);
        button.setMinHeight(40);
        button.setStyle("-fx-font-size:" + radius * 0.5);
        button.setLayoutX(margin / 4);
        button.setLayoutY(margin / 4);

        for (int i = 0; i < nodeQuantity; i++) unvisited.add("" + i);

        button.setOnAction(event -> BFS());

        Group graphGroup = new Group();
        graphGroup.getChildren().addAll(graphNodes, graphArrows, button);

        ToggleGroup toggle = new ToggleGroup();
        RadioButton graphButton = new RadioButton("Схема відвідування вершин");
        RadioButton treeButton = new RadioButton("Дерево");

        graphButton.setToggleGroup(toggle);
        treeButton.setToggleGroup(toggle);

        graphButton.setLayoutX(margin / 5 + 400);
        graphButton.setLayoutY(margin / 5);
        treeButton.setLayoutX(margin / 5 + 400);
        treeButton.setLayoutY(margin / 5 * 2);

        graphButton.setOnAction(event -> {
            graphGroup.setVisible(true);
            treeGroup.setVisible(false);
        });
        treeButton.setOnAction(event -> {
            graphGroup.setVisible(false);
            treeGroup.setVisible(true);
        });

        graphGroup.setVisible(false);
        treeGroup.setVisible(false);

        Group mainGroup = new Group();

        mainGroup.getChildren().addAll(graphGroup, treeGroup, graphButton, treeButton);

        // Load the picture
        primaryStage.setScene(new Scene(mainGroup, screenWidth, screenHeight));
        primaryStage.show();
    }
}