import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.QuadCurveTo;

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