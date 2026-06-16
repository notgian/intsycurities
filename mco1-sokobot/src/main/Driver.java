package main;

import gui.GameFrame;
import reader.FileReader;
import reader.MapData;
import solver.SokoBot;

public class Driver {
  public static void main(String[] args) {
    String mapName, mode;
    String mapContent = System.getenv("MAP_CONTENT");

    if (args.length < 2) {
      if ((mapContent == null && System.getenv("MAP_NAME") == null) || System.getenv("MODE") == null) {
        System.err.println("Usage: Driver <map name> <mode>");
        System.exit(1);
      }

      mapName = System.getenv("MAP_NAME");
      mode = System.getenv("MODE");
    } else {
      mapName = args[0];
      mode = args[1];
    }

    FileReader fileReader = new FileReader();
    MapData mapData;
    if (mapContent != null) {
      mapData = fileReader.readString(mapContent);
    } else {
      mapData = fileReader.readFile(mapName);
    }

    if (mapData == null) {
      System.err.println("Error: Map '" + (mapContent != null ? "MAP_CONTENT" : mapName) + "' could not be loaded.");
      System.exit(1);
    }

    if (mode.equals("raw")) {
      char[][] map = new char[mapData.rows][mapData.columns];
      char[][] items = new char[mapData.rows][mapData.columns];

      for (int i = 0; i < mapData.rows; i++) {
        for (int j = 0; j < mapData.columns; j++) {
          switch (mapData.tiles[i][j]) {
            case '#':
              map[i][j] = '#';
              items[i][j] = ' ';
              break;
            case '@':
              map[i][j] = ' ';
              items[i][j] = '@';
              break;
            case '$':
              map[i][j] = ' ';
              items[i][j] = '$';
              break;
            case '.':
              map[i][j] = '.';
              items[i][j] = ' ';
              break;
            case '+':
              map[i][j] = '.';
              items[i][j] = '@';
              break;
            case '*':
              map[i][j] = '.';
              items[i][j] = '$';
              break;
            case ' ':
              map[i][j] = ' ';
              items[i][j] = ' ';
              break;
          }
        }
      }

      SokoBot sokoBot = new SokoBot();
      String solution = sokoBot.solveSokobanPuzzle(mapData.columns, mapData.rows, map, items);
      System.out.println(solution);
    } else {
      GameFrame gameFrame = new GameFrame(mapData);

      if (mode.equals("fp")) {
        gameFrame.initiateFreePlay();
      } else if (mode.equals("bot")) {
        gameFrame.initiateSolution();
      } else if (mode.equals("check")) {
        gameFrame.initiateCheck();
      }
    }
  }
}
