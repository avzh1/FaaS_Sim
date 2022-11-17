package Samplers;

import java.util.Random;

public class Sampler {

  private static final Random rand = new Random(1234567890);

  /**
   * Generate a random number based on the exponential distribution. Uses the
   */
  public static double Exponential(double lambda) {
    return -Math.log(rand.nextDouble()) / lambda;
  }
}
