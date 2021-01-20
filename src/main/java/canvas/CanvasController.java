package canvas;

import domain.AutomataModel;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

import java.net.URL;

import java.util.ResourceBundle;


public class CanvasController implements Initializable {

    public static Color background = Color.web("#DEF2F1");
    public static Color foreground = Color.web("#2B7A78");

    @FXML
    private Canvas canvas;

    @FXML
    private Pane canvasBackground;

    private AutomataModel model;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.canvasBackground.getStyleClass().add("light-canvas");
    }

    public void initModel(AutomataModel model) {
        this.model = model;
    }

    public void drawCell(Integer cellID, Paint p) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        int row = cellID / 100;
        int col = cellID % 100;
        gc.setFill(p);
        gc.fillRect(col * 6, row * 6, 6, 6);
    }

    @FXML
    public void drawSingleAutomata(MouseEvent event) {
        double x, y;
        x = event.getX();
        y = event.getY();
        int col = (int) x / 6;
        int row = (int) y / 6;
        Integer cellID = col + 100 * row;
        this.model.add(cellID);
        this.redrawCanvas();
    }

    public void redrawCanvas(){
        for(int i=0; i < 10000; i++){
            if(this.model.contains(i)){
                drawCell(i, foreground);
            } else {
                drawCell(i, background);
            }
        }
    }
}
