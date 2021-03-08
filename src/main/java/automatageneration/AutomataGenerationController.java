package automatageneration;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXProgressBar;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.utils.JFXUtilities;
import domain.AutomataType;
import domain.InitialCondition;
import domain.automata_model.AutomataModel;
import domain.automata_model.AutomataModelImpl;
import domain.automata_model.UniverseSize;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.BoundingBox;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Paint;
import javafx.stage.FileChooser;
import javafx.util.StringConverter;
import settings.Settings;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicBoolean;

public class AutomataGenerationController implements Initializable {
    private AutomataModel model;
    private UniverseSize universeSize;
    private AtomicBoolean generating;
    private int generationsToGenerate;

    @FXML
    private JFXTextField generationNumberField;

    @FXML
    private JFXTextField ruleNumberField;

    @FXML
    private JFXComboBox<AutomataType> automataTypeSelect;

    @FXML
    private JFXComboBox<InitialCondition> initialConditionSelect;

    @FXML
    private JFXComboBox<UniverseSize> universeSizeSelect;

    @FXML
    private JFXProgressBar progressBar;

    @FXML
    private Canvas canvas;

    @FXML
    private ScrollPane scrollPane;

    private Service<Void> generationService;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.model = new AutomataModelImpl(AutomataModel.INIT_AUTOMATA_TYPE, AutomataModel.INIT_RULE_NUM, AutomataModel.INIT_CONDITION);
        this.universeSize = UniverseSize.STANDARD_GENERATE;
        this.initGenerationService();
        this.initGenerationNumberField();
        this.initRuleNumberField();
        this.initAutomataTypeSelect();
        this.initUniverseSizeSelect();
        this.initInitialConditionSelect();
        this.progressBar.setVisible(false);
        this.model.init();
    }

    public void generate(ActionEvent actionEvent) {
        this.generationService.reset();
        this.generationService.start();
    }

    public void saveCanvas(ActionEvent actionEvent){
        FileChooser fc = new FileChooser();
        File outputDir = new File(System.getProperty(""), "CellularAutomata/output");
        if( !outputDir.exists() ){
            outputDir.mkdirs();
        }
        fc.setInitialDirectory(outputDir);
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("PNG", "*.png"));
        fc.setTitle("Save Generated Image");
        File file = fc.showSaveDialog(this.scrollPane.getScene().getWindow());
        if(file != null){
            WritableImage wi = new WritableImage(this.universeSize.getWidth()*2, this.universeSize.getHeight()*2);
            this.canvas.snapshot(null, wi);
            try {
                ImageIO.write(SwingFXUtils.fromFXImage(wi, null), "png", file);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    public void resetCanvas(ActionEvent actionEvent) {
        this.model.resetModel();
        this.canvas.getGraphicsContext2D().setFill(Settings.getActivePalette().getCanvasBackground());
        this.canvas.getGraphicsContext2D().fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }

    private void initGenerationNumberField(){
        this.generationNumberField.textProperty().setValue(Integer.toString(0));
        this.generationNumberField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                int newGenNumber;
                try{
                    newGenNumber = Integer.parseInt(newValue);
                } catch (NumberFormatException f){
                    newGenNumber = 0;
                }
                if(newGenNumber < 0 || newGenNumber > AutomataModel.MAX_GENERATIONS){
                    newGenNumber = 0;
                }
                generationsToGenerate = newGenNumber;
            }
        });
    }

    private void initRuleNumberField(){
        this.ruleNumberField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                int newRuleNumber;
                try {
                    newRuleNumber = Integer.parseInt(newValue);
                } catch (NumberFormatException f){
                    newRuleNumber = 0;
                }
                if(newRuleNumber < 0 || newRuleNumber > model.getAutomataType().getRuleNumMax()){
                    newRuleNumber = 0;
                }
                model.setRuleNumber(newRuleNumber);
            }
        });
        this.ruleNumberField.textProperty().setValue(Integer.toString(this.model.getRuleNumber()));
    }

    private void initAutomataTypeSelect(){
        automataTypeSelect.getItems().addAll(AutomataType.values());
        automataTypeSelect.valueProperty().addListener((ChangeListener<AutomataType>) (observable, oldValue, newValue) -> {
            if(newValue == AutomataType.TimeSeries1D){
                if(!newValue.getInitialConditions().contains(initialConditionSelect.valueProperty().get())){
                    initialConditionSelect.valueProperty().setValue(InitialCondition.SINGLE_CELL);
                }
                initialConditionSelect.getItems().removeIf(e -> !model.getAutomataType().getInitialConditions().contains(e));
            } else {
                for(InitialCondition i : newValue.getInitialConditions()){
                    if(!initialConditionSelect.getItems().contains(i)){
                        initialConditionSelect.getItems().add(i);
                    }
                }
            }
            if (newValue == AutomataType.GameOfLife) {
                ruleNumberField.textProperty().setValue("224");
                ruleNumberField.setDisable(true);
            } else {
                ruleNumberField.setDisable(false);
            }
            model.setAutomataType(newValue);
        });
        automataTypeSelect.setConverter(new StringConverter<AutomataType>(){
            @Override
            public String toString(AutomataType object) {
                return object.getDisplayName();
            }

            @Override
            public AutomataType fromString(String string) {
                return AutomataType.valueOf(string);
            }
        });
        automataTypeSelect.valueProperty().setValue(AutomataModel.INIT_AUTOMATA_TYPE); //Set type
    }

    private void initUniverseSizeSelect(){
        this.universeSizeSelect.getItems().addAll(UniverseSize.values());
        this.universeSizeSelect.setConverter(new StringConverter<>() {
            @Override
            public String toString(UniverseSize object) {
                return object.getDisplayName();
            }

            @Override
            public UniverseSize fromString(String string) {
                return UniverseSize.valueOf(string);
            }
        });
        this.universeSizeSelect.valueProperty().addListener((observable, oldValue, newValue) -> {
            this.universeSize = newValue;
            this.model.setUniverseWidth(newValue.getWidth());
            this.model.setUniverseHeight(newValue.getHeight());
            this.model.init();
        });
        this.universeSizeSelect.valueProperty().setValue(UniverseSize.STANDARD_GENERATE); //Set universe size
    }

    private void initInitialConditionSelect(){
        this.initialConditionSelect.getItems().addAll(InitialCondition.values());

        this.initialConditionSelect.setConverter(new StringConverter<InitialCondition>(){

            @Override
            public String toString(InitialCondition object) {
                return object.getDisplayName();
            }

            @Override
            public InitialCondition fromString(String string) {
                return InitialCondition.valueOf(string);
            }
        });

        this.initialConditionSelect.valueProperty().addListener((ChangeListener<InitialCondition>) (observable, oldValue, newValue) -> {
            model.setInitialCondition(newValue);
        });

        this.initialConditionSelect.valueProperty().setValue(AutomataModel.INIT_CONDITION);
    }

    private void initGenerationService(){
        this.generationService = new Service<Void>() {
            @Override
            protected Task<Void> createTask() {
                progressBar.setVisible(true);
                Task<Void> task = new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        for(int i=0; i<generationsToGenerate; i++){
                            model.incrementGeneration();
                            updateProgress(i, generationsToGenerate-1);
                        }
                        Platform.runLater(() -> {
                            initCanvas();
                            drawModel();
                        });
                        return null;
                    }
                };
                progressBar.progressProperty().bind(task.progressProperty());
                task.setOnSucceeded(e ->{
                    Platform.runLater(()->{
                        progressBar.setVisible(false);
                    });
                });
                return task;
            }
        };
    }

    @FXML
    public void randomizeRuleNumber(ActionEvent actionEvent) {
        if (!this.ruleNumberField.isDisable()) {
            this.ruleNumberField.setText(Integer.toString(ThreadLocalRandom.current().nextInt(0, this.model.getAutomataType().getRuleNumMax())));
        }
    }

    private void initCanvas(){
        this.canvas.setWidth(this.model.getUniverseWidth()*2);
        this.canvas.setHeight(this.model.getUniverseHeight()*2);
        this.canvas.getGraphicsContext2D().setFill(Settings.getActivePalette().getCanvasBackground());
        this.canvas.getGraphicsContext2D().fillRect(0, 0, this.canvas.getWidth(), this.canvas.getHeight());
    }

    private void drawModel(){
        boolean[][] generation = model.getAutomata();
        GraphicsContext gc = this.canvas.getGraphicsContext2D();
        Paint foreground = Settings.getActivePalette().getCanvasForeground();
        Paint background = Settings.getActivePalette().getCanvasBackground();
        for(int row=1; row <= model.getUniverseHeight()+1; row++){
            for(int col=1; col <= model.getUniverseWidth()+1; col++){
                gc.setFill(generation[row][col] ? foreground : background);
                gc.fillRect((col-1)*2, (row-1)*2, 2, 2);
            }
        }
    }
}
