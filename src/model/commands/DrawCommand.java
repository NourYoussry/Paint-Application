package model.commands;

import model.Application;
import model.Memento;
import model.Shape;
import model.Shapes.IShape;

public class DrawCommand implements Command {

    private IShape shape;
    private Memento memento;
    private static Application application = Application.getInstance();;

    public DrawCommand (IShape shape) {

        this.shape = shape;

    }

    @Override
    public void execute() {

        /* 1. Get the old state before executing the command. */
        memento = application.createMemento();

        /* 2. Perform the request. */
        shape.draw(application.getCanvas()); // the draw method should add the shape to the shapes list

        /* 3. Push the command onto the undo stack. */
        application.pushCommand(this);
    }

    @Override
    public void undo() {

        /* 1. Revert the application state to the backup state. */
        application.setState(memento.getState());

    }
}
