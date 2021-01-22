package domain.automata_model;

import domain.AutomataType;

import java.util.List;
import java.util.Set;

public interface AutomataModel {
    //TODO Test different sizes performances here
    public static final int MAX_WIDTH = 500;
    public static final int MAX_HEIGHT = 250;
    public static final int INIT_RULE_NUM = 1;
    public static final AutomataType INIT_AUTOMATA_TYPE = AutomataType.TimeSeries1D;

    public AutomataType getAutomataType();
    public boolean setAutomataType(AutomataType automataType);

    public Integer getRuleNumber();
    public void setRuleNumber(int ruleNumber);

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
