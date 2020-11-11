public class RoboClaw {

  private final int address;
  private final Commands cmds;

  public RoboClaw(int address) {
    this.address = address;
    this.cmds = new Commands();
  }

  public void resetMotorsAndEncoders() {
  }

  public int getAddress() {
    return address;
  }

  public double getAngleM1() {
  }

  public void setAngleM1(double angle) {

  }

  public double getAngleM2() {

  }

  public void setAngleM2(double angle) {

  }

  public int getEnc1() {
  }

  public int getEnc2() {
  }

  public void setSpeedM1(double speed) {
  }

  public void setSpeedM2(double speed) {
  }

  public void setAcsM1(double acs) {
  }

  public void setAcsM2(double acs) {
  }

  public void setPosM1(int encPos) {
  }

  public void setPosM2(int encPos) {
  }

  public double[] getPidValues() {
  }

  public void stopAll() {

  }
}
