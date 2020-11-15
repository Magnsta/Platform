
/*
import java.util.ArrayList;

public class RoboClaw {

  private final int address;
  private final Commands cmds;
  private byte crc;

  public RoboClaw(int address) {
    this.address = address;
    this.cmds = new Commands();
    resetCrc();
  }

  public byte[] resetMotorsAndEncodersCmd() {

  }

  public int getAddress() {
    return address;
  }

  public byte[] getAngleM1Cmd() {
  }

  public byte[] setAngleM1Cmd(double angle) {

  }

  public byte[] getAngleM2Cmd() {

  }

  public byte[] setAngleM2Cmd(double angle) {

  }

  public byte[] getEnc1Cmd() {
  }

  public byte[] getEnc2Cmd() {
  }

  public byte[] setSpeedM1Cmd(double speed) {
  }

  public byte[] setSpeedM2Cmd(double speed) {
  }

  public byte[] setAcsM1Cmd(double acs) {
  }

  public byte[] setAcsM2Cmd(double acs) {
  }

  /**
   * 65 - Buffered Drive M1 with signed Speed, Accel, Deccel and Position
   * Move M1 position from the current position to the specified new position and hold the new
   * position. Accel sets the acceleration value and deccel the decceleration value. QSpeed sets the
   * speed in quadrature pulses the motor will run at after acceleration and before decceleration.
   * Send: [Address, 65, Accel(4 bytes), Speed(4 Bytes), Deccel(4 bytes),
   * Position(4 Bytes), Buffer, CRC(2 bytes)]
   * Receive: [0xFF]
   *
   * @param encPos
   * @return
   /
  public byte[] setPosM1Cmd(int encPos) {
    ArrayList<byte[]> command = new ArrayList<>(8);
    command[0] = toByte(address);
    cmds.get(Cmd.DRIVE_M1_POS);
  }

  public byte[] setPosM2Cmd(int encPos) {
  }

  public byte[] getPidValuesCmd() {
  }

  public byte[] stopAllCmd() {

  }

  public long updateCrc(byte[] packet, int nBytes) {
    for (int byte1 = 0; byte1 < nBytes; byte1++) {
      crc = (byte) (crc ^ (packet[byte1] << 8));
      for (char c = 0; c <= 7; c++) {
        if ((crc & 0x8000) == 0x8000) {
          crc = (byte) ((crc << 1) ^ 0x1021);
        } else {
          crc = (byte) (crc << 1);
        }
      }
    }
    return crc;
  }


  /**
   * converts an int to a byte array.
   *
   * @param i the int to be converted
   * @return a byte array
   /
  private byte[] toByte(int i) {
    byte[] theByte = new byte[4];
    theByte[0] = (byte) (i >> 24);
    theByte[1] = (byte) (i >> 16);
    theByte[2] = (byte) (i >> 8);
    theByte[3] = (byte) (i);
    return theByte;
  }

  private void resetCrc() {
    crc = 0;
  }

}
*/