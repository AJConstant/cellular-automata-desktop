package root;

import automatachoice.AutomataChoiceController;
import canvas.CanvasController;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXMasonryPane;
import com.jfoenix.controls.JFXTabPane;
import domain.AutomataType;
import domain.automata_model.AutomataModel;
import domain.automata_model.AutomataModelImpl;
import graphing.PopulationGraphController;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.stage.Stage;
import simulation.SimulationController;

import java.net.URL;
import java.util.ResourceBundle;

import java.util.concurrent.atomic.AtomicBoolean;



public class MainController implements Initializable {

    @FXML
    private CanvasController canvasController;

    @FXML
    private PopulationGraphController populationGraphController;

    @FXML
    private SimulationController simulationController;

    @FXML
    private AutomataChoiceController automataChoiceController;

    @FXML
    private JFXTabPane tabPane;

    @FXML
    private Button exitButton;

    private AutomataModelImpl model;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        this.model = new AutomataModelImpl(AutomataModel.INIT_AUTOMATA_TYPE, AutomataModel.INIT_RULE_NUM);

        this.canvasController.initModel(this.model);
        this.automataChoiceController.initModel(this.model);
        this.simulationController.initModel(this.model);

        this.simulationController.initCanvasController(this.canvasController);
        this.simulationController.initGraphController(this.populationGraphController);
        this.automataChoiceController.initSimulationController(this.simulationController);

        this.canvasController.drawModel(this.model);
        this.tabPane.getSelectionModel().select(1);
    }

    @FXML
    public void closeApplication(){
        ((Stage)(exitButton.getScene().getWindow())).close();
    }
}
