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
import javafx.scene.Parent;
import javafx.util.StringConverter;
import settings.Palette;
import settings.Settings;
import simulation.SimulationController;

import java.net.URL;
import java.util.ResourceBundle;

public class AutomataChoiceController implements Initializable {
    @FXML
    private Parent automataChoice;

    @FXML
    private JFXTextField ruleNumber;

    @FXML
    private JFXComboBox automataChoiceBox;

    @FXML
    private JFXComboBox initialConditions;

    private AutomataModelImpl model;

    private SimulationController simulationController;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.automataChoice.getStyleClass().add(Settings.getActivePalette().getCssName());
        // Initialize rule number control
        ruleNumber.setText(Integer.toString(AutomataModel.INIT_RULE_NUM));
        ruleNumber.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                try {
                    if (Integer.parseInt(newValue) < 0 || Integer.parseInt(newValue) > model.getAutomataType().getRuleNumMax()) {
                        ruleNumber.setText("");
                    } else {
                        AutomataChoiceController.this.model.setRuleNumber(Integer.parseInt(newValue));
                    }
                } catch (NumberFormatException e) {
                    ruleNumber.setText("");
                }
            }
        });

        //Initialize combo box control
        automataChoiceBox.getItems().addAll(AutomataType.values());

        automataChoiceBox.setConverter(new StringConverter<AutomataType>(){
            @Override
            public String toString(AutomataType object) {
                return object.getDisplayName();
            }

            @Override
            public AutomataType fromString(String string) {
                return AutomataType.valueOf(string);
            }
        });

        automataChoiceBox.valueProperty().setValue(AutomataModel.INIT_AUTOMATA_TYPE);

        automataChoiceBox.valueProperty().addListener(new ChangeListener<AutomataType>() {
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

    public void updateStyle(Palette oldStyle){
        this.automataChoice.getStyleClass().remove(oldStyle.getCssName());
        this.automataChoice.getStyleClass().add(Settings.getActivePalette().getCssName());
    }

    public void initSimulationController(SimulationController simulationController){
        this.simulationController = simulationController;
    }
}
