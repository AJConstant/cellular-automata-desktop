package domain;

import domain.automata_model.AutomataModel;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.logging.Logger;
import java.util.stream.Stream;

public class GenerationRule {
    public static void generateForRules(AutomataModel model, int ruleNum) throws IllegalArgumentException {
        if(model == null){ throw new IllegalArgumentException("Model is null"); }
        boolean[] nextGen;
        boolean[] ruleBoolean;
        switch(model.getAutomataType()){
            case TimeSeries1D:
                if(ruleNum < 0 || ruleNum > 255){ throw new IllegalArgumentException("Improper rule selected"); }
                nextGen = new boolean[AutomataModel.MAX_WIDTH];
                ruleBoolean = ruleBoolean(ruleNum);
                for(int i=0; i<AutomataModel.MAX_WIDTH; i++){
                    ArrayList<Boolean> neighbors = getNeighbors1D(model, i);
                    nextGen[i] = doRules1D(ruleBoolean, neighbors);
                }
                for(int i=0; i<AutomataModel.MAX_WIDTH; i++){
                    if (nextGen[i]) {
                        model.addCell(i);
                    } else {
                        model.removeCell(i);
                    }
                }
                break;
            case TimeSeries2D:
                if(ruleNum < 0 || ruleNum > 255){ throw new IllegalArgumentException("Improper rule selected"); }
                nextGen = new boolean[AutomataModel.MAX_WIDTH*AutomataModel.MAX_WIDTH];
                ruleBoolean = ruleBoolean(ruleNum);
                for(int i=0; i<AutomataModel.MAX_WIDTH*AutomataModel.MAX_WIDTH; i++){
                    int neighborSum = getNeighbors2D(model, i);
                    nextGen[i] = doRules2D(ruleBoolean, neighborSum);
                }
                for(int i=0; i < AutomataModel.MAX_WIDTH*AutomataModel.MAX_WIDTH; i++){
                    if(nextGen[i]){
                        model.addCell(i);
                    } else {
                        model.removeCell(i);
                    }
                }
                break;
            case GameOfLife:
                throw new IllegalArgumentException("Game of Life is Unimplemented");
        }
    }

    private static ArrayList<Boolean> getNeighbors1D(AutomataModel model, int cellID){
        //Note: Order of adding is important! Left to right
        ArrayList<Boolean> neighbors = new ArrayList<>();
        neighbors.add(model.contains(cellID-1));
        neighbors.add(model.contains(cellID));
        neighbors.add(model.contains(cellID+1));
        return neighbors;
    }

    // Note - we're doing the conway's game of life version, with totalistic models
    // Otherwise we'll have WAY too many rules here
    //TODO: non-totalistic model - is it feasible?
    private static int getNeighbors2D(AutomataModel model, int cellID){
        //Note: Order of adding is important! Clockwise from top
        int neighborSum = 0;
        int col = cellID % AutomataModel.MAX_WIDTH;
        int lCol = (cellID - 1)%AutomataModel.MAX_WIDTH;
        int rCol = (cellID + 1)%AutomataModel.MAX_WIDTH;

        neighborSum += (model.contains(cellID - AutomataModel.MAX_WIDTH)) ? 1: 0; // Top
        neighborSum +=  (model.contains(cellID - AutomataModel.MAX_WIDTH + 1) && rCol > col) ? 1: 0; // Top right
        neighborSum += (model.contains(cellID + 1) && rCol > col)? 1: 0; // Right
        neighborSum += (model.contains(cellID + AutomataModel.MAX_WIDTH + 1) && rCol > col)? 1: 0; // Bottom right
        neighborSum += (model.contains(cellID + AutomataModel.MAX_WIDTH))? 1: 0; // Bottom
        neighborSum += (model.contains(cellID + AutomataModel.MAX_WIDTH -1) && lCol < col)? 1: 0; // Bottom left
        neighborSum += (model.contains(cellID -1) && lCol < col)? 1: 0; // Left
        neighborSum += (model.contains(cellID -AutomataModel.MAX_WIDTH - 1) && lCol < col)? 1: 0; // Top left

        return neighborSum;
    }

    private static boolean[] ruleBoolean(int ruleNum){
        boolean[] ruleBool = new boolean[8];
        int num = ruleNum;
        for(int i= 7; i >= 0; i--){
            ruleBool[i] = (num % 2) == 1;
            num = num / 2;
        }
        return ruleBool;
    }

    private static boolean doRules1D(boolean[] ruleBool, ArrayList<Boolean> neighbors) throws IllegalArgumentException {
        if(neighbors.size() != 3){ throw new IllegalArgumentException("Neighbor array improperly shaped"); }
        boolean left = neighbors.get(0);
        boolean top = neighbors.get(1);
        boolean right = neighbors.get(2);
        return ((ruleBool[0] && (left && top && right)) ||
                (ruleBool[1] && (left && top && !right)) ||
                (ruleBool[2] && (left && !top && right)) ||
                (ruleBool[3] && (left && !top && !right)) ||
                (ruleBool[4] && (!left && top && right)) ||
                (ruleBool[5] && (!left && top && !right)) ||
                (ruleBool[6] && (!left && !top && right)) ||
                (ruleBool[7] && (!left && !top && !right)));
    }

    // This will NOT create Conway's Game of Life, which has different rules for alive and dead cells
    // These rules are totalistic, so we will not define different rules for different cells
    private static boolean doRules2D(boolean[] ruleBool, int neighborSum) throws IllegalArgumentException {
        if(neighborSum < 0 || neighborSum > 8) { throw new IllegalArgumentException("Neighbors improperly calculated"); }
        return ((ruleBool[0] && (neighborSum == 0)) ||
                (ruleBool[1] && (neighborSum == 1)) ||
                (ruleBool[2] && (neighborSum == 2)) ||
                (ruleBool[3] && (neighborSum == 3)) ||
                (ruleBool[4] && (neighborSum == 4)) ||
                (ruleBool[5] && (neighborSum == 5)) ||
                (ruleBool[6] && (neighborSum == 6)) ||
                (ruleBool[7] && (neighborSum == 7)));
    }

    private static boolean doRulesConway(int neighborSum, boolean alive) throws IllegalArgumentException {
        return false;
    }
}
