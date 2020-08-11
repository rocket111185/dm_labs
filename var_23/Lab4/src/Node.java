import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

class Node extends StackPane {

    private Circle circle;

    Node (String label, double x, double y, double radius) {
        // Create a circle and set color properties
        circle = new Circle(radius);
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

    void setColor (String paint) {
        circle.setFill(Color.valueOf(paint));
    }

}