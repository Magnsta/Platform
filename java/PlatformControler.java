public class PlatformControler {
  //static math classes

  static public double PI = Math.PI;

  // platform constants
  static public double PLATFORM_LENGTH = 10; //platform length in cm.
  static public double ARM_LENGTH = 5;       //arm length in cm.
  static public double ANGLE_CONSTANT = PLATFORM_LENGTH / (ARM_LENGTH * 4);
  static public double MAX_ANGLE = Math.asin(ANGLE_CONSTANT * Math.sin(PI / 2));

  /**
   * runs the program
   *
   * @param args
   */
  public static void main(String[] args) {
    double p = 0.01;
    double i = 0.000001;
    double d = 0.0001;
    StorageBox box = new StorageBox();
    // initialize pids for axies
    PID XPID = new PID(p, i, d);
    PID YPID = new PID(p, i, d);
    PID ZPID = new PID(p, i, d);
    // initialize io reader:

    Broadcast broadcaster = new Broadcast(3,box);
    Gyroscope IOReadingThread = new Gyroscope(broadcaster);
    // initialize consumer threads
    MotorController Xcalculator = new MotorController(XPID, broadcaster, Axies.X, box);
    MotorController Ycalculator = new MotorController(YPID, broadcaster, Axies.Y, box);
    MotorController Zcalculator = new MotorController(ZPID, broadcaster, Axies.Z, box);

    Thread thread1 = new Thread(IOReadingThread);
    Thread thread2 = new Thread(Xcalculator);
    Thread thread3 = new Thread(Ycalculator);
    Thread thread4 = new Thread(Zcalculator);
    // start Threads
    thread1.start();
    thread2.start();
    thread3.start();
    thread4.start();




    System.out.println("gyro running: " + thread1.isAlive());
    System.out.println("coontroller1 running: " + thread2.isAlive());
    System.out.println("coontroller2 running: " + thread3.isAlive());
    System.out.println("coontroller3 running: " + thread4.isAlive());

  }
}
