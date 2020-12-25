import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

import java.awt.font.GraphicAttribute;
import java.net.URL;
import java.util.ResourceBundle;

public class CanvasController implements Initializable {

    @FXML
    private Canvas canvas;
    @FXML
    private Button advanceGenerationButton;

    private AutomataModel model;

    private static final String IDLE_BUTTON_STYLE = "-fx-background-color: #3AAFA9;";
    private static final String HOVERED_BUTTON_STYLE = "-fx-background-color: #2B7A78;";

    @Override
    public void initialize(URL location, ResourceBundle resources) { ;
        advanceGenerationButton.setStyle(IDLE_BUTTON_STYLE);
        advanceGenerationButton.setOnMouseEntered(e -> advanceGenerationButton.setStyle(HOVERED_BUTTON_STYLE));
        advanceGenerationButton.setOnMouseExited(e -> advanceGenerationButton.setStyle(IDLE_BUTTON_STYLE));
    }

    public void initModel(AutomataModel model) {
        this.model = model;
        this.model.getAutomata().addListener((ListChangeListener.Change<? extends Integer> change) -> {
            while(change.next()){
                if(change.wasAdded()){
                    for(Integer i : change.getAddedSubList()){
                        drawCell(i, Color.web("#3AAFA9"));
                    }
                } else if (change.wasRemoved()){
                    for(Integer i : change.getRemoved()){
                        drawCell(i, Color.web("#DEF2F1"));
                    }
                }
            }
        });
    }

    public void drawCell(Integer cellID, Paint p) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        int row = cellID / 100;
        int col = cellID % 100;
        gc.setFill(p);
        gc.fillRect(col*6, row*6, 6, 6);
    }

    @FXML
    public void drawSingleAutomata(MouseEvent event){
        GraphicsContext gc = canvas.getGraphicsContext2D();
        double x, y;
        x = event.getX();
        y = event.getY();
        int col = (int) x/6;
        int row = (int) y/6;

        Integer cellID = col + 100 * row;

        drawCell(cellID, Color.web("#3AAFA9"));
    }

    @FXML
    public void drawBigRect() throws InterruptedException {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setFill(Color.BLACK);
        for(int i = 100; i < 150; i++){
            gc.fillRect(i*6, 0, 6, 6);
        }
    }

    @FXML
    public void advanceGeneration() {
        // DO IT
    }
}
