package solver;

import java.util.ArrayList;
import java.util.Arrays;

/*
   Example of a map for reference.
   Legend:
   - # = wall
   - $ = box
   - . = goal
   - * = solved goal (box already in its position)
   - @ = player

#######
#. $. #
##    #
#.  ###
### * #
#@$ $ #
# $ . #
#  ####
#  #
####
 *
 */

/*
 * ACTIONS:
 * actions are only the ff:
 * - l: move left
 * - r: move right
 * - u: move up
 * - d: move down
 */

/*
 * A class to represent a state of the game with auxilliary functions.
 */
public class GameState {
    private char[][] itemsData;
    private char prevAction = '\u0000';
    private double heuristic = -1.0;
    private String validActions = null;
    private int[] playerPos = null;

    private GameState predecessor = null;

    /* Create a state from a given map state */
    public GameState(char[][] itemsData, int[][] goalLocs) {
        this.itemsData = deepCopyItems(itemsData);
        calculateHeuristic(goalLocs);
    }    

    /* Create a state using the current map state and then enact the action*/
    public GameState(char[][] itemsData, char action, int[][] goalLocs) throws Exception {
        this.itemsData = deepCopyItems(itemsData);
        this.prevAction = action;

        // if (!this.getValidActions().contains("" + action))
        //     throw new Exception("Action provided is not valid action for this state!");

        int[] playerPos = this.getPlayerPos();
        int pX = playerPos[0];
        int pY = playerPos[1];

        // Clear old player position
        this.itemsData[pY][pX] = ' ';

        // Left, box | Left
        if (action == 'l' && this.itemsData[pY][pX-1] == '$') {
            this.itemsData[pY][pX-2] = '$';
            this.itemsData[pY][pX-1] = '@';
        } else if (action == 'l') {
            this.itemsData[pY][pX-1] = '@';
        }
        // Right, box | Right
        else if (action == 'r' && this.itemsData[pY][pX+1] == '$') {
            this.itemsData[pY][pX+2] = '$';
            this.itemsData[pY][pX+1] = '@';
        } else if (action == 'r') {
            this.itemsData[pY][pX+1] = '@';
        }
        // Up, box | Up
        else if (action == 'u' && this.itemsData[pY-1][pX] == '$') {
            this.itemsData[pY-2][pX] = '$';
            this.itemsData[pY-1][pX] = '@';
        } else if (action == 'u') {
            this.itemsData[pY-1][pX] = '@';
        }
        // Down, box | Down
        else if (action == 'd' && this.itemsData[pY+1][pX] == '$') {
            this.itemsData[pY+2][pX] = '$';
            this.itemsData[pY+1][pX] = '@';
        } else if (action == 'd') {
            this.itemsData[pY+1][pX] = '@';
        }

        calculateHeuristic(goalLocs);
    }    

    public GameState(GameState prevGameState, char action, int[][] goalLocs) throws Exception {
        this(prevGameState.itemsData, action, goalLocs);
    }

    /* Returns an independent copy of a char[][] so callers can safely mutate
     * one state's items without affecting another. */
    private static char[][] deepCopyItems(char[][] src) {
        char[][] dst = new char[src.length][];
        for (int i = 0; i < src.length; i++) {
            dst[i] = new char[src[i].length];
            System.arraycopy(src[i], 0, dst[i], 0, src[i].length);
        }
        return dst;
    }

    public char[][] getItemsData() { return this.itemsData; }

    /* Returns zero-indexed player position as an array (x,y)*/
    public int[] getPlayerPos() {
        if (this.playerPos != null)
            return this.playerPos;

        int x = 0;
        int y = 0;
        for (char[] row: this.itemsData) { for (char i: row) {
            if (i == '@') {
                int[] ret = {x, y};
                return ret;
            }
            x += 1;
        }
        y += 1;
        x = 0;
        }

        int[] ret = {-1, -1};
        this.playerPos = ret;
        return this.playerPos;
    }

    /* Returns the valid actions in one contiguous string
     * i.e lud, lr, ud
     *
     */
    public String getValidActions(char[][] mapData) {
        if (this.validActions != null)
            return this.validActions;
        validActions = "";

        int[] playerPos = this.getPlayerPos();
        int pX = playerPos[0];
        int pY = playerPos[1];

        int mapLen = (int) mapData[0].length;
        int mapHgt = (int) mapData.length;
        
        // Check left validity
        // Either: left to player is box AND square left of box is not wall AND not box,
        // or:    left to player is not wall AND not box.
        if ( (pX > 1 && itemsData[pY][pX-1] == '$' && mapData[pY][pX-2] != '#' && itemsData[pY][pX-2] != '$') ||
             (pX > 0 && mapData[pY][pX-1] != '#' && itemsData[pY][pX-1] != '$') )
            validActions = validActions.concat("l");
        // Check right validity
        if ( (pX < mapLen-2 && itemsData[pY][pX+1] == '$' && mapData[pY][pX+2] != '#' && itemsData[pY][pX+2] != '$') ||
             (pX < mapLen-1 && mapData[pY][pX+1] != '#' && itemsData[pY][pX+1] != '$') )
            validActions = validActions.concat("r");
        // Check up validity
        if ( (pY > 1 && itemsData[pY-1][pX] == '$' && mapData[pY-2][pX] != '#' && itemsData[pY-2][pX] != '$') ||
             (pY > 0 && mapData[pY-1][pX] != '#' && itemsData[pY-1][pX] != '$') )
            validActions = validActions.concat("u");
        // Check down validity
        if ( (pY < mapHgt-2 && itemsData[pY+1][pX] == '$' && mapData[pY+2][pX] != '#' && itemsData[pY+2][pX] != '$') ||
             (pY < mapHgt-1 && mapData[pY+1][pX] != '#' && itemsData[pY+1][pX] != '$') )
            validActions = validActions.concat("d");

        return validActions;
    }

    public int[][] getBoxLocations() {
        ArrayList<int[]> boxLocsList = new ArrayList<int[]>();

        int rows = this.itemsData.length;
        for (int i = 0; i<rows; i++) {
            int cols = this.itemsData[i].length;
            for (int j = 0; j < cols; j++) {
                if (this.itemsData[i][j] == '$'){
                    int[] loc = {(int) i, (int) j};
                    boxLocsList.add(loc);
                }
            }
        }
        int[][] locsList = new int[boxLocsList.size()][];
        locsList = boxLocsList.toArray(locsList);
        return locsList;
    }

    public char getPrevAction() {
        return this.prevAction;
    }

    public void setPredecessor(GameState g) {
        this.predecessor = g;
    }

    public GameState getPredecessor() {
        return this.predecessor;
    }

    public boolean checkWinState(char[][] mapData, int[][] goalLocs) {
        int[][] boxLocs = this.getBoxLocations();

        for (int[] goalLoc: goalLocs) {
            boolean found = false;
            for (int[] boxLoc: boxLocs) {
                if (Arrays.equals(goalLoc, boxLoc)) 
                    found = true;
            }
            if (!found) 
                return false;
        }
        return true;
    }

    public double getHeuristics() {
        return this.heuristic;
    }

    // The sum of the average distances of each box *NOT* in a goal location
    // to all goal locations without a box in them
    // To use this, get all the possible next states as GameStates and plug
    // them into this method individually
    public double calculateHeuristic(int[][] goalLocs) {
        if (this.heuristic >= 0)
            return this.heuristic;
        this.heuristic = 0.0;

        int[][] boxLocs = this.getBoxLocations();

        ArrayList<int[]> noBoxLocs = new ArrayList<int[]>(Arrays.asList(boxLocs));
        ArrayList<int[]> noGoalLocs = new ArrayList<int[]>(Arrays.asList(goalLocs));
        
        // save which boxes and goals to use for calculations
        for (int[] goalLoc: goalLocs) {
            for (int[] boxLoc: boxLocs) {
                if (Arrays.equals(goalLoc, boxLoc)) {
                    noBoxLocs.remove(boxLoc);
                    noGoalLocs.remove(goalLoc);
                }
            }
        }

        // edge case; already solved.
        if (noBoxLocs.isEmpty())
            return this.heuristic;
        // calculate the avg euclidian distance of each box
        // to each goal
        for (int[] boxLoc: noBoxLocs) {
            double totalDistances = 0.0;
            for (int[] goalLoc: noGoalLocs) {
                int xb = boxLoc[0];
                int yb = boxLoc[1];

                int xg = goalLoc[0];
                int yg = goalLoc[1];

                double x2 = (xg-xb)*(xg-xb);
                double y2 = (yg-yb)*(yg-yb);

                totalDistances += x2 + y2;
            }
            this.heuristic += totalDistances / noGoalLocs.size();
        }
        return this.heuristic;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GameState)) return false;

        GameState gs = (GameState) o;
        return Arrays.deepEquals(this.itemsData, gs.itemsData);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(this.itemsData);
    }
} 
