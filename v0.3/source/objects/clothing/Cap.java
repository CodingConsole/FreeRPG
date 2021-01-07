package source.objects.clothing;

import source.colors;
import source.formator;
import source.objects.Object;
import source.objects.Materials;        

import java.util.ArrayList;

public class Cap extends Clothing implements Object{
  public String material;
  
  public Cap(String material) {
    this.material = material;
  }
  
  public void inspector_view(int width) {
    colors color = new colors();
    formator form = new formator(width, 50);

    System.out.println(get_name());
    System.out.println(color.cyan_bg(form.fill(" ")));
    System.out.println("material: " + material);
    System.out.println("damage-reduction(in %): " + Materials.materials.get(material)[1]);
    System.out.println("damage: " + 0.7 * Materials.materials.get(material)[2]);
    System.out.println(color.cyan_bg(form.fill(" ")));
  }
  
  public String get_name() {
    return "Cap";
  }
  
  public String get_material() {
    return material;
  }
  public char get_symbol() {
    return '~';
  } 
  public double get_damage() {
    return 0.7 * Materials.materials.get(material)[2];
  }  
  
  public double get_protection() {
    return Materials.materials.get(material)[1];
  }
  
  public ArrayList<String> get_required_limbs() {
    ArrayList<String> limbs = new ArrayList();
    limbs.add("head");
    return limbs;
  }
  
  public ArrayList<String> get_required_sublimbs(String limb) {
    ArrayList<String> limbs = new ArrayList();
    switch(limb) {
      case "head":
        limbs.add("head");
        break;
    }
    return limbs;
  }
  
  public ArrayList<String> get_protected_limbs() {
    ArrayList<String> limbs = new ArrayList();
    limbs.add("head");
    return limbs;  
  }
  
  public ArrayList<String> get_sublimbs(String limb) {
    ArrayList<String> limbs = new ArrayList();
    switch(limb) {
      case "head":
        limbs.add("head");
        break;
    }
    return limbs;
  }
  
  public int get_id() {
    return 0;
  }
  
  public String save() {
    return material;
  }
}   
