import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class AutomataModel {
    private final ObservableList<Integer> automata = FXCollections.observableArrayList();

    public ObservableList<Integer> getAutomata() {
        return automata;
    }
}
