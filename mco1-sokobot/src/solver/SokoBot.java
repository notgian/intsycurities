package solver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.PriorityQueue;


import java.util.HashSet;

public class SokoBot {
    public String solveSokobanPuzzle(int width, int height, char[][] mapData, char[][] itemsData) {
        try {
            int[] goalLocs = this.getGoalLocs(mapData);
            HashSet<Integer> cornerDeadlocks = this.findCornerDeadlocks(mapData);
            HashSet<Integer> twoBoxDeadlocks = this.findTwoBoxDeadlocks(mapData);
            HashSet<Long> fourBoxDeadlocks = this.findFourBoxDeadlocks(mapData);
            DeadlockContext deadlockContext = new DeadlockContext(cornerDeadlocks, twoBoxDeadlocks, fourBoxDeadlocks);

            GameState initialState = new GameState(itemsData, goalLocs, deadlockContext);

            PriorityQueue<GameState> frontier = new PriorityQueue<>(new HeuristicsComparator());
            frontier.add(initialState);
            boolean solutionFound = false;

            HashSet<GameState> explored = new HashSet<GameState>();
            GameState lastExplored = null;

            while (!frontier.isEmpty() && !solutionFound) {
                // deque fronteir
                // get valid actions
                // add all valid actions to the frontier 
                // repeat
               
                GameState exploredState;
                do {
                    exploredState = frontier.poll();
                } while (explored.contains(exploredState) && exploredState != null);

                explored.add(exploredState);
                lastExplored = exploredState; 
                
                String validActions = exploredState.getValidActions(mapData);
                for (int i = 0; i < validActions.length(); i++) {
                    GameState nextState = new GameState(exploredState, validActions.charAt(i), goalLocs, deadlockContext);
                    nextState.setPredecessor(exploredState);

                    if (nextState.checkWinState(mapData, goalLocs)) {
                        lastExplored = nextState;
                        solutionFound = true;
                        continue;
                    }

                    if (!nextState.isDeadlocked() && !explored.contains(nextState))
                        frontier.add(nextState);
                }
            }
            
            GameState curr = lastExplored;
            String actions = "";
            
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

    public int[] getGoalLocs(char[][] mapData) {
        ArrayList<Integer> goalLocsList = new ArrayList<Integer>();
        int rows = mapData.length;
        for (int i = 0; i<rows; i++) {
            int cols = mapData[i].length;
            for (int j = 0; j < cols; j++) {
                if (mapData[i][j] == '.'){
                    goalLocsList.add( (i * cols) + j);
                }
            }
        }
        return goalLocsList.stream().mapToInt(i -> i).toArray();
    }

    public HashSet<Integer> findCornerDeadlocks(char[][] mapData) {
        HashSet<Integer> deadlocks = new HashSet<Integer>();
        int width = mapData[0].length;
        int height = mapData.length;
        for (int y = 1; y < height-1; y++) {
            for (int x = 1; x < width-1; x++) {
                // top left corner
                if (mapData[y][x] != '.' && mapData[y][x-1] == '#' && mapData[y-1][x] == '#')
                    deadlocks.add( (y * width) + x);
                // top right corner
                else if (mapData[y][x] != '.'  && y > 0 && mapData[y][x+1] == '#' && mapData[y-1][x] == '#')
                    deadlocks.add( (y * width) + x);
                // bottom left corner
                else if (mapData[y][x] != '.'  && mapData[y][x-1] == '#' && mapData[y+1][x] == '#')
                    deadlocks.add( (y * width) + x);
                // bottom right corner
                else if (mapData[y][x] != '.'  && mapData[y][x+1] == '#' && mapData[y+1][x] == '#')
                    deadlocks.add( (y * width) + x);
            } 
        }
        return deadlocks;
    }

    public HashSet<Integer> findTwoBoxDeadlocks(char[][] mapData) {
        HashSet<Integer> deadlocks = new HashSet<Integer>();
        int width = mapData[0].length;
        int height = mapData.length;
        for (int y = 1; y < height-1; y++) {
            for (int x = 1; x < width-1; x++) {
                // Skip horizontal checks if too far to the right
                if (x < width-2) {
                    boolean eitherIsGoal = mapData[y][x] == '.' || mapData[y][x+1] == '.';
                    boolean topWalls = mapData[y-1][x] == '#' && mapData[y-1][x+1] == '#';
                    boolean botWalls = mapData[y+1][x] == '#' && mapData[y+1][x+1] == '#';
                    if (!eitherIsGoal && (topWalls || botWalls)) {
                        int pos1 = (y * width) + x;
                        int pos2 = (y * width) + x + 1;
                        deadlocks.add((pos1 << 16) | (pos2 & 0xFFFF));
                    }
                }
                // Skip vertical checks if too far down
                if (y < height-2) {
                    boolean eitherIsGoal  = mapData[y][x] == '.' || mapData[y+1][x] == '.';
                    boolean leftWalls  = mapData[y][x-1] == '#' && mapData[y+1][x-1] == '#';
                    boolean rightWalls = mapData[y][x+1] == '#' && mapData[y+1][x+1] == '#';
                    if (!eitherIsGoal && (leftWalls || rightWalls)) {
                        int pos1 = (y * width) + x;
                        int pos2 = (y * width) + x + width;
                        deadlocks.add((pos1 << 16) | (pos2 & 0xFFFF));
                    }
                }
            } 
        }
        return deadlocks;
    }

    public HashSet<Long> findFourBoxDeadlocks(char[][] mapData) {
        HashSet<Long> deadlocks = new HashSet<Long>();
        int width = mapData[0].length;
        int height = mapData.length;
        for (int y = 0; y < height-1; y++) {
            for (int x = 0; x < width-1; x++) {
                boolean allGoals = mapData[y][x] == '.'
                                    && mapData[y][x+1] == '.'
                                    && mapData[y+1][x] == '.'
                                    && mapData[y+1][x+1] == '.';
                boolean hasWall = mapData[y][x] == '#'
                                    || mapData[y][x+1] == '#'
                                    || mapData[y+1][x] == '#'
                                    || mapData[y+1][x+1] == '#';
                // skip if all goals or has a wall
                if (allGoals || hasWall)
                    continue;
                long pos1 = (((y*width) + x) & 0xFFFFl) << 48;
                long pos2 = (((y*width) + x+1) & 0xFFFFl) << 32;
                long pos3 = ((((y+1)*width) + x) & 0xFFFFl) << 16;
                long pos4 = ((((y+1)*width) + x+1) & 0xFFFFl);
                deadlocks.add(pos1 | pos2 | pos3 | pos4);
            } 
        }
        return deadlocks;
    }
}
