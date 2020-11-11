import java.io.IOException;
import java.util.InputMismatchException;

public class Gyroscope implements Runnable {
  private static long readFrec;
  private double Xangle;
  private double Yangle;
  private double height;
  private double[] angles;
  private Broadcast broadcaster;
  private SerialCom ser;

  public Gyroscope(Broadcast broadcaster, long readFreq, String tty)  {
    this.Xangle = 0;
    this.Yangle = 0;
    this.height = 0;
    this.broadcaster = broadcaster;
    this.readFrec = readFreq;
    angles = new double[3];
    try{
    ser = new SerialCom(tty, 9600);}catch (IOException IOe){
      System.out.println(IOe.getMessage());

      //TODO:Stopp system could Not Connect
      // }

  }

  public void run(){
    while (true) {
      updateAngles();
      broadcastAngles();
    }
  }

  public double getAngle(Axies axie) throws InputMismatchException {
    switch (axie) {
      case X:
        return Xangle;
      case Y:
        return Yangle;
      default:
        throw new InputMismatchException("only X and Y is angles");

    }
  }

  private void updateAngles() {
    try {

      //TODO: read an update angles properly
      ser.read();
    } catch (IOException IOe) {
      //TODO: stop system and go to alarm
    }
  }

  private void broadcastAngles() {

    System.out.println("angles of the gyroscope" + angles[0] + " || " + angles[1]);
    broadcaster.send(angles);
    broadcaster.awaitTimed(readFrec);
  }

}
