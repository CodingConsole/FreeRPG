package source.entities.limbs;

import source.entities.Limb;
import source.objects.Object;
import source.objects.clothing.*;

import java.util.*;

public class Torso implements Limb{
  String name = "Torso";
  public HashMap<String, ArrayList<Object>> clothing_layers = new HashMap<String, ArrayList<Object>>();
  public boolean isEssential = true, is_functional = true;
  HashMap<String, Integer> sublimbs = new HashMap<String, Integer>();
  HashMap<String, Integer> max_lifepoints = new HashMap<String, Integer>();
  int[] maxhitpoints; 
  
  public Torso(double strength_factor, String name) {//strength compared to adult Human
    this.name = name;
    sublimbs.put("upper body", (int) Math.round(100 * strength_factor));
    clothing_layers.put("upper body", new ArrayList<Object>());
    sublimbs.put("lower body", (int) Math.round(60 * strength_factor));
    clothing_layers.put("lower body", new ArrayList<Object>());
    max_lifepoints.put("upper body", sublimbs.get("upper body"));
    max_lifepoints.put("lower body", sublimbs.get("lower body"));
  }
  
  public void receive_damage(String limb, int damage) {
    sublimbs.replace(limb, sublimbs.get(limb) - damage);
    if(sublimbs.get(limb) <= 0) {
      sublimbs.remove(limb);
      max_lifepoints.remove(limb);
      if (limb == "upper body") {
        sublimbs.remove("lower body");
        max_lifepoints.remove("lower body");
      }
      is_functional = false;
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
  public void apply_clothing(String[] protect, Object clothing) {
    for (int i = 0; i < protect.length; i++) {
      if (sublimbs.containsKey(protect[i]))
         clothing_layers.get(protect[i]).add(clothing);
    }
  }
  
  public void disapply_clothing(String[] protect, Object clothing) {
    for (int i = 0; i < protect.length; i++) {
      if (clothing_layers.containsKey(protect[i])) 
         clothing_layers.get(protect[i]).remove(clothing);
    }
  }
  
  public String getName() {
    return name;
  }
}
