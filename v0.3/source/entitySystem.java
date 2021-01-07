package source;

import java.util.*;
import java.io.*;
import source.entities.*;
import source.objects.*;
import source.objects.clothing.*;
import source.objects.weapons.*;
import source.objects.bio.*;
import source.entities.entities.*;


public class entitySystem {
  Random rand = new Random();
  colors color = new colors();
  Pathfinder path;
  String fileLocation; 
  List<building> buildings = new ArrayList<building>();
  List<entity> entities = new LinkedList<entity>();
  Player player;
  HashMap<source.objects.Object, int[]> objects = new HashMap<source.objects.Object, int[]>();
  
  public entitySystem(String file) { //used by game
    Player player = new Player(file);
    if (new File(file + "/player/data.txt").exists())
      player.load("", 0);
    this.player = player;
    entities.add(player);
    this.fileLocation = file + "/map/" + player.pos[0] + "_" + player.pos[1];
  }
  
  public void init_path(String[][] colMap) {
    path = new Pathfinder(colMap, false);
  }
  
  public String[][] refresh_map(String[][] real_map, String[][] to_edit) {
    int[] border = util.calc_view_border(player.pos, real_map.length, real_map[0].length);
    for (int i = border[2]; i <= border[3]; i++)
      for (int j = border[0]; j <= border[1]; j++)
        to_edit[i][j] = real_map[i][j];  

    for (source.objects.Object key : objects.keySet()) {//draw objects
      to_edit[objects.get(key)[1]][objects.get(key)[0]] = String.valueOf(key.get_symbol());
    } 
    for (int i = 1; i < entities.size(); i++) {//draw entities
      switch(entities.get(i).getClass().getSimpleName()) {
        case "Human":
          to_edit[entities.get(i).get_pos()[1]][entities.get(i).get_pos()[0]] = "H";
          break;
        default:
          to_edit[entities.get(i).get_pos()[1]][entities.get(i).get_pos()[0]] = "E";
          break;
      }
    }
    
    return to_edit;
  }
  
  public void gen_village(String location) {
    Names names = new Names();//load in namelist
    Random r = new Random();
    int workshops = (int) Math.floor(buildings.size() / 5), id = 0;
    String filled = "";
    List<Human> humans = new ArrayList<Human>();
    for (int i = 0; i < buildings.size(); i++) {
      if (i % 5 == 0) {
        switch (r.nextInt(5)) {
          case 0:
            if(!filled.contains("k")) {
              buildings.get(i).set_role("church");//church
              filled += "k";
              break;}
          case 1: 
            if(!filled.contains("s")) {
              buildings.get(i).set_role("blacksmith");//blacksmith
              filled += "s";
              break;}
          case 2:
            if(!filled.contains("c")) {
              buildings.get(i).set_role("carpender");//carpender
              filled += "c";
              break;}
          case 3:
            if(!filled.contains("b")) {
              buildings.get(i).set_role("butcher");//butcher
              filled += "b";
              break;}
          case 4:
            if(!filled.contains("w")) {
              buildings.get(i).set_role("clother");//clother 
              filled += "w"; 
            break;}
          default:
             buildings.get(i).set_role("mason");
        }
      }                 
      else {
        buildings.get(i).set_role("house");
        String surname = names.surnames[rand.nextInt(names.surnames.length)];
        int area = (buildings.get(i).pos[2] - buildings.get(i).pos[0]) * (buildings.get(i).pos[3] - buildings.get(i).pos[1]);
        int x = buildings.get(i).pos[0] + util.rand.nextInt(buildings.get(i).pos[2] - buildings.get(i).pos[0]), y = buildings.get(i).pos[1] + util.rand.nextInt(buildings.get(i).pos[3] - buildings.get(i).pos[1]);
        int[] loc = {x, y};
        humans.add(new Human(id++, loc, buildings.get(i).id, names.names_m[rand.nextInt(names.names_m.length)], surname, 'm'));
        loc = new int[]{buildings.get(i).pos[0] + util.rand.nextInt(buildings.get(i).pos[2] - buildings.get(i).pos[0]), buildings.get(i).pos[1] + util.rand.nextInt(buildings.get(i).pos[3] - buildings.get(i).pos[1])};
        humans.add(new Human(id++, loc, buildings.get(i).id, names.names_f[rand.nextInt(names.names_f.length)], surname, 'f'));
        for (int j = 0; j < area / 6 - 2; j++) {//spawn remaining entities
          loc = new int[]{x, y};
          boolean isMale = rand.nextBoolean();
          if (isMale) 
            humans.add(new Human(id++, loc, buildings.get(i).id, names.names_m[rand.nextInt(names.names_m.length)], surname, 'm'));
          else
            humans.add(new Human(id++, loc, buildings.get(i).id, names.names_f[rand.nextInt(names.names_f.length)], surname, 'f'));
          x = buildings.get(i).pos[0] + util.rand.nextInt(buildings.get(i).pos[2] - buildings.get(i).pos[0]);
          y = buildings.get(i).pos[1] + util.rand.nextInt(buildings.get(i).pos[3] - buildings.get(i).pos[1]);
        } 
      }
    }
    for(int i = 0; i < humans.size(); i++) {
      humans.get(i).wear(new Trousers("wool"));
      humans.get(i).wear(new Shirt("wool"));
      if(util.rand.nextBoolean()) humans.get(i).wear(new Cap("wool"));
      humans.get(i).wear(new Shoe("wool left"));
      humans.get(i).wear(new Shoe("wool right"));
      if (i % 5 == 0 && i < buildings.size()) {//give jobs
        humans.get(i).set_role(buildings.get(i).type);
      }
      else {
        humans.get(i).set_role("farmer");
      }
      entities.add(humans.get(i));
    }
  }
  
  public void add_building(int id, int[] pos, String role) {
    buildings.add(new source.entities.building(id, pos, role));
  }
  
  public void setup_npcs(int time, boolean act) {
    ArrayList<String> messages = new ArrayList<String>();
    for (int i = 0; i < entities.size(); i++) {
      if (act)
        entities.get(i).action(time, messages);   
      if (!entities.get(i).get_request().isEmpty()) {//if action was used for request -> process
        if (entities.get(i).getClass().getSimpleName().equals("Human")) {
          Human person = (Human) entities.get(i);
          building house = buildings.get(5);//please ignore
          int[] target;
          request: switch(entities.get(i).get_request()) {
            case "house":
              house = buildings.get(person.home_id);
              break;
            case "work":
              job: switch(person.get_role()) {  //search for right jobopportunities
                case "farmer":
                  house = buildings.get(buildings.size() - (rand.nextInt(4) + 1));
                  break;
                case "church":
                  for (int j = 0; j < buildings.size(); j += 5) {
                    if (buildings.get(j).type.equals("church")) {
                      house = buildings.get(j);
                      break;}
                  }
                  break;
                case "blacksmith":
                  for (int j = 0; j < buildings.size(); j += 5) {
                    if (buildings.get(j).type.equals("blacksmith")) {
                      house = buildings.get(j);
                      break;}
                  }               
                  break;
                  case "carpender":
                  for (int j = 0; j < buildings.size(); j += 5) {
                    if (buildings.get(j).type.equals("carpender")) {
                      house = buildings.get(j);
                      break;}
                  }              
                  break;
                case "butcher":
                  for (int j = 0; j < buildings.size(); j += 5) {
                    if (buildings.get(j).type.equals("butcher")) {
                      house = buildings.get(j);
                      break;}
                  }                
                  break;
                case "clother":
                  for (int j = 0; j < buildings.size(); j += 5) {
                    if (buildings.get(j).type.equals("clother")) {
                      house = buildings.get(j);
                      break;}
                  }
                  break; 
                case "mason":
                  for (int j = 0; j < buildings.size(); j += 5) {
                    if (buildings.get(j).type.equals("mason")) {
                      house = buildings.get(j);
                      break;}
                  }
                  break; 
              }
              target = new int[]{house.pos[0] + rand.nextInt(house.pos[2] - house.pos[0]), house.pos[1] + rand.nextInt(house.pos[3] - house.pos[1])};
              person.path = path.findPathTo(person.pos[0], person.pos[1], target[0], target[1]); 
              break;
            case "target_pos":
              target = person.target.get_pos();
              person.path = path.findPathTo(person.pos[0], person.pos[1], target[0], target[1]);
              break;
            case "dead":
              entities.remove(i);
              for (source.objects.Object cloth : person.clothings) {
                objects.put(cloth, person.get_pos().clone());
              }                                                                           
              if (person.in_left_hand != null) {
                objects.put(person.in_left_hand, person.get_pos().clone());
              }
              if (person.in_right_hand != null) {
                objects.put(person.in_right_hand, person.get_pos().clone());
              }
              objects.put(new Corpse("biological " + person.get_name()), person.get_pos().clone());
              break;   
            case "drop both hands":
              if (person.in_left_hand != null) {
                objects.put(person.in_left_hand, person.get_pos().clone());
                person.in_left_hand = null;
              }
              if (person.in_right_hand != null) {
                objects.put(person.in_right_hand, person.get_pos().clone());
                person.in_right_hand = null;
              }
              break; 
            case "drop left hand":
              if (person.in_left_hand != null) {
                objects.put(person.in_left_hand, person.get_pos().clone());
                person.in_left_hand = null;
              }
              break;
            case "drop right hand":
              if (person.in_right_hand != null) {
                objects.put(person.in_right_hand, person.get_pos().clone());
                person.in_right_hand = null;
              }
              break;                 
          }
        }
        else {//handle non-humans

        }
      }
    }
  }
  
  public ArrayList<String> manage_npcs(int time, ArrayList<String> messages) {
    for (int i = 0; i < entities.size(); i++) {
      messages = entities.get(i).action(time, messages);   
      if (!entities.get(i).get_request().isEmpty()) {//if action was used for request -> process
        if (entities.get(i).getClass().getSimpleName().equals("Human")) {
          Human person = (Human) entities.get(i);
          building house = buildings.get(5);//please ignore
          int[] target;
          request: switch(entities.get(i).get_request()) {
            case "house":
              house = buildings.get(person.home_id);
              break;
            case "work":
              job: switch(person.get_role()) {  //search for right jobopportunities
                case "farmer":
                  house = buildings.get(buildings.size() - (rand.nextInt(4) + 1));
                  break;
                case "church":
                  for (int j = 0; j < buildings.size(); j += 5) {
                    if (buildings.get(j).type.equals("church")) {
                      house = buildings.get(j);
                      break;}
                  }
                  break;
                case "blacksmith":
                  for (int j = 0; j < buildings.size(); j += 5) {
                    if (buildings.get(j).type.equals("blacksmith")) {
                      house = buildings.get(j);
                      break;}
                  }               
                  break;
                  case "carpender":
                  for (int j = 0; j < buildings.size(); j += 5) {
                    if (buildings.get(j).type.equals("carpender")) {
                      house = buildings.get(j);
                      break;}
                  }              
                  break;
                case "butcher":
                  for (int j = 0; j < buildings.size(); j += 5) {
                    if (buildings.get(j).type.equals("butcher")) {
                      house = buildings.get(j);
                      break;}
                  }                
                  break;
                case "clother":
                  for (int j = 0; j < buildings.size(); j += 5) {
                    if (buildings.get(j).type.equals("clother")) {
                      house = buildings.get(j);
                      break;}
                  }
                  break; 
                case "mason":
                  for (int j = 0; j < buildings.size(); j += 5) {
                    if (buildings.get(j).type.equals("mason")) {
                      house = buildings.get(j);
                      break;}
                  }
                  break; 
              }
              target = new int[]{house.pos[0] + rand.nextInt(house.pos[2] - house.pos[0]), house.pos[1] + rand.nextInt(house.pos[3] - house.pos[1])};
              person.path = path.findPathTo(person.pos[0], person.pos[1], target[0], target[1]); 
              break;
            case "target_pos":
              target = person.target.get_pos();
              person.path = path.findPathTo(person.pos[0], person.pos[1], target[0], target[1]);
              break;
            case "dead":
              messages.add(entities.get(i).get_name() + " died");
              
              entities.remove(i);
              for (source.objects.Object cloth : person.clothings) {
                objects.put(cloth, person.get_pos().clone());
              }                                                                           
              if (person.in_left_hand != null) {
                objects.put(person.in_left_hand, person.get_pos().clone());
              }
              if (person.in_right_hand != null) {
                objects.put(person.in_right_hand, person.get_pos().clone());
              }
              objects.put(new Corpse("biological" + person.get_name()), person.get_pos().clone());
              break;   
            case "drop both hands":
              if (person.in_left_hand != null) {
                objects.put(person.in_left_hand, person.get_pos().clone());
                person.in_left_hand = null;
              }
              if (person.in_right_hand != null) {
                objects.put(person.in_right_hand, person.get_pos().clone());
                person.in_right_hand = null;
              }
              break; 
            case "drop left hand":
              if (person.in_left_hand != null) {
                objects.put(person.in_left_hand, person.get_pos().clone());
                person.in_left_hand = null;
              }
              break;
            case "drop right hand":
              if (person.in_right_hand != null) {
                objects.put(person.in_right_hand, person.get_pos().clone());
                person.in_right_hand = null;
              }
              break;                 
          }
        }
        else {//handle non-humans

        }
        entities.get(i).clear_request();
        messages = entities.get(i).action(time, messages);//second attempt to work, because first was used as choice
      }
    }
    return messages;
  }
  
  public void save_location() {
    Corpse cache;
    try {
      PrintWriter writer = new PrintWriter(new File(fileLocation + "/buildings.txt"));
      for (int i = 0; i < buildings.size(); i++) {
        writer.println(buildings.get(i).id);
        writer.println(buildings.get(i).type);
        for (int j = 0; j < 4; j++) {
          writer.println(buildings.get(i).pos[j]);
        }
      }
      writer.close();
      //save entities
      new File(fileLocation + "/entities").mkdir();
      util.empty_directory(new File(fileLocation + "/entities"));
      for(int i = 0; i < entities.size(); i++) {
        entities.get(i).save(fileLocation + "/entities"); 
      }
      //save objects
      writer = new PrintWriter(new File(fileLocation + "/objects.txt"));
      for (source.objects.Object obj : objects.keySet()) {
        writer.println(obj.get_id());
        writer.println(obj.save());
      }
      writer.println("dn");
      for (source.objects.Object obj : objects.keySet()) {
        writer.println(objects.get(obj)[0] + " " + objects.get(obj)[1]);
      }
      writer.close();
    }
    catch(Exception e) {
      System.out.println(e);
      util.input_string(true);
    }
  }
  
  public void load_location(String location) {
    buildings.clear();  
    Player player = (Player) entities.get(0);
    entities.clear();
    entities.add(player);
    try{
      //load buildings
      Scanner reader = new Scanner(new File(fileLocation + "/buildings.txt"));
      while(reader.hasNext()) {
        int id = Integer.parseInt(reader.nextLine());
        String type = reader.nextLine();
        int[] coords = {Integer.parseInt(reader.nextLine()), Integer.parseInt(reader.nextLine()), Integer.parseInt(reader.nextLine()), Integer.parseInt(reader.nextLine())};
        buildings.add(new building(id, coords, type)); 
      }
      reader.close();
      
      //load entities
      for (final File fileEntry : new File(fileLocation + "/entities").listFiles()) {
        String name = fileEntry.getName();
        switch(name.charAt(0)) {//loading depending on type of entity(decided by first char)
        case 'h'://human
          entities.add(new Human(fileLocation + "/entities", Integer.parseInt(name.substring(1, name.length() - 4))));
        }
      }
      reader.close();
      
      //load objects
      reader = new Scanner(new File(fileLocation + "/objects.txt"));
      ArrayList<source.objects.Object> c_objects = new ArrayList();
      String cache = reader.nextLine();
      while(!cache.equals("dn")) {
        int id = Integer.parseInt(cache);
        String data = reader.nextLine();
        c_objects.add(util.create_object(id, data));
        cache = reader.nextLine();
      }
      int count = 0;
      while (reader.hasNext()) { 
        int[] pos = {Integer.parseInt(reader.next()), Integer.parseInt(reader.nextLine())};
        objects.put(c_objects.get(count), pos);
        count++;
      }
      reader.close();
    } catch(Exception e) {System.out.println("Error while loading:" + e);
      util.input_string(true);}    
  }
}
