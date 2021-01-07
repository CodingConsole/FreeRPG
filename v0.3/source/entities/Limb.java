package source.entities;

import source.objects.clothing.Clothing;
import java.util.HashMap;

public interface Limb {  
  public void receive_damage(String sublimbname, int damage);
  
  public String getName();
  public String[] get_sublimbs();
  public boolean get_functionality();
  public HashMap<String, Integer> get_lifepoints();
  public HashMap<String, Integer> get_maxpoints();
  
  public void apply_clothing(String[] protect, Clothing clothing);
  public void disapply_clothing(String[] protect, Clothing clothing);
}
