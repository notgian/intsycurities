package reader;

import java.io.File;
import java.util.Scanner;

public class FileReader {
  public MapData readFile(String keyword) {
    int rows = 0;
    int columns = 0;
    char tiles[][] = new char[100][100];
    for (int i = 0; i < 100; i++) {
      for (int j = 0; j < 100; j++) {
        tiles[i][j] = ' ';
      }
    }

    try {
      File file = new File("maps/" + keyword + ".txt");
      Scanner scanner = new Scanner(file);
      while (scanner.hasNext()) {
        String nextLine = scanner.nextLine();
        columns = Math.max(columns, nextLine.length());
        for (int i = 0; i < nextLine.length(); i++) {
          tiles[rows][i] = nextLine.charAt(i);
        }
        rows++;
      }
      scanner.close();
    } catch (java.io.FileNotFoundException ex) {
      // Just return null, the driver will handle reporting the error
      return null;
    } catch (Exception ex) {
      ex.printStackTrace(System.out);
      return null;
    }

    MapData result = new MapData();
    result.tiles = tiles;
    result.rows = rows;
    result.columns = columns;

    return result;
  }

  public MapData readString(String content) {
    int rows = 0;
    int columns = 0;
    char tiles[][] = new char[100][100];
    for (int i = 0; i < 100; i++) {
      for (int j = 0; j < 100; j++) {
        tiles[i][j] = ' ';
      }
    }

    try {
      Scanner scanner = new Scanner(content);
      while (scanner.hasNextLine()) {
        String nextLine = scanner.nextLine();
        columns = Math.max(columns, nextLine.length());
        for (int i = 0; i < nextLine.length(); i++) {
          tiles[rows][i] = nextLine.charAt(i);
        }
        rows++;
      }
      scanner.close();
    } catch (Exception ex) {
      ex.printStackTrace(System.out);
      return null;
    }

    MapData result = new MapData();
    result.tiles = tiles;
    result.rows = rows;
    result.columns = columns;

    return result;
  }
}
