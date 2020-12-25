import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Main extends Application {

    private AutomataModel model;
    private CanvasController canvasController;

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.model = new AutomataModel();

        Parent root = FXMLLoader.load(getClass().getResource("Main.fxml"));

        FXMLLoader loader = new FXMLLoader(getClass().getResource("Canvas.fxml"));
        loader.load();
        canvasController = loader.getController();
        canvasController.initModel(model);

        Scene scene = new Scene(root, 1280, 720);
        primaryStage.setScene(scene);
        primaryStage.initStyle(StageStyle.UNDECORATED);

        primaryStage.show();
    }

    public static void main(String[] args){
        launch();
    }
}
