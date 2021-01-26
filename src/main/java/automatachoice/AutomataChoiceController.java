package automatachoice;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import domain.AutomataType;
import domain.automata_model.AutomataModel;
import domain.automata_model.AutomataModelImpl;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import simulation.SimulationController;

import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;

public class AutomataChoiceController implements Initializable {

    @FXML
    JFXTextField ruleNumber;

    @FXML
    JFXComboBox automataChoice;

    @FXML
    JFXComboBox initialConditions;

    private AutomataModelImpl model;

    private SimulationController simulationController;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        // Initialize rule number control
        ruleNumber.setText(Integer.toString(AutomataModel.INIT_RULE_NUM));
        ruleNumber.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                try {
                    if (Integer.parseInt(newValue) < 0 || Integer.parseInt(newValue) > model.getAutomataType().getRuleNumMax()) {
                        ruleNumber.setText("");
                    } else {
                        model.setRuleNumber(Integer.parseInt(newValue));
                    }
                } catch (NumberFormatException e) {
                    ruleNumber.setText("");
                }
            }
        });


        //Initialize combo box control
        this.automataChoice.getItems().addAll(AutomataType.values());
        this.automataChoice.valueProperty().setValue(AutomataModel.INIT_AUTOMATA_TYPE);
        this.automataChoice.valueProperty().addListener(new ChangeListener<AutomataType>() {
            @Override
            public void changed(ObservableValue<? extends AutomataType> observable, AutomataType oldValue, AutomataType newValue) {
                model.setAutomataType(newValue);
                if (newValue == AutomataType.GameOfLife) {
                    ruleNumber.textProperty().setValue("224");
                    ruleNumber.setDisable(true);
                } else {
                    ruleNumber.setDisable(false);
                }
                simulationController.reset(null);
            }
        });
    }

    public void initModel(AutomataModelImpl model){
        this.model = model;
    }

    public void initSimulationController(SimulationController simulationController){
        this.simulationController = simulationController;
    }
}
