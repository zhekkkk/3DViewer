package com.cgvsu;

import com.cgvsu.render_engine.RenderEngine;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.FileChooser;
import javafx.util.Duration;
import com.cgvsu.math.Vector3f;
import com.cgvsu.math.Vector2f;
import com.cgvsu.math.Matrix4f;

import java.nio.file.Files;
import java.nio.file.Path;
import java.io.IOException;
import java.io.File;
import java.util.ArrayList;
//import javax.vecmath.Vector3f;

import com.cgvsu.model.Model;
import com.cgvsu.objreader.ObjReader;
import com.cgvsu.render_engine.Camera;

public class GuiController {

    final private float TRANSLATION = 1.5F;

    @FXML
    public Button delete;

    @FXML
    AnchorPane anchorPane;

    @FXML
    private TableColumn<ModelTable, File> model;

    @FXML
    private TableView<ModelTable> table;

    private ObservableList<ModelTable> models = FXCollections.observableArrayList();
    @FXML
    private Canvas canvas;

    private Model mesh = null;

    private Color color = Color.MAGENTA;

    @FXML
    private ColorPicker colorPicker = new ColorPicker();

    private boolean drawMesh = false;
    private boolean useLighting = false;
    private boolean texturePolygons = false;

    @FXML
    private CheckBox drawMeshCheckBox = new CheckBox();
    @FXML
    private CheckBox useLightingCheckBox = new CheckBox();
    @FXML
    private CheckBox texturePolygonsCheckBox = new CheckBox();

    private ArrayList<Camera> cameras = new ArrayList<>();

    private Camera camera = new Camera(
            new Vector3f(0, 00, 100),
            new Vector3f(0, 0, 0),
            1.0F, 1, 0.01F, 100);

    private Camera camera2 = new Camera(
            new Vector3f(0, 30, 100),
            new Vector3f(0, 0, 0),
            1.0F, 1, 0.01F, 100);

    private Timeline timeline;

    @FXML
    private void initialize() {
        anchorPane.prefWidthProperty().addListener((ov, oldValue, newValue) -> canvas.setWidth(newValue.doubleValue()));
        anchorPane.prefHeightProperty().addListener((ov, oldValue, newValue) -> canvas.setHeight(newValue.doubleValue()));

        timeline = new Timeline();
        timeline.setCycleCount(Animation.INDEFINITE);

        KeyFrame frame = new KeyFrame(Duration.millis(15), event -> {

            double width = canvas.getWidth();
            double height = canvas.getHeight();

            canvas.getGraphicsContext2D().clearRect(0, 0, width, height);
            camera.setAspectRatio((float) (width / height));

            /*ModelTable v = table.getSelectionModel().getSelectedItem();
            if (v != null) {
                RenderEngine.render(canvas.getGraphicsContext2D(), camera, handle(), (int) width, (int) height, canvas);
            } else {
                if (mesh != null) {
                    RenderEngine.render(canvas.getGraphicsContext2D(), camera, mesh, (int) width, (int) height, canvas);
                }
            }*/
            if (mesh != null) {
                RenderEngine.render(canvas.getGraphicsContext2D(),
                        camera, mesh, (int) width, (int) height,
                        canvas, color, drawMesh, useLighting, texturePolygons);
            }
        });

        timeline.getKeyFrames().add(frame);
        timeline.play();
    }

    @FXML
    private void setDrawMesh() {
        drawMesh = !drawMesh;
    }

    @FXML
    private void setUseLighting() {
        useLighting = !useLighting;
    }

    @FXML
    private void setTexturePolygons() {
        /*texturePolygons = texturePolygons;*/
    }

    @FXML
    private Color changeColor() {
        Color newColor = colorPicker.getValue();
        color = newColor;
        return color;
    }

    @FXML
    private Model handle() {
        ModelTable v = table.getSelectionModel().getSelectedItem();
        mesh = v.model;
        return mesh;
    }

    @FXML
    private void setDeleteModel(){
        ModelTable deleteModel = table.getSelectionModel().getSelectedItem();
        models.remove(deleteModel);
        table.getItems().remove(deleteModel);
        mesh = null;
    }

    @FXML
    private void onOpenModelMenuItemClick() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Model (*.obj)", "*.obj"));
        fileChooser.setTitle("Load Model");

        File file = fileChooser.showOpenDialog((Stage) canvas.getScene().getWindow());
        if (file == null) {
            return;
        }

        Path fileName = Path.of(file.getAbsolutePath());

        try {
            String fileContent = Files.readString(fileName);
            mesh = ObjReader.read(fileContent);
            models.add(new ModelTable(mesh, file.getName()));
            table.getItems().setAll(models);
            model.setCellValueFactory(new PropertyValueFactory<>("string"));
            // todo: обработка ошибок
        } catch (IOException exception) {

        }
    }

    @FXML
    public void handleCameraForward(ActionEvent actionEvent) {
        camera.movePosition(new Vector3f(0, 0, -TRANSLATION));
    }

    @FXML
    public void handleCameraBackward(ActionEvent actionEvent) {
        camera.movePosition(new Vector3f(0, 0, TRANSLATION));
    }

    @FXML
    public void handleCameraLeft(ActionEvent actionEvent) {
        camera.movePosition(new Vector3f(TRANSLATION, 0, 0));
    }

    @FXML
    public void handleCameraRight(ActionEvent actionEvent) {
        camera.movePosition(new Vector3f(-TRANSLATION, 0, 0));
    }

    @FXML
    public void handleCameraUp(ActionEvent actionEvent) {
        camera.movePosition(new Vector3f(0, TRANSLATION, 0));
    }

    @FXML
    public void handleCameraDown(ActionEvent actionEvent) {
        camera.movePosition(new Vector3f(0, -TRANSLATION, 0));
    }
}