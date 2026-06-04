package solver;
public class SokoBot {

    public String solveSokobanPuzzle(int width, int height, char[][] mapData, char[][] itemsData) {

        try {
            GameState initialState = new GameState(mapData, itemsData);

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

}
