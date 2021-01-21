package domain.automata_model;

import domain.AutomataType;
import domain.GenerationRule;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.IntStream;

public class AutomataModelImpl implements AutomataModel {

    private AutomataType automataType;
    private Integer generation;
    private Set<Integer> automata;
    private int ruleNum;

    public AutomataModelImpl(AutomataType automataType, int ruleNum){
        this.generation = 0;
        this.automata = new HashSet<>();
        this.automataType = automataType;
        this.ruleNum = ruleNum;
        switch (this.automataType){
            case TimeSeries1D:
                this.automata.add(MAX_WIDTH / 2);
                break;
            case TimeSeries2D:
                this.automata.add(MAX_WIDTH/2 +  + MAX_WIDTH*MAX_WIDTH/2);
                break;
            case GameOfLife:
                break;
            default:
                throw new IllegalStateException("Invalid Automata Type");
        }
    }

    @Override
    public AutomataType getAutomataType() {
        return this.automataType;
    }

    @Override
    public boolean setAutomataType(AutomataType automataType) {
        this.automataType = automataType;
        return true;
    }

    @Override
    public Set<Integer> getAutomata() {
        return this.automata;
    }

    @Override
    public boolean contains(int cellID) {
        return this.automata.contains(cellID);
    }

    @Override
    public boolean addCell(int cellID) {
        return this.automata.add(cellID);
    }

    @Override
    public boolean removeCell(int cellID) {
        return this.automata.remove(cellID);
    }

    @Override
    public void incrementGeneration() {
        GenerationRule.generateForRules(this, this.ruleNum);
        this.generation++;
    }

    @Override
    public void incrementGeneration(int numGens) {
        if(numGens > 0){
            throw new IllegalArgumentException("Cannot select negative generation number");
        }
        for(int i=0; i < numGens; i++){
            this.incrementGeneration();
        }
    }

    @Override
    public void resetModel() {
        this.generation = 0;
        this.automata.clear();
    }

    @Override
    public void initializeModel() {
        this.automata = new HashSet<>();
        this.generation = 0;
    }

    @Override
    public void initializeModel(List<Integer> cellIDs) {
        this.automata = new HashSet<>(cellIDs);
        this.generation = 0;
    }

    @Override
    //TODO: Generalize function for 1D case
    //NOTE: Only useful for GameOfLife currently
    public void randomizeModel() {
        // Currently only works for 2D
        this.resetModel();
        Random rand = new Random();
        // Generate number between 3000 and 50000
        int popNum = (rand.nextInt() & Integer.MAX_VALUE) % 47000 + 3000;
        System.out.println(popNum);
        for(int i =0; i < popNum; i++){
            int cellID = rand.nextInt() % (AutomataModel.MAX_WIDTH*AutomataModel.MAX_WIDTH); //Possible collisions not a big deal
            // Don't want double add though
            this.automata.add(cellID);
        }
        for(int el : automata){
            System.out.println(el);
        }
    }

    public Integer getGenerationNumber() {
        return this.generation;
    }

    public Integer getPopulation() { return this.automata.size(); }

}
