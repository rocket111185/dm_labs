import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.text.*;
import javafx.stage.Stage;
import java.util.ArrayList;

class Node extends StackPane {
    Node (String label, double x, double y, double radius) {
        // Create a circle and set color properties
        Circle circle = new Circle(radius);
        circle.setFill(Color.WHITE);
        circle.setStroke(Color.BLACK);
        // Text is a number of node
        Text text = new Text(label);
        text.setFont(Font.font(radius * 0.6));
        // Create a group of circle and text and set coordinates
        this.getChildren().addAll(circle, text);
        this.setLayoutX(x - radius);
        this.setLayoutY(y - radius);
    }
}

// Path is the best object for managing the curve in JavaFX
class Curve extends Path {
    Curve (double startX, double startY, double middleX, double middleY, double endX, double endY) {
        // MoveTo holds starting coordinates of curve
        MoveTo moveTo = new MoveTo();
        moveTo.setX(startX);
        moveTo.setY(startY);
        // QuadCurveTo hold two properties: control point of rotating curve, and end point
        QuadCurveTo quadTo = new QuadCurveTo();
        quadTo.setControlX(middleX);
        quadTo.setControlY(middleY);
        quadTo.setX(endX);
        quadTo.setY(endY);
        // Form the curve
        this.getElements().add(moveTo);
        this.getElements().add(quadTo);
    }
}

class Arrow extends Group {

    private double finalEndX;
    private double finalEndY;
    private double sin;
    private double cos;

    Arrow (double startX, double startY, double endX, double endY, double radius, boolean drawEnd, boolean isDoubling) {
        // If node has a link with itself, place small curve around it
        if (startX == endX && startY == endY) {
            double middleX = startX + 1.6 * radius;
            double middleY = startY + 1.6 * radius;
            finalEndX = startX + radius;
            finalEndY = startY;
            this.calculateAngle(middleX, middleY, finalEndX, finalEndY);
            Curve curve = new Curve(startX, startY + radius, middleX, middleY, finalEndX, finalEndY);
            this.getChildren().add(curve);
        } else {
            // Calculate the angle of the future line
            this.calculateAngle(startX, startY, endX, endY);
            // Calculate coordinates and prepare final coordinates for the line
            double finalStartX = startX + radius * cos;
            double finalStartY = startY + radius * sin;
            finalEndX = endX - radius * cos;
            finalEndY = endY - radius * sin;
            // Determine if we need curve or straight line
            if ((Math.round(startY) == Math.round(endY)
                    && Math.abs(Main.getScreenHeight() / 2 - startY) > Main.getScreenHeight() / 6.5)) {
                // Calculate necessary things for stable representing of the curve
                int sign = (startY > Main.getScreenHeight() / 2) ? 1 : -1;
                if (isDoubling) sign *= -1;
                double upper = radius * Math.abs(finalStartX - finalEndX) / (Main.getScreenWidth() * 0.23125);
                double middleX = (finalStartX + finalEndX) / 2;
                double middleY = finalStartY + upper * sign;
                // Recalculate angle in order to draw arrowEnd properly
                this.calculateAngle(middleX, middleY, finalEndX, finalEndY);
                Curve curve = new Curve(finalStartX, finalStartY, middleX, middleY, finalEndX, finalEndY);
                this.getChildren().add(curve);
            } else if (Math.round(startX) == Math.round(endX)
                    && Math.abs(Main.getScreenWidth() / 2 - startX) > Main.getScreenWidth() / 6.5) {
                int sign = (startX > Main.getScreenWidth() / 2) ? 1 : -1;
                if (isDoubling) sign *= -1;
                double upper = radius * Math.abs(finalStartY - finalEndY) / (Main.getScreenHeight() * 0.15125);
                double middleX = finalStartX + upper * sign;
                double middleY = (finalStartY + finalEndY) / 2;
                // Recalculate angle in order to draw arrowEnd properly
                this.calculateAngle(middleX, middleY, finalEndX, finalEndY);
                Curve curve = new Curve(finalStartX, finalStartY, middleX, middleY, finalEndX, finalEndY);
                this.getChildren().add(curve);
            } else if (isDoubling) {
                double middleX = (finalStartX + finalEndX) / 2 + 30;
                double middleY = (finalStartY + finalEndY) / 2 + 30;
                Curve curve = new Curve(finalStartX, finalStartY, middleX, middleY, finalEndX, finalEndY);
                this.getChildren().add(curve);
            } else {
                // Or draw straight line
                Line line = new Line();
                line.setStartX(finalStartX);
                line.setStartY(finalStartY);
                line.setEndX(finalEndX);
                line.setEndY(finalEndY);
                this.getChildren().add(line);
            }
        }
        if (drawEnd) this.pointMode();
    }

    private void calculateAngle (double startX, double startY, double endX, double endY) {
        double distance = Math.sqrt(Math.pow(endX - startX, 2) + Math.pow(endY - startY, 2));
        sin = (endY - startY) / distance;
        cos = (endX - startX) / distance;
    }

    private void pointMode () {
        double pointX1 = finalEndX - 8 * cos - 4 * sin;
        double pointY1 = finalEndY - 8 * sin + 4 * cos;
        double pointX2 = finalEndX - 8 * cos + 4 * sin;
        double pointY2 = finalEndY - 8 * sin - 4 * cos;
        Polygon arrowEnd = new Polygon();
        arrowEnd.getPoints().addAll(
                finalEndX, finalEndY,
                pointX1, pointY1,
                pointX2, pointY2
        );
        this.getChildren().add(arrowEnd);
    }
}

public class Main extends Application {

    // The code executes here
    public static void main(String[] args) { launch(args); }

    static double getScreenHeight () { return 900; }

    static double getScreenWidth () { return 1600; }

    private double[] getXPositions (double margin) {
        double step = (Main.getScreenWidth() - margin * 2) / 3;
        double[] list = new double[12];
        double currentCoord = margin;
        int i = 0;
        while (i < 3) { list[i] = currentCoord; i++; }
        while (i < 6) { currentCoord += step; list[i] = currentCoord; i++; }
        while (i < 9) { list[i] = currentCoord; i++; }
        while (i < 12) { currentCoord -= step; list[i] = currentCoord; i++; }
        return list;
    }

    private double[] getYPositions (double margin) {
        double step = (Main.getScreenHeight() - margin * 2) / 3;
        double[] list = new double[12];
        double currentCoord = margin;
        int i = 0;
        while (i < 3) { currentCoord += step; list[i] = currentCoord; i++; }
        while (i < 6) { list[i] = currentCoord; i++; }
        while (i < 9) { currentCoord -= step; list[i] = currentCoord; i++; }
        while (i < 12) { list[i] = currentCoord; i++; }
        return list;
    }

    //The matrix was generated with SciLab, gradebook number: 9423
    private int[][] matrix = new int[][]{
            { 0, 1, 0, 0, 1, 1, 0, 0, 1, 0, 0, 1 },
            { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
            { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
            { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
            { 0, 0, 1, 0, 0, 1, 0, 0, 0, 0, 0, 0 },
            { 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0 },
            { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0 },
            { 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0 },
            { 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0 },
            { 1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0 },
            { 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 1, 0 },
            { 1, 0, 1, 0, 0, 0, 0, 1, 1, 1, 1, 0 }
    };

    @Override
    public void start(Stage primaryStage) {
        // Set the label of the window
        primaryStage.setTitle("Лабораторна робота №1");

        // Create instant objects
        double margin = 100;
        double[] coordX = getXPositions(margin);
        double[] coordY = getYPositions(margin);
        int radius = 35;
        Group root = new Group();
        final double screenWidth = Main.getScreenWidth();
        final double screenHeight = Main.getScreenHeight();

        // isDirected is the switch of directed/undirected modes
        boolean isDirected = true;

        // Also it is needed to check double-links. If it exists, draw one of lines in some other way
        ArrayList<String> links = new ArrayList<>();
        boolean isDoubling = false;

        // Add necessary arrows and nodes
        for (int i = 0;  i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                if (matrix[i][j] > 0) {
                    if (matrix[j][i] > 0 && !links.contains(String.valueOf(j) + i) && j != i) {
                        links.add(String.valueOf(i) + j);
                        isDoubling = true;
                    }
                    root.getChildren().add(new Arrow(coordX[i], coordY[i], coordX[j], coordY[j], radius, isDirected, isDoubling));
                    isDoubling = false;
                }
            }
        }

        for (int i = 0; i < matrix.length; i++) {
            String label = String.valueOf(i + 1);
            root.getChildren().add(new Node(label, coordX[i], coordY[i], radius));
        }

        // Load the picture
        primaryStage.setScene(new Scene(root, screenWidth, screenHeight));
        primaryStage.show();
    }
}