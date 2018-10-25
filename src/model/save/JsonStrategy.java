package model.save;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.json.JettisonMappedXmlDriver;
import com.thoughtworks.xstream.io.xml.DomDriver;
import model.Application;
import model.Shape;
import model.commands.Command;
import sun.applet.AppletListener;

import javax.jws.soap.SOAPBinding;
import java.io.PrintWriter;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Stack;

public class JsonStrategy implements  ISaveNLoadStrategy{

    private Application application = Application.getInstance();


    @Override
    public void save(String fileName) {

        XStream xStream  = new XStream(new JettisonMappedXmlDriver());
        String jsonShapes = xStream.toXML(application.getShapes());
        String jsonUndoCommands =  xStream.toXML(application.getUndoStack());
        String jsonRedoCommands =  xStream.toXML(application.getRedoStack());

        try {
            PrintWriter shapesWriter = new PrintWriter(fileName);
            shapesWriter.write(jsonShapes);
            shapesWriter.close();

            PrintWriter undoWriter = new PrintWriter("undo.json");
            undoWriter.write(jsonUndoCommands);
            undoWriter.close();

            PrintWriter redoWriter = new PrintWriter("redo.json");
            redoWriter.write(jsonRedoCommands);
            redoWriter.close();
        } catch (Exception ex) {
            ex.getMessage();
        }



    }

    @Override
    public void load(String path) {
        XStream xstream = new XStream(new JettisonMappedXmlDriver());

        String json = "";
        String redoJson = "";
        String undoJson = "";
        try {

            json = new String(Files.readAllBytes(FileSystems.getDefault().getPath(path)));
            redoJson = new String(Files.readAllBytes(FileSystems.getDefault().getPath("redo.json")));
            undoJson = new String(Files.readAllBytes(FileSystems.getDefault().getPath("undo.json")));


        } catch (Exception ex) {
            ex.getMessage();
        }

        /* 2. Refill the shapes list. */
        application.getShapes().clear();
        ArrayList<Shape> shapes = (ArrayList<Shape>) xstream.fromXML(json);
        application.getShapes().addAll(shapes);

        application.getRedoStack().clear();
        Stack<Command> redo = (Stack<Command>) xstream.fromXML(redoJson);

        application.getUndoStack().clear();
        Stack<Command> undo = (Stack<Command>) xstream.fromXML(undoJson);

        application.getRedoStack().addAll(redo);
        application.getUndoStack().addAll(undo);

        /* 3. Refresh the canvas. */
        application.refresh(application.getCanvas());

    }
}
