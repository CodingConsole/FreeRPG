package source.objects;

public interface Object { 
  public void inspector_view(int width); 
  public String get_material();
  public String get_name();
  public char get_symbol();
  public double get_damage();
  public double get_protection();
}
