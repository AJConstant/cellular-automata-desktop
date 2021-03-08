package domain.automata_model;

import domain.AutomataType;
import domain.InitialCondition;

import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

public class AutomataModelImpl implements AutomataModel {

    private AutomataType automataType;
    private InitialCondition initialCondition;
    private int height;
    private int width;
    private int _outer_height;
    private int _outer_width;
    private int generation;
    private int population;
    private boolean[][][] automata;
    private int ruleNum = 1;
    private boolean[] lookupTable = new boolean[1];

    public AutomataModelImpl(AutomataType automataType, int ruleNum, InitialCondition initialCondition) {
        assert(automataType != null);
        assert(initialCondition != null);
        this.generation = 0;
        this.automataType = automataType;
        this.ruleNum = ruleNum;
        this.initialCondition = initialCondition;
        this.width = AutomataModel.INIT_UNIVERSE_WIDTH;
        this.height = AutomataModel.INIT_UNIVERSE_HEIGHT;
        this._outer_width = this.width+2;
        this._outer_height = this.height+2;
        this.init();
    }

    public AutomataModelImpl(AutomataType automataType, int ruleNum, InitialCondition initialCondition, int height, int width) {
        assert(automataType != null);
        assert(initialCondition != null);
        if(height < 0 || height > MAX_UNIVERSE_HEIGHT || width < 0 || width > MAX_UNIVERSE_WIDTH){
            throw new IllegalArgumentException("Model Dimensions out of Range (0, 10000)");
        }
        this.generation = 0;
        this.automataType = automataType;
        this.ruleNum = ruleNum;
        this.initialCondition = initialCondition;
        this.width = width;
        this._outer_width = width+2;
        this.height = height;
        this._outer_height = height+2;
        this.init();
    }

    /**
     * Builds a lookup table for the model's current Type and rule.
     * The lookup table contains all configurations of neighbors that exist and whether the center cell
     * should live or die.
     * See: https://codereview.stackexchange.com/questions/42718/optimize-conways-game-of-life
     */
    public void buildLookupTable(){
        switch (this.automataType){
            case TimeSeries1D:
                buildLookupTable3Neighbor();
                break;
            case TimeSeries2DFourNeighbor:
                buildLookupTable4Neighbor();
                break;
            case TimeSeries2DEightNeighbor:
            case GameOfLife:
                buildLookupTable8Neighbor();
                break;
        }
    }

    public String toString(){
        return "Automata Type: " + this.automataType + "\nRule Number: " + this.ruleNum + "\n Initial Condition Name: " + this.initialCondition.getDisplayName();
    }

    @Override
    public int getUniverseWidth() {
        return this.width;
    }

    @Override
    public void setUniverseWidth(int width) {
        this.width = width;
        this._outer_width = width+2;
    }

    @Override
    public int getUniverseHeight() {
        return this.height;
    }

    @Override
    public void setUniverseHeight(int height) {
        this.height = height;
        this._outer_height = height+2;
    }

    @Override
    public AutomataType getAutomataType() {
        return this.automataType;
    }

    @Override
    public void setAutomataType(AutomataType automataType) {
        this.automataType = automataType;
    }

    @Override
    public InitialCondition getInitialCondition() {
        return this.initialCondition;
    }

    @Override
    // Not pretty.
    public void setInitialCondition(InitialCondition cond) {
        if(this.automataType.getInitialConditions().contains(cond)){
            this.initialCondition = cond;
        } else {
            this.initialCondition = this.automataType.getInitialConditions().get(0);
        }
    }

    @Override
    public int getRuleNumber() {
        return this.ruleNum;
    }

    @Override
    public void setRuleNumber(int ruleNumber) {
        this.ruleNum = ruleNumber;
        this.buildLookupTable();
    }

    @Override
    public boolean[][] getAutomata() {
        return this.automata[this.generation%2];
    }

    @Override
    public void setAutomata(boolean[][] automata){
        if(automata == null || automata.length != this._outer_height || automata[0].length != this._outer_width){
            throw new IllegalArgumentException("Improper dimensions provided");
        }
        for(int i=0; i<automata.length; i++){
            this.automata[0][i] = Arrays.copyOf(automata[i], automata[i].length);
            this.automata[1][i] = Arrays.copyOf(automata[i], automata[i].length);
        }
    }

    @Override
    public int getPopulation() {
        return this.population;
    }

    @Override
    public int getGenerationNumber() {
        return this.generation;
    }

    @Override
    public void incrementGeneration() {
        // Which buffer are we on?
        int buffer = this.generation%2;
        boolean[][] thisGen = this.automata[buffer],
                nextGen = this.automata[(buffer + 1)%2];

        int neighborhoodID;
        boolean isAlive, willLive;

        switch(this.automataType){
            case TimeSeries1D:
                if(this.generation >= this.height-1){
                    // Horrible dependence universe size for number of generations available
                    throw new IllegalStateException("Cannot generate any further generations");
                }
                int currentGenRow = this.generation+1;
                int rowToModify = this.generation+2;
                for(int col=1; col <= this.width; col++){
                    neighborhoodID = (thisGen[currentGenRow][col-1]? 4 : 0) +
                            (thisGen[currentGenRow][col]? 2 : 0) +
                            (thisGen[currentGenRow][col+1]? 1 : 0);
                    willLive = this.lookupTable[neighborhoodID];
                    isAlive = thisGen[currentGenRow][col];
                    this.population += isAlive ? willLive ? 0 : -1 : willLive ? 1 : 0;
                    nextGen[rowToModify][col] = willLive;
                    thisGen[rowToModify][col] = willLive;
                }
                break;
            case TimeSeries2DFourNeighbor:
                for(int row=1;  row <= this.height; row++){
                    neighborhoodID = (thisGen[row][1] ? 1 : 0);
                    for(int col=1; col <= this.width; col++){
                        neighborhoodID = ((neighborhoodID & 0b0101) << 2);
                        neighborhoodID += (thisGen[row-1][col] ? 8 : 0) +
                                (thisGen[row][col+1] ? 1 : 0) +
                                (thisGen[row+1][col] ? 2 : 0);
                        willLive = this.lookupTable[neighborhoodID];
                        isAlive = thisGen[row][col];
                        this.population += isAlive ? willLive ? 0 : -1 : willLive ? 1 : 0;
                        nextGen[row][col] = this.lookupTable[neighborhoodID];
                    }
                }
                break;
            case TimeSeries2DEightNeighbor:
            case GameOfLife:
                for(int row=1;  row <= this.height; row++){
                    neighborhoodID = (thisGen[row-1][0] ? 32 : 0) + (thisGen[row-1][1] ? 4 : 0) +
                            (thisGen[row][0] ? 16 : 0) + (thisGen[row][1] ? 2 : 0) +
                            (thisGen[row+1][0] ? 8 : 0) + (thisGen[row+1][1] ? 1 : 0);
                    for(int col=1; col <= this.width; col++){
                        neighborhoodID = ((neighborhoodID & 0b111111) << 3) +
                                (thisGen[row-1][col+1] ? 4 : 0) +
                                (thisGen[row][col+1] ? 2 : 0) +
                                (thisGen[row+1][col+1] ? 1 : 0);
                        willLive = this.lookupTable[neighborhoodID];
                        isAlive = thisGen[row][col];
                        this.population += isAlive ? willLive ? 0 : -1 : willLive ? 1 : 0;
                        nextGen[row][col] = this.lookupTable[neighborhoodID];
                    }
                }
                break;
        }
        this.generation++;
    }

    @Override
    public void incrementGeneration(int numGens) {
        if(numGens < 0) {
            throw new IllegalArgumentException("Number of generations must be >= 0");
        }
        for(int i=0; i < numGens; i++){
            this.incrementGeneration();
        }
    }

    @Override
    public void resetModel() {
        this.generation = 0;
        this.init();
    }

    @Override
    public void randomizeModel() {
        this.resetModel();
        this.generation = 0;
        this.population = 0;
        int ngpop = ThreadLocalRandom.current().nextInt(25000, 125000);
        for(int i=0; i < ngpop; i++) {
            int row = (ThreadLocalRandom.current().nextInt() & Integer.MAX_VALUE) % (this.height);
            int col = (ThreadLocalRandom.current().nextInt() & Integer.MAX_VALUE) %  (this.width);
            if(row != 0 && col != 0){
                if(!this.automata[0][row][col]){
                    this.population++;
                }
                this.automata[0][row][col] = true;
                this.automata[1][row][col] = true;
            }
        }
    }

    /*
     * Helper methods
     */
    private void buildLookupTable3Neighbor() {
        synchronized (lookupTable) {
            this.lookupTable = new boolean[8];
            for (int i = 0; i < 7; i++) {
                this.lookupTable[i] = ((ruleNum >>> i & 1)==1);
            }
        }
    }

    private void buildLookupTable4Neighbor() {
        synchronized (lookupTable) {
            this.lookupTable = new boolean[32];
            for (int i = 0; i < 32; i++) {
                int neighborSum = (i >>> 0 & 1) + (i >>> 1 & 1) + (i >>> 3 & 1) + (i >>> 4 & 1);
                boolean isAlive = (i >>> 2 & 1)==1;
                boolean willLive;
                if(!isAlive){
                    willLive =  (((ruleNum >> 0 & 1) == 1 && neighborSum == 0)   ||
                            ((ruleNum >> 2 & 1) == 1 && neighborSum == 1)   ||
                            ((ruleNum >> 4 & 1) == 1 && neighborSum == 2)   ||
                            ((ruleNum >> 6 & 1) == 1 && neighborSum == 3)   ||
                            ((ruleNum >> 8 & 1) == 1 && neighborSum == 4));
                } else {
                    willLive  = (((ruleNum >> 1 & 1)== 1 && neighborSum == 0)   ||
                            ((ruleNum >> 3 & 1)== 1 && neighborSum == 1)   ||
                            ((ruleNum >> 5 & 1)== 1 && neighborSum == 2)   ||
                            ((ruleNum >> 7 & 1)== 1 && neighborSum == 3)   ||
                            ((ruleNum >> 9 & 1)== 1 && neighborSum == 4));
                }
                this.lookupTable[i] = willLive;
            }
        }
    }

    private void buildLookupTable8Neighbor() {
        synchronized (this.lookupTable){
            this.lookupTable = new boolean[512];
            for(int i=0; i<512; i+=1){
                boolean isAlive = (i >>> 4 & 1) == 1;
                boolean willLive;
                int neighborSum = (i >>> 8 & 1) + (i >>> 5 & 1) + (i >>> 2 & 1) +
                        (i >>> 7 & 1)                 + (i >>> 1 & 1) +
                        (i >>> 6 & 1) + (i >>> 3 & 1) + (i >>> 0 & 1);
                if(!isAlive){
                    willLive =  (((ruleNum >> 0 & 1) == 1 && neighborSum == 0)   ||
                            ((ruleNum >> 2 & 1) == 1 && neighborSum == 1)   ||
                            ((ruleNum >> 4 & 1) == 1 && neighborSum == 2)   ||
                            ((ruleNum >> 6 & 1) == 1 && neighborSum == 3)   ||
                            ((ruleNum >> 8 & 1) == 1 && neighborSum == 4)   ||
                            ((ruleNum >> 10 & 1) == 1 && neighborSum == 5)  ||
                            ((ruleNum >> 12 & 1) == 1 && neighborSum == 6)  ||
                            ((ruleNum >> 14 & 1) == 1 && neighborSum == 7)  ||
                            ((ruleNum >> 16 & 1) == 1 && neighborSum == 8));
                } else {
                    willLive  = (((ruleNum >> 1 & 1)== 1 && neighborSum == 0)   ||
                            ((ruleNum >> 3 & 1)== 1 && neighborSum == 1)   ||
                            ((ruleNum >> 5 & 1)== 1 && neighborSum == 2)   ||
                            ((ruleNum >> 7 & 1)== 1 && neighborSum == 3)   ||
                            ((ruleNum >> 9 & 1)== 1 && neighborSum == 4)   ||
                            ((ruleNum >> 11 & 1)== 1 && neighborSum == 5)  ||
                            ((ruleNum >> 13 & 1)== 1 && neighborSum == 6)  ||
                            ((ruleNum >> 15 & 1)== 1 && neighborSum == 7)  ||
                            ((ruleNum >> 17 & 1)== 1 && neighborSum == 8));
                }
                this.lookupTable[i] = willLive;
            }
        }
    }

    /**
     *
     */
    public void init(){
        if(this.width < 0 || this.width > MAX_UNIVERSE_WIDTH || this.height < 0 || this.height > MAX_UNIVERSE_HEIGHT){
            throw new IllegalStateException("Universe Dimensions Improperly Initialized");
        }
        if(this.automataType == null){
            throw new IllegalStateException("Automata Type Uninitialized");
        }
        if(this.initialCondition == null){
            throw new IllegalStateException("Initial Condition Uninitialized");
        }
        this.automata = new boolean[2][this._outer_height][this._outer_width];
        this.automata[0] = this.initialCondition.getInitialCondition(this);
        this.automata[1] = this.initialCondition.getInitialCondition(this);
        this.initializePopulation();
        this.buildLookupTable();
    }

    private void initializePopulation(){
       if(this.automata == null) { throw new IllegalStateException("Automata array is null"); }
       int pop=0;
       for(int i=0; i<this.automata[0].length; i++){
           for(int j=0; j<this.automata[0][i].length; j++){
               pop += this.automata[0][i][j]? 1 : 0;
           }
       }
       this.population = pop;
    }
}
