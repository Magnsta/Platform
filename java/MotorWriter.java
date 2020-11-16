
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import com.pi4j.io.*;
import com.pi4j.wiringpi.Gpio;
import com.pi4j.wiringpi.GpioUtil;
public class MotorWriter implements Runnable {
  private final long FREQ;
  private final String PORT;
  private SerialCom ser;
  private StorageBox box;
  private RoboClaw mc1; //controller for m1 and m2
  private RoboClaw mc2; //controller for m3
  private int maxEnc; //maximum encoder value
  private int minEnc; // minimum encoder value

  final GpioController gpio = GpioFactory.getInstance();

  Gpio.pinMode(0,Gpio.INPUT); //11 Sets pins on Raspberry pi as input
  Gpio.pinMode(1,Gpio.INPUT); //12
  Gpio.pinMode(2,Gpio.INPUT); //13
  Gpio.pinMode(3,Gpio.INPUT); //15
  Gpio.pinMode(4,Gpio.INPUT); //16
  Gpio.pinMode(5,Gpio.INPUT); //18

  public MotorWriter(long freq, StorageBox box, String address) {

    this.PORT = address;
    startSerialCom();
    this.mc1 = new RoboClaw(0x80);
    this.mc2 = new RoboClaw(0x81);
    this.FREQ = freq;
    this.box = box;
    resetMotorsAndEncoders();
    minEnc = 0;
    maxEnc = 100;
  }

  private void startSerialCom() {
    try {
      this.ser = new SerialCom(PORT, 115200);
    } catch (IllegalArgumentException e) {
      System.out.println(e.getMessage());
    } catch (IOException IOe) {
      throw new RuntimeException("error in thead: " + Thread.currentThread().getName() +
          "\n error Message: " + IOe.getMessage());
    }
  }

  public void run() {
    resetMotorsAndEncoders();

    int[] newPos;
    while (!Thread.currentThread().isInterrupted()) {
      try {
        this.wait(FREQ);
        newPos = getEncPositions(box.getMotorAngles());
        sendNewMotorPositions(newPos);
      } catch (InterruptedException e) {
        System.out.println(e.getMessage());
      }
    }
  }

  private void sendNewMotorPositions(int[] newValues) {
    sendM1Pos(newValues[0]);
    sendM2Pos(newValues[1]);
    sendM3Pos(newValues[2]);
  }

  public void sendM1Pos(int encValue) {
    ArrayList<byte[]> cmd = mc1.setPosM1Cmd(encValue);
    for(byte[] bs : cmd){
       System.out.println(ByteBuffer.wrap(bs).getInt());}
    sendCommandArray(cmd);
  }

  public void sendM2Pos(int encValue) {ArrayList<byte[]> cmd = mc1.setPosM2Cmd(encValue);
    for(byte[] bs : cmd){
       System.out.println(ByteBuffer.wrap(bs).getInt());}
    sendCommandArray(cmd);
  }

  public void sendM3Pos(int encValue) {ArrayList<byte[]> cmd = mc2.setPosM1Cmd(encValue);
    for(byte[] bs : cmd){
       System.out.println(ByteBuffer.wrap(bs).getInt());}
    sendCommandArray(cmd);
  }

  private void setMotorPos(int motor, int encValue) {
    ArrayList<byte[]> commandArray;
    switch (motor) {
      case 1:
        commandArray = mc1.setPosM1Cmd(encValue);
        break;
      case 2:
        commandArray = mc1.setPosM2Cmd(encValue);
        break;
      case 3:
        commandArray = mc2.setPosM1Cmd(encValue);
        break;

      default:
        throw new IllegalArgumentException("error int " + motor + " has no corresponding motor unit.");
    }
    sendCommandArray(commandArray);
    if (checkAch()) {
      return;
    }
    throw new RuntimeException("error motor " + motor + " did not acknowlage the message.");
  }

  private boolean checkAch() {
    try {
      byte[] ack = ser.read(1);
      int intAck = ByteBuffer.wrap(ack).getInt();
      if (intAck == 0xFF) {
        return true;
      } else return false;

    } catch (IOException IOe) {
      throw new RuntimeException(IOe.getMessage());
    }

  }

  /**
   * ////TODO save MaxEnc and MinEnc values
   * ////TODO Make method to find MinEnc aswell
   */
  private void resetMotorsAndEncoders() {
    //boolean atStart = false;
    boolean atEnd = false;
    while (!atEnd) {
      driveM1(-100);
      throw new RuntimeException("program the reset motors method you dufus!");
      //getInput form sensor
      atEnd = getInputFormSensor(0);
    }
    sendCommandArray(mc1.stopM1());
    setMotorPos(1, (int) ((maxEnc - minEnc) / 2));
    sendCommandArray(mc1.resetEnc1Cmd());

    atEnd = false;
    while (!atEnd) {
      driveM2(-100);
      //getInput form sensor
      atEnd = getInputFormSensor(2);
    }

    sendCommandArray(mc1.stopM2());
    setMotorPos(2, (int) ((maxEnc - minEnc) / 2));

    sendCommandArray(mc1.resetEnc2Cmd());

    atEnd = false;
    while (!atEnd) {
      driveM3(-100);
      //getInput from sensor
      atEnd = getInputFormSensor(4);
    }
    sendCommandArray(mc2.stopM1());
    setMotorPos(3, (int) ((maxEnc - minEnc) / 2));
    sendCommandArray(mc2.resetEnc1Cmd());
  }

  private void sendCommand(byte[] cmd) {
    try {
      ser.write(cmd);
    } catch (IOException IOe1) {
      try {
        Thread.sleep(100);
        ser.write(cmd);
      } catch (IOException IOe2) {
        throw new RuntimeException("IO connection error in thread: " + Thread.currentThread().getName()
            + "\nTwo tries gave error messages:" + "\n" + IOe1.getMessage() + "\n" + IOe2.getMessage());
      } catch (InterruptedException e) {
      }
    }
  }

  private void sendCommandArray(ArrayList<byte[]> ba) {
    for (byte[] bs : ba) {
      sendCommand(bs);
    }
    try{
    System.out.println(ser.readLine());}catch(IOException e){
      System.out.println(e.getMessage());}
  }

  /**
   * returns the encoder value of the motor given an angle for the motor.
   *
   * @param motorAngle the angleof the motor
   * @return the encodervalue
   */
  private int encPosFromAngle(double motorAngle) {
    return (int) Math.round(projectDouble(motorAngle, maxEnc, minEnc, MotorController.getOperatingAngle()));
  }

  /**
   * @param motorAngels
   * @return
   */
  private int[] getEncPositions(double[] motorAngels) {
    int[] returnInts = new int[motorAngels.length];
    for (int i = 0; i < motorAngels.length; i++) {
      returnInts[i] = encPosFromAngle(motorAngels[i]);
    }
    return returnInts;
  }

  /**
   * @param sensor
   * @return
   */
  private boolean getInputFormSensor(int sensor){
    if(sensor == 1) {
      int value = Gpio.digitalRead(sensor)
      if(value != 1)
      {
        return false;
      }
      else{
        return true;
      }
    }
    if(sensor == 2){
      int value = Gpio.digitalRead(sensor)
      if(value != 1)
      {
        return false;
      }
      else{
        return true;
      }
    }
    if(sensor == 3){
      int value = Gpio.digitalRead(sensor)
      if(value != 1)
      {
        return false;
      }
      else{
        return true;
      }
    }
    if(sensor == 4){
      int value = Gpio.digitalRead(sensor)
      if(value != 1)
      {
        return false;
      }
      else{
        return true;
      }
    }
    I
    if(sensor == 5){
      int value = Gpio.digitalRead(sensor)
      if(value != 1)
      {
        return false;
      }
      else{
        return true;
      }
    }
    I
    if(sensor == 6){
      int value = Gpio.digitalRead(sensor)
      if(value != 1)
      {
        return false;
      }
      else{
        return true;
      }
    }
  }
  private double projectDouble(double value, int max, int min, double scale) {
    return (value * ((max - min) / scale));
  }

  private void driveM1(int speed) {
    sendCommandArray(mc1.driveM1Cmd(speed));
  }

  private void driveM2(int speed) {
    sendCommandArray(mc1.driveM2Cmd(speed));
  }

  private void driveM3(int speed) {
    sendCommandArray(mc2.driveM1Cmd(speed));
  }


}
