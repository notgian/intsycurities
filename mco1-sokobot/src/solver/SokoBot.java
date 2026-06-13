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

            while (!frontier.isEmpty() && !solutionFound) {
                // deque fronteir
                // get valid actions
                // add all valid actions to the frontier 
                // repeat
                
                GameState exploredState = frontier.poll();
                explored.add(exploredState);
                lastExplored = exploredState; 
                if (exploredState.getPrevAction() != '\u0000')
                    actions = actions.concat("" + exploredState.getPrevAction());
                

                // Temporary only for DEBUGGING
                // IMPORTANT TODO: DELETE THIS
                if (explored.size() % 1000 == 0)
                    System.out.println("Explored: %d".formatted(explored.size()));
                //
                
                solutionFound = exploredState.checkWinState();
                if (solutionFound) continue;

                String validActions = exploredState.getValidActions();
                for (int i = 0; i < validActions.length(); i++) {
                    GameState nextState = new GameState(exploredState.getMapData(), exploredState.getItemsData(), validActions.charAt(i));
                    nextState.setPredecessor(exploredState);
                    nextState.setGoalLocs(goalLocs);
                    if (!explored.contains(nextState))
                        frontier.add(nextState);
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
