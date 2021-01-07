package source.objects;

import java.util.HashMap;

public abstract class Materials {
  public static HashMap<String, double[]> materials = new HashMap<String, double[]>();
  static {
    materials.put("wool", new double[] {1.314, 0.03, 0}); //BLUEPRINT: name / density(g per cm^3), armor in % damage reduction, damagemultiplier
    materials.put("iron", new double[] {7.874, 0.3, 4});
  }
}
