public class PlatformControler {
  //static math classes

  static public double PI = Math.PI;

  // platform constants
  static final public double PLATFORM_LENGTH = 10; //platform length in cm.
  static final public double ARM_LENGTH = 5;       //arm length in cm.
  static final public double ANGLE_CONSTANT = PLATFORM_LENGTH / (ARM_LENGTH * 4);
  static final public double MAX_ANGLE = Math.asin(ANGLE_CONSTANT * Math.sin(PI / 2));

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

    //TODO: add startup for system


    // initialize io reader:

    Broadcast broadcaster = new Broadcast(3,box);
    Gyroscope IOReadingThread = new Gyroscope(broadcaster,10);
    // initialize consumer threads
    MotorController Xcalculator = new MotorController(broadcaster, Axies.X, box);
    MotorController Ycalculator = new MotorController(broadcaster, Axies.Y, box);
    MotorController Zcalculator = new MotorController(broadcaster, Axies.Z, box);
    // initialise writer


    Thread thread1 = new Thread(IOReadingThread);
    Thread thread2 = new Thread(Xcalculator);
    Thread thread3 = new Thread(Ycalculator);
    Thread thread4 = new Thread(Zcalculator);
    Thread thread5 = new Thread();
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
