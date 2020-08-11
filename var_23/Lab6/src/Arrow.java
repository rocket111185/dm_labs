import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

class Arrow extends Group {

    private double sin;
    private double cos;

    Arrow (double startX, double startY, double endX, double endY, double radius, double weight) {

        // This is needed for proper painting of text
        double upper = 0;
        // If node has a link with itself, place small curve around it
        double finalEndX;
        double finalEndY;
        double middleX = 0;
        double middleY = 0;
        if (startX != endX || startY != endY) {

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
                upper = radius * Math.abs(finalStartX - finalEndX) / (Main.getScreenWidth() * 0.23125) * sign;
                middleX = (finalStartX + finalEndX) / 2;
                middleY = finalStartY + upper;
                // Recalculate angle in order to draw arrowEnd properly
                this.calculateAngle(middleX, middleY, finalEndX, finalEndY);
                Curve curve = new Curve(finalStartX, finalStartY, middleX, middleY, finalEndX, finalEndY);
                this.calculateAngle(finalStartX, finalStartY, middleX, middleY);
                this.getChildren().add(curve);

            } else if (Math.round(startX) == Math.round(endX)
                    && Math.abs(Main.getScreenWidth() / 2 - startX) > Main.getScreenWidth() / 6.5) {

                int sign = (startX > Main.getScreenWidth() / 2) ? 1 : -1;
                upper = radius * Math.abs(finalStartY - finalEndY) / (Main.getScreenHeight() * 0.15125) * sign;
                middleX = finalStartX + upper;
                middleY = (finalStartY + finalEndY) / 2;
                // Recalculate angle in order to draw arrowEnd properly
                this.calculateAngle(middleX, middleY, finalEndX, finalEndY);
                Curve curve = new Curve(finalStartX, finalStartY, middleX, middleY, finalEndX, finalEndY);
                this.calculateAngle(finalStartX, finalStartY, middleX, middleY);
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
            Text text = new Text((int)weight + "");
            text.setFill(Color.valueOf("C70036"));
            text.setFont(Font.font("Roboto", 13));
            double textX = finalStartX + radius * 2 * cos;
            double textY = finalStartY + radius * 2 * sin;
            if (upper != 0) {
                    textX = finalStartX + Math.abs(middleX - finalStartX) * 1.5 * cos;
                    textY = finalStartY + Math.abs(middleY - finalStartY) * 1.5 * sin;
            }
            text.setX(textX);
            text.setY(textY);
            this.getChildren().add(text);
        }
    }

    private void calculateAngle(double startX, double startY, double endX, double endY) {
        double distance = Math.sqrt(Math.pow(endX - startX, 2) + Math.pow(endY - startY, 2));
        sin = (endY - startY) / distance;
        cos = (endX - startX) / distance;
    }
}
