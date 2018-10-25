package model;

import javafx.scene.paint.Color;
import model.Shapes.IShape;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

public abstract class ShapeLoader {

    public static void extractjar(File file) throws ZipException {

        String source = file.getPath().replace("\\","/");
        String destination = file.getParentFile().getPath() + "\\" + file.getName().replace(".jar","");

        ZipFile zipFile = new ZipFile(source);
        zipFile.extractAll(destination);
    }

    public static Shape loadShape(File file) throws MalformedURLException, ClassNotFoundException,
            IllegalAccessException, InstantiationException {

        String shapeName = file.getName().replace(".jar", "");
        String classPath = file.getParentFile().getPath() + "\\" + shapeName;


        File classFile = new File(classPath);

        URL url = classFile.toURL();
        URL[] urls = new URL[]{url};


        Class cls = (Class) new URLClassLoader(urls).loadClass(shapeName);
        IShape shapeFace = (IShape) cls.newInstance();

        Shape shape = new Shape(shapeFace);

        shape.getProperties().put("x", 50.0);
        shape.getProperties().put("y", 50.0);
        shape.getProperties().put("width", 50.0);
        shape.getProperties().put("height", 50.0);
        shape.getProperties().put("opacity", 0.0);
        shape.getProperties().put("strokewidth", 0.0);
        shape.setBackColor(Color.RED);
        shape.setStrokeColor(Color.WHITE);
        shape.name = shapeName;
        shape.groupName = "";
        shape.oldX = 50;
        shape.oldY = 50;

        return shape;
    }
}
