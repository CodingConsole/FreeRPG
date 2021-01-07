package source.entities;

import source.entities.limbs.*;
import source.objects.Object;
import source.objects.clothing.Clothing;
import source.objects.clothing.*;
import source.objects.weapons.*;
import source.colors;
import source.util;

import java.util.*;
import java.io.File;
import java.io.PrintWriter;

public class Player extends entity{
  public ArrayList<Limb> limbs = new ArrayList();
  public Object in_left_hand, in_right_hand;
  public boolean has_left = true, has_right = true, is_conscious = true;
  public ArrayList<Clothing> clothings = new ArrayList();
  public int[] pos;
  private String request = "", savegame;
  
  public Player(String savegame) {
    this.savegame = savegame;
    pos = new int[4];
    try{//load data
      Scanner reader = new Scanner(new File(savegame + "/player/data.txt"));
      for(int i = 0; i < pos.length; i++) {
        pos[i] = Integer.parseInt(reader.nextLine());
      }
      reader.close();
    } catch(Exception e) {
      pos = new int[]{0, 0, 20, 20};
    }
    //init limbs
    limbs.add(new Torso(1, "torso"));
    limbs.add(new Arm(1, "left arm"));
    limbs.add(new Arm(1, "right arm"));
    limbs.add(new Head(1, "head"));
    limbs.add(new Leg(1, "left leg"));
    limbs.add(new Leg(1, "right leg"));
  }
  
  //used (maybe) later
  public ArrayList<String> action(int time, ArrayList<String> messages) {return messages;}
  
  public ArrayList<String> conversation(String said, entity partner, ArrayList<String> messages) {
    return messages;
  }
  
  public ArrayList<String> check_limb(Limb limb, String sublimb, ArrayList<String> messages) {
    switch (limb.getName()) {
    case "Body":
      if (limb.get_sublimbs().length <= 1) {
        //dead
      }
      break;
    case "Head":
      if (limb.get_sublimbs().length < 1) {
        //dead
      }
      else if(!limb.get_functionality()) {
        is_conscious = false;
        in_left_hand = null;
        in_right_hand = null;
        messages.add("you have become unconscious"); 
      } 
      break;      
    case "left arm":
      if (limb.get_sublimbs().length <= 2 || !limb.get_functionality()) {
        has_left = false;
        if (in_left_hand != null) {
          messages.add("You have lost hold of your " + in_left_hand.get_name());
          in_left_hand = null; }
        messages.add("Your left arm is dysfunctional!");
      }
      break;
    case "right arm":
      if (limb.get_sublimbs().length <= 2 || !limb.get_functionality()) {
        has_right = false;
        if (in_right_hand != null) {
          messages.add("You have lost hold of your " + in_right_hand.get_name());
          in_right_hand = null; }
        messages.add("Your right arm has become dysfunctional!");
      }
      break;
    }
    return messages;
  }
  
  public void inspector_view(int width) {}
  
  public String get_icon() {
    return "P";
  }
  
  public int[] get_pos() {
    return new int[]{pos[2], pos[3]};
  }
  
  public String get_name() {
    return new colors().blue("Player");
  }
  
  public String get_role() {
    return "Player";
  }
  
  public ArrayList<Limb> get_limbs() {
    return limbs;
  }
  
  public String get_request() {
    return request;
  }
  
  public void wear(Clothing cloth) {
    ArrayList<String> req_limbs = cloth.get_required_limbs();
    //check required limbs
    for (int i = 0; i < limbs.size(); i++) {
      for (int j = 0; j < req_limbs.size(); j++) {
        if (limbs.get(i).getName().equals(req_limbs.get(j))) {
          ArrayList<String> req_sublimbs = cloth.get_required_sublimbs(req_limbs.get(j));
          String[] sublimbs = limbs.get(i).get_sublimbs();
          //check required sublimbs
          for (int k = 0; k < sublimbs.length; k++) {
            for (int l = 0; l < req_sublimbs.size(); l++) {
              if (sublimbs[k].equals(req_sublimbs.get(l)))
                req_sublimbs.remove(l);
            }
          }
          if (req_sublimbs.size() == 0)
            req_limbs.remove(j);
        }
      }
    }
    
    //if conditions met, apply
    if (req_limbs.size() == 0) {
      clothings.add(cloth);
      ArrayList<String> prot_limbs = cloth.get_protected_limbs();
      for (int i = 0; i < limbs.size(); i++) {
        for (int j = 0; j < prot_limbs.size(); j++ ) {
          if (limbs.get(i).getName().equals(prot_limbs.get(j))) {
            //get sublimbs and convert
            java.lang.Object[] list = cloth.get_sublimbs(limbs.get(i).getName()).toArray();
            String[] data = new String[list.length];
            for (int k = 0; k < list.length; k++ ) {
              data[k] = (String) list[k];
            }
            limbs.get(i).apply_clothing(data, cloth);
            prot_limbs.remove(j);
          }
        }
      }    
    }
  }

  public void dewear(Clothing cloth) {
    clothings.remove(cloth);
    ArrayList<String> prot_limbs = cloth.get_protected_limbs();
    for (int i = 0; i < limbs.size(); i++) {
      for (int j = 0; j < prot_limbs.size(); j++ ) {
        if (limbs.get(i).getName().equals(prot_limbs.get(j))) {
          //get sublimbs and convert
          java.lang.Object[] list = cloth.get_sublimbs(limbs.get(i).getName()).toArray();
          String[] data = new String[list.length];
          for (int k = 0; k < list.length; k++ ) {
            data[k] = (String) list[k];
          }
          limbs.get(i).disapply_clothing(data, cloth);
          prot_limbs.remove(j);
        }
      }
    } 
  }
  
  public void add_enemy(entity enemy) {}
  
  public void save(String location) {
    try{ //save position
      PrintWriter writer = new PrintWriter(new File(savegame + "/player/data.txt"));
      for(int i = 0; i < pos.length; i++) {
        writer.println(pos[i]);
      }
      writer.close();
      
      //save cloathings + TODO: hands
      writer = new PrintWriter(new File(savegame + "/player/inventory.txt"));
      for (int i = 0; i < clothings.size(); i++) {
        writer.println(clothings.get(i).get_id());
        writer.println(clothings.get(i).save());  
      }
      writer.print("dn\n");
      
      //save hands
      if (in_left_hand != null) {
        writer.println(in_left_hand.get_id());
        writer.println(in_left_hand.save());
      } 
      writer.println("dn\n");
      if (in_right_hand != null) {
        writer.println(in_right_hand.get_id());
        writer.println(in_right_hand.save());
      } 
      writer.println("dn\n");
      
      writer.close();    
    } catch(Exception e) {
      System.out.println("ERROR while saving player:" + e);
      new Scanner(System.in).next();
    }
  }
  
  public void load(String location, long id) {
    try {
      Scanner reader = new Scanner(new File(savegame + "/player/inventory.txt"));
      String line;
    
      line = reader.nextLine();
      //load clothings  
      while (!line.equals("dn")) {
        wear((Clothing) util.create_object(Integer.parseInt(line), reader.nextLine()));
        line = reader.nextLine();
      }
      
      //load hands
      line = reader.nextLine();
      if (!line.equals("dn")) {
        in_left_hand = util.create_object(Integer.parseInt(line), reader.nextLine());
      }
      line = reader.nextLine();
      if (!line.equals("dn")) {
        in_right_hand = util.create_object(Integer.parseInt(line), reader.nextLine());
      }
      
      reader.close();
    } catch (Exception e) { 
      System.out.println("ERROR while loading player:\n" + e);
      new Scanner(System.in).next();}
  }
  
  public void clear_request() {
    request = "";
  }
  
  //currently unneeded
  public void set_role(String myRole) {}
}
