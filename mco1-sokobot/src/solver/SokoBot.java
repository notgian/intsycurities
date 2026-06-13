package solver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.PriorityQueue;
import java.util.HashSet;

public class SokoBot {

    public String solveSokobanPuzzle(int width, int height, char[][] mapData, char[][] itemsData) {
        try {
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

            GameState initialState = new GameState(mapDataRef, itemsData);
            byte[][] goalLocs = initialState.getGoalLocs();

            PriorityQueue<GameState> frontier = new PriorityQueue<>(new HeuristicsComparator());
            boolean solutionFound = false;
            frontier.add(initialState);

            // ArrayList<GameState> explored = new ArrayList<GameState>();
            HashSet<GameState> explored = new HashSet<GameState>();
            GameState lastExplored = null;
            String actions = "";

            while (!frontier.isEmpty() && !solutionFound) {
                // deque fronteir
                // get valid actions
                // add all valid actions to the frontier 
                // repeat
                
                GameState exploredState = frontier.poll();
                explored.add(exploredState);
                lastExplored = exploredState; 
                if (exploredState.getPrevAction() != null)
                    actions = actions.concat("" + exploredState.getPrevAction());
                
                solutionFound = exploredState.checkWinState();
                if (solutionFound) continue;

                String validActions = exploredState.getValidActions();
                for (int i = 0; i < validActions.length(); i++) {
                    GameState nextState = new GameState(exploredState.getMapData(), exploredState.getItemsData(), validActions.charAt(i));
                    nextState.setPredecessor(exploredState);
                    if (!explored.contains(nextState))
                        frontier.add(nextState);
                }


            }
            
            GameState curr = lastExplored;
            actions = "";
            
            boolean done = false;
            
            while (!done) {
                Character prevAction = curr.getPrevAction();
                if (prevAction != null)
                    actions = prevAction + actions;
                curr = curr.getPredecessor();
                if (curr == null) {
                    done = true;
                    continue;
                }
            }
            return actions;
        }
        catch (Exception e) {
            System.out.println("Smth shit itself");
            e.printStackTrace();
        }
        return "";
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
}
