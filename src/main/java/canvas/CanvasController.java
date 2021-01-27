package canvas;

import domain.automata_model.AutomataModel;
import domain.automata_model.AutomataModelImpl;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;
import settings.Palette;
import settings.Settings;

import java.net.URL;

import java.util.ResourceBundle;


public class CanvasController implements Initializable {

    @FXML
    private Parent root;

    @FXML
    private Canvas canvas;

    @FXML
    private Text populationText;

    @FXML
    private Text generationText;

    private AutomataModelImpl model;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.root.getStyleClass().add(Settings.getActivePalette().getCssName());
    }

    /**
     * The canvas controller contains a reference to the current model
     * so that it can add cells via clicking on the board
     * @param model
     */
    public void initModel(AutomataModelImpl model) {
        this.populationText.setText("Population: 0");
        this.generationText.setText("Generation: 0");
        this.model = model;
    }

    public void drawModel(AutomataModelImpl model) throws IllegalStateException {
        if(this.model == null){ throw new IllegalStateException("Model is not yet initialized"); }
        this.populationText.setText("Population: " + model.getPopulation());
        this.generationText.setText("Generation: " + model.getGenerationNumber());
        switch(model.getAutomataType()){
            case TimeSeries1D:
                int row = model.getGenerationNumber();
                for(int i = 0; i < AutomataModel.MAX_WIDTH; i++){
                    if(this.model.contains(i)){
                        this.drawCell1D(i, row, Settings.getActivePalette().getCanvasForeground());
                    } else {
                        this.drawCell1D(i, row, Settings.getActivePalette().getCanvasBackground());
                    }
                }
                break;
            case TimeSeries2DFourNeighbor:
            case TimeSeries2DEightNeighbor:
            case GameOfLife:
                for(int i=0; i < AutomataModel.MAX_WIDTH*AutomataModel.MAX_HEIGHT; i++){
                    if(this.model.contains(i)){
                        drawCell2D(i, Settings.getActivePalette().getCanvasForeground());
                    } else {
                        drawCell2D(i, Settings.getActivePalette().getCanvasBackground());
                    }
                }
                break;
        }
    }

    private void drawCell1D(Integer cellID, Integer genNumber, Paint p) throws IllegalStateException {
        if(this.model == null){ throw new IllegalStateException("Model is not yet initialized"); }
        int ppc = (int)(this.canvas.getWidth() / AutomataModel.MAX_WIDTH);
        GraphicsContext gc = this.canvas.getGraphicsContext2D();
        gc.setFill(p);
        gc.fillRect(cellID*ppc, genNumber*ppc, ppc, ppc);
    }

    private void drawCell2D(Integer cellID, Paint p) throws IllegalStateException {
        if(this.model == null){ throw new IllegalStateException("Model is not yet initialized"); }
        int ppc = (int)(this.canvas.getWidth() / AutomataModel.MAX_WIDTH);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        int row = cellID / AutomataModel.MAX_WIDTH;
        int col = cellID % AutomataModel.MAX_WIDTH;
        gc.setFill(p);
        gc.fillRect(col * ppc, row * ppc, ppc, ppc);
    }

    public void resetCanvas(){
        if(this.model == null){ throw new IllegalStateException("Model is not yet initialized"); }
        GraphicsContext gc = this.canvas.getGraphicsContext2D();
        gc.setFill(Settings.getActivePalette().getCanvasBackground());
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }

    public void updateStyle(Palette oldStyle){
        this.root.getStyleClass().remove(oldStyle.getCssName());
        this.root.getStyleClass().add(Settings.getActivePalette().getCssName());
    }

    @FXML
    public void drawSingleAutomata(MouseEvent event) throws IllegalStateException {
        if(this.model == null){ throw new IllegalStateException("Model is not yet initialized"); }
        double x, y;
        x = event.getX();
        y = event.getY();
        int col = (int) x / 6;
        int row = (int) y / 6;
        Integer cellID = col + 100 * row;
        this.model.addCell(cellID);
        this.drawModel(this.model);
    }
}
