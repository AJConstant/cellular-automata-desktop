package drawer;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.svg.SVGGlyph;
import com.jfoenix.svg.SVGGlyphLoader;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.SVGPath;
import javafx.stage.Stage;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ResourceBundle;

public class DrawerController implements Initializable {

    @FXML
    private JFXButton changeTheme;

    @FXML
    private JFXButton changeRules;

    @FXML
    private JFXButton quitButton;


    public void initialize(URL location, ResourceBundle resources) {

    }

    @FXML
    public void closeApplication(MouseEvent mouseEvent) {
        ((Stage)((Button)mouseEvent.getSource()).getScene().getWindow()).close();
    }

    @FXML
    public void openMoreInformation(MouseEvent mouseEvent) throws URISyntaxException, IOException {
        if(Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)){
            Desktop.getDesktop().browse(new URI("https://en.wikipedia.org/wiki/Conway%27s_Game_of_Life"));
        }
    }
}
