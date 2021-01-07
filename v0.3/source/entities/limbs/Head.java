package source.entities.limbs;

import source.entities.Limb;
import source.objects.clothing.*;
import java.util.*;

public class Head implements Limb{
  String name = "Head";
  public HashMap<String, ArrayList<Clothing>> clothing_layers = new HashMap<String, ArrayList<Clothing>>();
  public boolean isEssential = true, is_functional = true;
  HashMap<String, Integer> sublimbs = new HashMap<String, Integer>();
  HashMap<String, Integer> max_lifepoints = new HashMap<String, Integer>();
  int[] maxhitpoints; 
  
  public Head(double strength_factor, String name) {//strength compared to adult Human
    this.name = name;
    sublimbs.put("head", (int) Math.round(70 * strength_factor));
    clothing_layers.put("head", new ArrayList<Clothing>());
    max_lifepoints.put("head", sublimbs.get("head"));
  }
  
  public void receive_damage(String limb, int damage) {
    sublimbs.replace(limb, sublimbs.get(limb) - damage);
    if(sublimbs.get(limb) <= 0) {
      sublimbs.remove(limb);
    }
    else if(max_lifepoints.get(limb) / 4 > sublimbs.get(limb)) {
      is_functional = false;
    }
  }
  
  //-------------------//GET DATA//-----------------------------//
  public String[] get_sublimbs() {
    String[] limbs = new String[sublimbs.size()];
    
    Set<String> keys = sublimbs.keySet();
    Iterator<String> it = keys.iterator();
    for(int i = 0; i < sublimbs.size(); i++)
      limbs[i] = it.next();
    return limbs;
  }
  
  public HashMap<String, Integer> get_lifepoints() {
    return sublimbs;
  }
  
  public HashMap<String, Integer> get_maxpoints() {
    return max_lifepoints;
  } 
  
  public boolean get_functionality() {
    return is_functional;
  }
  
  //-------------------//CLOTHING//-----------------------------//
  public void apply_clothing(String[] protect, Clothing clothing) {
    for (int i = 0; i < protect.length; i++) {
      if (sublimbs.containsKey(protect[i]))
         clothing_layers.get(protect[i]).add(clothing);
    }
  }
  
  public void disapply_clothing(String[] protect, Clothing clothing) {
    for (int i = 0; i < protect.length; i++) {
      if (clothing_layers.containsKey(protect[i])) 
         clothing_layers.get(protect[i]).remove(clothing);
    }
  }
  
  public String getName() {
    return name;
  }
}
