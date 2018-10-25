package model.commands;

import model.Application;
import model.Memento;
import model.Shape;
import javafx.geometry.Point2D;

public class SetPositionCommand implements Command {

    private Shape shape;
    private Point2D position;

    private static Application application = Application.getInstance();;

    /* 1. Get the old state before executing the command. */
    private Memento memento = application.createMemento();

    public SetPositionCommand(Shape shape, Point2D position) {
        this.shape  = shape;
        this.position = position;
    }

    @Override
    public void execute() {

        /* 2. Perform the request. */
        for (Shape s : application.getShapes()) {
            if (s.name.equals(shape.name)) {
                s.setPosition(position);
                break;
            }
        }

        /* 3. Push the command onto the undo stack. */
        application.pushCommand(this);
    }

    @Override
    public void undo() {

        /* 1. Revert the application state to the backup state. */
        application.setState(memento.getState());

    }
}
