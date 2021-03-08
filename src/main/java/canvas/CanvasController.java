package canvas;

import domain.automata_model.AutomataModel;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;
import settings.Palette;
import settings.Settings;

import java.net.URL;

import java.util.ResourceBundle;


public class CanvasController implements Initializable {

    private int CANVAS_WIDTH = 1000;
    private int CANVAS_HEIGHT = 500;

    @FXML
    private Parent root;

    @FXML
    private Canvas canvas;

    @FXML
    private Text populationText;

    @FXML
    private Text generationText;

    private AutomataModel model;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.canvas.setHeight(CANVAS_HEIGHT);
        this.canvas.setWidth(CANVAS_WIDTH);
        this.root.getStyleClass().add(Settings.getActivePalette().getCssName());
    }

    public void drawModel() throws IllegalStateException {
        boolean[][] generation = model.getAutomata();
        GraphicsContext gc = this.canvas.getGraphicsContext2D();
        Paint foreground = Settings.getActivePalette().getCanvasForeground();
        Paint background = Settings.getActivePalette().getCanvasBackground();
        for(int row=1; row <= model.getUniverseHeight()+1; row++){
            for(int col=1; col <= model.getUniverseWidth()+1; col++){
                gc.setFill(generation[row][col] ? foreground : background);
                gc.fillRect((col-1)*2, (row-1)*2, 2, 2);
            }
        }
        this.populationText.setText("Population: " + this.model.getPopulation());
        this.generationText.setText("Generation: " + this.model.getGenerationNumber());
    }

    public void initModel(AutomataModel model){
        this.model = model;
    }

    public void resetCanvas(){
        GraphicsContext gc = this.canvas.getGraphicsContext2D();
        gc.setFill(Settings.getActivePalette().getCanvasBackground());
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }

    public void updateStyle(Palette oldStyle){
        this.root.getStyleClass().remove(oldStyle.getCssName());
        this.root.getStyleClass().add(Settings.getActivePalette().getCssName());
    }
}
