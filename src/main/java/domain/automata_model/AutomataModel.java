package domain.automata_model;

import domain.AutomataType;
import domain.InitialCondition;

import java.util.List;
import java.util.Set;

//TODO
// Rewrite based on lookuptable
//

public interface AutomataModel {
    public static final int INIT_UNIVERSE_WIDTH = 500;
    public static final int INIT_UNIVERSE_HEIGHT = 250;

    public static final int MAX_UNIVERSE_WIDTH = 10000;
    public static final int MAX_UNIVERSE_HEIGHT = 10000;

    public static final int MAX_GENERATIONS = 10000;

    public static final int INIT_RULE_NUM = 224;
    public static final UniverseSize INIT_UNIVERSE_SIZE = UniverseSize.STANDARD_VISUALIZE;
    public static final AutomataType INIT_AUTOMATA_TYPE = AutomataType.GameOfLife;
    public static final InitialCondition INIT_CONDITION = InitialCondition.GLIDER_GUN_GOSPER;

    public int getUniverseWidth();
    public void setUniverseWidth(int width);

    public int getUniverseHeight();
    public void setUniverseHeight(int height);

    public AutomataType getAutomataType();
    public void setAutomataType(AutomataType automataType);

    public InitialCondition getInitialCondition();
    public void setInitialCondition(InitialCondition cond);

    public int getRuleNumber();
    public void setRuleNumber(int ruleNumber);

    public boolean[][] getAutomata();
    public void setAutomata(boolean[][] automata);

    public int getPopulation();
    public int getGenerationNumber();

    public void init();
    public void incrementGeneration();
    public void incrementGeneration(int numGens);
    public void resetModel();
    public void randomizeModel();
}
