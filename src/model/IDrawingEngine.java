package model;

import model.Shape;
import java.util.ArrayList;
import java.util.List;

public interface IDrawingEngine {

    public void refresh(Object canvas);

    public void addShape(Shape shape);
    public void removeShape(Shape shape);
    public void updateShape(Shape oldShape, Shape newShape);

    public List<Shape> getShapes();

    public void undo();
    public void redo();

    public void save(String path);
    public void load(String path);

    public java.util.List<Class<? extends Shape>> getSupportedShapes();

    public void installPluginShape(String jarPath);
}
