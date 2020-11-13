import java.util.InputMismatchException;

public class Broadcast {
  private double[] message;
  private boolean arrived;
  private int waiting;
  private int numberOfConsumers;
  private StorageBox box;

  public Broadcast(int numberOfConsumers, StorageBox box) {
    this.numberOfConsumers = numberOfConsumers;
    this.arrived = false;
    this.waiting = 0;
    this.box = box;
  }

  public synchronized boolean send(double[] sendMessage) {
    boolean sent = false;
    if ((waiting >= 1) && (!arrived)) {

      arrived = true;
      this.notifyAll();
      sent = true;

      System.out.println("sendt numbers");
    } else {
      System.out.println("error: waiting:" + waiting + " || arrived: " + arrived);
    }
    return sent;
  }

  public synchronized double recive(Axies axie) throws InputMismatchException {

    try {
      while (!arrived) {
        System.out.println("added to waiting... " + waiting + " in waiting");
        waiting++;
        this.wait();
      }
      countDown();

    } catch (InterruptedException e) {
      countDown();
    }
    return box.getAxies(axie);
  }

  private void countDown() {
    waiting--;
    if (waiting < 1) {
      arrived = false;
      waiting = numberOfConsumers;
    }
  }

  public synchronized void await() {
    try {
      this.wait();
    } catch (InterruptedException e) {
    } catch (IllegalMonitorStateException e2) {
      System.out.println(e2.getMessage());

    }
  }

  public synchronized void awaitTimed(long time) {
    try {
      this.wait(time);
    } catch (InterruptedException e) {
    } catch (IllegalMonitorStateException e2) {
      System.out.println(e2.getMessage());

    }
  }
}
