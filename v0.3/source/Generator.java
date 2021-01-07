package source;

import java.io.*;
import java.util.*;

import source.entities.Player;
import source.objects.clothing.*;
import source.objects.weapons.*;

public class Generator { //manages loading and generating
  public String savegame = "default";
  formator form = new formator(100, 50);
  Random rand = new Random();
  entitySystem entities;
  int time;
  
  void Generator() {
                          
  }
  
  public entitySystem new_game() {
    System.out.println("Enter desired name of the savegame!");
    savegame = util.input_string(false);
    new File("data/" + savegame).mkdir();
    new File("data/" + savegame + "/player").mkdir();

    time = 21600;

    new File("data/" + savegame + "/map").mkdir();
    entities = new entitySystem("data/" + savegame);
    generate("village");
    
    //save important data(currently just time)
    try {
      PrintWriter writer = new PrintWriter(new File("data/" + savegame + "/data.txt"));
      writer.println(time);
      writer.close();
    } catch(Exception e) {}
    return entities;
  }
  
  public void generate(String type) {
    //setup player
    Player player = (Player) entities.entities.get(0); 
    player.wear(new Trousers("wool"));
    player.wear(new Shirt("wool"));
    player.in_left_hand = new Sword("iron");
    
    new File("data/" + savegame + "/map/" + player.pos[0] + "_" + player.pos[1]).mkdir();
    switch(type) {
    case "village":
      village("data/" + savegame + "/map/" + player.pos[0] + "_" + player.pos[1]);  
      break;
    }
  }
  
  private void village(String location) {//location is precise file location
    String[][] map = setUpMap(100, 100);
    String[][] materialmap = setUpMap(100, 100);
    int[] center = {rand.nextInt(11) + 45, rand.nextInt(11) + 45};
    for (int i = center[1] - 3; i <= center[1] + 3; i++) {
      for (int j = center[0] - 3; j <= center[0] + 3; j++) {
        map[i][j] = "+";
        materialmap[i][j] = "s";
      }
    }
    int start = rand.nextInt(4), lt = 0, rt = 0, lb = 0, rb = 0, dist, endpoint, width, height, offset, id = 0;
    String filled = "", buildings;
    for (int i = 0; i < 4; i++) {
      if (! filled.contains(String.valueOf(start))) {
        switch(start) {
          case 0://start at top path, going clockwise
            endpoint = center[1] - (rand.nextInt(25) + 10);
            map = vertical_way(map, materialmap, center[1], endpoint, center[0]);
            //gen houses
            width = rand.nextInt(4) + 5; height = rand.nextInt(4) + 5; offset = lt;
            if (lt == 0) {
              lt = width - 2;
            }
            //left side
            for (int j = center[1] - 4 - offset; j > endpoint; j -= dist + rand.nextInt(5)) { //for each house
              map = horizontal_line(map, center[0] - width - 1, center[0], j, "#");
              for (int k = j - 1; k > j - height + 1; k--) {
                map[k][center[0] - width - 1] = "#";
                map = horizontal_line(map, center[0] - width, center[0] - 3, k, " ");
                map[k][center[0] - 2] = "#";
              }
              map = horizontal_line(map, center[0] - width - 1, center[0] - 2, j - height + 1, "#");
              map[j - Math.round(height / 2)][center[0] - 2] = "&";
              
              //add entity
              int[] pos = {center[0]- width, j - height + 2, center[0] - 2, j - 1};
              entities.add_building(id++, pos, "");
              
              //prepare next house
              dist = height;
              width = rand.nextInt(4) + 5;
              height = rand.nextInt(4) + 5;
            }
            //right side
            offset = rt;
            if (rt == 0) {
              rt = width - 2;
            }
            for (int j = center[1] - 4 - offset; j > endpoint; j -= dist + rand.nextInt(5)) {
              map = horizontal_line(map, center[0] + 2, center[0] + width, j, "#");
              for (int k = j - 1; k > j - height + 1; k--) {
                map[k][center[0] + 2] = "#";
                map = horizontal_line(map, center[0] + 3, center[0] + width - 1, k, " ");
                map[k][center[0] + width] = "#";
              }
              map = horizontal_line(map, center[0] + 2, center[0] + width, j - height + 1, "#");
              map[j - Math.round(height / 2)][center[0] + 2] = "&";

              //add entity
              int[] pos = {center[0] + 3, j - height + 2, center[0] + width, j - 1};
              entities.add_building(id++, pos, "");
              
              //prepare next house
              dist = height;
              width = rand.nextInt(4) + 5;
              height = rand.nextInt(4) + 5;
            }
            break;
          case 1://right 
            endpoint = center[0] + (rand.nextInt(25) + 10);
            map = horizontal_way(map, materialmap, center[0], endpoint, center[1]);
            //gen houses
            width = rand.nextInt(4) + 5; height = rand.nextInt(4) + 5; offset = rt;
            if (rt == 0) {
              rt = height - 2;
            }
            //top side
            for (int j = center[0] + 4 + offset; j < endpoint; j += dist + rand.nextInt(5)) {
              map = horizontal_line(map, j, j + width - 2, center[1] - 2, "#");
              for (int k = center[1] - 3; k > center[1] - height - 1; k--) {
                map[k][j] = "#";
                map = horizontal_line(map, j + 1, j + width - 3, k, " ");
                map[k][j + width - 2] = "#";
              }
              map = horizontal_line(map, j, j + width - 2, center[1] - height - 1, "#");
              map[center[1] - 2][j + Math.round(width / 2)] = "&";
              
              //add entity
              int[] pos = {j + 1, center[1] - height, j + width - 2, center[1] - 3};
              entities.add_building(id++, pos, "");
              
              //prepare next house
              dist = width;
              width = rand.nextInt(4) + 5;
              height = rand.nextInt(4) + 5;
            }
            //bottom side 
            offset = rb;
            if (rb == 0) {
              rb = height - 2;
            }
            for (int j = center[0] + 4 + offset; j < endpoint; j += dist + rand.nextInt(5)) {
              map = horizontal_line(map, j, j + width, center[1] + 2, "#");
              for (int k = center[1] + 3; k < center[1] + height + 1; k++) {
                map[k][j] = "#";
                map = horizontal_line(map, j + 1, j + width - 1, k, " ");
                map[k][j + width] = "#";
              }
              map = horizontal_line(map, j, j + width, center[1] + height + 1, "#");
              map[center[1] + 2][j + Math.round(width / 2)] =  "&";

              //add entity
              int[] pos = {j + 1, center[1] + 3, j + width - 2, center[1] + height};
              entities.add_building(id++, pos, "");
              
              //prepare next house
              dist = width;
              width = rand.nextInt(4) + 5;
              height = rand.nextInt(4) + 5;
            }            
            break;
          case 2://bottom
            endpoint = center[1] + (rand.nextInt(25) + 10);
            map = vertical_way(map, materialmap, center[1], endpoint, center[0]);
            //gen houses
            width = rand.nextInt(4) + 5; height = rand.nextInt(4) + 5; offset = lb;
            if (lb == 0) {
              lb = width - 2;
            }
            //left side
            for (int j = center[1] + 4 + offset; j < endpoint; j += dist + rand.nextInt(5)) {
              map = horizontal_line(map, center[0] - width - 1, center[0] - 2, j, "#");
              for (int k = j + 1; k < j + height; k++) {
                map[k][center[0] - width - 1] = "#";
                map = horizontal_line(map, center[0] - width, center[0] - 3, k, " ");
                map[k][center[0] - 2] = "#";
              }
              map = horizontal_line(map, center[0] - width - 1, center[0] - 2, j + height - 1, "#");
              map[j + Math.round(height / 2)][(center[0] - 2)] = "&";

              //add entity
              int[] pos = {center[0] - width, j + 1, center[0] - 2, j + height - 1};
              entities.add_building(id++, pos, "");
              
              //prepare next house
              dist = height;
              width = rand.nextInt(4) + 5;
              height = rand.nextInt(4) + 5;
            }
            //right side
            offset = rb;
            if (rb == 0) {
              rb = width - 2;
            }
            for (int j = center[1] + 4 + offset; j < endpoint; j += dist + rand.nextInt(5)) {
              map = horizontal_line(map, center[0] + 2, center[0] + width, j, "#");
              for (int k = j + 1; k < j + height - 1; k++) {
                map[k][center[0] + 2] = "#";
                map = horizontal_line(map, center[0] + 3, center[0] + width - 1, k, " ");
                map[k][center[0] + width] = "#";
              }
              map = horizontal_line(map, center[0] + 2, center[0] + width, j + height - 1, "#");
              map[j + Math.round(height / 2)][center[0] + 2] = "&";

              //add entity
              int[] pos = {center[0] + 3, j + 1, center[0] + width, j + height - 2};
              entities.add_building(id++, pos, "");
              
              //prepare next house
              dist = height;
              width = rand.nextInt(4) + 5;
              height = rand.nextInt(4) + 5;
            }
            break;
          case 3://left
            endpoint = center[0] - (rand.nextInt(25) + 10);
            map = horizontal_way(map, materialmap, center[0], endpoint, center[1]);
            //gen houses
            width = rand.nextInt(4) + 5; height = rand.nextInt(4) + 5; offset = lt;
            if (lt == 0) {
              lt = height - 2;
            }
            //top side
            for (int j = center[0] - 4 - offset; j > endpoint; j -= dist + rand.nextInt(5)) {
              map = horizontal_line(map, j - width, j - 1, center[1] - 2, "#");
              for (int k = center[1] - 3; k > center[1] - height - 1; k--) {
                map[k][j - width] = "#";
                map = horizontal_line(map, j - width + 1, j - 2, k, " ");
                map[k][j - 1] = "#";
              }
              map = horizontal_line(map, j - width, j - 1, center[1] - height - 1, "#");      
              map[center[1] - 2][j - Math.round(width / 2)] = "&";

              //add entity
              int[] pos = {j - width + 1, center[1] - height, j - 1, center[1] - 3};
              entities.add_building(id++, pos, "");
              
              //prepare next house
              dist = width;
              width = rand.nextInt(4) + 5;
              height = rand.nextInt(4) + 5;
            } 
            //bottom side 
            offset = lb;
            if (lb == 0) {
              lb = height - 2;
            }
            for (int j = center[0] - 4 - offset; j > endpoint; j -= dist + rand.nextInt(5)) {
              map = horizontal_line(map, j - width, j - 1, center[1] + 2, "#");
              for (int k = center[1] + 3; k < center[1] + height + 1; k++) {
                map[k][j - width] = "#";
                map = horizontal_line(map, j - width + 1, j - 2, k, " ");
                map[k][ j - 1] = "#";
              }
              map = horizontal_line(map, j - width, j - 1, center[1] + height + 1, "#");
              map[center[1] + 2][j - Math.round(width / 2)] = "&";

              //add entity
              int[] pos = {j - width + 1, center[1] + 3, j - 1, center[1] + height};
              entities.add_building(id++, pos, "");
              
              //prepare next house
              dist = width;
              width = rand.nextInt(4) + 5;
              height = rand.nextInt(4) + 5;
            }            
            break;
        }
        filled += String.valueOf(start);
        start = rand.nextInt(4);
      }
    }
    entities.gen_village(location);
    //fields
    int[] pos = {center[0] - 20 - rand.nextInt(30), center[1] - 20 - rand.nextInt(30), center[0] - 15, center[1] - 15};
    map = field(map, materialmap, pos);
    entities.add_building(id++, pos, "field");
    pos = new int[4];
    pos[0] = center[0] + 15; pos[3] = center[1] - 15; pos[2] = center[0] + 30 + rand.nextInt(20); pos[1] = center[1] - 30 - rand.nextInt(20);
    map = field(map, materialmap, pos);
    entities.add_building(id++, pos, "field");
    pos = new int[4];
    pos[0] = center[0] + 15; pos[1] = center[1] + 15; pos[2] = center[0] + 30 + rand.nextInt(20); pos[3] = center[1] + 30 + rand.nextInt(20);
    map = field(map, materialmap, pos);
    entities.add_building(id++, pos, "field");
    pos = new int[4];
    pos[2] = center[0] - 15; pos[1] = center[1] + 15; pos[0] = center[0] - 30 - rand.nextInt(20); pos[3] = center[1] + 30 + rand.nextInt(20);
    map = field(map, materialmap, pos);
    entities.add_building(id++, pos, "field");
    //saving everything
    save_map(map, location);
    save_materialmap(materialmap, location);
    String[][] col_map = collisionMap(map);//generate collision map
    save_col_map(col_map, location);
    entities.init_path(col_map);
    entities.setup_npcs(time, true);
    entities.save_location();
  }
  
  //---------------------//SAVING THINGS//---------------------//
  
  public void save_pos(int[] pos){ //first two numbers are local, other two are global
    try {
      PrintWriter writer = new PrintWriter("data/" + savegame + "/player/data.txt");
      for (int i = 0; i < pos.length; i++) {
        writer.println(pos[i]);
      }
      writer.close();
    }
    catch(Exception e) {System.out.println(e);}  
  }
  
  public void save_map(String[][] map, String location) {
    try { //save map + data
      PrintWriter writer = new PrintWriter(location + "/map_data.txt");
      writer.println(map.length);//number of rows
      writer.println(map[0].length);
      writer.close();
      writer = new PrintWriter(location + "/map.txt");
      for (int i = 0; i < map.length; i++) {
        for (int j = 0; j < map[i].length; j++)
          writer.print(map[i][j]);
        writer.println();
      }
      writer.close(); 
    } catch(Exception e) {System.out.println("ERROR: Gen.save_map:\n" + e);}
  }
  
  public void save_materialmap(String[][] map, String location) {
    try { //save map + data
      PrintWriter writer = new PrintWriter(location + "/materialmap.txt");
      for (int i = 0; i < map.length; i++) {
        for (int j = 0; j < map[i].length; j++)
          writer.print(map[i][j]);
        writer.println();
      }
      writer.close(); 
    } catch(Exception e) {System.out.println("ERROR: Gen.save_materialmap:\n" + e);}  
  }
  
  public void save_col_map(String[][] map, String location) {
    try { //save map + data
      PrintWriter writer = new PrintWriter(location + "/col_map.txt");
      for (int i = 0; i < map.length; i++) {
        for (int j = 0; j < map[i].length; j++)
          writer.print(map[i][j]); 
        writer.println();
      }
      writer.close(); 
    } catch(Exception e) {System.out.println("ERROR: Gen.save_col_map:\n" + e);}
  }
  
  //------------------//LOADING THINGS//-----------------------//
  
  public String[][] load_map(int x, int y) {
    String[][] map = new String[1][];
    try {
      Scanner reader = new Scanner(new File("data/" + savegame + "/map/" + x + "_" + y + "/map_data.txt"));
      int height = Integer.parseInt(reader.nextLine());
      int width = Integer.parseInt(reader.nextLine());
      map = new String[height][width];
      reader = new Scanner(new File("data/" + savegame + "/map/" + x + "_" + y + "/map.txt"));
      for (int i = 0; i < height; i++) {
        String cache = reader.nextLine();
        for(int j = 0; j < width; j++)
          map[i][j] = String.valueOf(cache.charAt(j));
      }
      reader.close();
    } catch(Exception e) {System.out.println("ERROR: Gen.load_map:\n" + e);}
    return map;
  }
  
  public String[][] load_materialmap(int x, int y) {
    String[][] map = new String[1][];
    try {
      Scanner reader = new Scanner(new File("data/" + savegame + "/map/" + x + "_" + y + "/map_data.txt"));
      int height = Integer.parseInt(reader.nextLine());
      int width = Integer.parseInt(reader.nextLine());
      map = new String[height][width];
      reader = new Scanner(new File("data/" + savegame + "/map/" + x + "_" + y + "/materialmap.txt"));
      for (int i = 0; i < height; i++) {
        String cache = reader.nextLine();
        for(int j = 0; j < width; j++)
          map[i][j] = String.valueOf(cache.charAt(j));
      }
      reader.close();
    } catch(Exception e) {System.out.println("ERROR: Gen.load_map:\n" + e);}
    return map;  
  }
  
  public String[][] load_col_map(int x, int y) {
    String[][] map = new String[1][];
    try {
      Scanner reader = new Scanner(new File("data/" + savegame + "/map/" + x + "_" + y + "/map_data.txt"));
      int height = Integer.parseInt(reader.nextLine());
      int width = Integer.parseInt(reader.nextLine());
      map = new String[height][width];
      reader = new Scanner(new File("data/" + savegame + "/map/" + x + "_" + y + "/col_map.txt"));
      for (int i = 0; i < height; i++) {
        String cache = reader.nextLine();
        for(int j = 0; j < width; j++)
          map[i][j] = String.valueOf(cache.charAt(j));
      }
      reader.close();
    } catch(Exception e) {System.out.println("ERROR: Gen.load_col_map:\n" + e);}
    return map;
  }
  
  //--------------------//PRIVATE METHODS//-----------------------//
  
  private String[][] horizontal_way(String[][] map, String[][] materialmap, int start, int end, int y) {
    if (start > end) {
      int cache = start;
      start = end;
      end = cache; 
    }
    for (int i = y - 1; i <= y + 1; i++)
      for (int j = start; j <= end; j++) {
        map[i][j] = "+";
        materialmap[i][j] = "s";
      }
    return map;
  }
  
  private String[][] vertical_way(String[][] map, String[][] materialmap, int start, int end, int x) {
    if (start > end) {
      int cache = start;
      start = end;
      end = cache; 
    }
    for(int i = start; i <= end; i++) {
      map[i][x - 1] = "+";
      materialmap[i][x - 1] = "s";
      map[i][x] = "+";
      materialmap[i][x] = "s";
      map[i][x + 1] = "+";
      materialmap[i][x + 1] = "s";
    }
    return map;
  }
  
  private String[][] horizontal_line(String[][] map, int start, int end, int y, String icon) {
    for (int i = start; i <= end; i++)
      map[y][i] = icon;
    return map;
  }

  private String[][] field(String[][] map, String[][] materialmap, int[] pos) {
    if (pos[0] < 0) pos[0] = 0;
    if (pos[1] < 0) pos[1] = 0;
    if (pos[2] >= map[0].length) pos[2] = map[0].length - 1;
    if (pos[3] >= map.length) pos[3] = map.length - 1;
    for(int i = pos[1]; i < pos[3]; i++) {
      for (int j = pos[0]; j < pos[2]; j++) {
        map[i][j] = "\"";
        materialmap[i][j] = "y";
      }
    }
  return map;
  }  
  
  private String[][] setUpMap(int width, int height) {
    String[][] map = new String[height][width];
    for (int i = 0; i < map.length; i++)
      for (int j = 0; j <map[i].length; j++)
        map[i][j] = " ";
    return map;  
  }
  
  private String[][] collisionMap(String[][] map) {
    for (int i = 0; i < map.length; i++) {
      for (int j = 0; j < map[i].length; j++) {
        if (map[i][j].equals("#")) map[i][j] = "0";
        else map[i][j] = " ";
      }
    }
    return map;
  }
}
