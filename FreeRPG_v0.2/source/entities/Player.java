package source.entities;

import source.entities.limbs.*;
import source.objects.Object;
import source.objects.clothing.*;
import source.objects.weapon.*;
import source.colors;
import java.util.*;
import java.io.File;
import java.io.PrintWriter;

public class Player extends entity{
  public ArrayList<Limb> limbs = new ArrayList();
  public Object in_left_hand, in_right_hand;
  public boolean has_left = true, has_right = true, is_conscious = true;
  public LinkedList<Object> clothings = new LinkedList();
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
    limbs.add(new Torso(1, "Body"));
    limbs.add(new Arm(1, "left arm"));
    limbs.add(new Arm(1, "right arm"));
    limbs.add(new Head(1, "Head"));
    limbs.add(new Leg(1, "left leg"));
    limbs.add(new Leg(1, "right leg"));
    wear(new Trousers("wool"));
    wear(new Shirt("wool"));
    in_left_hand = new Sword("iron");
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
          messages.add("you have lost hold of your " + in_left_hand.get_name());
          in_left_hand = null; }
        messages.add("your left arm is seriously damaged!");
      }
      break;
    case "right arm":
      if (limb.get_sublimbs().length <= 2 || !limb.get_functionality()) {
        has_right = false;
        if (in_right_hand != null) {
          messages.add("you have lost hold of your " + in_right_hand.get_name());
          in_right_hand = null; }
        messages.add("your right arm is seriously damaged!");
      }
      break;
    }
    return messages;
  }
  
  public void inspector_view(int width) {}
  
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
  
  public void wear(source.objects.Object cloth) {
    switch (cloth.getClass().getSimpleName()) {
    case "Trousers":
      int counter = 0;
      for (int i = 0; i < limbs.size(); i++) {
        if (limbs.get(i).getClass().getSimpleName().equals("Leg")) counter++;
      }
      //if wearable, put on clothing;
      if (counter == 2) {
          clothings.add(cloth);
        for (int i = 0; i < limbs.size(); i++) {
          if (limbs.get(i).getClass().getSimpleName().equals("Leg")) {
            limbs.get(i).apply_clothing(new String[]{"upper leg", "lower leg"}, cloth);
          }
        }
      }
      break;
    case "Shirt":
      boolean valid = false;
      for (int i = 0; i < limbs.size(); i++) {
        if (limbs.get(i).getClass().getSimpleName().equals("Torso")) {
          valid = true; 
          limbs.get(i).apply_clothing(new String[]{"upper body", "lower body"}, cloth);
          break;
        }
      }
      //if wearable, put on clothing additional on arms;
      if (valid) {
        clothings.add(cloth);
        for (int i = 0; i < limbs.size(); i++) {
          if (limbs.get(i).getClass().getSimpleName().equals("Arm")) {
            limbs.get(i).apply_clothing(new String[]{"upper arm", "lower arm"}, cloth);
          }
        }
      }
      break;
    case "Shoe":
      String site = cloth.get_name().replace(" shoe", "");
      valid = true;
      for (int i = 0; i < limbs.size(); i++) {
        if (limbs.get(i).getName().equals(site + " leg")) {
          String[] slimbs = limbs.get(i).get_sublimbs();
          for (String lmb : slimbs) {
            if (lmb.equals("foot")){
              valid = true; break;}
          } 
        }
      }
      //if wearable, put on clothing;
      if (valid) {
        clothings.add(cloth);
        for (int i = 0; i < limbs.size(); i++) {
          if (limbs.get(i).getName().equals(site + " leg")) {
            limbs.get(i).apply_clothing(new String[]{"foot"}, cloth);
            break;
          }
        }
      }
      break;
    case "Cap":
      valid = false;
      for (int i = 0; i < limbs.size(); i++) {
        if (limbs.get(i).getClass().getSimpleName().equals("Head")) {valid = true; break;}
      }
      //if wearable, put on clothing;
      if (valid) {
        clothings.add(cloth);
        for (int i = 0; i < limbs.size(); i++) {
          if (limbs.get(i).getClass().getSimpleName().equals("Head")) {
            limbs.get(i).apply_clothing(new String[]{"Head"}, cloth);
            break;
          }
        }
      }
      break;
    }
  }

  public void dewear(source.objects.Object cloth) {
    clothings.remove(cloth);
    switch (cloth.getClass().getSimpleName()) {
    case "Trousers":
      for (int i = 0; i < limbs.size(); i++) {
        if (limbs.get(i).getClass().getSimpleName().equals("Leg")) limbs.get(i).disapply_clothing(new String[]{"upper leg", "lower leg"}, cloth);
      }
      break;
    case "Shirt":
      for (int i = 0; i < limbs.size(); i++) {
        if (limbs.get(i).getClass().getSimpleName().equals("Torso")) limbs.get(i).disapply_clothing(new String[]{"upper body", "lower body"}, cloth);
        else if (limbs.get(i).getClass().getSimpleName().equals("Arm")) limbs.get(i).disapply_clothing(new String[]{"upper arm", "lower arm"}, cloth);
      }
      break;
    case "Shoe":
      String site = cloth.get_name().replace(" shoe", "");
      for (int i = 0; i < limbs.size(); i++) {
        if (limbs.get(i).getName().equals(site + " leg")) {
          limbs.get(i).disapply_clothing(new String[]{"foot"}, cloth); 
        }
      }        
      break;
    case "Cap":
      for (int i = 0; i < limbs.size(); i++) {
        if (limbs.get(i).getClass().getSimpleName().equals("Head")) {limbs.get(i).disapply_clothing(new String[]{"Head"}, cloth); break;}
      }        
      break;
    }
  }
  
  public void add_enemy(entity enemy) {}
  
  public void save(String location) {
    try{
      PrintWriter writer = new PrintWriter(new File(savegame + "/player/data.txt"));
      for(int i = 0; i < pos.length; i++) {
        writer.println(pos[i]);
      }
      writer.close();
    } catch(Exception e) {
      System.out.println("ERROR while saving player:" + e);
      new Scanner(System.in).next();
    }
  }
  
  public void load(String location, long id) {}
  
  public void clear_request() {
    request = "";
  }
  
  //currently unneeded
  public void set_role(String myRole) {}
}
