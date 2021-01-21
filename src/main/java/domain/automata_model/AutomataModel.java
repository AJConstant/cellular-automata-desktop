package domain.automata_model;

import domain.AutomataType;

import java.util.List;
import java.util.Set;

public interface AutomataModel {
    //TODO Test different sizes performances here
    public static int MAX_WIDTH = 300;

    public AutomataType getAutomataType();
    public boolean setAutomataType(AutomataType automataType);

    public Set<Integer> getAutomata();
    public boolean contains(int cellID);
    public boolean addCell(int cellID);
    public boolean removeCell(int cellID);

    public Integer getPopulation();
    public Integer getGenerationNumber();

    public void incrementGeneration();
    public void incrementGeneration(int numGens);
    public void resetModel();
    public void randomizeModel();
    public void initializeModel();
    public void initializeModel(List<Integer> cellIDs);
}
