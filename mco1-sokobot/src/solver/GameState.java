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
    private final Character[][] mapData;
    private char[][] itemsData;
    private char[][] prevItemsData = null;
    private Character prevAction = null;
    private byte[][] goalLocs = null;

    private GameState predecessor = null;

    /* Create a state from a given map state */
    public GameState(Character[][] mapData, char[][] itemsData) {
        this.mapData = mapData;
        this.itemsData = deepCopyItems(itemsData);
    }    

    /* Create a state using the current map state and then enact the action*/
    public GameState(Character[][] mapData, char[][] itemsData, char action) throws Exception {
        this.mapData = mapData;
        // Snapshot the parent state separately from the array we will mutate.
        // Without these copies, every GameState created from the same parent
        // would share (and mutate) the same char[][] reference, which makes
        // equals() comparisons across states meaningless.
        this.prevItemsData = deepCopyItems(itemsData);
        this.itemsData = deepCopyItems(itemsData);
        this.prevAction = action;

        if (!this.getValidActions().contains("" + action))
            throw new Exception("Action provided is not valid action for this state!");

        byte[] playerPos = this.getPlayerPos();
        byte pX = playerPos[0];
        byte pY = playerPos[1];

        // Clear old player position so getPlayerPos() returns the new position
        // on subsequent calls (otherwise stale '@' marks accumulate).
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
    }    

    public GameState(GameState prevGameState, char action) throws Exception {
        this(prevGameState.mapData, prevGameState.itemsData, action);
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

    public Character[][] getMapData() { return this.mapData; }
    public char[][] getItemsData() { return this.itemsData; }

    /* Returns zero-indexed player position as an array (x,y)*/
    public byte[] getPlayerPos() {
        byte x = 0;
        byte y = 0;
        for (char[] row: this.itemsData) { for (char i: row) {
            if (i == '@') {
                byte[] ret = {x, y};
                return ret;
            }
            x += 1;
        }
        y += 1;
        x = 0;
        }

        byte[] ret = {-1, -1};
        return ret;
    }

    /* Returns the valid actions in one contiguous string
     * i.e lud, lr, ud
     *
     */
    public String getValidActions() {
        String validActions = "";

        byte[] playerPos = this.getPlayerPos();
        byte pX = playerPos[0];
        byte pY = playerPos[1];

        byte mapLen = (byte) this.mapData[0].length;
        byte mapHgt = (byte) this.mapData.length;
        
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

    public byte[][] getBoxLocations() {
        ArrayList<byte[]> boxLocsList = new ArrayList<byte[]>();

        int rows = this.itemsData.length;
        for (int i = 0; i<rows; i++) {
            int cols = this.itemsData[i].length;
            for (int j = 0; j < cols; j++) {
                if (this.itemsData[i][j] == '$'){
                    byte[] loc = {(byte) i, (byte) j};
                    boxLocsList.add(loc);
                }
            }
        }
        
        byte[][] locsList = new byte[boxLocsList.size()][];
        locsList = boxLocsList.toArray(locsList);
        return locsList;
    }

    public byte[][] getGoalLocs() {
        if (this.goalLocs != null)
            return goalLocs;

        ArrayList<byte[]> goalLocsList = new ArrayList<byte[]>();

        int rows = this.mapData.length;
        for (int i = 0; i<rows; i++) {
            int cols = this.mapData[i].length;
            for (int j = 0; j < cols; j++) {
                if (this.mapData[i][j] == '.'){
                    byte[] loc = {(byte) i, (byte) j};
                    goalLocsList.add(loc);
                }
            }
        }
        
        byte[][] locsList = new byte[goalLocsList.size()][];
        locsList = goalLocsList.toArray(locsList);
        return locsList;
    }

    public void setGoalLocs(byte[][] goalLocs) {
        this.goalLocs = goalLocs;
    }

    public Character getPrevAction() {
        return this.prevAction;
    }

    public char[][] getPrevItemsData() {
        return this.prevItemsData;
    }

    public void setPredecessor(GameState g) {
        this.predecessor = g;
    }

    public GameState getPredecessor() {
        return this.predecessor;
    }

    public boolean checkWinState() {
        byte[][] boxLocs = this.getBoxLocations();
        byte[][] goalLocs = this.getGoalLocs();

        for (byte[] goalLoc: goalLocs) {
            boolean found = false;
            for (byte[] boxLoc: boxLocs) {
                if (Arrays.equals(goalLoc, boxLoc)) 
                    found = true;
            }
            if (!found) 
                return false;
        }
        return true;
    }

    // The sum of the average distances of each box *NOT* in a goal location
    // to all goal locations without a box in them
    // To use this, get all the possible next states as GameStates and plug
    // them into this method individually
    public double calculateHeuristic() {
        byte[][] boxLocs = this.getBoxLocations();
        byte[][] goalLocs = this.getGoalLocs();

        ArrayList<byte[]> noBoxLocs = new ArrayList<byte[]>();
        ArrayList<byte[]> noGoalLocs = new ArrayList<byte[]>();
        
        // save which boxes and goals to use for calculations
        for (byte[] goalLoc: goalLocs) {
            for (byte[] boxLoc: boxLocs) {
                if (Arrays.equals(goalLoc, boxLoc))
                    continue;

                noBoxLocs.add(boxLoc);
                noGoalLocs.add(goalLoc);
            }
        }

        double heuristic = 0.0;
        // edge case; already solved.
        if (noBoxLocs.isEmpty())
            return heuristic;

        // calculate the avg euclidian distance of each box
        // to each goal
        for (byte[] boxLoc: noBoxLocs) {
            double totalDistances = 0.0;
            for (byte[] goalLoc: noGoalLocs) {
                byte xb = boxLoc[0];
                byte yb = boxLoc[1];

                byte xg = goalLoc[0];
                byte yg = goalLoc[1];

                double x2 = Math.pow((xg-xb), 2);
                double y2 = Math.pow((yg-yb), 2);

                totalDistances += Math.sqrt(x2 + y2);
            }
            heuristic += totalDistances / noGoalLocs.size();
        }
        return heuristic;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GameState)) return false;

        GameState gs = (GameState) o;

        // Two states are equal iff their itemsData arrays are element-wise equal.
        // Arrays.deepEquals handles null, length mismatches, and per-row lengths
        // correctly (which the previous hand-rolled loop did not -- it used the
        // row count as the column count, ignoring rightmost columns on wider
        // maps and risking AIOOBE on taller maps).
        return Arrays.deepEquals(this.itemsData, gs.itemsData);
    }

    @Override
    public int hashCode() {
        // Must be consistent with equals(): states equal under itemsData must
        // produce the same hash. Required for HashSet/HashMap lookups.
        return Arrays.deepHashCode(this.itemsData);
    }
} 
