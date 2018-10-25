package model.commands;

import model.Application;
import model.Memento;
import model.Shape;

public class CloneCommand implements Command {

    private Shape shape;
    private static Application application = Application.getInstance();

    /* 1. Get the old state before executing the command. */
    private Memento memento = application.createMemento();

    public CloneCommand(Shape shape) {
        this.shape  = shape;
    }

    @Override
    public void execute() {

        /* 2. Perform the request. */
        try {
            Shape clone = (Shape) shape.clone();
            clone.draw(application.getCanvas()); // the draw method should add the shape to the shapes list
        } catch (Exception ex) {
            System.out.println("Error!");
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
