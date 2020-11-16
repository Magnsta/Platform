import java.nio.ByteBuffer;
import java.util.ArrayList;

/**
 * Handler for commands for a Roboclaw Motor controller;
 * creates commands that can be sent to the controller to control the motors
 *
 * @author fredborg
 * @version 0
 */
public class RoboClaw {

  private final int address; // address of this Motor controller on the buss.
  private final Commands cmds; // a map of the commands that the motor controller can access.
  private int speed;// speed of the position control.
  private int accel;//acceleration of the position control.
  private int deccel;//deceleration of the position control.
  private ArrayList<byte[]> stoppBothMotorCommand; // stop command is pre generated for speedy delivery;

  /**
   * @param address
   */
  public RoboClaw(int address) {
    this.address = address;
    this.cmds = new Commands();
    this.speed = 1000;
    this.accel = 1000;
    this.deccel = 1000;
    this.stoppBothMotorCommand = generateStopAllCommand();
  }

  public int getAddress() {
    return address;
  }

  /**
   * Resets the value of the Encoder 1 register. Useful when homing motor 1. This command applies to
   * quadrature encoders only.
   *
   * @return [Address, 22, Value(0), CRC(2 bytes)]
   */
  public ArrayList<byte[]> resetEnc1Cmd() {
    int cmd = cmds.get(Cmd.SET_ENC1);
    return getCmdArray(new int[]{address, cmd, 0});
  }

  /**
   * Resets the value of the Encoder 2 register. Useful when homing motor 2. This command applies to
   * quadrature encoders only.
   *
   * @return [Address, 22, Value(0), CRC(2 bytes)]
   */
  public ArrayList<byte[]> resetEnc2Cmd() {
    int cmd = cmds.get(Cmd.SET_ENC2);
    return getCmdArray(new int[]{address, cmd, 0});
  }


  /**
   * Read M1 encoder count/position.
   * <p>
   * Receive: [Enc1(4 bytes), Status, CRC(2 bytes)]
   * Quadrature encoders have a range of 0 to 4,294,967,295. Absolute encoder values are
   * converted from an analog voltage into a value from 0 to 2047 for the full 2v range.
   *
   * @return [Address, 16]
   */
  public ArrayList<byte[]> getEnc1Cmd() {
    int cmd = cmds.get(Cmd.READ_ENC1);
    return getCmdArray(new int[]{address, cmd});
  }

  /**
   * Read M2 encoder count/position.
   * Receive: [EncCnt(4 bytes), Status, CRC(2 bytes)]
   * Quadrature encoders have a range of 0 to 4,294,967,295. Absolute encoder values are
   * converted from an analog voltage into a value from 0 to 2047 for the full 2v range.
   *
   * @return [Address, 17]
   */
  public ArrayList<byte[]> getEnc2Cmd() {
    int cmd = cmds.get(Cmd.READ_ENC2);
    return getCmdArray(new int[]{address, cmd});
  }


  /**
   * Drive M1 With Signed Speed
   * Drive M1 using a speed value. The sign indicates which direction the motor will turn. This
   * command is used to drive the motor by quad pulses per second. Different quadrature encoders
   * will have different rates at which they generate the incoming pulses. The values used will differ
   * from one encoder to another. Once a value is sent the motor will begin to accelerate as fast as
   * possible until the defined rate is reached.
   *
   * @param speed speed of motor
   * @return [Address, 35, Speed(4 Bytes), CRC(2 bytes)]
   */
  public ArrayList<byte[]> driveM1Cmd(int speed) {
    int cmd = cmds.get(Cmd.DRIVE_M1_SIGN_SPD);
    return setDriveMtrCmd(speed, cmd);
  }

  /**
   * Drive M2 With Signed Speed
   * Drive M2 using a speed value. The sign indicates which direction the motor will turn. This
   * command is used to drive the motor by quad pulses per second. Different quadrature encoders
   * will have different rates at which they generate the incoming pulses. The values used will differ
   * from one encoder to another. Once a value is sent the motor will begin to accelerate as fast as
   * possible until the defined rate is reached.
   *
   * @param speed speed of motor
   * @return [Address, 35, Speed(4 Bytes), CRC(2 bytes)]
   */
  public ArrayList<byte[]> driveM2Cmd(int speed) {
    int cmd = cmds.get(Cmd.DRIVE_M2_SIGN_SPD);
    return setDriveMtrCmd(speed, cmd);
  }

  /**
   * Buffered Drive M1 with signed Speed, Accel, Deccel and Position
   * Move M1 position from the current position to the specified new position and hold the new
   * position. Accel sets the acceleration value and deccel the decceleration value. QSpeed sets the
   * speed in quadrature pulses the motor will run at after acceleration and before decceleration.
   * Receive: [0xFF]
   *
   * @param encPos the new ecoder pos for the motor
   * @return [Address, 65, Accel(4 bytes), Speed(4 Bytes), Deccel(4 bytes), Position(4 Bytes), Buffer, CRC(2 bytes)]
   */
  public ArrayList<byte[]> setPosM1Cmd(int encPos) {
    return setPosCmd(encPos, Cmd.DRIVE_M1_SPD_ACL_DCL_POS);
  }

  /**
   * Buffered Drive M2 with signed Speed, Accel, Deccel and Position
   * Move M2 position from the current position to the specified new position and hold the new
   * position. Accel sets the acceleration value and deccel the decceleration value. QSpeed sets the
   * speed in quadrature pulses the motor will run at after acceleration and before decceleration.
   * Receive: [0xFF]
   *
   * @param encPos the new ecoder pos for the motor
   * @return [Address, 65, Accel(4 bytes), Speed(4 Bytes), Deccel(4 bytes), Position(4 Bytes), Buffer, CRC(2 bytes)]
   */
  public ArrayList<byte[]> setPosM2Cmd(int encPos) {
    return setPosCmd(encPos, Cmd.DRIVE_M2_SPD_ACL_DCL_POS);
  }

  /**
   * Sets Motor 1 speed to 0
   *
   * @return [Address, 7,0,crc(2 bytes)]
   */
  public ArrayList<byte[]> stopM1() {
    int cmd = cmds.get(Cmd.DRIVE_FORWARD_M1);
    return stopMotor(cmd);
  }

  /**
   * Sets Motor 2 speed to 0
   *
   * @return [Address, 7,0,crc(2 bytes)]
   */
  public ArrayList<byte[]> stopM2() {
    int cmd = cmds.get(Cmd.DRIVE_FORWARD_M2);
    return stopMotor(cmd);
  }

  /**
   * returns a premade array that stops both motors.
   *
   * @return [address, 7, 0, crc]
   */
  public ArrayList<byte[]> stopAllMotorsCmd() {
    return this.stoppBothMotorCommand;
  }

  private ArrayList<byte[]> getCmdArray(int[] cmds) {
    ArrayList<byte[]> ret = addIntsToByteArray(cmds);
    addCrc(ret);
    return ret;
  }

  private ArrayList<byte[]> setDriveMtrCmd(int speed, int cmd) {
    ArrayList<byte[]> ret = addIntsToByteArray(new int[]{address, cmd, speed});
    addCrc(ret);
    return ret;
  }


  private ArrayList<byte[]> addIntsToByteArray(int[] ints) {
    ArrayList<byte[]> arrayList = new ArrayList<>();
    for (int i : ints) {
      arrayList.add(ByteBuffer.allocate(4).putInt(i).array());
    }
    return arrayList;
  }

  private ArrayList<byte[]> setPosCmd(int encPos, Cmd m) {
    //adding values
    int[] ints = new int[]{address, cmds.get(m), accel, speed, deccel, encPos};
    ArrayList<byte[]> ret = addIntsToByteArray(ints);
    //buffer
    addBuffer((byte) 1, ret);

    //crc 16 calculation of command crc16 checksum
    addCrc(ret);

    return ret;
  }

  private void addCrc(ArrayList<byte[]> arrayList) {
    byte[] bytes = ByteBuffer.allocate(2).putLong(CRC16.crcForCommand(arrayList)).array();
    arrayList.add(bytes);
  }

  private void addBuffer(byte buffer, ArrayList<byte[]> arrayList) {
    if (buffer == 0 || buffer == 1) {
      byte[] bytes = new byte[]{buffer};
      arrayList.add(bytes);
    } else throw new IllegalArgumentException("buffer must be 0 for buffering or 1 for overriding last command");
  }


  private ArrayList<byte[]> stopMotor(int cmd) {
    int[] ints = new int[]{address, cmd, 0};
    return getCmdArray(ints);

  }


  private ArrayList<byte[]> generateStopAllCommand() {
    int cmd = cmds.get(Cmd.DRIVE_FORWARD);
    int[] ints = new int[]{address, cmd, 0};
    return getCmdArray(ints);
  }
}
