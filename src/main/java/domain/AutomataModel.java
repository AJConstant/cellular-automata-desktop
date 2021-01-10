package domain;

import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;

public class AutomataModel {

    private Integer generation;
    private ObservableSet<Integer> automata;

    public AutomataModel(){
        this.generation = 0;
        this.automata = FXCollections.observableSet(new HashSet<>());
    }

    public ObservableSet<Integer> getAutomata() {
        return this.automata;
    }

    public void incrementGeneration() {
        boolean[] nextGen = new boolean[10000];
        int[] neighbors = new int[8];

        for (int i = 0; i < 10000; i++) {
            int row = i / 100;
            int col = i % 100;

            neighbors[0] = this.automata.contains((row - 1) * 100 + col) ? 1 : 0;       // Top
            neighbors[1] = this.automata.contains((row - 1) * 100 + col + 1) && ((col+1)/100 == col/100) ? 1 : 0;   // Top right
            neighbors[2] = this.automata.contains(row * 100 + col + 1) && ((col+1)/100 == col/100) ? 1 : 0;         // Right
            neighbors[3] = this.automata.contains((row + 1) * 100 + col + 1) && ((col+1)/100 == col/100) ? 1 : 0;   //  etc.
            neighbors[4] = this.automata.contains((row + 1) * 100 + col) ? 1 : 0;
            neighbors[5] = this.automata.contains((row + 1) * 100 + col - 1) && ((col-1)/100 == col/100) ? 1 : 0;
            neighbors[6] = this.automata.contains(row * 100 + col - 1) && ((col-1)/100 == col/100) ? 1 : 0;
            neighbors[7] = this.automata.contains((row - 1) * 100 + col - 1) && ((col-1)/100 == col/100) ? 1 : 0;

            int sum = Arrays.stream(neighbors).sum();

            // Live cell
            if (this.automata.contains(i)) {
                nextGen[i] = !(sum > 3 || sum < 2);
            } else { // Dead cell
                nextGen[i] = (sum == 3);
            }
        }
        this.automata.clear();
        for (int i = 0; i < 10000; i++) {
            if (nextGen[i]) {
                this.automata.add(i);
            }
        }
        this.generation++;
    }

    public void resetModel() {
        this.generation = 0;
        this.automata.clear();
    }

    public void randomizeModel() {
        this.resetModel();
        Random rand = new Random();
        // Generate number between 300 and 5000
        int popNum = (rand.nextInt() & Integer.MAX_VALUE) % 4700 + 300;
        for(int i =0; i < popNum; i++){
            int cellID = rand.nextInt() % 10000; //Possible collisions not a big deal
            // Don't want double add though
            if(!this.automata.contains(cellID)){
                this.automata.add(cellID);
            }
        }
    }

    public Integer getGenerationNumber() {
        return this.generation;
    }
}
