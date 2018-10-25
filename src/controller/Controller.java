package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.Application;
import model.Shape;
import model.ShapeLoader;
import model.Shapes.*;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import model.commands.*;
import model.save.JsonStrategy;
import model.save.XmlStrategy;
import net.lingala.zip4j.exception.ZipException;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;

public class Controller implements Initializable {

    public Pane MyPane;
    public Canvas MyCanvas;
    public Pane MyToolsBar;
    public Pane MyTitle;

    public ColorPicker MyBackColorPicker;
    public ColorPicker MyStrokeColorPicker;

    public ToggleButton MyHandRadioBTN;
    public ToggleButton MySizeRadioBTN;
    public ToggleButton MyMouseRadioBTN;

    public Button CloseBTN;
    public Button MiniButton;
    public Button fileBTN;
    public Button InsertBTN;
    public Button DeleteBTN;
    public Button CopyBTN;

    public ToggleButton CircleBTN;
    public ToggleButton EllipseBTN;
    public ToggleButton RectangleBTN;
    public ToggleButton PolygonBTN;
    public ToggleButton SquareBTN;
    public ToggleButton LineBTN;
    public ToggleButton TriangleBTN;
    public ToggleButton PentagonBTN;

    public Slider MyOpacitySlider;
    public Slider MyStrokeSlider;

    private Application application = Application.getInstance();
    public TreeView <String> MyTreeView;

    private TreeItem<String> root = new TreeItem<String>("Paint");
    private List<MenuItem> groupsList = new ArrayList<>();

    private Stage currentStage;

    private ContextMenu contextMenu = new ContextMenu();
    private ContextMenu fileContextMenu = new ContextMenu();

    private MenuItem load = new MenuItem("Load shapes");
    private MenuItem save = new MenuItem("Save Shapes");

    private static int[] counter = new int[8];
    private int groupsCounter = 1;

    private double x, y;

    private Shape selectedShape = null;
    private Shape newShape = null;

    private TreeItem<String> selectedItem = null;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        contextMenu.setOnAction(this::contextMenu_Click);

        /* Set the canvas reference in the application singleton. */
        application.setCanvas(MyCanvas);

        MyOpacitySlider = new Slider();
        MyOpacitySlider.setValue(100);

        /* Default start. */
        MyMouseRadioBTN.setSelected(true);
        CircleBTN.setSelected(true);

        MyTreeView.setRoot(root);
        MyTreeView.setShowRoot(false);

        /* Indexing for shapes' names. */
        for (int i = 0; i < 8; i++) counter[i] = 1;

        /* Set the tree reference in the application singleton. */
        application.setTreeView(MyTreeView);
        load.setOnAction(this::FileMenu_Click);
        save.setOnAction(this::FileMenu_Click);
        fileContextMenu.getItems().addAll(load,save);

        initDrag();
    }

    private void initDrag() {

        MyTitle.setOnMousePressed(event -> {

            currentStage = (Stage) MyTitle.getScene().getWindow();

            x = currentStage.getX() - event.getScreenX();
            y = currentStage.getY() - event.getScreenY();

            MyTitle.setCursor(Cursor.CLOSED_HAND);

        });

        MyTitle.setOnMouseDragged(event -> {

            currentStage.setX(event.getScreenX() + x);
            currentStage.setY(event.getScreenY() + y);

        });


        MyTitle.setOnMouseReleased(event -> {

            MyTitle.setCursor(Cursor.DEFAULT);

        });
    }

    private void FileMenu_Click(ActionEvent actionEvent) {

        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("XML", "*.xml*"),
                new FileChooser.ExtensionFilter("JSON", "*.json")
        );

        if (actionEvent.getSource() == load) {
            fileChooser.setTitle("Open Image");
            File file = fileChooser.showOpenDialog(currentStage);

            if (file == null) return;

            if (fileChooser.getSelectedExtensionFilter().getDescription().equals("XML"))
                application.setSaveNLoadStrategy(new XmlStrategy());
            else
                application.setSaveNLoadStrategy(new JsonStrategy());
            application.load(file.getPath());
        }

        else if (actionEvent.getSource() == save){
            fileChooser.setTitle("Save Image");
            File file = fileChooser.showSaveDialog(currentStage);

            if (file == null) return;

            if (fileChooser.getSelectedExtensionFilter().getDescription().equals("XML")) {
                application.setSaveNLoadStrategy(new XmlStrategy());
                application.save(file.getPath() + ".xml");
            }
            else {
                application.setSaveNLoadStrategy(new JsonStrategy());
                application.save(file.getPath() + ".json");
            }

        }
    }

    private void resizeShape(){

        double width,height;

        if(selectedShape.getState() instanceof PCircle || selectedShape.getState() instanceof PSquare)
        {
            width = (x - selectedShape.oldX) * 2;
            height = (x - selectedShape.oldX) * 2;

            selectedShape.getProperties().replace("x", selectedShape.oldX - width / 2);
            selectedShape.getProperties().replace("y", selectedShape.oldY - width / 2);

            selectedShape.getProperties().replace("width", width);
            selectedShape.getProperties().replace("height",height);
        }
        else if(selectedShape.getState() instanceof PLine)
        {
            selectedShape.getProperties().replace("x2", x);
            selectedShape.getProperties().replace("y2", y);
        }
        else
        {
            width = (x - selectedShape.getProperties().get("x"));
            height = (y - selectedShape.getProperties().get("y"));

            selectedShape.getProperties().replace("width", width);
            selectedShape.getProperties().replace("height",height);
        }
    }

    private void removeShape(TreeItem<String> selectedItem){

        /* remove shape from shapesList */
        for (Shape shape: application.getShapes())
            if (shape.name.equals(selectedItem.getValue())) {

                RemoveShapeCommand removeShapeCommand = new RemoveShapeCommand(shape);
                removeShapeCommand.execute();
                application.refresh(application.getCanvas());

                break;
            }
    }

    private void removeGroup(TreeItem<String> selectedItem) {

        for (MenuItem current : groupsList) {

            if (current.getText().equals(selectedItem.getValue())) {

                RemoveGroupCommand removeGroupCommand = new RemoveGroupCommand(selectedItem.getValue());
                removeGroupCommand.execute();

                application.refresh(application.getCanvas());

                selectedItem.getParent().getChildren().remove(selectedItem);
                contextMenu.getItems().remove(current);
                groupsList.remove(current);
                break;
            }
        }
    }

    public void MyCanvas_Press(MouseEvent mouseEvent) {

        /* Get the mouse press coordinates. */
        x = mouseEvent.getX();
        y = mouseEvent.getY();

        /* If the user wants to draw and selected the mouse radio button. */
        if (MyMouseRadioBTN.isSelected()) {
            Shape newShape = null;

            int index = -1;

            if(CircleBTN.isSelected()){
                newShape = new Shape(new PCircle(x, y, 0, 0, MyOpacitySlider.getValue(),
                        MyStrokeSlider.getValue()));

                index = 0;
            }

            if(EllipseBTN.isSelected()){
                newShape = new Shape(new PEllipse(x, y, 0, 0, MyOpacitySlider.getValue(),
                        MyStrokeSlider.getValue()));

                index = 1;
            }

            if(RectangleBTN.isSelected()){
                newShape = new Shape(new PRectangle(x, y, 0, 0, MyOpacitySlider.getValue(),
                        MyStrokeSlider.getValue()));

                index = 2;
            }

            if(SquareBTN.isSelected()){
                newShape = new Shape(new PSquare(x, y, 0, 0, MyOpacitySlider.getValue(),
                        MyStrokeSlider.getValue()));

                index = 3;
            }

            if(LineBTN.isSelected()){
                newShape = new Shape(new PLine(x, y, x, y, MyOpacitySlider.getValue(),
                        MyStrokeSlider.getValue()));

                index = 4;
            }

            if(PolygonBTN.isSelected()){
                newShape = new Shape(new PHexagon(x, y, 0, 0, MyOpacitySlider.getValue(),
                        MyStrokeSlider.getValue()));

                index = 5;
            }

            if(TriangleBTN.isSelected()) {
                newShape = new Shape(new PTriangle(x, y, 0, 0, MyOpacitySlider.getValue(),
                        MyStrokeSlider.getValue()));

                index = 6;
            }

            if(PentagonBTN.isSelected()) {
                newShape = new Shape(new PPentagon(x, y, 0, 0, MyOpacitySlider.getValue(),
                        MyStrokeSlider.getValue()));

                index = 7;
            }

            assert newShape != null;

            newShape.oldX = x;
            newShape.oldY = y;

            /* Set the fill color. */
            newShape.setBackColor(MyBackColorPicker.getValue());

            /* Set the stroke color. */
            newShape.setStrokeColor(MyStrokeColorPicker.getValue());

            /* Set the shape's name in the tree view. */
            newShape.name = newShape.getState().getClass().getName().replace("model.Shapes.P","") + " (" + counter[index]++ + ")";

            /* Add the new shape to the array list. */
            AddShapeCommand addShapeCommand = new AddShapeCommand(newShape);
            addShapeCommand.execute();

            selectedShape = newShape;
            newShape = null;
        }
    }

    public void MyCanvas_Drag(MouseEvent mouseEvent) {

        /* Get the cursor's coordinates. */
        x = mouseEvent.getX();
        y = mouseEvent.getY();

        if (!root.getChildren().isEmpty())
        {
            selectedItem = MyTreeView.getSelectionModel().getSelectedItem();

            if (!selectedItem.getValue().contains("Group")){

                /* Get a reference to the selected shape. */
                for (Shape current : application.getShapes()) {
                    if (current.name.equals(selectedItem.getValue())) {
                        selectedShape = current;
                        break;
                    }
                }

                /* If the user wants to drag the selected shape. */
                if (MyHandRadioBTN.isSelected()) {

                    /* Change the cursor shape. */
                    MyCanvas.setCursor(Cursor.CLOSED_HAND);

                    if(selectedShape.getState() instanceof PLine)
                    {
                        double width = selectedShape.getProperties().get("x2") - selectedShape.getProperties().get("x");
                        double height = selectedShape.getProperties().get("y2") - selectedShape.getProperties().get("y");

                        selectedShape.getProperties().replace("x", x);
                        selectedShape.getProperties().replace("y", y);

                        selectedShape.getProperties().replace("x2", selectedShape.getProperties().get("x") +  width);
                        selectedShape.getProperties().replace("y2", selectedShape.getProperties().get("y") +  height);
                    }

                    else
                        selectedShape.setPosition(new Point2D(
                                x - selectedShape.getProperties().get("width") / 2,
                                y - selectedShape.getProperties().get("height") / 2));
                }

                if(MySizeRadioBTN.isSelected() || MyMouseRadioBTN.isSelected()) {

                    assert selectedShape != null;
                    resizeShape();
                }

                /* Refresh the canvas to reflect the changes. */
                application.refresh(application.getCanvas());
            }
        }
    }

    public void MyCanvas_Release(MouseEvent mouseEvent) {

        /* New shape coordinates. */
        x = mouseEvent.getX();
        y = mouseEvent.getY();

        if (selectedShape != null) {
            if (MyHandRadioBTN.isSelected()) {
                if (selectedShape.getState() instanceof PCircle || selectedShape.getState() instanceof PSquare) {
                    selectedShape.oldX = x;
                    selectedShape.oldY = y;
                }

                if (selectedShape.getState() instanceof  PEllipse
                        || selectedShape.getState() instanceof PRectangle
                        || selectedShape.getState() instanceof PLine)
                {
                    selectedShape.oldX = selectedShape.getProperties().get("x");
                    selectedShape.oldY = selectedShape.getProperties().get("y");
                }

                MyCanvas.setCursor(Cursor.DEFAULT);
            }
        }
    }

    public void Toggle_Click(MouseEvent mouseEvent) {

        if (mouseEvent.getSource () == CircleBTN) {
            CircleBTN.setSelected (true);
            EllipseBTN.setSelected (false);
            RectangleBTN.setSelected (false);
            PolygonBTN.setSelected (false);
            SquareBTN.setSelected (false);
            LineBTN.setSelected (false);
            TriangleBTN.setSelected (false);
            PentagonBTN.setSelected (false);
        }
        if (mouseEvent.getSource () == EllipseBTN) {
            CircleBTN.setSelected (false);
            EllipseBTN.setSelected (true);
            RectangleBTN.setSelected (false);
            PolygonBTN.setSelected (false);
            SquareBTN.setSelected (false);
            LineBTN.setSelected (false);
            TriangleBTN.setSelected (false);
        }
        if (mouseEvent.getSource () == RectangleBTN) {
            CircleBTN.setSelected (false);
            EllipseBTN.setSelected (false);
            RectangleBTN.setSelected (true);
            PolygonBTN.setSelected (false);
            SquareBTN.setSelected (false);
            LineBTN.setSelected (false);
            TriangleBTN.setSelected (false);
            PentagonBTN.setSelected (false);
        }
        if (mouseEvent.getSource () == PolygonBTN) {
            CircleBTN.setSelected (false);
            EllipseBTN.setSelected (false);
            RectangleBTN.setSelected (false);
            PolygonBTN.setSelected (true);
            SquareBTN.setSelected (false);
            LineBTN.setSelected (false);
            TriangleBTN.setSelected (false);
            PentagonBTN.setSelected (false);

        }
        if (mouseEvent.getSource () == SquareBTN) {
            CircleBTN.setSelected (false);
            EllipseBTN.setSelected (false);
            RectangleBTN.setSelected (false);
            PolygonBTN.setSelected (false);
            SquareBTN.setSelected (true);
            LineBTN.setSelected (false);
            TriangleBTN.setSelected (false);
            PentagonBTN.setSelected (false);

        }
        if (mouseEvent.getSource () == LineBTN) {
            CircleBTN.setSelected (false);
            EllipseBTN.setSelected (false);
            RectangleBTN.setSelected (false);
            PolygonBTN.setSelected (false);
            SquareBTN.setSelected (false);
            LineBTN.setSelected (true);
            TriangleBTN.setSelected (false);
            PentagonBTN.setSelected (false);
        }
        if (mouseEvent.getSource () == TriangleBTN) {
            CircleBTN.setSelected (false);
            EllipseBTN.setSelected (false);
            RectangleBTN.setSelected (false);
            PolygonBTN.setSelected (false);
            SquareBTN.setSelected (false);
            LineBTN.setSelected (false);
            TriangleBTN.setSelected (true);
            PentagonBTN.setSelected (false);
        }
        if (mouseEvent.getSource () == PentagonBTN) {
            CircleBTN.setSelected (false);
            EllipseBTN.setSelected (false);
            RectangleBTN.setSelected (false);
            PolygonBTN.setSelected (false);
            SquareBTN.setSelected (false);
            LineBTN.setSelected (false);
            TriangleBTN.setSelected (false);
            PentagonBTN.setSelected (true);
        }
    }

    public void Slider_Release(MouseEvent mouseEvent) {

        if(MySizeRadioBTN.isSelected() || MyHandRadioBTN.isSelected()) {
            if (!root.getChildren().isEmpty()) {
                TreeItem<String> selectedItem = MyTreeView.getSelectionModel().getSelectedItem();
                if (!selectedItem.getValue().contains("Group")) {
                    application.getShapes().forEach((current) -> {
                        if (current.name.equals(selectedItem.getValue())) {
                            Map<String, Double> properties = new HashMap<>(current.getProperties());
                            properties.replace("strokewidth", MyStrokeSlider.getValue());

                            SetPropertiesCommand setPropertiesCommand = new SetPropertiesCommand(current, properties);
                            setPropertiesCommand.execute();
                        }
                    });
                }
            }
        }
    }

    public void Delete_Click() {

        selectedItem = MyTreeView.getSelectionModel().getSelectedItem();

        if(!application.getShapes().isEmpty() || !groupsList.isEmpty())
            if(selectedItem.getValue().contains("Group")) removeGroup(selectedItem);
            else removeShape(selectedItem);

    }

    public void FocusGroup(){
        MyCanvas.requestFocus ();
    }

    public void handle(MouseEvent mouseEvent) {
        if (mouseEvent.getSource() == CloseBTN) {

            Stage stage = (Stage) CloseBTN.getScene().getWindow();
            stage.close();
        }
        if (mouseEvent.getSource() == MiniButton) {
            Stage stage = (Stage) CloseBTN.getScene().getWindow();
            stage.setIconified(true);
        }
    }

    public void TitleBTN_Click(MouseEvent mouseEvent) throws MalformedURLException,
            ClassNotFoundException, IllegalAccessException,
            InstantiationException, ZipException {

        if (mouseEvent.getSource() == InsertBTN)
        {

            FileChooser fileChooser = new FileChooser();

            /* Set extension filter */
            FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("JAR Files", "*.jar");
            fileChooser.getExtensionFilters().add(extFilter);

            fileChooser.setTitle("Open File");
            File file = fileChooser.showOpenDialog(currentStage);

            /* Load shape */
            ShapeLoader.extractjar(file);
            Shape loadedShape = ShapeLoader.loadShape(file);

            AddShapeCommand addShapeCommand = new AddShapeCommand(loadedShape);
            addShapeCommand.execute();

            selectedShape = loadedShape;

            application.refresh(application.getCanvas());
        }
        if (mouseEvent.getSource() == fileBTN)
        {
            fileContextMenu.hide();
            fileContextMenu.show(fileBTN, mouseEvent.getScreenX(), mouseEvent.getScreenY());
        }
    }

    public void MyTreeView_Click(MouseEvent mouseEvent) {

        if (!application.getShapes().isEmpty())
        {
            if(!MyTreeView.getSelectionModel().getSelectedItem().getValue().contains("Group"))
            {
                if (mouseEvent.getButton() == MouseButton.SECONDARY)
                {
                    /* set contextMenu position & show it */
                    contextMenu.show(MyTreeView, mouseEvent.getScreenX(), mouseEvent.getScreenY());
                }
                else contextMenu.hide();
            }
            else if (mouseEvent.getButton() == MouseButton.PRIMARY)
            {
                MyCanvas.requestFocus ();
            }
        }
    }

    private void contextMenu_Click(ActionEvent event) {

        /* retrieve group & shape name */
        String groupName = ((MenuItem)event.getTarget()).getText();
        selectedItem = MyTreeView.getSelectionModel().getSelectedItem();

        /* search for the shape in the shapesList & assign groupName to it */
        for(Shape shape: application.getShapes())
            if (shape.name.equals(selectedItem.getValue()))
            {
                shape.groupName = groupName;
                break;
            }


        /* remove shape from old node in the treeView */
        selectedItem.getParent().getChildren().remove(selectedItem);


        /* search for the group in the treeView & add the shape as sub treeItem */
        for (TreeItem<String> treeItem: root.getChildren())
            if (treeItem.getValue().equals(groupName))
            {
                treeItem.getChildren().add(selectedItem);
                break;
            }
    }

    public void CreateGroup_Click(MouseEvent mouseEvent) {

        /* create a MenuItem that holds group name */
        MenuItem item = new MenuItem("Group " + "(" + groupsCounter++ + ")");

        /* add the group to the treeview
        & groups list (Arraylist<MenuItem> groups)
        & context menu */

        root.getChildren().add(new TreeItem<>(item.getText()));
        contextMenu.getItems().add(item);
        groupsList.add(item);
    }

    public void MyPane_Press(KeyEvent keyEvent) {

        if(!application.getShapes().isEmpty() && MyHandRadioBTN.isSelected())
        {
            TreeItem<String> selectedItem = MyTreeView.getSelectionModel().getSelectedItem();

            if (selectedItem.getValue().contains("Group"))
            {

                if(keyEvent.getCode()==KeyCode.W) {
                    application.getShapes().forEach (shape -> {
                        if (shape.groupName.equals (selectedItem.getValue ()))
                            shape.getProperties ().replace ("y", shape.getProperties ().get ("y") - 1);
                    });
                }

                if(keyEvent.getCode()==KeyCode.S || keyEvent.getCode()==KeyCode.DOWN) {
                    application.getShapes().forEach (shape -> {
                        if (shape.groupName.equals (selectedItem.getValue ()))
                            shape.getProperties ().replace ("y", shape.getProperties ().get ("y") + 1);
                    });
                }

                if(keyEvent.getCode()==KeyCode.A || keyEvent.getCode()==KeyCode.LEFT){
                    application.getShapes().forEach(shape -> { if (shape.groupName.equals(selectedItem.getValue()))
                        shape.getProperties().replace("x", shape.getProperties().get("x") - 1); });
                }


                if(keyEvent.getCode()==KeyCode.D || keyEvent.getCode()==KeyCode.RIGHT) {
                    application.getShapes().forEach (shape -> {
                        if (shape.groupName.equals (selectedItem.getValue ()))
                            shape.getProperties ().replace ("x", shape.getProperties ().get ("x") + 1);
                    });
                }
                application.refresh(application.getCanvas());
            }
        }
    }

    public void undoClicked() {
        application.undo();
    }

    public void redoClicked() {
        application.redo();
    }

    public void StrokeColorPicker_Action(ActionEvent actionEvent) {

        if(MySizeRadioBTN.isSelected() || MyHandRadioBTN.isSelected())
        {
            if (!root.getChildren().isEmpty())
            {
                TreeItem<String> selectedItem = MyTreeView.getSelectionModel().getSelectedItem();

                if (!selectedItem.getValue().contains("Group")) {
                    application.getShapes().forEach((current) -> {
                        if (current.name.equals(selectedItem.getValue())) {
                            Color color = MyStrokeColorPicker.getValue();
                            SetColorCommand setColorCommand = new SetColorCommand(current, color);
                            setColorCommand.execute();
                        }
                    });
                }

                application.refresh(application.getCanvas());
            }
        }
    }

    public void BackColorPicker_Action(ActionEvent actionEvent) {

        if(MySizeRadioBTN.isSelected() || MyHandRadioBTN.isSelected())
        {
            if (!root.getChildren().isEmpty())
            {
                TreeItem<String> selectedItem = MyTreeView.getSelectionModel().getSelectedItem();

                if (!selectedItem.getValue().contains("Group")) {
                    application.getShapes().forEach((current) -> {
                        if (current.name.equals(selectedItem.getValue())) {
                            Color color = MyBackColorPicker.getValue();
                            SetFillColorCommand setFillColorCommand = new SetFillColorCommand(current, color);
                            setFillColorCommand.execute();
                        }
                    });
                }

                application.refresh(application.getCanvas());
            }
        }
    }

    public void Clone_Click(MouseEvent mouseEvent) throws CloneNotSupportedException {

        if (!root.getChildren().isEmpty())
        {
            TreeItem<String> selectedItem = MyTreeView.getSelectionModel().getSelectedItem();

            if (!selectedItem.getValue().contains("Group"))
                for (Shape current : application.getShapes())
                    if (current.name.equals(selectedItem.getValue()))
                    {
                        cloneShape(current);
                        break;
                    }

            application.refresh(application.getCanvas());
        }
    }

    public void Toggle_Click_My_Buttons(MouseEvent mouseEvent) {

        if(mouseEvent.getSource()==MyMouseRadioBTN)

        {
            MyMouseRadioBTN.setSelected (true);
            MyHandRadioBTN.setSelected (false);
            MySizeRadioBTN.setSelected (false);
        }
        if(mouseEvent.getSource()==MyHandRadioBTN)

        {
            MyMouseRadioBTN.setSelected (false);
            MyHandRadioBTN.setSelected (true);
            MySizeRadioBTN.setSelected (false);
        }
        if(mouseEvent.getSource()==MySizeRadioBTN)

        {
            MyMouseRadioBTN.setSelected (false);
            MyHandRadioBTN.setSelected (false);
            MySizeRadioBTN.setSelected (true);
        }

    }

    private void cloneShape(Shape originShape) throws CloneNotSupportedException {

        Shape clonedShape = diagnoseShape(originShape);

        clonedShape.setPosition(new Point2D(
                clonedShape.getPosition().getX() + 10, clonedShape.getPosition().getY() + 10));

        AddShapeCommand addShapeCommand = new AddShapeCommand(clonedShape);
        addShapeCommand.execute();

        application.refresh(application.getCanvas());
    }

    public static Shape diagnoseShape(Shape originShape) throws CloneNotSupportedException {

        Shape tempShape = null;

        if (originShape.getState() instanceof PCircle) {

            tempShape = new Shape((PCircle) originShape.clone());
            tempShape.name = "Circle " + "(" + counter[0]++ + ")";
        }
        if (originShape.getState() instanceof PEllipse) {
            tempShape = new Shape((PEllipse) originShape.clone());
            tempShape.name = "Ellipse " + "(" + counter[1]++ + ")";
        }
        if (originShape.getState() instanceof PRectangle) {
            tempShape = new Shape((PRectangle) originShape.clone());
            tempShape.name = "Rectangle " + "(" + counter[2]++ + ")";
        }
        if (originShape.getState() instanceof PSquare) {
            tempShape = new Shape((PSquare) originShape.clone());
            tempShape.name = "Square " + "(" + counter[3]++ + ")";
        }
        if (originShape.getState() instanceof PLine) {
            tempShape = new Shape((PLine) originShape.clone());
            tempShape.name = "Line " + "(" + counter[4]++ + ")";
        }
        if (originShape.getState() instanceof PHexagon) {
            tempShape = new Shape((PHexagon) originShape.clone());
            tempShape.name = "Hexagon " + "(" + counter[5]++ + ")";
        }
        if (originShape.getState() instanceof PTriangle) {
            tempShape = new Shape((PTriangle) originShape.clone());
            tempShape.name = "Triangle " + "(" + counter[6]++ + ")";
        }
        if (originShape.getState() instanceof PPentagon) {
            tempShape = new Shape((PPentagon) originShape.clone());
            tempShape.name = "Pentagon " + "(" + counter[7]++ + ")";
        }

        assert tempShape != null;

        tempShape.oldX = originShape.oldX;
        tempShape.oldY = originShape.oldY;
        tempShape.groupName = "";

        return tempShape;
    }

}