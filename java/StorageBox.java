import java.util.InputMismatchException;

/**
 * stores the height of each motor and changes them based on angle inputs.
 *
 * @author fredborg
 * @version 0.1
 */
public class StorageBox {
  // max height of the motors
  private static double MAX_HEIGHT = 100;
  // min height of the motors
  private static double MIN_HEIGHT = -75;
  // height of the motors
  private double motor1;
  private double motor2;
  private double motor3;

  public StorageBox() {
    this.motor1 = 0;
    this.motor2 = 0;
    this.motor3 = 0;
  }

  /**
   * sets an axie to a spesific value. fex x angle to 30 deg or z to 10 cm
   * @param axies the axie that is to be controlled
   * @param value the new value for the axie, angle for x and y, mm for z.
   * @throws InputMismatchException
   */
  public synchronized void setAxies(Axies axies, double value) throws InputMismatchException {
    switch (axies) {

      case X:
        if (isInsideLimits(value + motor1) && isInsideLimits(value - motor2) && isInsideLimits(value - motor3)) {
          this.motor1 += value;
          this.motor2 -= value;
          this.motor3 -= value;
        }
        break;
      case Y:
        if (isInsideLimits(value + motor2) && isInsideLimits(value - motor3)) {
          this.motor2 += value;
          this.motor3 -= value;
        }
        break;
      case Z:
        if (isInsideLimits(value + motor1) && isInsideLimits(value + motor2) && isInsideLimits(value + motor3)) {
          this.motor1 += value;
          this.motor2 += value;
          this.motor3 += value;
        }
        break;
      default:
        throw new InputMismatchException("only X,Y and Z enums are valid inputs");
    }
  }

  public double getMotor1() {
    return motor1;
  }

  public double getMotor2() {
    return motor2;
  }

  public double getMotor3() {
    return motor3;
  }

  /**
   * returnes the angle or height of an axie.
   * @param axies the axie.
   * @return the angle or heigth.
   * @throws InputMismatchException if the axie is not valid
   */
  public double getAxies(Axies axies) throws InputMismatchException {
    System.out.println("motor 1 , 2 , 3:   " + getMotor1()+ " , "+ getMotor2() + " , " + getMotor3());
    switch (axies) {
      case X:
        return Math.asin( (getMotor1() - (getMotor2() + getMotor3()) / 2) / 2);

      case Y:
        return Math.asin((getMotor2() - getMotor3()) / 2);

      case Z:
        return (getMotor3() + getMotor2() + getMotor1()) / 3;

      default:
        throw new InputMismatchException("only X,Y and Z enums are valid inputs");
    }
  }

  /**
   * checks that the value provided is inside the limits of the system
   * @param value
   * @return
   */
  private boolean isInsideLimits(double value) {
    return isBetween(MIN_HEIGHT, MAX_HEIGHT, value);
  }

  /**
   * checks that a value is greater than the min value provided and less that the max value provided.
   * @param value the value that you want to check
   * @return true if the value is between the min and max value
   */
  private boolean isBetween(double min, double max, double value) {
    return (value > min) && (max > value);

  }
}
