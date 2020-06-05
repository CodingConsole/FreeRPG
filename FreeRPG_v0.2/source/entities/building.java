package source.entities;

public class building{
  public int id;
  public int[] pos;
  public String type;
  
  public building(int id, int[] pos, String type) {
    this.id = id;
    this.pos = pos;
    this.type = type;
  }
  
  public void set_role(String role) {
    type = role;
  }
}
