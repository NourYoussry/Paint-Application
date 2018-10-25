package model;

import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.control.TreeItem;
import model.Shapes.IShape;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import model.commands.DrawCommand;
import model.commands.SetPositionCommand;
import model.commands.SetPropertiesCommand;

import java.util.HashMap;
import java.util.Map;

public class Shape {

    /* Make this field static to prevent serialization. */
    private static Application application = Application.getInstance();

    private IShape currentState;
    public String name;
    public String groupName = "";

    public double oldX;
    public double oldY;

    public Shape (IShape currentState) {
        this.currentState = currentState;
    }

    public void setPosition(Point2D position) {
        currentState.setPosition(position);
    }

    public Point2D getPosition() {
        return currentState.getPosition();
    }

    public void setProperties(Map<String, Double> properties) {
        currentState.setProperties(properties);
    }

    public Map<String, Double> getProperties() {
        return currentState.getProperties();
    }

    public void setBackColor(Color color) {
        currentState.setBackColor(color);
    }

    public Color getBackColor() {
        return currentState.getBackColor();
    }

    public void setStrokeColor(Color color) {
        currentState.setStrokeColor(color);
    }

    public Color getStrokeColor() {
        return currentState.getStrokeColor();
    }

    public void draw(Object canvas) {
       currentState.draw(canvas);
    }

    public void erase(Object canvas){
        currentState.erase(canvas);
    }

    public Object clone() throws CloneNotSupportedException {

        return currentState.clone();

    }

    public IShape getState() {
        return currentState;
    }

    public void setState(IShape currentState) {
        this.currentState = currentState;
    }

    public void remove() {

        /* 1. Remove the shape from shapes list. */
        for (Shape shape : application.getShapes()) {
            if (shape.name.equals(this.name)) {
                application.getShapes().remove(shape);
                break;
            }
        }

        /* 2. Remove the shape from the tree view. */
        TreeItem<String> selectedItem = application.getTreeView().getSelectionModel().getSelectedItem();
        selectedItem.getParent().getChildren().remove(selectedItem);

    }

}
