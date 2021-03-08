package domain;

import domain.automata_model.AutomataModel;

public enum InitialCondition {
    /** GENERAL **/
    SINGLE_CELL("Single Cell"),
    ROW_OF_THREE("Row of Three"),
    ROW_OF_FIVE("Row of Five"),
    RANDOM("Random"),

    /** CONWAY'S GAME OF LIFE **/
    GLIDER_GUN_GOSPER("Glider Gun (Gosper)"),
    GLIDER_GUN_SIMKIN("Glider Gun (Simkin)"),
    R_PENTONIMO("R Pentonimo"),
    DIEHARD("Diehard"),
    ACORN("Acorn");

    private String displayName;

    InitialCondition(String displayName){
        this.displayName = displayName;
    }

    public String getDisplayName(){
        return this.displayName;
    }

    public boolean[][] getInitialCondition(AutomataModel forModel){
        boolean[][] automata = new boolean[forModel.getUniverseHeight()+2][forModel.getUniverseWidth()+2];
        int midWidth = (forModel.getUniverseWidth()+2)/2;
        int midHeight = forModel.getAutomataType() == AutomataType.TimeSeries1D ?
                1 : (forModel.getUniverseHeight()+2)/2;
        switch(forModel.getInitialCondition()){
            case SINGLE_CELL:
                automata[midHeight][midWidth] = true;
                break;
            case ROW_OF_THREE:
                automata[midHeight][midWidth - 1] = true;
                automata[midHeight][midWidth] = true;
                automata[midHeight][midWidth + 1] = true;
                break;
            case ROW_OF_FIVE:
                automata[midHeight][midWidth - 2] = true;
                automata[midHeight][midWidth - 1] = true;
                automata[midHeight][midWidth] = true;
                automata[midHeight][midWidth + 1] = true;
                automata[midHeight][midWidth + 2] = true;
                break;
            case GLIDER_GUN_GOSPER:

                // Central structure
                automata[midHeight][midWidth] = true;
                automata[midHeight][midWidth + 1] = true;
                automata[midHeight - 1][midWidth] = true;
                automata[midHeight + 1][midWidth] = true;
                automata[midHeight - 2][midWidth-1] = true;
                automata[midHeight + 2][midWidth-1] = true;
                automata[midHeight][midWidth - 2] = true;

                // Left arc
                automata[midHeight][midWidth - 6] = true;
                automata[midHeight-1][midWidth - 6] = true;
                automata[midHeight+1][midWidth - 6] = true;
                automata[midHeight-2][midWidth - 5] = true;
                automata[midHeight+2][midWidth - 5] = true;
                automata[midHeight-3][midWidth - 4] = true;
                automata[midHeight+3][midWidth - 4] = true;
                automata[midHeight-3][midWidth - 3] = true;
                automata[midHeight+3][midWidth - 3] = true;

                // Left endpoint
                automata[midHeight][midWidth - 16] = true;
                automata[midHeight][midWidth - 15] = true;
                automata[midHeight-1][midWidth - 16] = true;
                automata[midHeight-1][midWidth - 15] = true;

                // Right structure
                automata[midHeight-3][midWidth + 4] = true;
                automata[midHeight-3][midWidth + 5] = true;
                automata[midHeight-2][midWidth + 4] = true;
                automata[midHeight-2][midWidth + 5] = true;
                automata[midHeight-1][midWidth + 4] = true;
                automata[midHeight-1][midWidth + 5] = true;
                automata[midHeight-4][midWidth + 6] = true;
                automata[midHeight-4][midWidth + 8] = true;
                automata[midHeight-5][midWidth + 8] = true;
                automata[midHeight][midWidth   + 6] = true;
                automata[midHeight][midWidth   + 8] = true;
                automata[midHeight+1][midWidth + 8] = true;

                // Right endpoint
                automata[midHeight-2][midWidth + 18] = true;
                automata[midHeight-2][midWidth + 19] = true;
                automata[midHeight-3][midWidth + 18] = true;
                automata[midHeight-3][midWidth + 19] = true;
                break;
            case GLIDER_GUN_SIMKIN:
                break;
            case R_PENTONIMO:
                break;
            case DIEHARD:
                break;
            case ACORN:
                break;
        }
        return automata;
    }
}
