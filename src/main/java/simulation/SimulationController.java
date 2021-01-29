package simulation;

import canvas.CanvasController;
import com.jfoenix.controls.JFXButton;
import domain.AutomataType;
import domain.automata_model.AutomataModel;
import graphing.PopulationGraphController;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.shape.SVGPath;
import settings.Palette;
import settings.Settings;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicBoolean;

public class SimulationController implements Initializable {
    @FXML
    private Parent simulation;

    @FXML
    JFXButton playButton;

    @FXML
    JFXButton resetButton;

    @FXML
    JFXButton randomizeButton;

    @FXML
    JFXButton advanceGenButton;

    @FXML
    JFXButton advanceTenButton;

    @FXML
    JFXButton advanceThirtyButton;

    @FXML
    SVGPath playButtonIcon;

    private AtomicBoolean animating;

    private AutomataModel model;

    private CanvasController canvasController;

    private PopulationGraphController populationGraphController;

    private Long TIME = 100L;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.animating = new AtomicBoolean(false);
        this.simulation.getStyleClass().add(Settings.getActivePalette().getCssName());
    }

    public void initCanvasController(CanvasController controller){
        this.canvasController = controller;
    }

    public void initGraphController(PopulationGraphController controller){
        this.populationGraphController = controller;
    }

    public void initModel(AutomataModel model){
        this.model = model;
    }

    @FXML
    public void togglePlayback(ActionEvent actionEvent){
        try {
            if(this.animating.compareAndSet(true, false)){
                this.playButton.setText("Play");
                this.playButtonIcon.setContent("M8 5v14l11-7z");

                // Enable buttons
                this.advanceGenButton.setDisable(false);
                this.randomizeButton.setDisable(false);
                this.resetButton.setDisable(false);
                this.advanceTenButton.setDisable(false);
                this.advanceThirtyButton.setDisable(false);

            } else {
                // Disable buttons
                this.animating.set(true);
                this.advanceGenButton.setDisable(true);
                this.advanceTenButton.setDisable(true);
                this.advanceThirtyButton.setDisable(true);
                this.randomizeButton.setDisable(true);
                this.resetButton.setDisable(true);

                //TODO Refactor for ses
                new Thread(new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        while(animating.get()){
                            try{
                                model.incrementGeneration();
                                Platform.runLater(() -> {
                                    populationGraphController.plotPopulation(model);
                                    canvasController.drawModel(model);
                                });
                                Thread.sleep(TIME);
                            } catch (InterruptedException e) {
                                if(isCancelled()){
                                    break;
                                }
                            }
                        }
                        return null;
                    }
                }).start();

                this.playButton.setText("Pause");
                this.playButtonIcon.setContent("M6 19h4V5H6v14zm8-14v14h4V5h-4z");
            }
        } catch (Exception e){
            e.printStackTrace();
        }

    }

    @FXML
    public void advanceGeneration(ActionEvent actionEvent){
        this.model.incrementGeneration();
        this.canvasController.drawModel(this.model);
        this.populationGraphController.plotPopulation(this.model);
    }

    @FXML
    public void randomize(ActionEvent actionEvent) {
        this.model.randomizeModel();
        this.canvasController.resetCanvas();
        this.canvasController.drawModel(this.model);
        this.populationGraphController.resetGraph();
        this.populationGraphController.plotPopulation(this.model);
    }

    @FXML
    public void reset(ActionEvent actionEvent) {
        this.model.resetModel();
        this.canvasController.resetCanvas();
        this.canvasController.drawModel(this.model);
        this.populationGraphController.resetGraph();
        this.populationGraphController.plotPopulation(this.model);
    }

    @FXML
    public void advance30Generations(ActionEvent actionEvent) {
            this.model.incrementGeneration(30);
            this.canvasController.drawModel(this.model);
            this.populationGraphController.plotPopulation(this.model);
    }

    @FXML
    public void advance10Generations(ActionEvent actionEvent) {
            this.model.incrementGeneration(10);
            this.canvasController.drawModel(this.model);
            this.populationGraphController.plotPopulation(this.model);
    }

    public void updateStyle(Palette oldStyle){
        this.simulation.getStyleClass().remove(oldStyle.getCssName());
        this.simulation.getStyleClass().add(Settings.getActivePalette().getCssName());
    }
}
