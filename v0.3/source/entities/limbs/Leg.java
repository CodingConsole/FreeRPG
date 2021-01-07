package source.entities.limbs;

import source.entities.Limb;
import source.objects.clothing.*;

import java.util.*;

public class Leg implements Limb{
  String name = "Leg";
  public HashMap<String, ArrayList<Clothing>> clothing_layers = new HashMap<String, ArrayList<Clothing>>();
  public boolean isEssential = false, is_functional = true;
  HashMap<String, Integer> sublimbs = new HashMap<String, Integer>();
  HashMap<String, Integer> max_lifepoints = new HashMap<String, Integer>();
  int[] maxhitpoints; 
  
  public Leg(double strength_factor, String name) {//strength compared to adult Human
    this.name = name;
    sublimbs.put("upper leg", (int) Math.round(50 * strength_factor));
    clothing_layers.put("upper leg", new ArrayList<Clothing>());
    sublimbs.put("lower leg", (int) Math.round(50 * strength_factor));
    clothing_layers.put("lower leg", new ArrayList<Clothing>());
    sublimbs.put("foot", (int) Math.round(25 * strength_factor));
    clothing_layers.put("foot", new ArrayList<Clothing>());
    max_lifepoints.put("upper leg", sublimbs.get("upper leg"));
    max_lifepoints.put("lower leg", sublimbs.get("lower leg"));
    max_lifepoints.put("foot", sublimbs.get("foot"));
  }
  
  public void receive_damage(String limb, int damage) {
    sublimbs.replace(limb, sublimbs.get(limb) - damage);
    if(sublimbs.get(limb) <= 0) {
      sublimbs.remove(limb);
      max_lifepoints.remove(limb);
      if (limb == "lower leg") {
        sublimbs.remove("foot");
        max_lifepoints.remove("foot");
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
