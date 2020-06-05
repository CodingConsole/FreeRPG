package source.entities.entities;
import source.entities.*;
import source.entities.limbs.*;
import source.objects.Object;
import source.objects.clothing.*;
import source.objects.weapon.*;
import source.formator;
import source.util;
import source.colors;
import java.io.*;
import java.util.*;

public class Human extends entity{
  public long id;
  public int[] pos = new int[2];
  public char gender;
  
  public String role = "", profession, request = "", current_task = "", name, surname;
  public int time_done = 0;//time, where task will be done
  public int home_id;
  public List <Node> path;
  
  public boolean in_combat = false, is_alive = true;
  private int conversation = 0;
  public Object in_left_hand, in_right_hand;
  public boolean has_left = true, has_right = true, is_conscious = true;
  public List<entity> enemies = new ArrayList<entity>();
  public entity target;
  
  private ArrayList<Limb> limbs = new ArrayList<>();
  public LinkedList<Object> clothings = new LinkedList();
  
  public Human(long id, int[] pos2, int home, String name, String surname, char gender) {//constructor used for new creation
    this.id = id;
    pos[0] = pos2[0];
    pos[1] = pos2[1];
    this.home_id = home;
    this.name = name;
    this.surname = surname;
    this.gender = gender;
    
    init_limbs();  
  }
  
  public Human(String location, long id) {//constructor used for loading
    init_limbs();
    load(location, id);
  }  
  
  private void init_limbs() {
    //init limbs
    limbs.add(new Torso(1, "Body"));
    limbs.add(new Arm(1, "left arm"));
    limbs.add(new Arm(1, "right arm"));
    limbs.add(new Head(1, "Head"));
    limbs.add(new Leg(1, "left leg"));
    limbs.add(new Leg(1, "right leg"));
  }
  
  public ArrayList<String> check_limb(Limb limb, String sublimb, ArrayList<String> messages) {
    switch (limb.getName()) {
    case "Body":
      if (limb.get_sublimbs().length <= 1) {
        request = "dead";
        is_alive = false;
      }
      break;
    case "Head":
      if (limb.get_sublimbs().length < 1) {
        request = "dead";
        is_alive = false;
      }
      else if(!limb.get_functionality()) {
        is_conscious = false;
        request = "drop both hands";
        messages.add(name + " " + surname + " has become unconscious"); 
      } 
      break;      
    case "left arm":
      if (limb.get_sublimbs().length <= 2 || !limb.get_functionality()) {
        has_left = false;
        request = "drop left hand";
        messages.add(name + " " + surname + " left arm is seriously damaged!");
      }
      break;
    case "right arm":
      if (limb.get_sublimbs().length <= 2 || !limb.get_functionality()) {
        has_right = false;
        request = "drop right hand";
        messages.add(name + " " + surname + " right arm is seriously damaged!");
      }
      break;
    }
    return messages;
  }
  
  public void inspector_view(int width) {
    formator form = new formator(width, 50);
    colors color = new colors();
    System.out.println(name  + " " + surname);
    System.out.println(color.yellow_bg(form.fill(" ")));
    
    //show limbs
    HashMap<String, Integer> cachelimbs;
    HashMap<String, Integer> cachepoints;
    String site = "", content; 
    int filler = 0;   
    for (int i = 0; i < limbs.size(); i++) {
      cachelimbs = limbs.get(i).get_lifepoints();
      cachepoints = limbs.get(i).get_maxpoints();
      Iterator it = cachelimbs.entrySet().iterator();
      for (String key : cachelimbs.keySet()) {
        site = (limbs.get(i).getName().split(" ")[0].equals("right") || limbs.get(i).getName().split(" ")[0].equals("left")) ? limbs.get(i).getName().split(" ")[0] + " " : "";
        content = site + key + "( " + cachelimbs.get(key) + " / " + cachepoints.get(key) + " )   ";
        filler = 35 - content.length();
        if (filler > 0)
          System.out.print(content + form.fill(" ", filler));
        else 
          System.out.print(content);
      }
      System.out.print("\n");
    }
    System.out.println(color.yellow_bg(form.fill(" ")));
    //show clothings
    for (source.objects.Object obj : clothings) {
      System.out.println(obj.get_name() + "(worn)");
    }
  }
  
  public int[] get_pos() {
    return pos;
  }
  
  public String get_name() {
    return name + " " + surname;
  }
  
  public ArrayList<Limb> get_limbs() {
    return limbs;
  }
  
  public String get_request() {
    return request;
  }
  
  public void clear_request() {
    request = "";
  }
  
  public String get_role() {
    return role;
  }
  
  private void choose_task(int time) {
    if (time >= 21600 && time <= 64800) { 
      request = "work";
      current_task = "going to work";
      if (role == "farmer" && current_task == "going to work") {
        time_done = time + 7200 + new Random().nextInt(3600);
      } 
      else {//shop is open for a specific amount of time
        time_done = 64800;
      }
      isTomorrow = false;
    }
    else {
      request = "home";
      current_task = "going to sleep";
      time_done = 21600;
      isTomorrow = true;
    }
  }
  
  public ArrayList<String> action(int time, ArrayList<String> messages) { //loop where AI does what has to be done
    if (is_conscious && in_combat) {//combat behaviour
      if (target == null) {//choose closest enemy if no target 
        if (enemies.size() != 0) {
          entity closest = enemies.get(0);
          int x_dist = 1000, y_dist = 1000;
          int distance = 10000;
          for (int i = 0; i < enemies.size(); i++) {
            if (enemies.get(i).is_alive) {
              int dist = Math.abs(enemies.get(i).get_pos()[0] - pos[0]) + Math.abs(enemies.get(i).get_pos()[1] - pos[1]);
              if (dist < x_dist + y_dist) {
                x_dist = Math.abs(enemies.get(i).get_pos()[0] - pos[0]);
                y_dist = Math.abs(enemies.get(i).get_pos()[1] - pos[1]);
                distance = dist;
                target = enemies.get(i);
              }
            } else {//remove dead enemies
              enemies.remove(i);
              if (enemies.size() == 0) {
                target = null;//cleaning up
                in_combat = false;
                choose_task(time);
              }
            }
          }
          //request path to target
          if (x_dist > 1 || y_dist > 1) {
            request = "target_pos";
            return messages;
          }
          else {//already at position
            path.clear();
          }
          fight(messages);
        } 
        else {// no more enemies => leave combat mode
          target = null;//cleaning up
          in_combat = false;
          choose_task(time);
        }
      }
      else {//all parameters set:
        fight(messages);
      }
    }
    else if (is_conscious && conversation == 0){//outside combat behaviour
      if (time_done <= time && !isTomorrow) {
        choose_task(time);
      }
      else if (current_task.startsWith("go")){
        if (path != null && path.size() != 0) {
          pos[0] = path.get(0).x;
          pos[1] = path.get(0).y;
          path.remove(0);
          return messages;}
        else {//at end of path
          switch(current_task) {
          case "going to work":
              current_task = "working";
              break;
          case "going to sleep":
              current_task = "sleeping";
              break;
          }
        }
      }
    }
    else if (conversation > 0)
      conversation--;
    return messages;
  }
  
  public ArrayList<String> conversation(String said, entity partner, ArrayList<String> messages) {
    conversation = 4;
    if (in_combat && target.getClass().getSimpleName().equals("Player")) {
      switch (said) {
      case "Demand surrender":
        messages.add("I will no more attack you.");
        break;
      case "Ask to cease hostilities":
        messages.add("ok. Let us end this battle.");
        break;
      }
      target = null;
      for(int i = 0; i < enemies.size(); i++) {
        if (enemies.get(i).getClass().getSimpleName().equals("Player")) {
          enemies.remove(i);
          return messages;
        }
      }
    }
    else {
      switch (said) {
      case "Who are you?":
        messages.add("My name is " + get_name());
        break;
      case "What are you doing?":
        messages.add("I am " + current_task);
        break;
      case "What is your profession?":
        messages.add("I work as a " + profession);
        break;
      case "I want to buy something":
        if (!profession.equals("farmer") && !profession.equals("temple")) 
          messages.add("This feature is not implemented right now!");
        else 
          messages.add("I am not selling anything!");  
        break;
      }
    }
    return messages;
  }
  
  private ArrayList<String> fight(ArrayList<String> messages) {
    //check position
    int[] tpos = target.get_pos();
    String type = "fist";
    if (Math.abs(tpos[0] - pos[0]) <= 1 && Math.abs(tpos[1] - pos[1]) <= 1 && (has_left || has_right)) { //attack if close enough
      int attack = util.rand.nextInt(3) + 3;
      if (in_left_hand != null) {
        attack = (int) Math.round(in_left_hand.get_damage() * (util.rand.nextInt(2) + 2));
        type = in_left_hand.get_name();
      }
      if (in_right_hand != null) {
        attack = in_right_hand.get_damage() > attack ? (int) Math.round(in_left_hand.get_damage() * (util.rand.nextInt(2) + 2)) : attack;
        type = in_right_hand.get_name();
      }
      ArrayList<Limb> tar_limbs = target.get_limbs();
      int choice = util.rand.nextInt(tar_limbs.size());
      String[] tar_sublimbs = tar_limbs.get(choice).get_sublimbs();
      int choice2 = util.rand.nextInt(tar_sublimbs.length);
      //do attack
      if (util.rand.nextInt(100) >= 20) {
        tar_limbs.get(choice).receive_damage(tar_sublimbs[choice2], attack);
        target.check_limb(tar_limbs.get(choice), tar_sublimbs[choice2], messages);
        //add message
        messages.add(get_name() + " hits " + target.get_name() + " in the " + tar_sublimbs[choice2] + " with their " + type + " dealing " + attack + " damage");
      } else
        messages.add(name + " " + surname + " missed " + target.get_name() + " with their " + type);
      return messages;
    }
    else {//move to enemy
      if (path != null && path.size() > 0) {
        pos[0] = path.get(0).x;
        pos[1] = path.get(0).y;
        path.remove(0);
      }
      else  //enemy moved away
        request = "target_pos";
    }
    return messages;
  }
  
  public void add_enemy(entity enemy) {
    in_combat = true;
    if (!enemies.contains(enemy)) {
      enemies.add(enemy);
    }
  }
  
  public void set_role(String myRole) {
    role = myRole;//used for call
    profession = role;//will be used later
  }
  
  public void save(String location) {
    try {
      PrintWriter writer = new PrintWriter(new File(location + "/h" + id + ".txt"));
      writer.println(pos[0]);
      writer.println(pos[1]);
      writer.println(get_name());
      writer.println(gender);
      writer.println(role);
      writer.println(home_id);
      writer.println(current_task);
      if (path != null && path.size() > 0) {
        writer.println(path.get(path.size() - 1).x + " " + path.get(path.size() - 1).y);
      }
      for (int i = 0; i < clothings.size(); i++) {
        switch (clothings.get(i).get_name()) {
        case "Trousers":
          writer.print("tr ");  
          break;
        case "Shirt":
          writer.print("sh ");
          break;  
        case "left shoe":
          writer.print("ls ");
          break;
        case "right shoe":
          writer.print("rs ");
          break;
        case "Cap":
          writer.print("c ");
          break;
        }
        writer.print(clothings.get(i).get_material() + " ");
      }
      writer.print("dn\n");
      writer.close();
    }
    catch(Exception e) {}
  }
  
  public void load(String location, long id) {
    try {
      Scanner reader = new Scanner(new File(location + "/h" + id + ".txt"));
      this.id = id;
      pos[0] = Integer.parseInt(reader.nextLine());
      pos[1] = Integer.parseInt(reader.nextLine());
      name = reader.next();
      surname = reader.nextLine();
      gender = reader.nextLine().charAt(0);
      role = reader.nextLine();
      profession = role;
      home_id = Integer.parseInt(reader.nextLine());
      current_task = reader.nextLine();
      String coord1 = reader.next(), isdone;
      try { //coordinates exist
        int i = Integer.parseInt(coord1);
        request = coord1 + " " + reader.nextLine();
        isdone = reader.next();
      }
      catch(Exception e) {
        isdone = coord1;
      }

      while (!isdone.equals("dn")) {
        switch(isdone) {
        case "tr":
          wear(new Trousers(reader.next()));
          break;
        case "sh":
          wear(new Shirt(reader.next()));
          break;
        case "ls":
          wear(new Shoe(reader.next(), "left"));
          break;
        case "rs":
          wear(new Shoe(reader.next(), "right"));
          break;
        case "c":
          wear(new Cap(reader.next()));
          break;        
        }
        isdone = reader.next();
      }
      reader.close();
    }
    catch(Exception e) {System.out.println("Class: Human | ERROR while loading\n" + location + "/h" + id + ".txt\n" + e);
      Scanner scan = new Scanner(System.in);
      scan.next();
    }    
  }
  
  public void wear(Object cloth) {
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
        if (limbs.get(i).getClass().getSimpleName().equals("Torso")) {valid = true; break;}
      }
      //if wearable, put on clothing;
      if (valid) {
        clothings.add(cloth);
        for (int i = 0; i < limbs.size(); i++) {
          if (limbs.get(i).getClass().getSimpleName().equals("Arm")) {
            limbs.get(i).apply_clothing(new String[]{"upper arm", "lower arm"}, cloth);
          }
          else if (limbs.get(i).getClass().getSimpleName().equals("Torso")) {
            limbs.get(i).apply_clothing(new String[]{"upper body", "lower body"}, cloth);
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
  
  public void dewear(Object cloth) {
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
}
