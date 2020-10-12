public class MotorController implements Runnable {

  private PID pid;
  private Broadcast broadcaster;
  private boolean recived;
  private Axies axie;
  private StorageBox box;

  public MotorController(PID pid, Broadcast broadcaster, Axies axie, StorageBox box) {
    this.pid = pid;
    this.axie = axie;
    this.broadcaster = broadcaster;
    this.box = box;
    this.recived = true;

  }

  private double getRotations(double angle) {
    double returnvalue = 0;
    if (PlatformControler.PI / 2 > Math.abs(angle)) {
      returnvalue = Math.asin(PlatformControler.ANGLE_CONSTANT * angle);
    }
    else if (angle >= 0) returnvalue = PlatformControler.MAX_ANGLE;
    else returnvalue = -PlatformControler.MAX_ANGLE;
    return returnvalue;
  }

  public void run() {
    double value;
    double gain;
    double motorRotation;
    while (true) {
      while (!recived) {
        broadcaster.await();
        recived = true;
      }
      value = broadcaster.recive(axie);
      System.out.println(axie.name() + "   " + value);
      gain = pid.calculatePID(value, 0);
      System.out.println("PID gain = "+gain);
      motorRotation = getRotations(gain);
      box.setAxies(axie, motorRotation);
      //System.out.println(axie.name() + "--> system gain is : " + gain + " angle is : " + value + " motor is rotated " + motorRotation / Math.PI + "*PI degrees");
      //System.out.println("motor_1: " + box.getMotor1() + " ||  motor_2: " + box.getMotor2() + "  ||  motor_3: " + box.getMotor3());
      recived = false;

    }
  }

}
