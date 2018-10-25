package model.save;

import com.thoughtworks.xstream.io.xml.DomDriver;
import javafx.geometry.Point2D;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.control.TreeItem;
import javafx.scene.paint.Color;
import model.Application;
import model.Shape;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.StaxDriver;
import model.Shapes.PCircle;
import model.commands.Command;

import java.io.File;
import java.io.PrintWriter;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

public class XmlStrategy implements ISaveNLoadStrategy {

    private final String undoCommands = "undoCommands.xml";
    private final String redoCommands = "redoCommands.xml";

    private Application application = Application.getInstance();

    public void save (String fileName) {

        /* 1. Initialize the serializer. */
        XStream xstream = new XStream(new DomDriver());

        /* 2. Generate the XML string. */
        String shapesXML = xstream.toXML(application.getShapes());
        String undoStackXML = xstream.toXML(application.getUndoStack());
        String redoStackXML = xstream.toXML(application.getRedoStack());

        /* 3. Print the XML string into a file with the given file name. */
        try {
            PrintWriter writer = new PrintWriter(fileName);
            writer.write(shapesXML);
            writer.close();

            writer = new PrintWriter(undoCommands);
            writer.write(undoStackXML);
            writer.close();

            writer = new PrintWriter(redoCommands);
            writer.write(redoStackXML);
              writer.close();
        } catch (Exception ex) {
            ex.getMessage();
        }
    }

    public void load(String fileName) {

        /* 1. Initialize and open the stream. */
        XStream xstream = new XStream(new DomDriver());

        String shapesXML = "", undoStackXML = "", redoStackXML = "";
        try {
            shapesXML = new String(Files.readAllBytes(FileSystems.getDefault().getPath(fileName)));
            undoStackXML = new String(Files.readAllBytes(FileSystems.getDefault().getPath(undoCommands)));
            redoStackXML = new String(Files.readAllBytes(FileSystems.getDefault().getPath(redoCommands)));
        } catch (Exception ex) {
            ex.getMessage();
        }

        /* 2. Reload the shapes list. */
        application.getShapes().clear();
        ArrayList<Shape> shapes = (ArrayList<Shape>) xstream.fromXML(shapesXML);
        application.getShapes().addAll(shapes);

        /* 3. Reload the undo stack. */
        application.getUndoStack().clear();
        Stack<Command> undoCommands = (Stack<Command>) xstream.fromXML(undoStackXML);
        application.getUndoStack().addAll(undoCommands);

        /* 4. Reload the redo stack. */
        application.getRedoStack().clear();
        Stack<Command> redoCommands = (Stack<Command>) xstream.fromXML(redoStackXML);
        application.getRedoStack().addAll(redoCommands);

        /* 5. Refresh the canvas to reflect changes. */
        application.refresh(application.getCanvas());

        /* 6. Refill the tree view. */
        application.getTreeView().getRoot().getChildren().clear();
        MultipleSelectionModel msm = application.getTreeView().getSelectionModel();

        int first = 0;
        for (Shape shape : application.getShapes()) {
            TreeItem<String> item = new TreeItem<>(shape.name);
            application.getTreeView().getRoot().getChildren().add(item);
            if (first == 0)
                msm.select(application.getTreeView().getRow(item));
            first++;
        }
    }

}