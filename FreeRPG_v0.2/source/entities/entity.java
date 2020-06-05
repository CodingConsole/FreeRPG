package source.entities;

import source.entities.Limb;
import source.objects.Object;
import java.util.ArrayList;

public abstract class entity {
  public boolean isTomorrow = false, is_alive = true;
  public abstract void save(String location);
  public abstract void load(String location, long id); 
  
  public abstract ArrayList<String> action(int time, ArrayList<String> messages);
  public abstract ArrayList<String> conversation(String said, entity partner, ArrayList<String> messages);
  
  public abstract ArrayList<String> check_limb(Limb limb, String sublimb, ArrayList<String> messages);
  
  public abstract void inspector_view(int width);
  public abstract int[] get_pos();
  public abstract String get_name();
  public abstract ArrayList<Limb> get_limbs();
  public abstract String get_request();
  public abstract void clear_request();
  public abstract String get_role();
  public abstract void set_role(String newRole);
  
  public abstract void wear(source.objects.Object cloth);
  public abstract void dewear(source.objects.Object cloth);
  
  public abstract void add_enemy(entity enemy);
}
