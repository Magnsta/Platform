import com.pi4j.io.serial.*;
import com.pi4j.util.Console;

import java.io.IOException;

/**
 * connects to and sends information to the MCU. usint ttyAMA0 with 115200 bandthith
 *
 * @author sophu
 * @version 0
 */
public class Roboclaw {
  //valid addresses
  private static final int[] ADDRESSES = new int[]{0x80, 0x81};

  //--------------------COMMANDS--------------------------------------------------

  //valid Roboclaw commands, se Roboclaw user manual for more info

  // reading commands
  private static int READ_ENC1 = 16;//Read Encoder Count/Value for M1.
  private static int READ_ENC2 = 17; //Read Encoder Count/Value for M2.
  private static int READ_M1_SPEED = 18;//Read M1 Speed in Encoder Counts Per Second.
  private static int READ_M2_SPEED = 19;//Read M2 Speed in Encoder Counts Per Second.
  private static int READ_CUR_1 = 30;//Read Current M1 Raw Speed
  private static int READ_CUR_2 = 31; //Read Current M2 Raw Speed
  private static int READ_ENC_CNT = 78; //Read Encoders Counts
  private static int READ_RAW_MTR_SPD = 79; //Read Raw Motor Speeds
  private static int READ_AVG_MTR_SPD = 108; //Read Motor Average Speeds
  private static int READ_SPD_ERR = 111; //Read Speed Errors
  private static int READ_POS_ERR = 114; //Read Position Errors
  private static int READ_BUFFER_LEN = 47;//Read Buffer Length.
  private static int READ_M1_VEL_PID_CONST = 55;//Read Motor 1 Velocity PID Constants
  private static int READ_M2_VEL_PID_CONST = 56;//Read Motor 2 Velocity PID Constants
  private static int READ_POS_PID_M1 = 63;//Read Motor 1 Position PID Constants
  private static int READ_POS_PID_M2 = 64;//Read Motor 2 Position PID Constants


  //set commands
  private static int SET_ENC1 = 22;//Set Encoder 1 Register(Quadrature only).
  private static int SET_ENC2 = 23;//Set Encoder 2 Register(Quadrature only).
  private static int SET_VEl_PID_CONST1 = 28; //Set Velocity PID Constants for M1.
  private static int SET_VEl_PID_CONST2 = 29;//Set Velocity PID Constants for M2.
  private static int SET_POS_PID_M1 = 61;//Set Position PID Constants for M1.
  private static int SET_POS_PID_M2 = 62;//Set Position PID Constants for M2


  //reset commands
  private static int RESET_ENCS = 20;//Resets Encoder Registers for M1 and M2(Quadrature only).

  //drive  commands
  private static int DRIVE_M1_DUTY_CYCL = 32;//Drive M1 With Signed Duty Cycle. (Encoders not required)
  private static int DRIVE_M2_DUTY_CYCL = 33;//Drive M2 With Signed Duty Cycle. (Encoders not required)
  private static int DRIVE_BOTH_DUTY_CYCL = 34;//Drive M1 / M2 With Signed Duty Cycle. (Encoders not required)
  private static int DRIVE_M1_SIGN_SPD = 35;//Drive M1 With Signed Speed.
  private static int DRIVE_M2_SIGN_SPD = 36;//Drive M2 With Signed Speed.
  private static int DRIVE_BOTH_SIGN_SPD = 37;//Drive M1 / M2 With Signed Speed.
  private static int DRIVE_M1_SIGN_SPD_ACL = 38;//Drive M1 With Signed Speed And Acceleration.
  private static int DRIVE_M2_SIGN_SPD_ACL = 39;//Drive M2 With Signed Speed And Acceleration.
  private static int DRIVE_BOTH_SIGN_SPD_ACL = 40;//Drive M1 / M2 With Signed Speed And Acceleration.
  private static int DRIVE_M1_SIGN_SPD_DIST = 41;//Drive M1 With Signed Speed And Distance. Buffered.
  private static int DRIVE_M2_SIGN_SPD_DIST = 42;//Drive M2 With Signed Speed And Distance. Buffered.
  private static int DRIVE_BOTH_SIGN_SPD_DIST = 43;//Drive M1 / M2 With Signed Speed And Distance. Buffered.
  private static int DRIVE_M1_SIGN_SPD_DIST_ACL = 44;//Drive M1 With Signed Speed, Acceleration and Distance. Buffered.
  private static int DRIVE_M2_SIGN_SPD_DIST_ACL = 45;//Drive M2 With Signed Speed, Acceleration and Distance. Buffered.
  private static int DRIVE_BOTH_SIGN_SPD_DIST_ACL = 46;//Drive M1 / M2 With Signed Speed, Acceleration And Distance. Buffered.
  private static int DRIVE_BOTH_INDIVIDUAL_SIGN_SPD_ACL = 50;//Drive M1 / M2 With Individual Signed Speed and Acceleration
  private static int DRIVE_BOTH_INDIVIDUAL_SIGN_SPD_ACL_DIST = 51;//Drive M1 / M2 With Individual Signed Speed, Accel and Distance
  private static int DRIVE_M1_SIGN_DUTY_SPD = 52;//Drive M1 With Signed Duty and Accel. (Encoders not required)
  private static int DRIVE_M2_SIGN_DUTY_SPD = 53;//Drive M2 With Signed Duty and Accel. (Encoders not required)
  private static int DRIVE_BOTH_SIGN_DUTY_SPD = 54;//Drive M1 / M2 With Signed Duty and Accel. (Encoders not required)
  private static int DRIVE_M1_SPD_ACL_DCL_POS = 65;//Drive M1 with Speed, Accel, Deccel and Position
  private static int DRIVE_M2_SPD_ACL_DCL_POS = 66;// Drive M2 with Speed, Accel, Deccel and Position
  private static int DRIVE_BOTH_SPD_ACL_DCL_POS = 67;//Drive M1 / M2 with Speed, Accel, Deccel and Position
  private static int DRIVE_M1_POS = 119; //Drive M1 with Position.
  private static int DRIVE_M2_POS = 120; //Drive M2 with Position.
  private static int DRIVE_BOTH_POS = 121; //Drive M1/M2 with Position.
  private static int DRIVE_M1_SPD_POS = 122; //Drive M1 with Speed and Position.
  private static int DRIVE_M2_SPD_POS = 123; //Drive M2 with Speed and Position.
  private static int DRIVE_BOTH_SPD_PSO = 124; //Drive M1/M2 with Speed and Postion.

  //---------------------------------------------------------------------------------------------------------
  private Console console;

  // the commeand we are sending
  private int command = 0;

  private com.pi4j.io.serial.Serial serial;

  public Roboclaw() {

  }


  public void start() throws IOException, InterruptedException {
    // create Pi4J console wrapper/helper
    // (This is a utility class to abstract some of the boilerplate code)
    console = new Console();

    // print program title/header
    console.title("Platform controller", "Serial Communication with Roboclaw");

    // allow for user to exit program using CTRL-C
    console.promptForExit();

    // create an instance of the serial communications class
    serial = SerialFactory.createInstance();

    // create and register the serial data listener
    serial.addListener(new SerialDataEventListener() {
      public void dataReceived(SerialDataEvent event) {

        // NOTE! - It is extremely important to read the data received from the
        // serial port.  If it does not get read from the receive buffer, the
        // buffer will continue to grow and consume memory.

        // print out the data received to the console
        try {
          console.println("[HEX DATA]   " + event.getHexByteString());
          console.println("[ASCII DATA] " + event.getAsciiString());
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    });

    // create serial config object
    SerialConfig config = new SerialConfig();

    // set default serial settings (device, baud rate, flow control, etc)
    //
    // by default, use the DEFAULT com port on the Raspberry Pi (exposed on GPIO header)
    // NOTE: this utility method will determine the default serial port for the
    //       detected platform and board/model.  For all Raspberry Pi models
    //       except the 3B, it will return "/dev/ttyAMA0".  For Raspberry Pi
    //       model 3B may return "/dev/ttyS0" or "/dev/ttyAMA0" depending on
    //       environment configuration.
    config.device(SerialPort.getDefaultPort())
        .baud(Baud._115200)
        .dataBits(DataBits._8)
        .parity(Parity.NONE)
        .stopBits(StopBits._1)
        .flowControl(FlowControl.NONE);

    //TODO: check if needed:
    /*
        // parse optional command argument options to override the default serial settings.
        if (args.length > 0) {
          config = CommandArgumentParser.getSerialConfig(config, args);
        }
    */


    // display connection details
    console.box(" Connecting to: " + config.toString(),
        " We are sending ASCII data on the serial port every 1 second.",
        " Data received on serial port will be displayed below.");


    // open the default serial device/port with the configuration settings
    serial.open(config);

  }
}


