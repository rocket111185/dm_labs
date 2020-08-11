import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.Group;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

class Node extends Group {

    private Circle circle;
    // Initial value of the weight -- infinity
    private Text weightText = new Text("\u221E");

    Node (String label, double x, double y, double radius) {
        // Create a circle and set color properties
        circle = new Circle(radius);
        circle.setFill(Color.WHITE);
        circle.setStroke(Color.BLACK);
        // Text is a number of node
        Text text = new Text(label);
        text.setFont(Font.font(radius * 0.6));
        StackPane round = new StackPane();

        round.getChildren().addAll(circle, text);
        round.setLayoutX(x - radius);
        round.setLayoutY(y - radius);


        weightText.setFont(Font.font(radius * 0.6));
        weightText.setFill(Color.valueOf("5F5F0A"));
        weightText.setLayoutX(x - radius * 1.1);
        weightText.setLayoutY(y - radius * 1.1);

        // Create a group of circle and text and set coordinates
        this.getChildren().addAll(round, weightText);

    }

    void setColor (String paint) {
        circle.setFill(Color.valueOf(paint));
    }

    void setWeight (String weight) {
        StringProperty valueProperty = new SimpleStringProperty();
        valueProperty.setValue(weight);
        weightText.textProperty().bind(valueProperty);
    }

    int getWeight () {
        if (weightText.getText().equals("\u221E"))
            return Integer.MAX_VALUE;
        return Integer.parseInt(weightText.getText());
    }

}