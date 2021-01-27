package domain;

import domain.automata_model.AutomataModel;
import java.util.ArrayList;


// TODO Javadoc pls
public class GenerationRule {
    public static void generateForRules(AutomataModel model, int ruleNum) throws IllegalArgumentException {
        if(model == null){ throw new IllegalArgumentException("Model is null"); }
        if(ruleNum < 0 || ruleNum > model.getAutomataType().getRuleNumMax()){ throw new IllegalArgumentException("Improper rule selected"); }
        boolean[] nextGen;
        boolean[] ruleBitset = ruleBitset(ruleNum, model.getAutomataType());
        switch(model.getAutomataType()){
            case TimeSeries1D:
                nextGen = new boolean[AutomataModel.MAX_WIDTH];
                for(int i=0; i<AutomataModel.MAX_WIDTH; i++){
                    ArrayList<Boolean> neighbors = getNeighbors1D(model, i);
                    nextGen[i] = doRules1D(ruleBitset, neighbors);
                }
                for(int i=0; i<AutomataModel.MAX_WIDTH; i++){
                    if (nextGen[i]) {
                        model.addCell(i);
                    } else {
                        model.removeCell(i);
                    }
                }
                break;
            case TimeSeries2DFourNeighbor:
                nextGen = new boolean[AutomataModel.MAX_WIDTH*AutomataModel.MAX_HEIGHT];
                for(int i=0; i<AutomataModel.MAX_WIDTH*AutomataModel.MAX_HEIGHT; i++){
                    int neighborSum = getFourNeighbors2D(model, i);
                    nextGen[i] = doRules2DFourNeighbor(ruleBitset, neighborSum, model.contains(i));
                }
                for(int i=0; i < AutomataModel.MAX_WIDTH*AutomataModel.MAX_HEIGHT; i++){
                    if(nextGen[i]){
                        model.addCell(i);
                    } else {
                        model.removeCell(i);
                    }
                }
                break;
            case TimeSeries2DEightNeighbor:
                nextGen = new boolean[AutomataModel.MAX_WIDTH*AutomataModel.MAX_HEIGHT];
                for(int i=0; i<AutomataModel.MAX_WIDTH*AutomataModel.MAX_HEIGHT; i++){
                    int neighborSum = getEightNeighbors2D(model, i);
                    nextGen[i] = doRules2DEightNeighbor(ruleBitset, neighborSum, model.contains(i));
                }
                for(int i=0; i < AutomataModel.MAX_WIDTH*AutomataModel.MAX_HEIGHT; i++){
                    if(nextGen[i]){
                        model.addCell(i);
                    } else {
                        model.removeCell(i);
                    }
                }
                break;
            case GameOfLife:
                nextGen = new boolean[AutomataModel.MAX_WIDTH*AutomataModel.MAX_HEIGHT];
                for(int i=0; i<AutomataModel.MAX_WIDTH*AutomataModel.MAX_HEIGHT; i++){
                    int neighborSum = getEightNeighbors2D(model, i);
                    nextGen[i] = doRulesConway(neighborSum, model.contains(i));
                }
                for(int i=0; i < AutomataModel.MAX_WIDTH*AutomataModel.MAX_HEIGHT; i++){
                    if(nextGen[i]){
                        model.addCell(i);
                    } else {
                        model.removeCell(i);
                    }
                }
                break;
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

    private static int getFourNeighbors2D(AutomataModel model, int cellID){
        int neighborSum = 0;
        int col = cellID % AutomataModel.MAX_WIDTH;
        int lCol = (cellID - 1)%AutomataModel.MAX_WIDTH;
        int rCol = (cellID + 1)%AutomataModel.MAX_WIDTH;

        neighborSum += (model.contains(cellID - AutomataModel.MAX_WIDTH)) ? 1: 0; // Top
        neighborSum += (model.contains(cellID + 1) && rCol > col)? 1: 0; // Right
        neighborSum += (model.contains(cellID + AutomataModel.MAX_WIDTH))? 1: 0; // Bottom
        neighborSum += (model.contains(cellID -1) && lCol < col)? 1: 0; // Left

        return neighborSum;
    }

    private static int getEightNeighbors2D(AutomataModel model, int cellID){
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

    private static boolean[] ruleBitset(int ruleNum, AutomataType type){
        if(type == null) { throw new IllegalArgumentException("Invalid Automata Type"); }
        boolean[] rules = new boolean[type.getRulesetSize()];
        for(int i= type.getRulesetSize()-1; i >= 0; i--){
            rules[i] = ((ruleNum % 2) == 1);
            ruleNum = ruleNum >>> 1;
        }
        return rules;
    }

    /**
     * Returns true if cell should be alive next generation, false otherwise
     * @param neighbors
     * @return true if cell should be alive next generation, false otherwise
     * @throws IllegalArgumentException
     */
    private static boolean doRules1D(boolean[] rules, ArrayList<Boolean> neighbors) throws IllegalArgumentException {
        if(rules.length != (AutomataType.TimeSeries1D.getRulesetSize())){ throw new IllegalArgumentException("Improper Rules For 1D Time Series"); }
        if(neighbors.size() != 3){ throw new IllegalArgumentException("Neighbor array improperly shaped"); }
        boolean left = neighbors.get(0);
        boolean top = neighbors.get(1);
        boolean right = neighbors.get(2);
        return ((rules[0] && (left && top && right)) ||
                (rules[1] && (left && top && !right)) ||
                (rules[2] && (left && !top && right)) ||
                (rules[3] && (left && !top && !right)) ||
                (rules[4] && (!left && top && right)) ||
                (rules[5] && (!left && top && !right)) ||
                (rules[6] && (!left && !top && right)) ||
                (rules[7] && (!left && !top && !right)));
    }

    /**
     * Returns true if cell should be alive next generation, false otherwise
     * @param neighborSum
     * @param alive
     * @return true if cell should be alive next generation, false otherwise
     * @throws IllegalArgumentException
     */
    private static boolean doRules2DFourNeighbor(boolean[] rules, int neighborSum, boolean alive) throws IllegalArgumentException {
        if(rules.length != (AutomataType.TimeSeries2DFourNeighbor.getRulesetSize())){ throw new IllegalArgumentException("Improper Rules For 2D Time Series Four Neighbor"); }
        if(neighborSum < 0 || neighborSum > 4) { throw new IllegalArgumentException("Neighbors improperly calculated"); }
        if(alive){
            return ((rules[0] && neighborSum == 4) ||
                    (rules[2] && neighborSum == 3) ||
                    (rules[4] && neighborSum == 2) ||
                    (rules[6] && neighborSum == 1) ||
                    (rules[8] && neighborSum == 0));
        } else {
            return ((rules[1] && neighborSum == 4) ||
                    (rules[3] && neighborSum == 3) ||
                    (rules[5] && neighborSum == 2) ||
                    (rules[7] && neighborSum == 1) ||
                    (rules[9] && neighborSum == 0));
        }
    }

    /**
     *
     * @param rules
     * @param neighborSum
     * @param alive
     * @return
     * @throws IllegalArgumentException
     */
    private static boolean doRules2DEightNeighbor(boolean[] rules, int neighborSum, boolean alive) throws IllegalArgumentException {
        if(rules.length != (AutomataType.TimeSeries2DEightNeighbor.getRulesetSize())){ throw new IllegalArgumentException("Improper Rules For 2D Time Series Eight Neighbor"); }
        if(neighborSum < 0 || neighborSum > 8) { throw new IllegalArgumentException("Neighbors improperly calculated"); }
        if(alive){
            return ((rules[0] && neighborSum == 8)   ||
                    (rules[2] && neighborSum == 7)   ||
                    (rules[4] && neighborSum == 6)   ||
                    (rules[6] && neighborSum == 5)   ||
                    (rules[8] && neighborSum == 4)  ||
                    (rules[10] && neighborSum == 3)  ||
                    (rules[12] && neighborSum == 2)  ||
                    (rules[14] && neighborSum == 1)  ||
                    (rules[16] && neighborSum == 0));
        } else {
            return ((rules[1] && neighborSum == 8)   ||
                    (rules[3] && neighborSum == 7)   ||
                    (rules[5] && neighborSum == 6)   ||
                    (rules[7] && neighborSum == 5)   ||
                    (rules[9] && neighborSum == 4)   ||
                    (rules[11] && neighborSum == 3)  ||
                    (rules[13] && neighborSum == 2)  ||
                    (rules[15] && neighborSum == 1)  ||
                    (rules[17] && neighborSum == 0));
        }
    }

    private static boolean doRulesConway(int neighborSum, boolean alive) throws IllegalArgumentException {
        return doRules2DEightNeighbor(ruleBitset(224, AutomataType.GameOfLife), neighborSum, alive);
    }
}
