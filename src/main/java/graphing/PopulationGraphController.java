package graphing;
import domain.automata_model.AutomataModelImpl;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import settings.Palette;
import settings.Settings;

import java.net.URL;
import java.util.ResourceBundle;

public class PopulationGraphController implements Initializable {

    @FXML
    private Parent root;

    @FXML
    private LineChart<Integer, Integer> populationGraph;

    @FXML
    private NumberAxis xAxis;

    @FXML
    private NumberAxis yAxis;

    public XYChart.Series<Integer, Integer> series;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        xAxis.setForceZeroInRange(false);
        yAxis.setForceZeroInRange(false);
        this.series = new XYChart.Series<>();
        this.populationGraph.getData().add(this.series);
        this.root.getStyleClass().add(Settings.getActivePalette().getCssName());
    }

    public void plotPopulation(AutomataModelImpl model){
        this.series.getData().add(new XYChart.Data<>(model.getGenerationNumber(), model.getPopulation()));
        // This is a regression from Java 8 to Java 12, still unfixed, see here:
        // https://bugs.openjdk.java.net/browse/JDK-8209172?jql=status%20%3D%20Open%20AND%20text%20~%20%22javafx%20chart%22
        this.populationGraph.requestLayout();
    }

    public void updateStyle(Palette oldStyle){
        this.root.getStyleClass().remove(oldStyle.getCssName());
        this.root.getStyleClass().add(Settings.getActivePalette().getCssName());
    }

    public void resetGraph(){
        this.series.getData().clear();
        this.populationGraph.requestLayout();
    }
}
