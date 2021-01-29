package domain.automata_model;

import domain.AutomataType;

import java.util.List;
import java.util.Set;

//TODO
// Rewrite based on lookuptable
//

public interface AutomataModel {
    //TODO Test different sizes performances here
    public static final int UNIVERSE_WIDTH = 500;
    public static final int UNIVERSE_HEIGHT = 250;

    public static final int INIT_RULE_NUM = 1;
    public static final AutomataType INIT_AUTOMATA_TYPE = AutomataType.TimeSeries1D;


    public AutomataType getAutomataType();
    public void setAutomataType(AutomataType automataType);

    public int getRuleNumber();
    public void setRuleNumber(int ruleNumber);

    public boolean[][] getAutomata();

    public int getPopulation();
    public int getGenerationNumber();

    public void incrementGeneration();
    public void incrementGeneration(int numGens);
    public void resetModel();
    public void randomizeModel();

    public void printLookupTable();

    public boolean[][][] getWholeUniverse();
}
