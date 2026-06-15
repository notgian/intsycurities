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
    // private char[][] itemsData;
    private int[] boxLocations; // flattened array: (y * mapWidth) + x
    private int playerPos; // flattened array: (y * mapWidth) + x
    private int mapWidth;

    // Cached results
    private char prevAction = '\u0000';
    private int heuristic = -1;
    private GameState predecessor = null;

    private String validActions = null;

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
        calculateHeuristic(goalLocs, deadlockContext);
    }

    /* Create a state using the previous state and then enact the action*/
    public GameState(GameState prevState, char action, int[] goalLocs, DeadlockContext deadlockContext) throws Exception {
        if (this.validActions != null && !this.validActions.contains("" + action))
            throw new Exception("Action provided is not valid action for this state!");

        this.mapWidth = prevState.mapWidth;
        this.prevAction = action;

        int prevPlayerX = prevState.playerPos % mapWidth;
        int prevPlayerY = prevState.playerPos / mapWidth;

        int dX = action == 'l' ? -1 : action == 'r' ? 1 : 0;
        int dY = action == 'u' ? -1 : action == 'd' ? 1 : 0;

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
        calculateHeuristic(goalLocs, deadlockContext);
    }

    /* Returns the valid actions in one contiguous string
     * i.e lud, lr, ud
     *
     */
    public String getValidActions(char[][] mapData) {
        if (this.validActions != null)
            return this.validActions;
        validActions = "";

        int pX = this.playerPos % this.mapWidth;
        int pY = this.playerPos / this.mapWidth;

        int mapLen = (int) mapData[0].length;
        int mapHgt = (int) mapData.length;

        // Check left validity
        // Either: left to player is box AND square left of box is not wall AND not box,
        // or:    left to player is not wall AND not box.
        if ( (pX > 1 && hasBoxInTile(pX-1, pY) && mapData[pY][pX-2] != '#' && !hasBoxInTile(pX-2, pY)) ||
             (pX > 0 && mapData[pY][pX-1] != '#' && !hasBoxInTile(pX-1, pY)) )
            validActions = validActions.concat("l");

        // Check right validity
        if ( (pX < mapLen-2 && hasBoxInTile(pX+1, pY) && mapData[pY][pX+2] != '#' && !hasBoxInTile(pX+2, pY)) ||
             (pX < mapLen-1 && mapData[pY][pX+1] != '#' && !hasBoxInTile(pX+1, pY)) )
            validActions = validActions.concat("r");
        // Check up validity
        if ( (pY > 1 && hasBoxInTile(pX, pY-1) && mapData[pY-2][pX] != '#' && !hasBoxInTile(pX, pY-2)) ||
             (pY > 0 && mapData[pY-1][pX] != '#' && !hasBoxInTile(pX, pY-1)) )
            validActions = validActions.concat("u");
        // Check down validity
        if ( (pY < mapHgt-2 && hasBoxInTile(pX, pY+1) && mapData[pY+2][pX] != '#' && !hasBoxInTile(pX, pY+2)) ||
             (pY < mapHgt-1 && mapData[pY+1][pX] != '#' && !hasBoxInTile(pX, pY+1)) )
            validActions = validActions.concat("d");

        return validActions;
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
    
    /* 
     * Calculates the heuristic value and sets the
     * object's internal property
     *
     * */
    public void calculateHeuristic(int[] goalLocs, DeadlockContext deadlockContext) {
        if (this.heuristic != -1) return;
        this.heuristic = 0;

        int boxCount = this.boxLocations.length;
        for (int i = 0; i < boxCount; i++) {
            int boxPos = this.boxLocations[i];

            // Check corner deadlocks
            if (deadlockContext.getCornerDeadlocks().contains(boxPos)) {
                this.heuristic = 9999;
                return;
            }

            // Check horizontal adjacency deadlocks
            if (i < boxCount-1) {
                int nextBoxPos = this.boxLocations[i+1];
                if (nextBoxPos / this.mapWidth == boxPos / this.mapWidth &&
                        deadlockContext.getTwoBoxDeadlocks().contains((boxPos << 16) | (nextBoxPos & 0xFFFF)) ) {
                    this.heuristic = 9999;
                    return;
                }
            }
            
            // Check vertical adjacency deadlocks
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
                    this.heuristic = 9999;
                    return;
                }
            }

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
    public char getPrevAction() { return this.prevAction; }
    public GameState getPredecessor() { return this.predecessor; }
    public void setPredecessor(GameState g) { this.predecessor = g; }
    public int getHeuristics() { return this.heuristic; }

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
