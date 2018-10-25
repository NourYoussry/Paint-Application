package model.Shapes;

import javafx.geometry.Point2D;
import javafx.scene.paint.Color;

import java.util.Map;

public interface IShape extends IShapeDNA {

    void setPosition(Point2D position);
    Point2D getPosition();

    void setProperties(Map<String, Double> properties);
    Map<String, Double> getProperties();

    void setBackColor(Color color);
    Color getBackColor();

    void setStrokeColor(Color color);
    Color getStrokeColor();

    void draw(Object canvas);

    Object clone() throws CloneNotSupportedException;
}
