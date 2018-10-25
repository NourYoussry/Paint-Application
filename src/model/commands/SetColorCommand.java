package model.commands;

import model.Application;
import model.Memento;
import model.Shape;
import javafx.scene.paint.Color;

public class SetColorCommand implements Command {

    private Shape shape;
    private Color color;
    private static Application application = Application.getInstance();

    /* 1. Get the old state before executing the command. */
    private Memento memento = application.createMemento();

    public SetColorCommand(Shape shape, Color color) {

        this.shape       = shape;
        this.color       = color;
    }

    @Override
    public void execute() {

        /* 2. Perform the request. */
        for (Shape s : application.getShapes()) {
            if (s.name.equals(shape.name)) {
                s.setStrokeColor(color);
                break;
            }
        }

        /* 3. Push the command onto the undo stack. */
        application.pushCommand(this);

        /* 4. Refresh the canvas to reflect changes. */
        application.refresh(application.getCanvas());
    }

    @Override
    public void undo() {

        /* 1. Revert the application state to the backup state. */
        application.setState(memento.getState());

        /*2. Refresh to reflect changes. */
        application.refresh(application.getCanvas());

    }

}
