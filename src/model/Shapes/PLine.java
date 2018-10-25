package model.Shapes;

import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.HashMap;
import java.util.Map;

public class PLine implements IShape {

    private Map<String, Double> properties= new HashMap<String, Double>();
    private Color backColor;
    private Color strokeColor;


    public PLine(){

    }

    public PLine(double x, double y, double x2, double y2, double opacity, double strokeWdith) {

        properties = new HashMap<String, Double>();
        properties.put("x", x);
        properties.put("y", y);
        properties.put("x2", x2);
        properties.put("y2", y2);
        properties.put("opacity", opacity);
        properties.put("strokewidth", strokeWdith);
    }

    /*******************************************************/

    //region position

    @Override
    public void setPosition(Point2D position) {

        this.properties.replace("x", position.getX());
        this.properties.replace("y", position.getY());
    }

    @Override
    public Point2D getPosition() {

        return new Point2D(properties.get("x"),properties.get("y"));
    }

    //endregion

    /*******************************************************/

    //region properties

    @Override
    public void setProperties(Map<String, Double> properties) {
        this.properties = properties;
    }

    @Override
    public Map<String, Double> getProperties() {
        return this.properties;
    }

    //endregion

    /*******************************************************/

    //region color

    @Override
    public void setBackColor(Color color) {
        this.backColor = color;
    }

    @Override
    public Color getBackColor() {
        return this.backColor;
    }

    @Override
    public void setStrokeColor(Color color) {
        this.strokeColor = color;
    }

    @Override
    public Color getStrokeColor() {
        return this.strokeColor;
    }

    //endregion

    /*******************************************************/

    //region draw & erase

    @Override
    public void draw(Object canvas) {

        GraphicsContext graphicsContext = ((Canvas) canvas).getGraphicsContext2D();

        graphicsContext.setLineWidth(properties.get("strokewidth"));

        graphicsContext.setFill(backColor);
        graphicsContext.setStroke(strokeColor);

        double x = properties.get("x");
        double y = properties.get("y");
        double x2 = properties.get("x2");
        double y2 = properties.get("y2");

        if(getProperties().get("strokewidth") != 0)
        graphicsContext.strokeLine(x, y, x2, y2);
    }

    @Override
    public void erase(Object canvas) {

        GraphicsContext graphicsContext = ((Canvas)canvas).getGraphicsContext2D();
        graphicsContext.clearRect(0,0,1072,603);
    }

    //endregion

    /*******************************************************/

    @Override
    public Object clone() throws CloneNotSupportedException {

        PLine newLine = new PLine(
                getProperties().get("x"),
                getProperties().get("y"),
                getProperties().get("x2"),
                getProperties().get("y2"),
                getProperties().get("opacity"),
                getProperties().get("strokewidth")

        );
        newLine.setBackColor(getBackColor());
        newLine.setStrokeColor(getStrokeColor());


        return newLine;
    }

    /*******************************************************/
}
