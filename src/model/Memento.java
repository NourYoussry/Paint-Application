package model;

import controller.Controller;
import model.Shapes.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Memento {

    private List<Shape> state;

    public void setState(List<Shape> applicationState){


        this.state = new ArrayList<>();

        for (int i = 0, n = applicationState.size(); i < n; i++) {
            try {
                Shape clone = diagnoseShape(applicationState.get(i));
                this.state.add(clone);
            } catch (Exception ex) {
                ex.getMessage();
            }
        }

    }

    public List<Shape> getState(){

        return this.state;

    }

    public static Shape diagnoseShape(Shape originShape) throws CloneNotSupportedException {

        Shape tempShape = null;

        if (originShape.getState() instanceof PCircle)
            tempShape = new Shape((PCircle) originShape.clone());
        if (originShape.getState() instanceof PEllipse)
            tempShape = new Shape((PEllipse) originShape.clone());
        if (originShape.getState() instanceof PRectangle)
            tempShape = new Shape((PRectangle) originShape.clone());
        if (originShape.getState() instanceof PSquare)
            tempShape = new Shape((PSquare) originShape.clone());
        if (originShape.getState() instanceof PLine)
            tempShape = new Shape((PLine) originShape.clone());
        if (originShape.getState() instanceof PHexagon)
            tempShape = new Shape((PHexagon) originShape.clone());
        if (originShape.getState() instanceof PTriangle)
            tempShape = new Shape((PTriangle) originShape.clone());
        if (originShape.getState() instanceof PPentagon)
            tempShape = new Shape((PPentagon) originShape.clone());

        assert tempShape != null;

        tempShape.oldX = originShape.oldX;
        tempShape.oldY = originShape.oldY;
        tempShape.name = originShape.name;
        tempShape.groupName = "";

        return tempShape;
    }

}
