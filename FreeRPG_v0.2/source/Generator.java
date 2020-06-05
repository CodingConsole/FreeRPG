package source;

import java.io.*;
import java.util.*;

import source.entities.Player;

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
    Player player = (Player) entities.entities.get(0);
    new File("data/" + savegame + "/map/" + player.pos[0] + "_" + player.pos[1]).mkdir();
    switch(type) {
    case "village":
      village("data/" + savegame + "/map/" + player.pos[0] + "_" + player.pos[1]);  
      break;
    }
  }
  
  private void village(String location) {//location is precise file location
    StringBuilder[] map = setUpMap(100, 100);
    int[] center = {rand.nextInt(11) + 45, rand.nextInt(11) + 45};
    for (int i = center[1] - 3; i <= center[1] + 3; i++) {
      map[i].replace(center[0] - 3, center[0] + 4, form.fill("+", 7));
    }
    int start = rand.nextInt(4), lt = 0, rt = 0, lb = 0, rb = 0, dist, endpoint, width, height, offset, id = 0;
    String filled = "", buildings;
    for (int i = 0; i < 4; i++) {
      if (! filled.contains(String.valueOf(start))) {
        switch(start) {
          case 0://start at top path, going clockwise
            endpoint = center[1] - (rand.nextInt(25) + 10);
            map = vertical_way(map, center[1], endpoint, center[0]);
            //gen houses
            width = rand.nextInt(4) + 5; height = rand.nextInt(4) + 5; offset = lt;
            if (lt == 0) {
              lt = width - 2;
            }
            //left side
            for (int j = center[1] - 4 - offset; j > endpoint; j -= dist + rand.nextInt(5)) {
              map[j].replace(center[0] - width - 1, center[0] - 1, form.fill("#", width));
              for (int k = j - 1; k > j - height + 1; k--) {
                map[k].replace(center[0] - width - 1, center[0] - 1, "#" + form.fill(" ", width - 2) + "#");
              }
              map[j - height + 1].replace(center[0] - width - 1, center[0] - 1, form.fill("#", width));
              map[j - Math.round(height / 2)].setCharAt(center[0] - 2, '&');
              
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
              map[j].replace(center[0] + 2, center[0] + width + 1, form.fill("#", width));
              for (int k = j - 1; k > j - height + 1; k--) {
                map[k].replace(center[0] + 2, center[0] + width + 1, "#" + form.fill(" ", width - 2) + "#");
              }
              map[j - height + 1].replace(center[0] + 2, center[0] + width + 1, form.fill("#", width));
              map[j - Math.round(height / 2)].setCharAt(center[0] + 2, '&');

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
            map = horizontal_way(map, center[0], endpoint, center[1]);
            //gen houses
            width = rand.nextInt(4) + 5; height = rand.nextInt(4) + 5; offset = rt;
            if (rt == 0) {
              rt = height - 2;
            }
            //top side
            for (int j = center[0] + 4 + offset; j < endpoint; j += dist + rand.nextInt(5)) {
              map[center[1] - 2].replace(j, j + width - 1, form.fill("#", width));
              for (int k = center[1] - 3; k > center[1] - height - 1; k--) {
                map[k].replace(j, j + width - 1, "#" + form.fill(" ", width - 2) + "#");
              }
              map[center[1] - height - 1].replace(j, j + width - 1, form.fill("#", width));
              map[center[1] - 2].setCharAt(j + Math.round(width / 2), '&');
              
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
              map[center[1] + 2].replace(j, j + width - 1, form.fill("#", width));
              for (int k = center[1] + 3; k < center[1] + height + 1; k++) {
                map[k].replace(j, j + width - 1, "#" + form.fill(" ", width - 2) + "#");
              }
              map[center[1] + height + 1].replace(j, j + width - 1, form.fill("#", width));
              map[center[1] + 2].setCharAt(j + Math.round(width / 2), '&');

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
            map = vertical_way(map, center[1], endpoint, center[0]);
            //gen houses
            width = rand.nextInt(4) + 5; height = rand.nextInt(4) + 5; offset = lb;
            if (lb == 0) {
              lb = width - 2;
            }
            //left side
            for (int j = center[1] + 4 + offset; j < endpoint; j += dist + rand.nextInt(5)) {
              map[j].replace(center[0] - width - 1, center[0] - 1, form.fill("#", width));
              for (int k = j + 1; k < j + height; k++) {
                map[k].replace(center[0] - width - 1, center[0] - 1, "#" + form.fill(" ", width - 2) + "#");
              }
              map[j + height - 1].replace(center[0] - width - 1, center[0] - 1, form.fill("#", width));
              map[j + Math.round(height / 2)].setCharAt(center[0] - 2, '&');

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
              map[j].replace(center[0] + 2, center[0] + width + 1, form.fill("#", width));
              for (int k = j + 1; k < j + height - 1; k++) {
                map[k].replace(center[0] + 2, center[0] + width + 1, "#" + form.fill(" ", width - 2) + "#");
              }
              map[j + height - 1].replace(center[0] + 2, center[0] + width + 1, form.fill("#", width));
              map[j + Math.round(height / 2)].setCharAt(center[0] + 2, '&');

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
            map = horizontal_way(map, center[0], endpoint, center[1]);
            //gen houses
            width = rand.nextInt(4) + 5; height = rand.nextInt(4) + 5; offset = lt;
            if (lt == 0) {
              lt = height - 2;
            }
            //top side
            for (int j = center[0] - 4 - offset; j > endpoint; j -= dist + rand.nextInt(5)) {
              map[center[1] - 2].replace(j - width, j, form.fill("#", width));
              for (int k = center[1] - 3; k > center[1] - height - 1; k--) {
                map[k].replace(j - width, j, "#" + form.fill(" ", width - 2) + "#");
              }
              map[center[1] - height - 1].replace(j - width, j, form.fill("#", width));
              map[center[1] - 2].setCharAt(j - Math.round(width / 2), '&');

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
              map[center[1] + 2].replace(j - width, j, form.fill("#", width));
              for (int k = center[1] + 3; k < center[1] + height + 1; k++) {
                map[k].replace(j - width, j, "#" + form.fill(" ", width - 2) + "#");
              }
              map[center[1] + height + 1].replace(j - width, j, form.fill("#", width));
              map[center[1] + 2].setCharAt(j - Math.round(width / 2), '&');

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
    map = field(map, pos);
    entities.add_building(id++, pos, "field");
    pos = new int[4];
    pos[0] = center[0] + 15; pos[3] = center[1] - 15; pos[2] = center[0] + 30 + rand.nextInt(20); pos[1] = center[1] - 30 - rand.nextInt(20);
    map = field(map, pos);
    entities.add_building(id++, pos, "field");
    pos = new int[4];
    pos[0] = center[0] + 15; pos[1] = center[1] + 15; pos[2] = center[0] + 30 + rand.nextInt(20); pos[3] = center[1] + 30 + rand.nextInt(20);
    map = field(map, pos);
    entities.add_building(id++, pos, "field");
    pos = new int[4];
    pos[2] = center[0] - 15; pos[1] = center[1] + 15; pos[0] = center[0] - 30 - rand.nextInt(20); pos[3] = center[1] + 30 + rand.nextInt(20);
    map = field(map, pos);
    entities.add_building(id++, pos, "field");
    //saving everything
    save_map(map, location);
    StringBuilder[] col_map = collisionMap(map);//generate collision map
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
  
  public void save_map(StringBuilder[] map, String location) {
    try { //save map + data
      PrintWriter writer = new PrintWriter(location + "/map_data.txt");
      writer.println(map.length);//number of rows
      writer.close();
      writer = new PrintWriter(location + "/map.txt");
      for (int i = 0; i < map.length; i++) {
        writer.println(map[i]);
      }
      writer.close(); 
    } catch(Exception e) {System.out.println(e);}
  }
  
  public void save_col_map(StringBuilder[] map, String location) {
    try { //save map + data
      PrintWriter writer = new PrintWriter(location + "/col_map.txt");
      for (int i = 0; i < map.length; i++) {
        writer.println(map[i]);
      }
      writer.close(); 
    } catch(Exception e) {System.out.println(e);}
  }
  
  //------------------//LOADING THINGS//-----------------------//
  
  public StringBuilder[] load_map(int x, int y) {
    StringBuilder[] map = new StringBuilder[1];
    try {
      Scanner reader = new Scanner(new File("data/" + savegame + "/map/" + x + "_" + y + "/map_data.txt"));
      int counter = reader.nextInt();
      map = new StringBuilder[counter];
      reader = new Scanner(new File("data/" + savegame + "/map/" + x + "_" + y + "/map.txt"));
      for (int i = 0; i < counter; i++) {
        map[i] = new StringBuilder(reader.nextLine());
      }
      reader.close();
    } catch(Exception e) {System.out.println(e);}
    return map;
  }
  
  public StringBuilder[] load_col_map(int x, int y) {
    StringBuilder[] map = new StringBuilder[1];
    try {
      Scanner reader = new Scanner(new File("data/" + savegame + "/map/" + x + "_" + y + "/map_data.txt"));
      int counter = reader.nextInt();
      map = new StringBuilder[counter];
      reader = new Scanner(new File("data/" + savegame + "/map/" + x + "_" + y + "/col_map.txt"));
      for (int i = 0; i < counter; i++) {
        map[i] = new StringBuilder(reader.nextLine());
      }
      reader.close();
    } catch(Exception e) {System.out.println(e);}
    return map;
  }
  
  //--------------------//PRIVATE METHODS//-----------------------//
  
  private StringBuilder[] horizontal_way(StringBuilder[] map, int start, int end, int y) {
    if (start > end) {
      int cache = start;
      start = end;
      end = cache; 
    }
    map[y - 1].replace(start, end + 1, form.fill("+", end - start + 1));
    map[y].replace(start, end + 1, form.fill("+", end - start + 1));
    map[y + 1].replace(start, end + 1, form.fill("+", end - start + 1));
    return map;
  }
  
  private StringBuilder[] vertical_way(StringBuilder[] map, int start, int end, int x) {
    if (start > end) {
      int cache = start;
      start = end;
      end = cache; 
    }
    for(int i = start; i <= end; i++) {
      map[i].replace(x - 1, x + 2, "+++");
    }
    return map;
  }

  private StringBuilder[] field(StringBuilder[] map, int[] pos) {
    if (pos[0] < 0) pos[0] = 0;
    if (pos[1] < 0) pos[1] = 0;
    if (pos[2] >= map[0].length()) pos[2] = map[0].length() - 1;
    if (pos[3] >= map.length) pos[3] = map.length - 1;
    for(int i = pos[1]; i < pos[3]; i++) {
      map[i].replace(pos[0], pos[2], form.fill("\"", pos[2] - pos[0]));
    }
  return map;
  }  
  
  private StringBuilder[] setUpMap(int width, int height) {
    StringBuilder[] map = new StringBuilder[height];
    for (int i = 0; i < map.length; i++) {
      map[i] = new StringBuilder(form.fill(" ", width));
    }
    return map;  
  }
  
  private StringBuilder[] collisionMap(StringBuilder[] map) {
    for (int i = 0; i < map.length; i++) {
      for (int j = 0; j < map[i].length(); j++) {
        if (map[i].charAt(j) == '#') map[i].setCharAt(j, '0');
        else map[i].setCharAt(j, ' ');
      }
    }
    return map;
  }
}
