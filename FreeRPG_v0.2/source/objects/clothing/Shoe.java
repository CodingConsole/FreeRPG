package source.objects.clothing;

import source.colors;
import source.formator;
import source.objects.Object;
import source.objects.Materials;

public class Shoe implements Object{
  public String material, site;
  double protection, attack;
  
  public Shoe(String material, String site) {
    this.material = material;
    this.site = site;
    protection = Materials.materials.get(material)[1];
    attack = 1 * Materials.materials.get(material)[2];
  }
  
  public void inspector_view(int width) {
    colors color = new colors();
    formator form = new formator(width, 50);

    System.out.println(get_name());
    System.out.println(color.cyan_bg(form.fill(" ")));
    System.out.println("material: " + material);
    System.out.println("damage-reduction(in %): " + protection);
    System.out.println("damage: " + attack);
    System.out.println(color.cyan_bg(form.fill(" ")));
  }
  
  public String get_name() {
    return site + " shoe";
  }
  
  public String get_material() {
    return material;
  }
  public char get_symbol() {
    return '~';
  }

  public double get_damage() {
    return attack;
  }  
  
  public double get_protection() {
    return protection;
  }
}       
