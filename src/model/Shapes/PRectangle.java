package model.Shapes;

import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.HashMap;
import java.util.Map;

public class PRectangle implements IShape {

    private Map<String, Double> properties= new HashMap<String, Double>();
    private Color backColor;
    private Color strokeColor;



    public PRectangle(){

    }

    public PRectangle(double x, double y, double width, double height, double opacity, double strokeWdith) {

        properties = new HashMap<String, Double>();
        properties.put("x", x);
        properties.put("y", y);
        properties.put("width", width);
        properties.put("height", height);
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

        graphicsContext.fillRect(properties.get("x"), properties.get("y"),
                properties.get("width"), properties.get("height"));

        if(getProperties().get("strokewidth") != 0)
        graphicsContext.strokeRect(properties.get("x"), properties.get("y"),
                properties.get("width"), properties.get("height"));
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
        PRectangle newRectangle = new PRectangle(
                getProperties().get("x"),
                getProperties().get("y"),
                getProperties().get("width"),
                getProperties().get("height"),
                getProperties().get("opacity"),
                getProperties().get("strokewidth")

        );
        newRectangle.setBackColor(getBackColor());
        newRectangle.setStrokeColor(getStrokeColor());


        return newRectangle;
    }

    /*******************************************************/
}
