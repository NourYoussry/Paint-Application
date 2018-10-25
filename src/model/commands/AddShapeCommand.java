package model.commands;

import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.control.TreeItem;
import model.Application;
import model.Memento;
import model.Shape;

public class AddShapeCommand implements Command {

    private Shape shape;
    private static Application application = Application.getInstance();

    /* 1. Get the old state before executing the command. */
    private Memento memento = application.createMemento();

    public AddShapeCommand (Shape shape) {
        this.shape = shape;
    }

    @Override
    public void execute () {

        /* 2. Perform the request. */
        application.getShapes().add(shape);

        /* 3. Push the command onto the undo stack. */
        application.pushCommand(this);

        /* 4. Refresh the canvas to reflect changes. */
        application.refresh(application.getCanvas());

        /* 5. Add the shape to the tree list. */
        TreeItem<String> newItem = new TreeItem<>(shape.name);
        application.getTreeView().getRoot().getChildren().add(newItem);

        MultipleSelectionModel msm = application.getTreeView().getSelectionModel();
        msm.select(application.getTreeView().getRow(newItem));
    }

    @Override
    public void undo() {

        /* 1. Revert the application state to the backup state. */
        application.setState(memento.getState());

        /*2. Refresh to reflect changes. */
        application.refresh(application.getCanvas());

        /*3. Remove the shape from the tree view. */
        for (TreeItem<String> item : application.getTreeView().getRoot().getChildren())
            if (item.getValue().equals(shape.name)) {
                application.getTreeView().getRoot().getChildren().remove(item);
                break;
            }

    }
}
