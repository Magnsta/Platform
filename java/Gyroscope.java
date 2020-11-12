import sun.nio.cs.UTF_8;

import java.io.IOException;

public class Gyroscope implements Runnable {
  private static long readFrec;
  private double[] gyroValues = new double[6];
  private Broadcast broadcaster;
  private SerialCom ser;

  public Gyroscope(Broadcast broadcaster, long readFreq, String tty) {

    this.broadcaster = broadcaster;
    this.readFrec = readFreq;
    try {
      ser = new SerialCom(tty, 9600);
    } catch (IOException IOe) {
      System.out.println(IOe.getMessage());

      //TODO:Stopp system could Not Connect

    }
  }

  public void run() {
    while (true) {
      updateGyroValues();
      broadcastGyroValues();
      broadcaster.awaitTimed(readFrec);
    }
  }


  private void updateGyroValues() {
    try {
      byte[] data_recived = ser.read(8);
      String gyroInfo = new String(data_recived, UTF_8.INSTANCE);
      System.out.println(gyroInfo);
      String[] gyroSplit = gyroInfo.substring(22).split(",");
      System.out.println(gyroInfo);
      gyroValues = parseStringArrayToDouble(gyroSplit);
      System.out.println(gyroValues);
    } catch (IOException IOe) {
      //TODO: stop system and go to alarm

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
}
