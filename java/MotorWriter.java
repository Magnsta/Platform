import java.io.IOException;
import java.util.ArrayList;

public class MotorWriter implements Runnable {
  private final long FREQ;
  private final String PORT;
  private SerialCom ser;
  private StorageBox box;
  private RoboClaw mc1; //controller for m1 and m2
  private RoboClaw mc2; //controller for m3
  private int maxEnc; //maximum encoder value
  private int minEnc; // minimum encoder value

  public MotorWriter(long freq, StorageBox box, String address) {
    this.PORT = address;
    startSerialCom();
    this.mc1 = new RoboClaw(0x80);
    this.mc2 = new RoboClaw(0x81);
    this.FREQ = freq;
    this.box = box;
    resetMotorsAndEncoders();
  }

  private void startSerialCom() {
    try {
      this.ser = new SerialCom(PORT, 115200);
    } catch (IllegalArgumentException e) {
      System.out.println(e.getMessage());
    } catch (IOException IOe) {
      throw new RuntimeException("error in thead: " + Thread.currentThread().getName() +
          "\n error Message: " + IOe.getMessage());
    }
  }

  public void run() {
    resetMotorsAndEncoders();

    int[] newPos;
    while (true) {
      try {
        this.wait(FREQ);
        newPos = getEncPositions(box.getMotorAngles());
        sendNewMotorPositions(newPos);
      } catch (InterruptedException e) {
        System.out.println(e.getMessage());
      }
    }
  }

  private void sendNewMotorPositions(int[] newValues) {
    sendM1Pos(newValues[0]);
    sendM2Pos(newValues[1]);
    sendM3Pos(newValues[2]);
  }

  private void sendM1Pos(int encValue) {
    sendCommandArray(mc1.setPosM1Cmd(encValue));
  }

  private void sendM2Pos(int encValue) {
    sendCommandArray(mc1.setPosM2Cmd(encValue));
  }

  private void sendM3Pos(int encValue) {
    sendCommandArray(mc2.setPosM1Cmd(encValue));
  }

  private void setMotorPos(int motor, int encValue) {
    ArrayList<byte[]> commandArray;
    switch (motor) {
      case 1:
        commandArray = mc1.setPosM1Cmd(encValue);
        break;
      case 2:
        commandArray = mc1.setPosM2Cmd(encValue);
        break;
      case 3:
        commandArray = mc2.setPosM1Cmd(encValue);
        break;

      default:
        throw new IllegalArgumentException("error int " + motor + "has no corresponding motor unit.");
    }
    sendCommandArray(commandArray);
  }


  private void resetMotorsAndEncoders() {
    throw new RuntimeException("program the reset motors method you dufus!");
  }

  private void sendCommand(byte[] cmd) {
    try {
      ser.write(cmd);
    } catch (IOException IOe1) {
      try {
        Thread.sleep(100);
        ser.write(cmd);
      } catch (IOException IOe2) {
        throw new RuntimeException("IO connection error in thread: " + Thread.currentThread().getName()
            + "\nTwo tries gave error messages:" + "\n" + IOe1.getMessage() + "\n" + IOe2.getMessage());
      } catch (InterruptedException e) {
      }
    }
  }

  private void sendCommandArray(ArrayList<byte[]> ba) {
    for (byte[] bs : ba) {
      sendCommand(bs);
    }
  }

  /**
   * returns the encoder value of the motor given an angle for the motor.
   *
   * @param motorAngle the angleof the motor
   * @return the encodervalue
   */
  private int encPosFromAngle(double motorAngle) {
    return (int) Math.round(projectDouble(motorAngle, maxEnc, minEnc, MotorController.getOperatingAngle()));
  }

  private int[] getEncPositions(double[] motorAngels) {
    int[] returnInts = new int[motorAngels.length];
    for (int i = 0; i < motorAngels.length; i++) {
      returnInts[i] = encPosFromAngle(motorAngels[i]);
    }
    return returnInts;
  }

  private double projectDouble(double value, int max, int min, double scale) {
    return (value * ((max - min) / scale));
  }
  private void driveM1(int speed){
    sendCommandArray(mc1.driveM1Cmd(speed));
  }
  private void driveM2(int speed){
    sendCommandArray(mc1.driveM2Cmd(speed));
  }
  private void driveM3(int speed){
    sendCommandArray(mc2.driveM1Cmd(speed));
  }
}
