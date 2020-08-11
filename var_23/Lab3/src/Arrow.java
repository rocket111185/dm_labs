import javafx.scene.Group;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;

class Arrow extends Group {

    private final double finalEndX;
    private final double finalEndY;
    private double sin;
    private double cos;
    private final double rad;

    Arrow (double startX, double startY, double endX, double endY, double radius, boolean isDoubling) {

        // This is needed for proper painting of pointer *pointMode()*
        this.rad = radius;

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
        this.pointMode();
    }

    private void calculateAngle(double startX, double startY, double endX, double endY) {
        double distance = Math.sqrt(Math.pow(endX - startX, 2) + Math.pow(endY - startY, 2));
        sin = (endY - startY) / distance;
        cos = (endX - startX) / distance;
    }

    private void pointMode() {
        double pointSize = rad / 7;
        double pointX1 = finalEndX - 2 * pointSize * cos - pointSize * sin;
        double pointY1 = finalEndY - 2 * pointSize * sin + pointSize * cos;
        double pointX2 = finalEndX - 2 * pointSize * cos + pointSize * sin;
        double pointY2 = finalEndY - 2 * pointSize * sin - pointSize * cos;
        Polygon arrowEnd = new Polygon();
        arrowEnd.getPoints().addAll(
                finalEndX, finalEndY,
                pointX1, pointY1,
                pointX2, pointY2
        );
        this.getChildren().add(arrowEnd);
    }
}
