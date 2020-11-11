import java.util.InputMismatchException;

/**
 * stores the height of each motor and changes them based on angle inputs.
 *
 * @author fredborg
 * @version 0.1
 */
public class StorageBox {
  // max height of the motors
  private static double MAX_HEIGHT;
  // min height of the motors
  private static double MIN_HEIGHT;
  // height of the motors
  private double[] motors = new double[3];
  private double[] gyro = new double[3];

  public StorageBox() {
    this.motors[0] = 0;
    this.motors[1] = 0;
    this.motors[2] = 0;
    this.gyro[0] = 0;
    this.gyro[1] = 0;
    this.gyro[2] = 0;
  }

  /**
   * sets an axie to a spesific value. fex x angle to 30 deg or z to 10 cm
   *
   * @param axies the axie that is to be controlled
   * @param value the new value for the axie, angle for x and y, mm for z.
   * @throws InputMismatchException
   */
  public synchronized void setAxies(Axies axies, double value) throws InputMismatchException {


    //TODO:add limiting function based on the max angle of the motors so that they newer max out at the top or bottom
    //     but aways has move to correct the angle of the system(limit height regulation)
    switch (axies) {

      case X:
        if (isInsideLimits(value + motors[0]) && isInsideLimits(value - motors[1]) && isInsideLimits(value - motors[2])) {
          this.motors[0] += value;
          this.motors[1] -= value;
          this.motors[2] -= value;
        }
        break;
      case Y:
        if (isInsideLimits(value + motors[1]) && isInsideLimits(value - motors[2])) {
          this.motors[1] += value;
          this.motors[2] -= value;
        }
        break;
      case Z:
        if (isInsideLimits(value + motors[0]) && isInsideLimits(value + motors[1]) && isInsideLimits(value + motors[2])) {
          this.motors[0] += value;
          this.motors[1] += value;
          this.motors[2] += value;
        }
        break;
      default:
        throw new InputMismatchException("only X,Y and Z enums are valid inputs");
    }
  }

  public double getMotor1() {
    return motors[0];
  }

  public double getMotor2() {
    return motors[1];
  }

  public double getMotor3() {
    return motors[2];
  }

  public double[] getGyro() {
    return gyro;
  }

  public double getGyroAxie(Axies axies) {
    switch (axies) {
      case X:
        return gyro[0];
      case Y:
        return gyro[1];
      case Z:
        return gyro[2];
      default:
        throw new InputMismatchException(" not an valid axies");
    }
  }

  public synchronized void setMotor(int motorNbr, double value) {
    switch (motorNbr) {
      case 1:
        motors[0] = value;
        break;
      case 2:
        motors[1] = value;
        break;
      case 3:
        motors[2] = value;
        break;

    }
  }

  /**
   * returnes the angle or height of an axie.
   *
   * @param axies the axie.
   * @return the angle or heigth.
   * @throws InputMismatchException if the axie is not valid
   */
  public double getAxies(Axies axies) throws InputMismatchException {
    System.out.println("motor 1 , 2 , 3:   " + getMotor1() + " , " + getMotor2() + " , " + getMotor3());
    switch (axies) {
      case X:
        return Math.asin((getMotor1() - (getMotor2() + getMotor3()) / 2) / 2);

      case Y:
        return Math.asin((getMotor2() - getMotor3()) / 2);

      case Z:
        return (getMotor3() + getMotor2() + getMotor1()) / 3;

      default:
        throw new InputMismatchException("only X,Y and Z enums are valid inputs");
    }
  }

  /**
   * @return
   */
  public double[] getMotorAngles() {
    return motors;
  }

  /**
   * checks that the value provided is inside the limits of the system
   *
   * @param value
   * @return
   */
  private boolean isInsideLimits(double value) {
    return isBetween(MIN_HEIGHT, MAX_HEIGHT, value);
  }

  /**
   * checks that a value is greater than the min value provided and less that the max value provided.
   *
   * @param value the value that you want to check
   * @return true if the value is between the min and max value
   */
  private boolean isBetween(double min, double max, double value) {
    return (value > min) && (max > value);

  }
}
