package source;

public class colors {
  private final String RESET = "\u001B[0m";
  private final String BLACK = "\u001B[30m";
  private final String RED = "\u001B[31m";
  private final String GREEN = "\u001B[32m";
  private final String YELLOW = "\u001B[33m";
  private final String BLUE = "\u001B[34m";
  private final String PURPLE = "\u001B[35m";
  private final String CYAN = "\u001B[36m";
  private final String WHITE = "\u001B[37m";
  
  private final String BLACK_BG = "\u001B[40m";
  private final String RED_BG = "\u001B[41m";
  private final String GREEN_BG = "\u001B[42m";
  private final String YELLOW_BG = "\u001B[43m";
  private final String BLUE_BG = "\u001B[44m";
  private final String PURPLE_BG = "\u001B[45m";
  private final String CYAN_BG = "\u001B[46m";
  private final String WHITE_BG = "\u001B[47m";
  
  public String black(String input) {
    input = BLACK + input + RESET;
    return input;
  }
  public String red(String input) {
    input = RED + input + RESET;
    return input;
  }
  public String green(String input) {
    input = GREEN + input + RESET;
    return input;
  }
  public String yellow(String input) {
    input = YELLOW + input + RESET;
    return input;
  }
  public String blue(String input) {
    input = BLUE + input + RESET;
    return input;
  }
  public String purple(String input) {
    input = PURPLE + input + RESET;
    return input;
  }
  public String cyan(String input) {
    input = CYAN + input + RESET;
    return input;
  }
  public String white(String input) {
    input = WHITE + input + RESET;
    return input;
  }
  
  public String black_bg(String input) {
    input = BLACK_BG + input + RESET;
    return input;
  }
  public String red_bg(String input) {
    input = RED_BG + input + RESET;
    return input;
  }
  public String green_bg(String input) {
    input = GREEN_BG + input + RESET;
    return input;
  }
  public String yellow_bg(String input) {
    input = YELLOW_BG + input + RESET;
    return input;
  }
  public String blue_bg(String input) {
    input = BLUE_BG + input + RESET;
    return input;
  }
  public String purple_bg(String input) {
    input = PURPLE_BG + input + RESET;
    return input;
  }
  public String cyan_bg(String input) {
    input = CYAN_BG + input + RESET;
    return input;
  }
  public String white_bg(String input) {
    input = WHITE_BG + input + RESET;
    return input;
  }
}
