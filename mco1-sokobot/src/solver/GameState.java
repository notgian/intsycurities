package solver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

/*
   Legend:
   - # = wall
   - $ = box
   - . = goal
   - * = solved goal (box already in its position)
   - @ = player

   ACTIONS:
   actions are only the ff:
   - l: move left
   - r: move right
   - u: move up
   - d: move down
*/

/*
 * A class to represent a state of the game with auxilliary functions.
 */
public class GameState {
    private int[] boxLocations; // flattened array: (y * mapWidth) + x
    private int playerPos; // flattened array: (y * mapWidth) + x
    private int mapWidth;

    // Cached results
    private int heuristic = -1;
    private GameState predecessor = null;
    private boolean isDeadlocked = false;

    private int prevAction = -1;
    private int validActions = -1;

    public static final int MOVE_LEFT  = 0b1000;
    public static final int MOVE_RIGHT = 0b0100;
    public static final int MOVE_UP    = 0b0010;
    public static final int MOVE_DOWN  = 0b0001;

    /* Create a state from a given map state */
    public GameState(char[][] itemsData, int[] goalLocs, DeadlockContext deadlockContext) {
        this.mapWidth = itemsData[0].length;

        ArrayList<Integer> boxes = new ArrayList<>();
        int pPos = 0;

        for (int i = 0; i < itemsData.length; i++) {
            for (int j = 0; j < itemsData[i].length; j++) {
                int flattenedPos = (i * mapWidth) + j;
                if (itemsData[i][j] == '$' || itemsData[i][j] == '*') {
                    boxes.add(flattenedPos);
                } else if (itemsData[i][j] == '@') {
                    pPos = flattenedPos;
                }
            }
        }

        this.playerPos = pPos;
        this.boxLocations = boxes.stream().mapToInt(i -> i).toArray();

        Arrays.sort(this.boxLocations);
    }

    /* Create a state using the previous state and then enact the action*/
    public GameState(GameState prevState, int action, int[] goalLocs, DeadlockContext deadlockContext) throws Exception {
        if (this.validActions != -1 && !((this.validActions & action) == action))
            throw new Exception("Action provided is not valid action for this state!");
        this.mapWidth = prevState.mapWidth;
        this.prevAction = action;

        int prevPlayerX = prevState.playerPos % mapWidth;
        int prevPlayerY = prevState.playerPos / mapWidth;

        int dX = action == GameState.MOVE_LEFT ? -1 : action == GameState.MOVE_RIGHT ? 1 : 0;
        int dY = action == GameState.MOVE_UP ? -1 : action == GameState.MOVE_DOWN ? 1 : 0;

        int newPlayerX = prevPlayerX + dX;
        int newPlayerY = prevPlayerY + dY;
        this.playerPos = (newPlayerY * mapWidth) + newPlayerX;
        this.boxLocations = new int[prevState.boxLocations.length];

        for (int i = 0; i < prevState.boxLocations.length; i++) {
            int boxPos = prevState.boxLocations[i];
            if (boxPos == this.playerPos) {
                // Move this box forward
                this.boxLocations[i] = ((newPlayerY + dY) * mapWidth) + (newPlayerX + dX);
            } else {
                this.boxLocations[i] = boxPos;
            }
        }

        Arrays.sort(this.boxLocations);
    }

    /* Returns the valid actions in one contiguous string
     * i.e lud, lr, ud
     *
     */
    public int getValidActions(char[][] mapData) {
        if (this.validActions != -1)
            return this.validActions;
        validActions = 0;

        int pX = this.playerPos % this.mapWidth;
        int pY = this.playerPos / this.mapWidth;
        int mapLen = (int) mapData[0].length;
        int mapHgt = (int) mapData.length;
        // Check left validity
        // Either: left to player is box AND square left of box is not wall AND not box,
        // or:    left to player is not wall AND not box.
        if ( (pX > 1 && hasBoxInTile(pX-1, pY) && mapData[pY][pX-2] != '#' && !hasBoxInTile(pX-2, pY)) ||
             (pX > 0 && mapData[pY][pX-1] != '#' && !hasBoxInTile(pX-1, pY)) )
            this.validActions |= GameState.MOVE_LEFT;
        // Check right validity
        if ( (pX < mapLen-2 && hasBoxInTile(pX+1, pY) && mapData[pY][pX+2] != '#' && !hasBoxInTile(pX+2, pY)) ||
             (pX < mapLen-1 && mapData[pY][pX+1] != '#' && !hasBoxInTile(pX+1, pY)) )
            this.validActions |= GameState.MOVE_RIGHT;
        // Check up validity
        if ( (pY > 1 && hasBoxInTile(pX, pY-1) && mapData[pY-2][pX] != '#' && !hasBoxInTile(pX, pY-2)) ||
             (pY > 0 && mapData[pY-1][pX] != '#' && !hasBoxInTile(pX, pY-1)) )
            this.validActions |= GameState.MOVE_UP;
        // Check down validity
        if ( (pY < mapHgt-2 && hasBoxInTile(pX, pY+1) && mapData[pY+2][pX] != '#' && !hasBoxInTile(pX, pY+2)) ||
             (pY < mapHgt-1 && mapData[pY+1][pX] != '#' && !hasBoxInTile(pX, pY+1)) )
            this.validActions |= GameState.MOVE_DOWN;
        return this.validActions;
    }
    
    /*
     * Checks if all boxes are in the goals 
     */
    public boolean checkWinState(char[][] mapData, int[] goalLocs) {
        for (int goalLoc: goalLocs) {
            boolean found = false;
            for (int boxLoc: this.boxLocations) {
                if (boxLoc == goalLoc) 
                    found = true;
            }
            if (!found) 
                return false;
        }
        return true;
    }


    public boolean checkDeadlock(int[] goalLocs, DeadlockContext deadlockContext) {
        int boxCount = this.boxLocations.length;
        for (int i = 0; i < boxCount; i++) {
            int boxPos = this.boxLocations[i];
            // Check corner deadlocks
            if (deadlockContext.getCornerDeadlocks().contains(boxPos)) {
                this.isDeadlocked = true;
                return this.isDeadlocked;
            }
            // Check 2-box horizontal adjacency deadlocks
            if (i < boxCount-1) {
                int nextBoxPos = this.boxLocations[i+1];
                if (nextBoxPos / this.mapWidth == boxPos / this.mapWidth &&
                        deadlockContext.getTwoBoxDeadlocks().contains((boxPos << 16) | (nextBoxPos & 0xFFFF)) ) {
                    this.isDeadlocked = true;
                    return this.isDeadlocked;
                }
            }
            // Check 2-box vertical adjacency deadlocks
            if (i < boxCount-1) {
                int nextBoxPos = -1;
                int boxCol = boxPos % this.mapWidth;
                for (int j = i+1; j < boxCount; j++) {
                    int currBox = this.boxLocations[j];
                    if (boxCol == currBox % this.mapWidth) {
                        nextBoxPos = currBox;
                        j = boxCount;
                    }
                }
                if (nextBoxPos != -1 && deadlockContext.getTwoBoxDeadlocks().contains((boxPos << 16) | (nextBoxPos & 0xFFFF)) ) {
                    this.isDeadlocked = true;
                    return this.isDeadlocked;
                }
            }

            // Check 4-box deadlocks
            if (i < boxCount-3) {
                int box2 = this.boxLocations[i+1];
                int box2Col = boxPos % this.mapWidth;
                int boxCol = boxPos % this.mapWidth;
                int boxRow = boxPos / this.mapWidth;
                // skip if next box is not adjacent and
                // box is not on the last column
                if (box2 != boxPos+1 && boxCol == this.mapWidth-1)
                    continue;
                int box3 = -1;
                int box4 = -1;
                for (int j = i+2; j < boxCount; j++) {
                    int currBox = this.boxLocations[j];
                    int currBoxCol = currBox % this.mapWidth;
                    int currBoxRow = currBox / this.mapWidth;
                    if (currBoxRow > boxRow+1 || (box3 > -1 && box4 > -1)) {
                        j = boxCount;
                        continue;
                    }
                    if (currBoxCol == boxCol && currBoxRow == boxRow+1) {
                        box3 = currBox;
                    }
                    else if (currBoxCol == box2Col && currBoxRow == boxRow+1) {
                        box4 = currBox;
                    }
                }
                
                if (box3 > -1 && box4 > -1) {
                    long pos1 = (boxPos & 0xFFFFl) << 48;
                    long pos2 = (box2 & 0xFFFFl) << 32;
                    long pos3 = (box3 & 0xFFFFl) << 16;
                    long pos4 = (box4 & 0xFFFFl);
                    long packedPos = pos1 | pos2 | pos3 | pos4;
                    if (deadlockContext.getFourBoxDeadlocks().contains(packedPos)) {
                        this.isDeadlocked = true;
                        return this.isDeadlocked;
                    }
                }
            }
        }
        this.isDeadlocked = false;
        return this.isDeadlocked;
    }
    
    /* 
     * Calculates the heuristic value and sets the
     * object's internal property
     *
     * */
    public void calculateHeuristic(int[] goalLocs, DeadlockContext deadlockContext) {
        if (this.heuristic != -1) 
            return;
        if (this.isDeadlocked) {
            this.heuristic = 99999999;
            return;
        }
        this.heuristic = 0;
        int boxCount = this.boxLocations.length;
        for (int i = 0; i < boxCount; i++) {
            int boxPos = this.boxLocations[i];

            int xb = boxPos % mapWidth;
            int yb = boxPos / mapWidth;
            int minDistance = Integer.MAX_VALUE;
            for (int goalPos : goalLocs) {
                int xg = goalPos % mapWidth;
                int yg = goalPos / mapWidth;
                int distance = Math.abs(xg-xb) + Math.abs(yg-yb);
                if (distance < minDistance) {
                    minDistance = distance;
                }
            }
            this.heuristic += minDistance;
        }
    }

    public boolean hasBoxInTile(int x, int y) {
        return Arrays.binarySearch(this.boxLocations, (y * this.mapWidth) + x ) >= 0;
    }

    public boolean hasBoxInTile(int pos1D) {
        return Arrays.binarySearch(this.boxLocations, pos1D) >= 0;
    }

    // Ordinary getters and setters
    public int getPlayerPos() { return this.playerPos; }
    public int[] getBoxLocations() { return this.boxLocations; }
    public int getPrevAction() { return this.prevAction; }
    public GameState getPredecessor() { return this.predecessor; }
    public void setPredecessor(GameState g) { this.predecessor = g; }
    public int getHeuristics() { return this.heuristic; }
    public boolean isDeadlocked() { return this.isDeadlocked; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GameState)) return false;
        GameState gs = (GameState) o;
        return this.playerPos == gs.playerPos && Arrays.equals(this.boxLocations, gs.boxLocations);
    }

    @Override
    public int hashCode() {
        int result = Arrays.hashCode(this.boxLocations);
        return 31 * result + this.playerPos;
    }
} 
