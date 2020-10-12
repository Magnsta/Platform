public class PlatformMotorControll implements Runnable {
  private double motorAngle;
  private double MotorHeight;
  private double opositeHeight;
  private double platformAngle;
  private double platformHeight;
  private boolean controlling;
  private StorageBox box;
  private Axies axie;

  public PlatformMotorControll(StorageBox box, Axies axie) {
    controlling = false;
    this.box = box;
    this.axie = axie;

  }

  public void run() {

    controlling = true;
    double error = getNewWantAngle() - platformAngle;
    while (controlling) {
      error = getNewWantAngle() - platformAngle;

    }
  }

  private double getNewWantAngle() {
    return box.getAxies(axie);
  }
}
