import java.util.InputMismatchException;

public class Gyroscope implements Runnable {
  private double Xangle;
  private double Yangle;
  private double height;
  private double[] angles;
  private Broadcast broadcaster;
  public Gyroscope(Broadcast broadcaster) {
    this.Xangle = 0;
    this.Yangle = 0;
    this.height = 0;
    this.broadcaster = broadcaster;

    angles = new double[3];
  }

  public void run() {
    while (true) {
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

  private void broadcastAngles() {
    //read angles:
    // TODO: implement Angle reading
    setAngles();
    System.out.println("angles of the gyroscope" + angles[0] + " || " + angles[1]);
    broadcaster.send(angles);
    broadcaster.awaitTimed(100);
  }
  private void setAngles(){

    angles[0] = Xangle += (Math.random()-0.4)*1.1;
    angles[1] = Yangle += (Math.random()-0.6)*1.1;
    angles[2] = height += (Math.random()-0.6)*1.1;
  }
}
