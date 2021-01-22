package domain;

public enum AutomataType {
    TimeSeries1D("Time Series 1D", 255, 8),
    TimeSeries2DFourNeighbor("Time Series 2D (Four Neighbor)", 1023, 10),
    TimeSeries2DEightNeighbor("Time Series 2D (Eight Neighbor)", 262143, 18),
    GameOfLife("Conway's Game of Life", 262143, 18);

    private String displayName;
    private int ruleNumMax;
    private int rulesetSize;

    AutomataType(String displayName, int ruleNumMax, int rulesetSize){
        this.ruleNumMax = ruleNumMax;
        this.displayName = displayName;
        this.rulesetSize = rulesetSize;
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
}
