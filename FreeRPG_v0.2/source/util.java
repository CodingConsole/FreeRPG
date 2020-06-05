package source;

import java.util.*;
import java.io.*;

import source.entities.*;
import source.entities.entities.*;

public class util {
  private static Scanner scan = new Scanner(System.in);
  public static Random rand = new Random();
 
  public static void clear() {
    try {
      clear_prv();
    } catch(Exception e) {
      
    }
  }
  
  public static void rescale(int x, int y) {
    try {
      rescale_prv(x, y);
    } catch(Exception e) {
      
    }
  }
  
  public static void set_title(String title) {
    try {
      set_title_prv(title);
    } catch(Exception e) {
      
    }
  }
  
  public static int input_char(char max) {
    char i = 0;
    boolean done = false;
    while (!done) {
      try {
        i = scan.nextLine().charAt(0);
        if (i <= max) 
          done = true;
      } catch(Exception e) {}
    }
    return Integer.valueOf(i - 97);      
  }
  
  public static char input_char(boolean allow_letter, boolean allow_number, char max) {
    char i = 0;
    boolean done = false;
    while (!done) {
      try {
        i = scan.nextLine().charAt(0); 
        if ((allow_letter && Character.isLetter(i) || allow_number && Character.isDigit(i)) && i <= max) 
          done = true;
      } catch(Exception e) {}
    }
    return i;      
  }
  
  public static int input_letter(char max) {
    char i = 0;
    boolean done = false;
    while (!done) {
      try {
        i = scan.nextLine().charAt(0); 
        if (Character.isLetter(i) && i <= max) 
          done = true;
      } catch(Exception e) {}
    }
    return i - 97;      
  }
  
  public static int input_numb() {
    int i = 0;
    boolean done = false;
    while (!done) {
      try {
        i = scan.nextInt(); 
        done = true;
      } catch(Exception e) {}
    }
    return i;      
  }
  
  public static int input_numb(int min, int max) {
    int i = 0;
    boolean done = false;
    while (!done) {
      try {    
        String input = scan.nextLine();
        i = Integer.parseInt(input);
        if (i >= min && i <= max)
          done = true;
      } catch(Exception e) {}
    }
    return i;      
  } 
  
  public static int input_numb(int max) {
    int i = 0;
    boolean done = false;
    while (!done) {
      try {    
        String input = scan.nextLine();
        i = Integer.parseInt(input);
        if (i <= max)
          done = true;
      } catch(Exception e) {}
    }
    return i;      
  }  
  
  public static String input_string(boolean allow_empty) {
    String content = scan.nextLine();
    if (allow_empty == false && content.isEmpty()) input_string(allow_empty);
    return content;
  }
  
  public static int input_choice(String[] name) {
    String i; int choose = -100;
    try {
      i = scan.nextLine();
      for (int j = 0;j < name.length ;j++ ) {
        if (i.equals(name[j])) {
          choose = j;
        }  
      }
      if (choose == -100) {throw new EmptyStackException();}
    } catch(Exception e) {
      choose = input_choice(name);   
    }
    return choose;      
  }
  
  public static int[] combine_int(int[] one, int[] two) {
    int length = one.length + two.length;
    int[] three = new int[length];
    for (int i = 0; i < one.length; i++) {
      three[i] = one[i];
    }
    int count = 0;
    for (int i = one.length; i < three.length; i++) {
      three[i] = two[0];
      count++;
    }
    return three;
  }

  public static int[] calc_view_border(int[] pos, int length, int height) {
    int[] border = {pos[2] - 20, pos[2] + 20, pos[3] - 20, pos[3] + 20};
    for (int i = 0; i < 4; i++){
      if (border[i] < 0) border[i] = 0;
      else if(border[i] > length - 1) border[i] = length - 1;
    }
    return border;    
  }
  
  public static void delete(File file) {
    if (file.isDirectory()) {
      for (File fileEntry : file.listFiles()) {
        if (fileEntry.isDirectory()) {
          delete(fileEntry); }
        else {
          fileEntry.delete();
        }
      }
    }
    file.delete();
  }

//---------------------------PRIVATE STUFF----------------------------------------------
  
  private static void clear_prv(String... arg) throws IOException, InterruptedException {
    new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
  }
  private static void rescale_prv(int x, int y) throws IOException, InterruptedException {
    new ProcessBuilder("cmd", "/c", "mode " + x + ", " + y).inheritIO().start().waitFor();
  }
  private static void set_title_prv(String title) throws IOException, InterruptedException {
    new ProcessBuilder("cmd", "/c", "title " + title).inheritIO().start().waitFor();
  } 
}

