package domain.automata_model;

import domain.AutomataType;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

public class AutomataModelImpl implements AutomataModel {

    private AutomataType automataType;
    private int generation;
    private int population;
    private boolean[][][] automata;
    private int ruleNum = 1;
    private boolean[] lookupTable = new boolean[1];

    public AutomataModelImpl(AutomataType automataType, int ruleNum) {
        this.generation = 0;
        this.automataType = automataType;
        this.ruleNum = ruleNum;
        this.initModel();
        this.buildLookupTable();
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

    @Override
    public AutomataType getAutomataType() {
        return this.automataType;
    }

    @Override
    public void setAutomataType(AutomataType automataType) {
        this.automataType = automataType;
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
                if(this.generation >= AutomataModel.UNIVERSE_HEIGHT-1){
                    // Horrible dependence universe size for number of generations available
                    throw new IllegalStateException("Cannot generate any further generations");
                }
                int currentGenRow = this.generation+1;
                int rowToModify = this.generation+2;
                for(int col=1; col <= AutomataModel.UNIVERSE_WIDTH; col++){
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
                for(int row=1;  row <= AutomataModel.UNIVERSE_HEIGHT; row++){
                    neighborhoodID = (thisGen[row][1] ? 1 : 0);
                    for(int col=1; col <= AutomataModel.UNIVERSE_WIDTH; col++){
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
                for(int row=1;  row <= AutomataModel.UNIVERSE_HEIGHT; row++){
                    neighborhoodID = (thisGen[row-1][0] ? 32 : 0) + (thisGen[row-1][1] ? 4 : 0) +
                            (thisGen[row][0] ? 16 : 0) + (thisGen[row][1] ? 2 : 0) +
                            (thisGen[row+1][0] ? 8 : 0) + (thisGen[row+1][1] ? 1 : 0);
                    for(int col=1; col <= AutomataModel.UNIVERSE_WIDTH; col++){
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
        this.automata = new boolean[2][UNIVERSE_HEIGHT+2][UNIVERSE_WIDTH+2];
        switch (this.automataType){
            case TimeSeries1D:
                // Must initialize both
                this.automata[0][1][UNIVERSE_WIDTH/2] = true;
                this.automata[1][1][UNIVERSE_WIDTH/2] = true;
                break;
            case TimeSeries2DFourNeighbor:
            case TimeSeries2DEightNeighbor:
            case GameOfLife:
                this.automata[0][UNIVERSE_HEIGHT/2][UNIVERSE_WIDTH/2] = true;
                this.automata[1][UNIVERSE_HEIGHT/2][UNIVERSE_WIDTH/2] = true;
                break;
        }
        this.generation = 0;
        this.population = 1;
        this.buildLookupTable();
    }

    @Override
    public void randomizeModel() {
        this.resetModel();
        this.generation = 0;
        this.population = 0;
        int ngpop = ThreadLocalRandom.current().nextInt(25000, 125000);
        for(int i=0; i < ngpop; i++) {
            int row = (ThreadLocalRandom.current().nextInt() & Integer.MAX_VALUE) % (UNIVERSE_HEIGHT);
            int col = (ThreadLocalRandom.current().nextInt() & Integer.MAX_VALUE) %  (UNIVERSE_WIDTH);
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

    public void printLookupTable(){
        switch(this.automataType){
            case TimeSeries1D:
                System.out.println("\nLookup table for rule number: " + this.ruleNum);
                for(int i=0; i < 8; i++) {
                    System.out.println("Index: " + i);
                    System.out.print((i >>> 2 & 1) == 1? "x ": "_ ");
                    System.out.print((i >>> 1 & 1) == 1? "x ": "_ ");
                    System.out.println((i >>> 0 & 1) == 1? "x ": "_ ");
                    if(lookupTable[i]){
                        System.out.println("  x");
                    } else {
                        System.out.println("  _");
                    }
                }
                break;
            default:
                break;
        }
    }

    public void printNeighborhood8Neighbor(int neighborhoodID){
        System.out.print((neighborhoodID >> 8 & 1) == 1 ? "X " : "_ ");
        System.out.print((neighborhoodID >> 5 & 1) == 1 ? "X " : "_ ");
        System.out.println((neighborhoodID >> 2 & 1) == 1 ? "X" : "_");

        System.out.print((neighborhoodID >> 7 & 1) == 1 ? "X " : "_ ");
        System.out.print((neighborhoodID >> 4 & 1) == 1 ? "X " : "_ ");
        System.out.println((neighborhoodID >> 1 & 1) == 1 ? "X" : "_");

        System.out.print((neighborhoodID >> 6 & 1) == 1 ? "X " : "_ ");
        System.out.print((neighborhoodID >> 3 & 1) == 1 ? "X " : "_ ");
        System.out.println((neighborhoodID >> 0 & 1) == 1 ? "X" : "_");

        System.out.println();
    }

    private void initModel(){
        this.automata = new boolean[2][AutomataModel.UNIVERSE_HEIGHT+2][AutomataModel.UNIVERSE_WIDTH+2];
    }

    public boolean[][][] getWholeUniverse(){
        return this.automata;
    }
}
