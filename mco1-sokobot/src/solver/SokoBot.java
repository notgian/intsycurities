package solver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.PriorityQueue;
import java.util.HashSet;

public class SokoBot {
    public String solveSokobanPuzzle(int width, int height, char[][] mapData, char[][] itemsData) {
        try {
            GameState initialState = new GameState(mapData, itemsData);
            byte[][] goalLocs = initialState.getGoalLocs();

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
                solutionFound = exploredState.checkWinState();
                if (solutionFound) {
                    System.out.println("lksdjglksdj!");
                    continue;
                }
                checkWinTimeCum += System.nanoTime() - startTime;

                startTime = System.nanoTime();
                String validActions = exploredState.getValidActions();
                for (int i = 0; i < validActions.length(); i++) {
                    GameState nextState = new GameState(exploredState.getMapData(), exploredState.getItemsData(), validActions.charAt(i));
                    nextState.setPredecessor(exploredState);
                    nextState.setGoalLocs(goalLocs);
                    // if (!explored.contains(nextState))
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
}
