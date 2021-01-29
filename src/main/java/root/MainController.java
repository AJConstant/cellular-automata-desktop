package root;

import automatachoice.AutomataChoiceController;
import canvas.CanvasController;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTabPane;
import domain.automata_model.AutomataModel;
import domain.automata_model.AutomataModelImpl;
import graphing.PopulationGraphController;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import settings.Palette;
import settings.Settings;
import simulation.SimulationController;

import java.awt.*;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ResourceBundle;


public class MainController implements Initializable {

    @FXML
    private Parent root;

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

    @FXML
    private JFXComboBox<Palette> themeSelect;

    @FXML
    private JFXButton visitGithubButton;

    private AutomataModel model;

    private boolean minimized = false;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.model = new AutomataModelImpl(AutomataModel.INIT_AUTOMATA_TYPE, AutomataModel.INIT_RULE_NUM);

        this.themeSelect.getItems().addAll(Palette.values());
        this.themeSelect.valueProperty().setValue(Settings.getActivePalette());
        this.themeSelect.setConverter(new StringConverter<Palette>() {
            @Override
            public String toString(Palette object) {
                return object.getDisplayName();
            }

            @Override
            public Palette fromString(String string) {
                return Palette.valueOf(string);
            }
        });
        this.themeSelect.valueProperty().addListener(new ChangeListener<Palette>() {
            @Override
            public void changed(ObservableValue<? extends Palette> observable, Palette oldValue, Palette newValue) {
                Settings.setActivePalette(newValue);
                canvasController.updateStyle(oldValue);
                populationGraphController.updateStyle(oldValue);
                automataChoiceController.updateStyle(oldValue);
                simulationController.updateStyle(oldValue);
                updateStyle(oldValue);
                canvasController.drawModel(model);
            }
        });

        this.automataChoiceController.initModel(this.model);
        this.simulationController.initModel(this.model);

        this.simulationController.initCanvasController(this.canvasController);
        this.simulationController.initGraphController(this.populationGraphController);
        this.automataChoiceController.initSimulationController(this.simulationController);

        this.canvasController.initModel(this.model);
        this.canvasController.drawModel(this.model);
        this.tabPane.getSelectionModel().select(1);
        this.root.getStyleClass().add(Settings.getActivePalette().getCssName());
    }

    public void updateStyle(Palette oldStyle){
        this.root.getStyleClass().remove(oldStyle.getCssName());
        this.root.getStyleClass().add(Settings.getActivePalette().getCssName());
    }

    @FXML
    public void closeApplication(){
        ((Stage)(exitButton.getScene().getWindow())).close();
    }

    public void minimize(ActionEvent actionEvent) {
        this.minimized = !this.minimized;
        ((Stage)this.root.getScene().getWindow()).setIconified(this.minimized);
    }

    public void browseGithubPage(ActionEvent actionEvent) throws IOException, URISyntaxException {
        Desktop.getDesktop().browse(new URL("http://github.com/AJConstant/ConwaysGameOfLifeDesktop").toURI());
    }
}
