package source;

import java.util.concurrent.TimeUnit;

public class formator {
  int x, y;
  public formator(int x, int y) {
    this.x = x; this.y = y;
  }
  
  public String center(String content) {
    String product = "";
    for (int i = 0; i <= (x - content.length())/2 ;i++) {
      product = product.concat(" ");
    }
    product = product.concat(content);
    return product;
  }
  
  public String fill(String content) {
    String product = "";
    for (int i = 0;i < x/content.length(); i++ ) {
      product = product.concat(content);
    }
    return product;
  }
  
  public String fill(String content, int length) {
    String product = "";
    for (int i = 0;i < length/content.length(); i++ ) {
      product = product.concat(content);
    }
    return product;
  }  
  
  public void spell(String content, int milliseconds) {
    for (int i = 0; i < content.length(); i++) {
      System.out.print(content.charAt(i));
      try {
        TimeUnit.MILLISECONDS.sleep(milliseconds);
      } catch(Exception e) {}
    }
  }
}

