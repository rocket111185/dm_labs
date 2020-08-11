import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.util.ArrayList;

public class Main extends Application {

    // The code executes here
    public static void main(String[] args) { launch(args); }

    static double getScreenHeight () { return 720; }

    static double getScreenWidth () { return 1280; }


    private double[] getPositions (double margin, int nodeQuantity, boolean isY) {
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
    /*
    private final int[][] matrix = new int[][]{
            { 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 1 },
            { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
            { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
            { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
            { 0, 0, 1, 0, 0, 1, 0, 0, 0, 0, 0, 0 },
            { 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0 },
            { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0 },
            { 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0 },
            { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
            { 1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0 },
            { 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0 },
            { 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 1, 0 }
    };

     */

    private final int[][] matrix = new int[][]{
            { 0, 0, 0, 1, 0, 0, 1 },
            { 0, 0, 0, 1, 1, 0, 0 },
            { 1, 0, 0, 0, 0, 1, 0 },
            { 1, 0, 0, 1, 1, 0, 0 },
            { 0, 1, 1, 1, 0, 0, 1 },
            { 0, 0, 0, 1, 1, 1, 1 },
            { 0, 0, 0, 1, 1, 1, 0 },
    };

    @Override
    public void start(Stage primaryStage) {
        // Set the label of the window
        primaryStage.setTitle("Лабораторна робота №2");

        // Create instant objects
        double margin = 100;
        double sideMargin = 100;
        int nodeQuantity = matrix.length;
        double[] x = getPositions(sideMargin, nodeQuantity, false);
        double[] y = getPositions(margin, nodeQuantity, true);
        int radius = 35;
        Group root = new Group();
        final double screenWidth = Main.getScreenWidth();
        final double screenHeight = Main.getScreenHeight();

        // isDirected is the switch of directed/undirected modes
        boolean isDirected = true;

        // Create a list of Vertex
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
                    root.getChildren().add(new Arrow(x[i], y[i], x[j], y[j], radius, isDirected, isDoubling));
                    isDoubling = false;
                }
                if (matrix[j][i] > 0) nodes[i].increaseNegativeDegree();
            }
        }


        ArrayList<String> isolatedNodes = new ArrayList<>();
        ArrayList<String> hangingNodes = new ArrayList<>();

        System.out.println("Node\tDegree\tIN\t\tOUT");

        for (Node node : nodes) {
            System.out.println(node.getIndex() + "\t\t" + node.getDegree() + "\t\t" + node.getNegativeDegree() +
                    "\t\t" + node.getPositiveDegree());
            if (node.isIsolated()) isolatedNodes.add(node.getIndex());
            if (node.isHanging()) hangingNodes.add(node.getIndex());
        }

        int graphDegree = nodes[0].getDegree();
        for (Node node : nodes) {
            if (node.getDegree() != graphDegree) {
                graphDegree = 0;
                break;
            }
        }
        if (graphDegree > 0) System.out.println("Graph is homogeneous, degree of the graph: " + graphDegree);

        System.out.println("\nIsolated nodes: " + isolatedNodes.toString());
        System.out.println("Hanging nodes: " + hangingNodes.toString());

        // Load the picture
        primaryStage.setScene(new Scene(root, screenWidth, screenHeight));
        primaryStage.show();
    }
}