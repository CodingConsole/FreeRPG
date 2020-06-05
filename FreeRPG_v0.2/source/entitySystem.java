package source;

import java.util.*;
import java.io.*;
import source.entities.*;
import source.objects.*;
import source.objects.clothing.*;
import source.objects.weapon.*;
import source.objects.bio.*;
import source.entities.entities.*;


public class entitySystem {
  Random rand = new Random();
  Pathfinder path;
  String fileLocation; 
  List<building> buildings = new ArrayList<building>();
  List<entity> entities = new LinkedList<entity>();
  HashMap<source.objects.Object, int[]> objects = new HashMap<source.objects.Object, int[]>();
  
  public entitySystem(String file) { //used by game
    Player player = new Player(file);
    entities.add(player);
    this.fileLocation = file + "/map/" + player.pos[0] + "_" + player.pos[1];
  }
  
  public void init_path(StringBuilder[] colMap) {
    path = new Pathfinder(colMap, false);
  }
  
  public StringBuilder[] refresh_map(StringBuilder[] real_map) {
    StringBuilder[] current_map = new StringBuilder[real_map.length];
    for (int i = 0; i < real_map.length; i++) {//copy real map
      current_map[i] = new StringBuilder(real_map[i]);
    }
    for (source.objects.Object key : objects.keySet()) {//draw objects
      current_map[objects.get(key)[1]].setCharAt(objects.get(key)[0], key.get_symbol());
    } 
    for (int i = 1; i < entities.size(); i++) {//draw entities
      switch(entities.get(i).getClass().getSimpleName()) {
        case "Human":
          current_map[entities.get(i).get_pos()[1]].setCharAt(entities.get(i).get_pos()[0], 'H');
          break;
        default:
          current_map[entities.get(i).get_pos()[1]].setCharAt(entities.get(i).get_pos()[0], 'E');
          break;
      }
    }
    return current_map;
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
      humans.get(i).wear(new Shoe("wool", "left"));
      humans.get(i).wear(new Shoe("wool", "right"));
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
              objects.put(new Corpse(person.get_name()), person.get_pos().clone());
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
              objects.put(new Corpse(person.get_name()), person.get_pos().clone());
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
      for(int i = 0; i < entities.size(); i++) {
        entities.get(i).save(fileLocation + "/entities"); 
      }
      //save objects
      writer = new PrintWriter(new File(fileLocation + "/objects.txt"));
      for (source.objects.Object obj : objects.keySet()) {
        if (obj.getClass().getName().contains("clothing"))
          writer.println("c");
        else
          writer.println("o");
        writer.println(obj.get_material());
        writer.println(obj.get_name());
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
      String type, material, name;
      reader = new Scanner(new File(fileLocation + "/objects.txt"));
      while(reader.hasNext()) {
        type = reader.nextLine();
        material = reader.nextLine();
        name = reader.nextLine();
        System.out.println(name);
        int[] pos = {reader.nextInt(), reader.nextInt()}; 
        if (reader.hasNext())//go to next line if not at end
          reader.nextLine();
        switch (type) { //check what it is
        case "c": //clothing
          switch (name) {
          case "Cap":
            objects.put(new Cap(material), pos);
            break;
          case "Shirt":
            objects.put(new Shirt(material), pos);
            break;
          case "Trousers":
            objects.put(new Trousers(material), pos);
            break;
          case "left shoe":
          case "right shoe":
            objects.put(new Shoe(material, name.split(" ")[0]), pos);
            break;
          }
          break;
        case "o": //others
          switch (name.split(" ")[0]) {
          case "Sword":
            objects.put(new Sword(material), pos);
            break;
          case "Corpse":
            objects.put(new Corpse(material), pos);
            break;
          }
          break;
        }
      }
      reader.close();
    } catch(Exception e) {System.out.println("Error while loading:" + e);
      util.input_string(true);}    
  }
}
