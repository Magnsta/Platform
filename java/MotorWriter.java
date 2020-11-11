import java.io.IOException;

public class MotorWriter implements Runnable {
  private final long FREQ;
  private final String PORT;
  private SerialCom ser;
  private StorageBox box;
  private RoboClaw mc1; //controller for m1 and m2
  private RoboClaw mc2; //controller for m3

  public MotorWriter(long freq, StorageBox box) {
    PORT = "/dev/ttyAMA0";
    startSerialCom();
    this.mc1 = new RoboClaw(0x80);
    this.mc2 = new RoboClaw(0x81);
    this.FREQ = freq;
    this.box = box;

  }

  private void startSerialCom() {
    try {
      this.ser = new SerialCom(PORT, 115200);
    } catch (IllegalArgumentException e) {
      System.out.println(e.getMessage());
    } catch (IOException IOe) {
      System.out.println(IOe.getMessage());
      //TODO:stop system comunication could not start}
    }
  }

  public void run() {
    resetMotorsAndEncoders();
    while (true) {
      try {
        this.wait(FREQ);
        sendMotorPos(box.getMotorAngles());
      } catch (InterruptedException e) {
        System.out.println(e.getMessage());
      }
    }
  }

  private void sendInstructions(RoboClaw mc) {
    Instruction c = mc.getInstructions();
    ser.write(c, mc.getAddress());
  }

  private void resetMotorsAndEncoders() {
  }
}
