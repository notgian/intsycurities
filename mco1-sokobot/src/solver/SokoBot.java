package solver;

import java.util.ArrayList;
import java.util.Arrays;

public class SokoBot {

    public String solveSokobanPuzzle(int width, int height, char[][] mapData, char[][] itemsData) {
        // Construct a version of the map data using a Ref Type
        // This should help reduce the redundancy of having exact copies of the 
        // map data in different parts of the memory
        
        Character[][] mapDataRef = new Character[mapData.length][];
        for (int i = 0; i < mapData.length; i++) {
            int cols = mapData[i].length;
            mapDataRef[i] = new Character[cols];
            for (int j = 0; j < cols; j++) {
                mapDataRef[i][j] = mapData[i][j];
            }
        }

        byte[][] locs = this.getGoalLocs(mapDataRef);
        for (byte[] row: locs) {
            System.out.println("%d, %d".formatted(row[0], row[1]));
        }

        try {
            GameState initialState = new GameState(mapDataRef, itemsData);

            GameState currState = initialState;
            int x = 0;

            String allActions = "";

            while (x <= 30) {
                String actions = currState.getValidActions();
                int i = (int) Math.floor(Math.random() * actions.length());
                
                // System.out.println(currState.getItemsData().toString());

                char nextAction = actions.substring(i, i+1).charAt(0);
                allActions = allActions.concat("" + nextAction);
                currState = new GameState(currState.getMapData(), currState.getItemsData(), nextAction);

                System.out.println("%s - %.2f".formatted(nextAction, this.calculateHeuristic(currState, locs)));
                x++;
            } 

            System.out.println(allActions);

            return allActions;
        }
        catch (Exception e) {
            System.out.println("Smth shit itself");
            System.out.println(e.getMessage());
        }

        // Saving the old stupid code below, but returning null in case 
        // return null;

        /*
         * Default stupid behavior: Think (sleep) for 3 seconds, and then return a
         * sequence
         * that just moves left and right repeatedly.
         */
        // try {
        //     Thread.sleep(3000);
        // } catch (Exception ex) {
        //     ex.printStackTrace();
        // }
        return "lrudlrudlrudlrudlrudlrudlrudlrudlrudlrudlrudlrudlrudlrudlrudlrudlrudlrudlrudlrud";
        // return "lrlrlrlrlrlrlrlrlrlrlrlrlrlrlrlrlrlrlrlrlrlrlrlrlrlrlrlrlrlrlrlrlrlrlrlrlrlr";
    }

    private byte[][] getGoalLocs(Character[][] mapData) {
        ArrayList<byte[]> goalLocsList = new ArrayList<byte[]>();

        int rows = mapData.length;
        for (int i = 0; i<rows; i++) {
            int cols = mapData[i].length;
            for (int j = 0; j < cols; j++) {
                if (mapData[i][j] == '.'){
                    byte[] loc = {(byte) i, (byte) j};
                    goalLocsList.add(loc);
                }
            }
        }
        
        byte[][] locsList = new byte[goalLocsList.size()][];
        locsList = goalLocsList.toArray(locsList);
        return locsList;
    }

    private boolean isGoalState(GameState state, byte[][] goalLocs) {
        int rows = goalLocs.length;
        char[][] mapItems = state.getItemsData();
        for (int i = 0; i < rows; i++) {
            int cols = goalLocs[i].length;
            for (int j = 0; j < cols; j++) {
                if (mapItems[i][j] != '$') {
                    return false;
                }
            }
        }

        return true;
    }
    
    // The sum of the average distances of each box *NOT* in a goal location
    // to all goal locations without a box in them
    // To use this, get all the possible next states as GameStates and plug
    // them into this method individually
    private double calculateHeuristic(GameState state, byte[][] goalLocs) {
        byte[][] boxLocs = state.getBoxLocations();

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
}
