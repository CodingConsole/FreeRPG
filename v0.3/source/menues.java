package source;

import java.util.*;
import java.io.File;
import java.io.PrintWriter;
import java.util.concurrent.TimeUnit;
import source.entities.*;
import source.entities.entities.*;
import source.objects.clothing.Clothing;

public class menues{
  private formator form = new formator(200, 72);
  private Generator gen = new Generator();
  private colors color = new colors();
  private Pathfinder path;
  
  //provide access to important variables across all menues
  String[][] real_map, materialmap, col_map, comb_map, map, draw;
  entitySystem entities;
  private Player player;
  private ArrayList<String> messages = new ArrayList<String>();
  boolean isNew;
  int x, y;
  int[] border;
  
  private ArrayList<String> menu;
  
  public int title() {
    util.clear();
    System.out.println(color.white_bg(form.fill(" ")) + "\n\n" + form.center("FreeRPG v0.3\n\n") + color.white_bg(form.fill(" ")) + "\n");
    System.out.println("1.) New Game\n2.) Load Game\n3.) Delete Game\n4.) Exit");
    int choice = util.input_numb(4);
    if (choice == 1) isNew = true;
    else if (choice == 2) isNew = false;
    else if (choice == 3) return 3;
    else if (choice == 4) return 4;
    return choice;
  }
  
  public int delete() {
    util.clear();
    ArrayList<String> names = new ArrayList<String>();
    int counter = 1;
    
    System.out.println("Which savegame do you want to delete? Press '0' to abort.");
    for (final File fileEntry : new File("data").listFiles()) {
      names.add(fileEntry.getName());
      System.out.println(counter + ".)" + names.get(counter - 1));
      counter++;
    }
    if (names.size() == 0) {
      System.out.println(color.red("No savegames detected. Returning to main menu."));
      try {TimeUnit.SECONDS.sleep(3);} catch(Exception e) {}
      return 0;
    }
    int choice = util.input_numb(1, names.size()) - 1;
    File delete = new File("data/" + names.get(choice));

    //delete
    for (File fileEntry : delete.listFiles()) {
      if (fileEntry.isDirectory()) {
        util.delete(fileEntry);
      }
      else
        fileEntry.delete();
    }
    delete.delete();
    
    return 0;
  }
  
  public int game() {
    util.clear();
    int time = 0;
    String[] controls = {"8", "2", "4", "6", "a", "l", "g", "i", "s", "t", "esc"};//up, down, left, right, attack, look around, grab, inventory, status, exit
    if (isNew) {
      entities = gen.new_game();
      player = (Player) entities.entities.get(0);
      time = 21600;
      try {
        /*
        form.spell("You wake up and look slowly around. ", 70);
        TimeUnit.SECONDS.sleep(1);
        form.spell("It seems like you have slept in a field.\n", 70);
        TimeUnit.SECONDS.sleep(1);
        form.spell("That is definitly not your home. ", 70);
        TimeUnit.SECONDS.sleep(1);
        form.spell("So why are you here? ", 70);
        TimeUnit.SECONDS.sleep(1);
        form.spell("In shock you realize that you cannot remember what happened to you.\n", 70);
        TimeUnit.SECONDS.sleep(1);
        form.spell("You do not even know your name or anything else about your life anymore.\n", 70);
        TimeUnit.SECONDS.sleep(1);
        form.spell("Maybe somebody in the village can help...\n", 70);
        TimeUnit.SECONDS.sleep(4);
        */
      } catch(Exception e) {}
      real_map = gen.load_map(player.pos[0], player.pos[1]);
      materialmap = gen.load_materialmap(player.pos[0], player.pos[1]);
      col_map = gen.load_col_map(player.pos[0], player.pos[1]);
      
      entities.init_path(col_map);
    }
    else {//load game (savegame-name)
      ArrayList<String> names = new ArrayList<String>();
      int counter = 1;
      
      System.out.println("Which savegame do you want to load? Press '0' to abort.");
      for (final File fileEntry : new File("data").listFiles()) {
        names.add(fileEntry.getName());
        System.out.println(counter + ".)" + names.get(counter - 1));
        counter++;
      }
      if (names.size() == 0) {
        System.out.println(color.red("No savegames detected. Returning to main menu."));
        try {TimeUnit.SECONDS.sleep(3);} catch(Exception e) {}
        return 0;
      }
      int choice = util.input_numb(1, names.size()) - 1;
      
      gen.savegame = names.get(choice);
      
      //load important data
      try {
        Scanner reader = new Scanner(new File("data/" + gen.savegame + "/data.txt"));
        time = reader.nextInt();
      } catch(Exception e) {
        System.out.println("Error while loading data in menues:" + e);
      }
      
      //create maplayers and entitysystem
      entities = new entitySystem("data/" + gen.savegame);
      player = (Player) entities.entities.get(0);
      //load map
      real_map = gen.load_map(player.pos[0], player.pos[1]);
      materialmap = gen.load_materialmap(player.pos[0], player.pos[1]);
      col_map = gen.load_col_map(player.pos[0], player.pos[1]);
      entities.init_path(col_map);
      entities.load_location(player.pos[0] + "_" + player.pos[1]);
      entities.setup_npcs(time, false);
    }
    
    comb_map = util.merge_maps(real_map, materialmap);
    
    map = new String[real_map.length][real_map[0].length];
    map = entities.refresh_map(comb_map, map); 
    
    //set up menu          
    int hour = (int) Math.floor(time / 3600), minute = (int) Math.floor(time % 3600 / 60), second = (int) Math.floor(time % ((hour * 3600) + minute * 60));
    menu = new ArrayList<String>();
    menu.add("pos:   " + player.pos[2] + " " + player.pos[3]);
    menu.add("time:  " + hour + ":" + minute + ":" + second);
    
    path = new Pathfinder(col_map, false);
    
    draw = new String[41][41];
    border = util.calc_view_border(player.pos, real_map[0].length, real_map.length);
    x = 20; y = 20; 
    if (border[1] == map[0].length - 1) {//far right
      x = draw[y].length - 1 - border[1] + player.pos[2];
    }
    else if(player.pos[2] <= 20) {//far left
      x = player.pos[2];
    }
    if (border[3] == map.length - 1) {//far bottom
      y = (border[3] - border[2]) - border[3] + player.pos[3];
    }
    else if(player.pos[3] <= 20) {//far top
      y = player.pos[3];
    }
    
    for (int i = 0; i <= border[3] - border[2]; i++) {
      draw[i] = new String[41];
      for (int j = 0; j <= border[1] - border[0]; j++)
        draw[i][j] = map[border[2] + i][border[0] + j];
    }
    draw[y][x] = "P";
    
    //gameloop
    boolean done = false;
    int dtime = 0, ctime = time;
    while(!done) {
      //draw
      util.clear();
      int draw_left = 0;
      for (; draw_left < menu.size(); draw_left++) {
        for (int i = 0; i < draw[draw_left].length; i++)
          System.out.print(draw[draw_left][i]);
        System.out.println(color.white_bg(" ") + menu.get(draw_left));
      }
      for (int i = draw_left; i <= border[3] - border[2]; i++) {
        for (int j = 0; j < draw[i].length; j++)
          System.out.print(draw[i][j]);
        System.out.println(color.white_bg(" "));
      }
      System.out.println(color.white_bg(form.fill(" ")));
      for (String message : messages) {
        System.out.println(message);
      }
      
      messages.clear();
      
      ctime = time;//save time for later dtime

      //input & controls
      int choose = util.input_choice(controls);
      switch(choose) {
      case 0://movement
        if (player.pos[3] > 0 && !col_map[player.pos[3] - 1][player.pos[2]].equals("0")){ player.pos[3] -= 1;
          dtime++;}
        break;
      case 1:
        if (player.pos[3] < map.length - 1 && !col_map[player.pos[3] + 1][player.pos[2]].equals("0")) {player.pos[3] += 1;
          dtime++;}
        break; 
      case 2:
        if (player.pos[2] > 0 && !col_map[player.pos[3]][player.pos[2] - 1].equals("0")){ player.pos[2] -= 1;
          dtime++;}
        break;
      case 3:
        if (player.pos[2] < map[0].length - 1 && !col_map[player.pos[3]][player.pos[2] + 1].equals("0")) {player.pos[2] += 1;
          dtime++;}
        break;
      case 4://other actions
        boolean has_attacked = attack();//enter attack menu
        if(has_attacked) dtime++;
        break;
      case 5:
        looking(border);
        break;
      case 6:
        if(grab())
          dtime++;
        break;
      case 7:
        inventory();
        break;
      case 8:
        status_screen();
        break;
      case 9:
        dtime = talk();
        break;    
      case 10://exit
        //save important data
        try {
          PrintWriter writer = new PrintWriter(new File("data/" + gen.savegame + "/data.txt"));
          writer.println(time);
          writer.close();
        } catch(Exception e) {
          System.out.println("Error while saving data in menues:" + e);
        }
        entities.save_location();
        done = true;
        break;
      }
      
      time += dtime;
      
      //simulation of world
      if (time > 86400) {//passed midnight
        time -= 86400;
        for (int i = 1; i < entities.entities.size(); i++) 
          entities.entities.get(i).isTomorrow = false;
      }
      
      //all npcs refresh if time has passed by
      for (int i = 0; i < dtime; i++) {
        messages = entities.manage_npcs(time, messages); 
      }

      if (dtime > 0) {   
        hour = (int) Math.floor(time / 3600);
        minute = (int) Math.floor((time - hour * 3600) / 60);
        second = (int) Math.floor(time % ((hour * 3600) + minute * 60));
      
        //refresh menu
        menu.set(0, "pos:   " + player.pos[2] + " " + player.pos[3]);
        menu.set(1, "time:  " + hour + ":" + minute + ":" + second);
      
        //limited view-distance
        border = util.calc_view_border(player.pos, map[0].length, map.length);
        for (int i = 0; i <= border[3] - border[2]; i++) {
            for (int j = 0; j < border[1] - border[0]; j++)
              draw[i][j] = map[i][j];
        }
        x = 20; y = 20; 
        if (border[1] == map[0].length - 1) {//far right
          x = draw[y].length - 1 - border[1] + player.pos[2];
        }
        else if(player.pos[2] <= 20) {//far left
          x = player.pos[2];
        }
        if (border[3] == map.length - 1) {//far bottom
          y = (border[3] - border[2]) - border[3] + player.pos[3];
        }
        else if(player.pos[3] <= 20) {//far top
          y = player.pos[3];
        }
          
        //refresh map & draw
        map = entities.refresh_map(comb_map, map);
        for (int i = 0; i <= border[3] - border[2]; i++) {
          for (int j = 0; j <= border[1] - border[0]; j++)
            draw[i][j] = map[border[2] + i][border[0] + j];
        }
        draw[y][x] = "P"; 
        
        //reset time
        dtime = 0;
      }    
    }
    
    return 0;
  }
  
  private boolean grab() {
    util.clear();
    ArrayList<source.objects.Object> pickable = new ArrayList<source.objects.Object>();
    for (source.objects.Object key : entities.objects.keySet()) { //find all pickables
      if (entities.objects.get(key)[0] == player.pos[2] && entities.objects.get(key)[1] == player.pos[3]) {
        pickable.add(key);  
      }  
    }
    if (pickable.size() == 0) {
      messages.add("There is nothing to grab");
      return false;
    }
    else if(player.in_left_hand != null && player.in_right_hand != null) {
      messages.add("You have no free hand");
      return false;
    }
      
    System.out.println("Choose object to pick up:");
    //output list of pickable objects  
    int counter = 1;
    for (int i = 0; i < pickable.size(); i++) {
      System.out.println(counter++ + ") " + pickable.get(i).get_material() + " " + pickable.get(i).get_name());
    } 
    int input = util.input_numb(1, counter) - 1;
    if (player.in_left_hand == null) {
      player.in_left_hand = pickable.get(input);
      entities.objects.remove(pickable.get(input));
    }
    else if (player.in_right_hand == null) {
      player.in_right_hand = pickable.get(input);
      entities.objects.remove(pickable.get(input));
    }
    return true;
  }
  
  //--------------//Gamemenues//---------------------//
  private int talk() {
    util.clear();
    //find valid partners(Human and in range 10)
    ArrayList<Human> partners = new ArrayList<Human>();
    char counter = 'a';
    for (int i = 1; i < entities.entities.size(); i++) {
      double x = Math.abs(entities.entities.get(i).get_pos()[0] - player.pos[2]), y = Math.abs(entities.entities.get(i).get_pos()[1] - player.pos[3]);
      if (entities.entities.get(i).getClass().getSimpleName().equals("Human") && 
         Math.pow(  Math.pow(x, 2) + Math.pow(y, 2), 1.0 / 2.0) <= 10) {
        partners.add((Human) entities.entities.get(i));
        map[partners.get(partners.size() - 1).get_pos()[1]][partners.get(partners.size() - 1).get_pos()[0]] = String.valueOf(counter++);
      }  
    }
    for (Human partner : partners) {
      draw[partner.get_pos()[1] - border[2]][partner.get_pos()[0] - border[0]] = map[partner.get_pos()[1]][partner.get_pos()[0]];
    }
    
    //output new menu
    int draw_left = 0;
    counter = 'a';
    for (; draw_left < partners.size(); draw_left++) {
      for (int i = 0; i < draw[draw_left].length; i++)
        System.out.print(draw[draw_left][i]);
      System.out.println(color.white_bg(" ") + counter + ")" + partners.get(draw_left).get_name());
      counter++;
    }
    for (int i = draw_left; i <= border[3] - border[2]; i++) {
      for (int j = 0; j < draw[i].length; j++)
          System.out.print(draw[i][j]);
      System.out.println(color.white_bg(" "));
    }
    System.out.println(color.white_bg(form.fill(" ")));
    for (String message : messages) {
      System.out.println(message);
    } 
    
    //choose valid options
    int choice = util.input_char(counter);
    ArrayList<String> options = new ArrayList<String>();
    if (partners.get(choice).in_combat && partners.get(choice).target == player) {
      options.add("Demand surrender");
      options.add("Ask to cease hostilities");
    } 
    else if (partners.get(choice).getClass().getSimpleName().equals("Human")){
      options.add("Who are you?");
      options.add("What are you doing?");
      options.add("What is your profession?");
      options.add("I want to buy something");
    }
    else {//talk to animals

    }
    
    //output options & input choice
    util.clear();
    for (int i = 0; i < options.size(); i++) {
      System.out.println(i + ") " + options.get(i));
    }
    int choice2 = util.input_numb(0, options.size() - 1);
    //partner answers
    messages = partners.get(choice).conversation(options.get(choice2), player, messages);

    return 4;
  }
  
  private boolean attack() {
    util.clear();
    char target = 'a';
    int count = 0;
    HashMap<entity, Integer[]> targets = new HashMap<entity, Integer[]>();
    
    //find valid entities and edit draw
    for (int i = 1; i < entities.entities.size(); i++) {
      if (Math.abs(entities.entities.get(i).get_pos()[0] - player.pos[2]) <= 1 && Math.abs(entities.entities.get(i).get_pos()[1] - player.pos[3]) <= 1) {
        draw[entities.entities.get(i).get_pos()[1] - border[2]][entities.entities.get(i).get_pos()[0] - border[0]]  = String.valueOf(target++);
        targets.put(entities.entities.get(i), new Integer[]{count++, i});
      }
    }
    
    //abort if no targets
    if (targets.size() == 0) return false;
    
    //output
    int draw_left = 0; target = 'a';
    for (entity etarget : targets.keySet()) {
      for (int i = 0; i < draw[draw_left].length; i++)
        System.out.print(draw[draw_left][i]);
      System.out.println(color.white_bg(" ") + target++ + ".)" + etarget.get_name());
      draw_left++;
    }
    for (int i = draw_left; i <= border[3] - border[2]; i++) {//draw remaining map after menu is done
      for (int j = 0; j < draw[draw_left].length; j++)
        System.out.print(draw[i][j]);
      System.out.println(color.white_bg(" "));
    } 
    System.out.println(color.white_bg(form.fill(" ")));
    
    //choose target
    int decision = util.input_letter(target, true);
    if (decision == -1) {//abort attack if player decides against it
      //refresh draw
      for (entity mtarget : targets.keySet()) {
        draw[mtarget.get_pos()[1] - border[2]][mtarget.get_pos()[0] - border[0]] = mtarget.get_icon();
      }  
      return false;
    }
    util.clear();
    
    //collect and draw limbs
    entity person = entities.entities.get(0);
    for (entity etarget : targets.keySet()) {
      if (targets.get(etarget)[0] == decision) {
        person = entities.entities.get(targets.get(etarget)[1]);
      }
    }
    HashMap<String, Integer> cachelimbs;
    HashMap<String, Integer> cachepoints;  
    int counter = 0, filler;  
    HashMap<Integer, Integer> main_limbs = new HashMap<Integer, Integer>();
    HashMap<Integer, String> sub_limbs = new HashMap<Integer, String>();
    String site = "", content;
    for (int i = 0; i < person.get_limbs().size(); i++) {
      cachelimbs = person.get_limbs().get(i).get_lifepoints();
      cachepoints = person.get_limbs().get(i).get_maxpoints();
      
      site = (person.get_limbs().get(i).getName().split(" ")[0].equals("right") || person.get_limbs().get(i).getName().split(" ")[0].equals("left")) ? person.get_limbs().get(i).getName().split(" ")[0] + " " : "";
      for (String key : cachelimbs.keySet()) {
        main_limbs.put(counter, i);
        sub_limbs.put(counter, key);
        content = counter++ + ": " + site + key + "( " + cachelimbs.get(key) + " / " + cachepoints.get(key) + " )"; 
        filler = 35 - content.length();
        if (filler > 0)
          System.out.print(content + form.fill(" ", filler));
        else 
          System.out.print(content);
      }
      System.out.print("\n");
    }
    
    //choose limb
    int t_limb = util.input_numb(0, counter); 
    int mlimb = main_limbs.get(t_limb);
    String sublimb = sub_limbs.get(t_limb);

    //choose attack 
    counter = 0;
    int attack; 
    ArrayList <source.objects.Object> attackop = new ArrayList<source.objects.Object>();
    if (player.in_left_hand == null && player.has_left) {
      System.out.println(counter++ + ": punch with left hand");
      attackop.add(null);
    }
    else {
      System.out.println(counter++ + ": beat with " + player.in_left_hand.get_name());
      attackop.add(player.in_left_hand);
    }
    if (player.in_right_hand == null && player.has_right) {
      System.out.println(counter++ + ": punch with right hand");
      attackop.add(null);
    }
    else {
      System.out.println(counter++ + ": beat with " + player.in_right_hand.get_name());
      attackop.add(player.in_right_hand);
    }
    
    //do attack
    int input = util.input_numb(0, counter);
    String type = "";
    if (attackop.get(input) == null) {//punch
      type = "fist";
      attack = util.rand.nextInt(3) + 3; 
    }
    else if (attackop.get(input) == player.in_left_hand){//if left hand
      type = player.in_left_hand.get_name();
      attack = (int) Math.round(player.in_left_hand.get_damage() * (util.rand.nextInt(2) + 2));
    }
    else {
      type = player.in_left_hand.get_name();
      attack = (int) Math.round(player.in_right_hand.get_damage() * (util.rand.nextInt(2) + 2));
    }
      
    person.add_enemy(player);
    
    if (util.rand.nextInt(100) >= 20) {
      person.get_limbs().get(mlimb).receive_damage(sublimb, attack);
      person.check_limb(person.get_limbs().get(mlimb), sublimb, messages);
      messages.add("you beat " + color.cyan(person.get_name()) + " in the " + sublimb + " with your " + type + " dealing " + color.red(String.valueOf(attack)) + " damage");
      messages = person.check_limb(person.get_limbs().get(mlimb), sublimb, messages);
    } else 
      messages.add("you missed " + person.get_name() + " with your " + type);

    return true;//completed attack successful
  }
  
  private void inventory() {
    boolean done = false;
    while (!done) {
      util.clear();
      System.out.println("0.) Exit");
      for (int i = 0; i < player.clothings.size(); i++) {
        System.out.println((i + 1) + ".)" + color.green(player.clothings.get(i).get_material() + " " + player.clothings.get(i).get_name()) + " (worn)");
      }
      int inhands = 0;
      if ( player.in_left_hand != null) {
       System.out.println((player.clothings.size() + 1) + ".)" + player.in_left_hand.get_material() + " " + player.in_left_hand.get_name() + "(left hand)"); inhands++;}
      if ( player.in_right_hand != null) {
       System.out.println((player.clothings.size() + 2) + ".)" + player.in_right_hand.get_material() + " " + player.in_right_hand.get_name() + "(right hand)"); inhands++;}
  
      int choice = util.input_numb(player.clothings.size() + inhands);
      if ( choice == 0 ) {done = true;}
      else if (choice != 0 && choice <= player.clothings.size()) {
        System.out.println("what do you want to do with " + player.clothings.get(choice - 1).get_material() + " " + player.clothings.get(choice - 1).getClass().getSimpleName() + "?");
        System.out.println("1.) undress");
        int choice2 = util.input_numb(1, 1); 
        switch(choice2) {
        case 1:
          if (player.in_left_hand == null)
            player.in_left_hand = player.clothings.get(choice - 1);
          else if (player.in_right_hand == null) player.in_right_hand = player.clothings.get(choice - 1);
          else entities.objects.put(player.clothings.get(choice - 1), new int[]{player.pos[2], player.pos[3]});
          player.dewear(player.clothings.get(choice - 1));
          break;
        }
      }
      else {//in hands or invalid if empty
        int selection = choice - player.clothings.size();
        source.objects.Object reference;
        if (selection == 1 && player.in_left_hand != null)
            reference = player.in_left_hand;
        else reference = player.in_right_hand;
        if (reference.getClass().getName().contains("clothing")) {
          System.out.println("1.) drop\n2.) wear");
          int choice2 = util.input_numb(1, 2);
          switch (choice2) {
          case 1:
            if (reference == player.in_left_hand) {
              entities.objects.put(reference, new int[]{player.pos[2], player.pos[3]});
              player.in_left_hand = null;}
            else if (reference == player.in_right_hand) {
              entities.objects.put(reference, new int[]{player.pos[2], player.pos[3]});
              player.in_right_hand = null;}
            break;
          case 2:
            if (reference == player.in_left_hand) {
              player.wear((Clothing) player.in_left_hand);
              player.in_left_hand = null;
            } else {
              player.wear((Clothing) player.in_right_hand);
              player.in_right_hand = null;
            }
            break;
          }
        }
        else { //no clothing
          System.out.println("1.) drop");
          int choice2 = util.input_numb(1, 2);
          switch (choice2) {
          case 1:
            if (reference == player.in_left_hand) {
              entities.objects.put(reference, new int[]{player.pos[2], player.pos[3]});
              player.in_left_hand = null;}
            else if (reference == player.in_right_hand) {
              entities.objects.put(reference, new int[]{player.pos[2], player.pos[3]});
              player.in_right_hand = null;}
            break;
          }
        }
      }   
    }
  }
  
  //manage player
  private void status_screen() {
    util.clear();
    formator form = new formator(200, 50);
    colors color = new colors();
    System.out.println(player.get_name());
    System.out.println(color.yellow_bg(form.fill(" ")));
    
    //show limbs
    HashMap<String, Integer> cachelimbs;
    HashMap<String, Integer> cachepoints;    
    for (int i = 0; i < player.limbs.size(); i++) {
      cachelimbs = player.limbs.get(i).get_lifepoints();
      cachepoints = player.limbs.get(i).get_maxpoints();
      Iterator it = cachelimbs.entrySet().iterator();
      for (String key : cachelimbs.keySet()) {
        System.out.print(key + "( " + cachelimbs.get(key) + " / " + cachepoints.get(key) + " )   ");
      }
      System.out.print("\n");
    }
    System.out.println(color.yellow_bg(form.fill(" ")));
    
    System.out.println("press ENTER to leave statusscreen");
    util.input_string(true);
  }          
  
  private void looking(int[] border) {
    util.clear();
    ArrayList<String> menu = new ArrayList<String>();
    HashMap<String, entity> entity_options = new HashMap<String, entity>();
    HashMap<String, source.objects.Object> object_options = new HashMap<String, source.objects.Object>();
    menu.add("use the movement keys to look around. press '0' to leave");
    menu.add("Here we have:");
    menu.add("You");

    int[] marker = {player.pos[2], player.pos[3], x, y};
    char count = 'a'; //find entities + objects and add to options
    for (int i = 0; i < entities.entities.size(); i++) {
      if (entities.entities.get(i).get_pos()[0] == marker[0] && entities.entities.get(i).get_pos()[1] == marker[1]) {
        menu.add(count + ") " + entities.entities.get(i).get_name()); 
        entity_options.put(String.valueOf(count++), entities.entities.get(i));
      }
    }
    for (source.objects.Object obj : entities.objects.keySet()) {
      if (entities.objects.get(obj)[0] == marker[0] && entities.objects.get(obj)[1] == marker[1]) {
        menu.add(count + ") " + obj.get_name()); 
        object_options.put(String.valueOf(count++), obj);
      }
    }
    //set marker
    draw[marker[3]][marker[2]] = "X";

    //look-around-loop
    boolean done = false;
    while(!done) {
      util.clear();
      int draw_left = 0;
      for (; draw_left < menu.size(); draw_left++) {
        for (int i = 0; i < draw[draw_left].length; i++)
          System.out.print(draw[draw_left][i]);
        System.out.println(color.white_bg(" ") + menu.get(draw_left));
      }
      for (int i = draw_left; i <= border[3] - border[2]; i++) {
        for (int j = 0; j < draw[i].length; j++)
          System.out.print(draw[i][j]);
        System.out.println(color.white_bg(" "));
      }
      System.out.println(color.white_bg(form.fill(" ")));
      
      //remove old cursor
      draw[marker[3]][marker[2]] = map[marker[1]][marker[0]];
      
      //CONTROLS
      char choose = util.input_char(true, true, count);

      switch(choose) {
      case '8'://movement
        if (marker[3] > 0){ 
          marker[1]--; marker[3]--;
        }
        break;
      case '2':
        if (marker[3] < draw.length - 1){ 
          marker[1]++; marker[3]++;
        }
        break;
      case '4':
        if (marker[2] > 0) {
          marker[0]--; marker[2]--;
        }
        break;
      case '6':
        if (marker[2] < draw[0].length - 1) {
          marker[0]++; marker[2]++;
        }
        break; 
      case '0':
        //remove old cursor
        draw[marker[3]][marker[2]] = map[marker[1]][marker[0]];
        return;
      default:
        if (entity_options.containsKey(String.valueOf(choose)))
          inspect_entity(entity_options.get(String.valueOf(choose)));
        else if (object_options.containsKey(String.valueOf(choose)))
          inspect_object(object_options.get(String.valueOf(choose)));
        break;
      }
      
      //draw player(because not included in map)
      draw[y][x] = "P"; 
      //draw marker
      draw[marker[3]][marker[2]] = "X";
      
      //clear menu
      menu.clear();
      menu.add("Here we have:");
      
      //refresh entities
      entity_options.clear();
      count = 'a';
      if (!map[marker[1]][marker[0]].equals(real_map[marker[1]][marker[0]])) {
        for (int i = 0; i < entities.entities.size(); i++) {
          if(entities.entities.get(i).get_pos()[0] == marker[0] && entities.entities.get(i).get_pos()[1] == marker[1]) {
            menu.add(count + ")" + entities.entities.get(i).get_name());
            entity_options.put(String.valueOf(count++), entities.entities.get(i));
          }
        }
        for (source.objects.Object obj : entities.objects.keySet()) {
          if (entities.objects.get(obj)[0] == marker[0] && entities.objects.get(obj)[1] == marker[1]) {
            menu.add(count + ") " + obj.get_name()); 
            object_options.put(String.valueOf(count++), obj);
          }
        }
      }      
    }    
  }
  
  //inspect person
  private void inspect_entity(entity to_inspect) {
    util.clear();
    to_inspect.inspector_view(200);
    
    util.input_string(true);
  }
  
  //inspect object
  private void inspect_object(source.objects.Object object) {
    util.clear();
    object.inspector_view(200);
    
    util.input_string(true);
  }  
}
