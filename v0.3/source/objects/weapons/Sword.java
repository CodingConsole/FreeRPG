package source.objects.weapons;

import source.colors;
import source.formator;
import source.objects.Object;
import source.objects.Materials;

public class Sword implements Object{
  private String material;
  double attack;
  
  public Sword(String material) {
    this.material = material;
    attack = 2 * Materials.materials.get(material)[2];
  }
  
  public void inspector_view(int width) {
    colors color = new colors();
    formator form = new formator(width, 50);

    System.out.println(get_name());
    System.out.println(color.cyan_bg(form.fill(" ")));
    System.out.println("material: " + material);
    System.out.println("damage: " + attack);
    System.out.println(color.cyan_bg(form.fill(" ")));
  }
  
  public String get_name() {
    return "Sword";
  }
  
  public String get_material() {
    return material;
  }
  
  public char get_symbol() {
    return '|';
  }
  
  public double get_damage() {
    return attack;
  } 
  
  public int get_id() {
    return 2000;
  }
  
  public String save() {
    return material;
  }
}
