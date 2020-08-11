import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

class Node extends StackPane {

    private final String index;
    private int positiveDegree = 0;
    private int negativeDegree = 0;

    Node (String label, double x, double y, double radius) {
        this.index = label;
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

    void increasePositiveDegree() { positiveDegree++; }
    void increaseNegativeDegree () { negativeDegree++; }

    String getIndex () { return index; }
    int getPositiveDegree () { return positiveDegree; }
    int getNegativeDegree () { return negativeDegree; }
    int getDegree () { return positiveDegree + negativeDegree; }

    boolean isIsolated () { return positiveDegree + negativeDegree == 0; }
    boolean isHanging () { return positiveDegree + negativeDegree == 1; }
}
