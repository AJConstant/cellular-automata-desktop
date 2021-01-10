package root;

import canvas.CanvasController;
import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXHamburger;
import com.jfoenix.transitions.hamburger.HamburgerBackArrowBasicTransition;
import domain.AutomataModel;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;


public class MainController implements Initializable {

    @FXML
    private CanvasController canvasController;

    @FXML
    private JFXDrawer drawer;

    @FXML
    private JFXHamburger menuButton;

    private AutomataModel model;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.model = new AutomataModel();
        canvasController.initModel(this.model);;
        HamburgerBackArrowBasicTransition menuTransition = new HamburgerBackArrowBasicTransition(menuButton);
        menuTransition.setRate(-1);

        try {
            VBox sidePanelContent = FXMLLoader.load(getClass().getResource("../drawer/Drawer.fxml"));
            drawer.setSidePane(sidePanelContent);
        } catch (Exception e) {
            e.printStackTrace();
        }

        menuButton.addEventHandler(MouseEvent.MOUSE_PRESSED, (e) -> {
            menuTransition.setRate(menuTransition.getRate()*-1);
            menuTransition.play();
            if(drawer.isOpened()){
                drawer.close();
            } else if (drawer.isClosed()){
                drawer.open();
            }
        });
    }
}
