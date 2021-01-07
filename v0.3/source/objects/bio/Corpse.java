package source.objects.bio;

import source.colors;
import source.formator;
import source.objects.Object;
import source.objects.Materials;

public class Corpse implements Object{
  public String material;
  double attack = 1;
  String name;
  
  public Corpse(String content) {
    this.material = material.split(" ")[0];
    this.name = name.split(" ")[1];
  }
  
  public void inspector_view(int width) {
    System.out.println("This is the corpse of " + name);
    colors color = new colors();
    formator form = new formator(width, 50);

    System.out.println(get_name());
    System.out.println(color.cyan_bg(form.fill(" ")));
    System.out.println("material: " + material);
    System.out.println("damage: " + attack);
    System.out.println(color.cyan_bg(form.fill(" ")));
  }
  
  public String get_name() {
    return "Corpse of " + name;
  }
  
  public String get_person() {
    return name;
  }
  
  public String get_material() {
    return "biological";
  }
  
  public char get_symbol() {
    return 'h';
  } 
  public double get_damage() {
    return attack;
  }  
  
  public int get_id() {
    return 1000;
  }
  
  public String save() {
    return material + " " + name;
  }
}   
