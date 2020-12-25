import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import java.awt.*;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MainController implements Initializable {

    @FXML
    private CanvasController canvasController;

    private AutomataModel model;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.model = new AutomataModel();
        canvasController.initModel(this.model);
    }
}
