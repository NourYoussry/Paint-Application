package model.commands;

import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.control.TreeItem;
import model.Application;
import model.Memento;
import model.Shape;

import java.util.ArrayList;

public class RemoveShapeCommand implements Command {

    private Shape shape;
    private static Application application = Application.getInstance();;

    /* 1. Get the old state before executing the command. */
    private Memento memento = application.createMemento();

    public RemoveShapeCommand (Shape shape) {
        this.shape = shape;
    }

    @Override
    public void execute() {

        /* 2. Perform the request. */
        shape.remove();

        /* 3. Push the command onto the undo stack. */
        application.pushCommand(this);

        /* 4. Refresh to reflect changes. */
        application.refresh(application.getCanvas());
    }

    @Override
    public void undo() {

        /* 1. Revert the application state to the backup state. */
        application.setState((ArrayList<Shape>) memento.getState());

        /*2. Refresh to reflect changes. */
        application.refresh(application.getCanvas());

        /* 3. Add the removed shape back to the treeview. */
        TreeItem<String> shapeItem = new TreeItem<>(this.shape.name);
        application.getTreeView().getRoot().getChildren().add(shapeItem);

        /* Reselect the removed shape. */
        MultipleSelectionModel msm = application.getTreeView().getSelectionModel();
        msm.select(application.getTreeView().getRow(shapeItem));

    }
}
