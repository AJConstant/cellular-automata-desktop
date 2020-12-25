import com.jfoenix.controls.JFXButton;
import com.jfoenix.svg.SVGGlyph;
import com.jfoenix.svg.SVGGlyphLoader;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.SVGPath;

import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class CanvasController implements Initializable {

    @FXML
    private Canvas canvas;

    @FXML
    private JFXButton togglePlaybackButton;

    private AtomicBoolean generating;

    private AutomataModel model;

    private ScheduledExecutorService canvasAnimator;

    private ScheduledFuture generateNext;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.generating = new AtomicBoolean(false);
        this.canvasAnimator = Executors.newScheduledThreadPool(1);
    }

    public void initModel(AutomataModel model) {
        this.model = model;
        this.model.getAutomata().addListener((ListChangeListener.Change<? extends Integer> change) -> {
            while (change.next()) {
                if (change.wasAdded()) {
                    for (Integer i : change.getAddedSubList()) {
                        drawCell(i, Color.web("#3AAFA9"));
                    }
                } else if (change.wasRemoved()) {
                    for (Integer i : change.getRemoved()) {
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
        gc.fillRect(col * 6, row * 6, 6, 6);
    }

    @FXML
    public void drawSingleAutomata(MouseEvent event) {
        if (generating.get()) {
            return;
        }
        double x, y;
        x = event.getX();
        y = event.getY();
        int col = (int) x / 6;
        int row = (int) y / 6;
        Integer cellID = col + 100 * row;
        if (!this.model.getAutomata().contains(cellID)) {
            this.model.getAutomata().add(cellID);
        }
    }

    @FXML
    public void advanceGeneration() {
        boolean[] nextGen = new boolean[10000];
        int[] neighbors = new int[8];
        ObservableList<Integer> genModel = this.model.getAutomata();

        for (int i = 0; i < 10000; i++) {
            int row = i / 100;
            int col = i % 100;

            neighbors[0] = genModel.contains((row - 1) * 100 + col) ? 1 : 0;
            neighbors[1] = genModel.contains((row - 1) * 100 + col + 1) ? 1 : 0;
            neighbors[2] = genModel.contains(row * 100 + col + 1) ? 1 : 0;
            neighbors[3] = genModel.contains((row + 1) * 100 + col + 1) ? 1 : 0;
            neighbors[4] = genModel.contains((row + 1) * 100 + col) ? 1 : 0;
            neighbors[5] = genModel.contains((row + 1) * 100 + col - 1) ? 1 : 0;
            neighbors[6] = genModel.contains(row * 100 + col - 1) ? 1 : 0;
            neighbors[7] = genModel.contains((row - 1) * 100 + col - 1) ? 1 : 0;

            int sum = Arrays.stream(neighbors).sum();
            // Live cell
            if (genModel.contains(i)) {
                nextGen[i] = !(sum > 3 || sum < 2);
            } else { // Dead cell
                nextGen[i] = (sum == 3);
            }
        }
        genModel.clear();
        for (int i = 0; i < 10000; i++) {
            if (nextGen[i]) {
                genModel.add(i);
            }
        }
    }

    @FXML
    public void togglePlayback() {
        if(this.generating.get()){
            this.generating.set(false);
            this.generateNext.cancel(true);
            this.togglePlaybackButton.setText("PLAY");
        } else {
            this.generating.set(true);
            restartTimer();
            this.togglePlaybackButton.setText("PAUSE");
        }
    }

    private void restartTimer(){
        this.generateNext = this.canvasAnimator.scheduleAtFixedRate(new Runnable() {
            public void run() {
                advanceGeneration();
            }
        }, 1, 1, TimeUnit.SECONDS);
    }
}
