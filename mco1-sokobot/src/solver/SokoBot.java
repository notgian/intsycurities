package solver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.PriorityQueue;
import java.util.HashSet;

public class SokoBot {
    public String solveSokobanPuzzle(int width, int height, char[][] mapData, char[][] itemsData) {
        try {
            int[][] goalLocs = this.getGoalLocs(mapData);
            GameState initialState = new GameState(itemsData, goalLocs);

            PriorityQueue<GameState> frontier = new PriorityQueue<>(new HeuristicsComparator());
            boolean solutionFound = false;
            frontier.add(initialState);

            // ArrayList<GameState> explored = new ArrayList<GameState>();
            HashSet<GameState> explored = new HashSet<GameState>();
            GameState lastExplored = null;
            String actions = "";
            
            double exploreTimeCum = 0.0;
            double checkWinTimeCum = 0.0;
            double getActionsTimeCum = 0.0;

            // For logging only. TODO: REMOVE
            int skip = 10000;

            while (!frontier.isEmpty() && !solutionFound) {
                // deque fronteir
                // get valid actions
                // add all valid actions to the frontier 
                // repeat
                
                double startTime;
               
                startTime = System.nanoTime();
                GameState exploredState;
                do {
                    exploredState = frontier.poll();
                } while (explored.contains(exploredState));
                
                explored.add(exploredState);
                exploreTimeCum += System.nanoTime() - startTime;

                lastExplored = exploredState; 
                if (exploredState.getPrevAction() != '\u0000')
                    actions = actions.concat("" + exploredState.getPrevAction());
                
                startTime = System.nanoTime();
                solutionFound = exploredState.checkWinState(mapData, goalLocs);
                if (solutionFound) {
                    System.out.println("lksdjglksdj!");
                    continue;
                }
                checkWinTimeCum += System.nanoTime() - startTime;

                startTime = System.nanoTime();
                String validActions = exploredState.getValidActions(mapData);
                for (int i = 0; i < validActions.length(); i++) {
                    GameState nextState = new GameState(exploredState.getItemsData(), validActions.charAt(i), goalLocs);
                    nextState.setPredecessor(exploredState);

                    if (!explored.contains(nextState))
                        frontier.add(nextState);
                }
                getActionsTimeCum += System.nanoTime() - startTime;

                // Temporary only for DEBUGGING
                // IMPORTANT TODO: DELETE THIS
                int size = explored.size();
                if (size % skip == 0) {
                    System.out.println("Explored: %d".formatted(size));
                    System.out.println("Explore Time: %.2f ns (%.2f ns)".formatted(exploreTimeCum / skip, exploreTimeCum / skip / skip));
                    System.out.println("Check Win Time: %.2f ns (%.2f ns)".formatted(checkWinTimeCum / skip, checkWinTimeCum / skip / skip));
                    System.out.println("Get Actions Time: %.2f ns (%.2f ns)".formatted(getActionsTimeCum / skip, getActionsTimeCum / skip / skip));
                    System.out.println("==================================");

                    exploreTimeCum = 0.0;
                    checkWinTimeCum = 0.0;
                    getActionsTimeCum = 0.0;
                }
                
            }
            
            GameState curr = lastExplored;
            actions = "";
            
            boolean done = false;
            
            while (!done) {
                Character prevAction = curr.getPrevAction();
                if (prevAction != '\u0000')
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

    public int[][] getGoalLocs(char[][] mapData) {
        ArrayList<int[]> goalLocsList = new ArrayList<int[]>();

        int rows = mapData.length;
        for (int i = 0; i<rows; i++) {
            int cols = mapData[i].length;
            for (int j = 0; j < cols; j++) {
                if (mapData[i][j] == '.'){
                    int[] loc = {i, j};
                    goalLocsList.add(loc);
                }
            }
        }
        
        int[][] locsList = new int[goalLocsList.size()][];
        locsList = goalLocsList.toArray(locsList);
        return locsList;
    }
}
