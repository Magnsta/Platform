import java.util.InputMismatchException;

public class rotateAxies {
  private static double motor_1 = 0;
  private static double motor_2 = 0;
  private static double motor_3 = 0;

  public static void SET_MOTOR_HEIGHT(Axies axies, double heigth) {
    switch (axies) {
      case X:
        motor_2 = heigth / 2;
        motor_3 = -heigth / 2;
        break;
      case Y:
        motor_1 = heigth / 2;
        motor_2 = motor_3 = -heigth / 2;
        break;
      case Z:
        motor_3 = motor_2 = motor_1 = heigth;
        break;
      default:
        throw new InputMismatchException();
    }
    System.out.println("motor 1:" + motor_1);
    System.out.println("motor 2:" + motor_2);
    System.out.println("motor 3:" + motor_3);
  }
}

