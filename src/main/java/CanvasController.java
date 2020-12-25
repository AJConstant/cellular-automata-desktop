import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;

public class CanvasController implements Initializable {

    @FXML
    private Canvas canvas;
    @FXML
    private Button advanceGenerationButton;

    private Thread generationTimer;
    private boolean generating;

    private AutomataModel model;

    private static final String IDLE_BUTTON_STYLE = "-fx-background-color: #3AAFA9;";
    private static final String HOVERED_BUTTON_STYLE = "-fx-background-color: #2B7A78;";

    @Override
    public void initialize(URL location, ResourceBundle resources) {
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
        double x, y;
        x = event.getX();
        y = event.getY();
        int col = (int) x/6;
        int row = (int) y/6;
        Integer cellID = col + 100 * row;
        if(!this.model.getAutomata().contains(cellID)){
            this.model.getAutomata().add(cellID);
        }
    }

    @FXML
    public void advanceGeneration() {
        boolean[] nextGen = new boolean[10000];
        int[] neighbors = new int[8];
        ObservableList<Integer> genModel = this.model.getAutomata();

        for(int i=0; i < 10000; i++){
            int row = i/100;
            int col = i%100;

            neighbors[0] = genModel.contains((row-1)*100 + col) ? 1 : 0;
            neighbors[1] = genModel.contains((row-1)*100 + col+1) ? 1 : 0;
            neighbors[2] = genModel.contains(row*100 + col+1) ? 1 : 0;
            neighbors[3] = genModel.contains((row+1)*100 + col+1) ? 1 : 0;
            neighbors[4] = genModel.contains((row+1)*100 + col) ? 1 : 0;
            neighbors[5] = genModel.contains((row+1)*100 + col-1) ? 1 : 0;
            neighbors[6] = genModel.contains(row*100 + col-1) ? 1 : 0;
            neighbors[7] = genModel.contains((row-1)*100 + col-1) ? 1 : 0;

            int sum = Arrays.stream(neighbors).sum();
            // Live cell
            if(genModel.contains(i)){
                nextGen[i] = !(sum > 3 || sum < 2);
            } else { // Dead cell
                nextGen[i] = (sum == 3);
            }
        }
        genModel.clear();
        for(int i=0; i < 10000; i++){
            if(nextGen[i]){
                genModel.add(i);
            }
        }
    }
}
