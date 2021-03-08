package automatavisualization;

import canvas.CanvasController;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import domain.AutomataType;
import domain.InitialCondition;
import domain.automata_model.AutomataModel;
import domain.automata_model.AutomataModelImpl;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.util.StringConverter;
import settings.Palette;
import settings.Settings;
import simulation.SimulationController;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.ThreadLocalRandom;


public class AutomataVisualizationController implements Initializable {
    @FXML
    private Parent automataVisualization;

    @FXML
    private JFXTextField ruleNumber;

    @FXML
    private JFXComboBox automataChoiceBox;

    @FXML
    private JFXComboBox initialConditions;

    private AutomataModel model;

    private SimulationController simulationController;
    private CanvasController canvasController;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.automataVisualization.getStyleClass().add(Settings.getActivePalette().getCssName());
        this.model = new AutomataModelImpl(AutomataModel.INIT_AUTOMATA_TYPE, AutomataModel.INIT_RULE_NUM, AutomataModel.INIT_CONDITION);
        this.initRuleNumber();
        this.initAutomataChoice();
        this.initInitialConditions();
        this.model.init();
    }

    public void updateStyle(Palette oldStyle){
        this.automataVisualization.getStyleClass().remove(oldStyle.getCssName());
        this.automataVisualization.getStyleClass().add(Settings.getActivePalette().getCssName());
    }

    public void initSimulationController(SimulationController simulationController){
        this.simulationController = simulationController;
        this.simulationController.initModel(this.model);
    }

    public void randomizeRuleNumber(ActionEvent actionEvent) {
        if (!this.ruleNumber.isDisable()) {
            this.ruleNumber.setText(Integer.toString(ThreadLocalRandom.current().nextInt(0, this.model.getAutomataType().getRuleNumMax())));
        }
    }

    private void initRuleNumber(){
        // Initialize rule number control
        this.ruleNumber.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                if (Integer.parseInt(newValue) < 0 || Integer.parseInt(newValue) > model.getAutomataType().getRuleNumMax()) {
                    ruleNumber.setText("");
                } else {
                    model.setRuleNumber(Integer.parseInt(newValue));
                }
            } catch (NumberFormatException e) {
                ruleNumber.setText("");
            }
        });
        this.ruleNumber.setText(Integer.toString(AutomataModel.INIT_RULE_NUM));
    }

    private void initAutomataChoice(){
        automataChoiceBox.getItems().addAll(AutomataType.values());
        automataChoiceBox.valueProperty().addListener((ChangeListener<AutomataType>) (observable, oldValue, newValue) -> {
            this.model.setAutomataType(newValue);
            if(newValue == AutomataType.TimeSeries1D){
                if(!newValue.getInitialConditions().contains(initialConditions.valueProperty().get())){
                    initialConditions.valueProperty().setValue(InitialCondition.SINGLE_CELL);
                }
                initialConditions.getItems().removeIf(e -> !model.getAutomataType().getInitialConditions().contains(e));
            } else {
                for(InitialCondition i : newValue.getInitialConditions()){
                    if(!initialConditions.getItems().contains(i)){
                        initialConditions.getItems().add(i);
                    }
                }
            }
            if (newValue == AutomataType.GameOfLife) {
                ruleNumber.textProperty().setValue("224");
                ruleNumber.setDisable(true);
            } else {
                ruleNumber.setDisable(false);
            }
        });
        automataChoiceBox.valueProperty().setValue(AutomataModel.INIT_AUTOMATA_TYPE);
    }

    private void initInitialConditions(){
        initialConditions.getItems().addAll(InitialCondition.values());

        initialConditions.setConverter(new StringConverter<InitialCondition>(){

            @Override
            public String toString(InitialCondition object) {
                return object.getDisplayName();
            }

            @Override
            public InitialCondition fromString(String string) {
                return InitialCondition.valueOf(string);
            }
        });
        initialConditions.valueProperty().addListener((ChangeListener<InitialCondition>) (observable, oldValue, newValue) -> {
            model.setInitialCondition(newValue);
        });
        initialConditions.valueProperty().setValue(AutomataModel.INIT_CONDITION);
    }

    public void initCanvasController(CanvasController canvasController) {
        this.canvasController = canvasController;
        this.canvasController.initModel(this.model);
    }
}
