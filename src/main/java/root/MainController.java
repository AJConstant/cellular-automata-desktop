package root;

import canvas.CanvasController;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXHamburger;
import com.jfoenix.transitions.hamburger.HamburgerBackArrowBasicTransition;
import domain.AutomataType;
import domain.GenerationRule;
import domain.automata_model.AutomataModel;
import domain.automata_model.AutomataModelImpl;
import graphing.PopulationGraphController;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.layout.VBox;
import javafx.scene.shape.SVGPath;

import java.net.URL;
import java.util.ResourceBundle;

import java.util.concurrent.atomic.AtomicBoolean;



public class MainController implements Initializable {

    @FXML
    private CanvasController canvasController;

    @FXML
    private PopulationGraphController populationGraphController;
    
    @FXML
    private JFXButton togglePlaybackButton;

    @FXML
    private JFXButton advanceGenerationButton;

    @FXML
    private JFXButton randomizeBoardButton;

    @FXML
    private SVGPath togglePlaybackSVG;

    @FXML
    private JFXButton resetButton;

    @FXML
    private JFXDrawer drawer;

    @FXML
    private JFXHamburger menuButton;

    private Long TIME;

    private AutomataModelImpl model;

    private AtomicBoolean animating;

    private Task<Void> task;

    private Timeline t;

    private HamburgerBackArrowBasicTransition menuTransition;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.model = new AutomataModelImpl(AutomataType.TimeSeries2D, 42);
        this.canvasController.initModel(this.model);
        this.TIME = 50L;

        menuTransition = new HamburgerBackArrowBasicTransition(menuButton);
        menuTransition.setRate(-1);

        try {
            VBox sidePanelContent = FXMLLoader.load(getClass().getResource("../drawer/Drawer.fxml"));
            drawer.setSidePane(sidePanelContent);
        } catch (Exception e) {
            e.printStackTrace();
        }

        this.animating = new AtomicBoolean(false);
        this.canvasController.drawModel(this.model);
    }

    @FXML
    public void togglePlayback(ActionEvent actionEvent){
        try {
            if(this.animating.compareAndSet(true, false)){
                this.togglePlaybackButton.setText("PLAY");
                this.togglePlaybackSVG.setContent("M8 5v14l11-7z");

                // Enable buttons
                this.advanceGenerationButton.setDisable(false);
                this.randomizeBoardButton.setDisable(false);
                this.resetButton.setDisable(false);
                this.menuButton.setDisable(false);

            } else {
                // Disable buttons
                this.animating.set(true);
                this.advanceGenerationButton.setDisable(true);
                this.randomizeBoardButton.setDisable(true);
                this.resetButton.setDisable(true);
                this.menuButton.setDisable(true);

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

                this.togglePlaybackButton.setText("PAUSE");
                this.togglePlaybackSVG.setContent("M6 19h4V5H6v14zm8-14v14h4V5h-4z");
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
        this.canvasController.drawModel(this.model);
        this.populationGraphController.resetGraph();
        this.populationGraphController.plotPopulation(this.model);
    }

    @FXML
    public void reset(ActionEvent actionEvent) {
        this.model.resetModel();
        this.canvasController.drawModel(this.model);
        this.populationGraphController.resetGraph();
        this.populationGraphController.plotPopulation(this.model);
    }

    @FXML
    public void toggleDrawer(){
        this.menuTransition.setRate(menuTransition.getRate()*-1);
        this.menuTransition.play();
        if(this.drawer.isOpened()){
            this.drawer.close();
        } else if (this.drawer.isClosed()){
            this.drawer.open();
        }
    }
}
