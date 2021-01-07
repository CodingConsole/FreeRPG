import source.*;

class Main {
  static int x = 200, y = 72;
  
  public static void main(String[] args) {
    util.rescale(x, y);
    util.set_title("FreeRPG v0.3");
    
    menues menu = new menues();

    int choice = 0;
    while (true) {
      switch(choice) {
      case 0:
        choice = menu.title();
        break;
      case 1:
      case 2:
        choice = menu.game();
        break;
      case 3:
        choice = menu.delete();
        break;
      case 4:
        System.exit(0);
      }     
    }    
  }
}
