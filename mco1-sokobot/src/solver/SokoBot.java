package solver;

import java.util.ArrayList;

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

}
