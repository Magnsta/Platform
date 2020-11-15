import java.io.IOException;

public class Gyroscope implements Runnable {
  private static long readFrec;
  private double[] gyroValues;
  private Broadcast broadcaster;
  private SerialCom ser;
  private boolean isReady;

  public Gyroscope(Broadcast broadcaster, long readFreq, String tty) {
    this.broadcaster = broadcaster;
    readFrec = readFreq;
    gyroValues = new double[6];
    isReady = false;
    try {
      ser = new SerialCom(tty, 115200);
    } catch (IOException IOe) {
      System.out.println(IOe.getMessage());
      throw new RuntimeException("error in gyrothread: during creation" + IOe.getMessage());
    }
  }

  public void run() {
    startUp();
    while (!Thread.interrupted()) {
      System.out.println("gyro");
      updateGyroValues();
      broadcastGyroValues();
      broadcaster.awaitTimed(readFrec);
    }
    System.out.println("gyro thread interupted during run time");
  }

  private void startUp() {
    try {
      String loggerOutput = ser.readLine();
      System.out.println(loggerOutput);
      broadcaster.awaitTimed(250);
      while (!loggerOutput.matches("[\\d,.-]+")) {
        loggerOutput = ser.readLine();
        System.out.println(loggerOutput);
        broadcaster.awaitTimed(250);
      }
      System.out.println("startup is done thread has no more characters " +
          loggerOutput + ser.readLine() + "\n starting gyro readings");
      broadcaster.awaitTimed(250);
      isReady = true;
    } catch (IOException IOe) {
      throw new RuntimeException("error in gyrothread start up method: " + IOe.getMessage());
    }
  }

  private void updateGyroValues() {
    try {
      String[] gyroInfo = ser.readLine().split(",");
      broadcaster.send(parseStringArrayToDouble(gyroInfo));

    } catch (IOException IOe) {
      throw new RuntimeException("error in gyrothread when updating values: " + IOe.getMessage());
    }
  }

  private void broadcastGyroValues() {
    broadcaster.send(gyroValues);
  }

  private double[] parseStringArrayToDouble(String[] s) {
    double[] d = new double[s.length];
    for (int i = 0; i < s.length; i++) {
      d[i] = Double.parseDouble(s[i]);
    }
    return d;
  }

  public boolean isReady() {
    return isReady;
  }
}
