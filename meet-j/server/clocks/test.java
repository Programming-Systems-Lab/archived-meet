/*
 * test.java
 *
 * Created on December 24, 2002, 10:27 AM
 */

package psl.meet.server.clocks;

/**
 *
 * @author  phil
 */
public class test {
  
  /** Creates a new instance of test */
  public test() {
  }
  
  static long DateToMjd(int y, int m, int d) {
    return
    367 * y
    - 7 * (y + (m + 9) / 12) / 4
    - 3 * ((y + (m - 9) / 7) / 100 + 1) / 4
    + 275 * m / 9
    + d
    + 1721028L
    - 2400000L;
  }
  
/*
 * Calculate number of seconds since 1-Jan-1900.
 * - Ignores UTC leap seconds.
 */
  
  static long SecondsSince1900(int y, int m, int d) {
    long Days = DateToMjd(y, m, d) - DateToMjd(1900, 1, 1);
    return Days * 86400L;
  }
  
  public static void main(String args[]) {
    System.out.println(SecondsSince1900(1970,1,1));
  }
}