public class MotorController implements Runnable {

  private PID pid;
  private Broadcast broadcaster;
  private boolean recived;
  private Axies axie;
  private StorageBox box;
  private int maxEnc; //maximum encoder value
  private int minEnc; // minimum encoder value

  public MotorController( Broadcast broadcaster, Axies axie, StorageBox box) {
    this.axie = axie;
    this.broadcaster = broadcaster;
    this.box = box;
    this.recived = true;

  }
  private double getRotations(double angle) {
    double returnvalue = 0;
    if (PlatformControler.PI / 2 > Math.abs(angle)) {
      returnvalue = Math.asin(PlatformControler.ANGLE_CONSTANT * angle);
    } else if (angle >= 0) returnvalue = PlatformControler.MAX_ANGLE;
    else returnvalue = -PlatformControler.MAX_ANGLE;
    return returnvalue;
  }

  /**
   * waits for new angles from the gyro then sends the angles to the storage box.
   */
  public void run() {
    double newAngle;
    while (true) {
      while (!recived) {
        broadcaster.await();
        recived = true;
      }
      // calculates the angle we want the motor to change to...
      newAngle = calculateMotorAngle(broadcaster.recive(axie));
      box.setAxies(axie, newAngle);
      recived = false;
    }
  }

  /**
   * returns the angle of the motor given the current encoder value
   *
   * @param encoderValue the encoder value.
   * @return angle of the motor
   */
  private double calculateMotorAngle(double encoderValue) {
    return (encoderValue - minEnc) / maxEnc * PlatformControler.PI;
  }

}
