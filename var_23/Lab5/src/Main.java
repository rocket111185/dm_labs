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
            { 1, 1, 0, 1, 1, 1, 0, 0, 1, 1, 0, 1 },
            { 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 1, 0 },
            { 0, 1, 0, 1, 0, 0, 1, 0, 0, 0, 0, 0 },
            { 0, 0, 0, 0, 1, 0, 1, 1, 0, 0, 0, 1 },
            { 0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1 },
            { 1, 1, 0, 1, 1, 0, 0, 1, 0, 0, 0, 1 },
            { 0, 0, 0, 0, 1, 0, 1, 1, 0, 1, 1, 0 },
            { 1, 0, 0, 1, 0, 0, 0, 0, 0, 1, 1, 0 },
            { 1, 1, 1, 0, 1, 0, 1, 1, 0, 1, 0, 0 },
            { 1, 0, 1, 0, 0, 0, 1, 1, 0, 1, 0, 0 },
            { 1, 1, 1, 1, 0, 0, 1, 1, 0, 0, 1, 0 },
            { 1, 0, 1, 1, 1, 0, 0, 1, 1, 1, 1, 0 }

    };

    private final int[][] weightMatrix = new int[][]{
            { 0,  53, 0,  8,  44, 22, 0,  55, 91, 34, 28, 51 },
            { 53, 0,  68, 0,  53, 86, 51, 27, 81, 0,  81, 0 },
            { 0,  68, 0,  3,  42, 0,  38, 0,  49, 70, 82, 18 },
            { 8,  0,  3,  0,  59, 46, 26, 29, 0,  0,  72, 7 },
            { 44, 53, 42, 59, 0,  47, 72, 0,  29, 0,  0,  60 },
            { 22, 86, 0,  46, 47, 0,  0,  58, 0,  0,  0,  99 },
            { 0,  51, 38, 26, 72, 0,  0,  69, 47, 30, 72, 0 },
            { 55, 27, 0,  29, 0,  58, 69, 0,  18, 24, 4,  41 },
            { 91, 81, 49, 0,  29, 0,  47, 18, 0,  49, 0,  47 },
            { 34, 0,  70, 0,  0,  0,  30, 24, 49, 0,  0,  18 },
            { 28, 81, 82, 72, 0,  0,  72, 4,  0,  0,  0,  6 },
            { 51, 0,  18, 7,  60, 99, 0,  41, 47, 18, 6,  0 },
    };

    private int nodeQuantity = matrix.length;
    private Node[] nodes = new Node[nodeQuantity];
    private int activeNode = 0;
    private ArrayList<String> unvisited = new ArrayList<>();
    private int nextNode = 0;
    private double margin = 150;
    private int radius = 35;

    private double[] x = getPositions(margin, nodeQuantity, false);
    private double[] y = getPositions(margin, nodeQuantity, true);

    private int[][] treeMatrix = new int[nodeQuantity][nodeQuantity];
    private Group treeGroup = new Group();

    // Colors for states of a node
    private String visited = "9FCA56";  // Green
    private String active = "BF4040";   // Red

    private void minimalEdge() {
        int weight = Integer.MAX_VALUE;
        for (int i = 0; i < nodeQuantity; i++) {
            for (int j = 0; j < nodeQuantity; j++) {
                if (i != j
                        && weightMatrix[i][j] < weight
                        && weightMatrix[i][j] > 0
                        && (matrix[i][j] > 0 || matrix[j][i] > 0)
                        && !unvisited.contains(i + "")
                        && unvisited.contains(j + "")) {
                    activeNode = i;
                    nextNode = j;
                    weight = weightMatrix[i][j];
                }
            }
        }
    }

    private void activateNode(int activeNode) {
        nodes[activeNode].setColor(active);
        unvisited.remove("" + activeNode);
    }

    private void activateNode(int activeNode, int nodeIndex) {
        nodes[nodeIndex].setColor(active);
        unvisited.remove("" + nodeIndex);
        treeGroup.getChildren().add(new Arrow(x[activeNode], y[activeNode], x[nodeIndex], y[nodeIndex],
                radius, weightMatrix[activeNode][nodeIndex]));
        treeMatrix[activeNode][nodeIndex] = 1;
    }

    /*
     * actionNumber is responsible for current action.
     * It is needed for doing actions step-by-step.
     *
     * 0 - Initialize
     * 1 - Add next node and edge to the tree
     * 2 - all nodes are visited, end Prim's Algorithm.
     * 3 - Disable on-button actions
     */

    private int actionNumber = 0;

    private void primsAlgorithm() {
        if (actionNumber == 0) {
            activateNode(activeNode);
            minimalEdge();
            actionNumber = 1;
        } else if (actionNumber == 1) {
            activateNode(activeNode, nextNode);
            minimalEdge();
            if (unvisited.isEmpty())
                actionNumber = 2;
        } else if (actionNumber == 2) {
            for (Node node: nodes)
                node.setColor(visited);
            for (int[] row : treeMatrix) {
                for (int digit : row)
                    System.out.print(digit + " ");
                System.out.println();
            }
            actionNumber = 3;
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
        primaryStage.setTitle("Лабораторна робота №5");

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
                    if (!isDoubling)
                        graphArrows.getChildren().add(new Arrow(x[i], y[i], x[j], y[j], radius, weightMatrix[i][j]));
                    isDoubling = false;
                }
            }
        }

        for (int i = 0; i < nodeQuantity; i++) unvisited.add("" + i);

        Button button = new Button("halt()");
        button.setMinWidth(75);
        button.setMinHeight(40);
        button.setStyle("-fx-font-size:" + radius * 0.5);
        button.setLayoutX(margin / 4);
        button.setLayoutY(margin / 4);

        button.setOnAction(event -> primsAlgorithm());

        Group graphGroup = new Group();
        graphGroup.getChildren().addAll(graphNodes, graphArrows, button);

        ToggleGroup toggle = new ToggleGroup();
        RadioButton graphButton = new RadioButton("Схема відвідування вершин");
        RadioButton treeButton = new RadioButton("Дерево кістяка");

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