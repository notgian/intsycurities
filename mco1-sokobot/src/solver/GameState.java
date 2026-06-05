package solver;
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

    /* Create a state from a given map state */
    public GameState(Character[][] mapData, char[][] itemsData) {
        this.mapData = mapData;
        this.itemsData = itemsData;
    }    

    /* Create a state using the current map state and then enact the action*/
    public GameState(Character[][] mapData, char[][] itemsData, char action) throws Exception {
        this.mapData = mapData;
        this.itemsData = itemsData;

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
} 
