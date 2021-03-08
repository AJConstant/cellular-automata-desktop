package domain;

import domain.automata_model.AutomataModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum AutomataType {
    TimeSeries1D("Time Series 1D", 255, 8,
            Arrays.asList(InitialCondition.SINGLE_CELL, InitialCondition.ROW_OF_THREE, InitialCondition.ROW_OF_FIVE)),
    TimeSeries2DFourNeighbor("Time Series 2D (Four Neighbor)", 1023, 10,
            Arrays.asList(InitialCondition.values())),
    TimeSeries2DEightNeighbor("Time Series 2D (Eight Neighbor)", 262143, 18,
            Arrays.asList(InitialCondition.values())),
    GameOfLife("Conway's Game of Life", 262143, 18,
            Arrays.asList(InitialCondition.values()));

    private String displayName;
    private int ruleNumMax;
    private int rulesetSize;
    private List<InitialCondition> associatedConditions;

    AutomataType(String displayName, int ruleNumMax, int rulesetSize, List<InitialCondition> initialConditions){
        this.ruleNumMax = ruleNumMax;
        this.displayName = displayName;
        this.rulesetSize = rulesetSize;
        this.associatedConditions = initialConditions;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public int getRuleNumMax() {
        return this.ruleNumMax;
    }

    public int getRulesetSize() {
        return this.rulesetSize;
    }

    public List<InitialCondition> getInitialConditions(){
        return this.associatedConditions;
    }

}
