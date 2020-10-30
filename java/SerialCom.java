import com.pi4j.io.serial.*;
import com.pi4j.util.CommandArgumentParser;
import com.pi4j.util.Console;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class SerialCom {
  private int address = 0x80;
  private int port ;
  private InputStream in;
  private OutputStream out;
  private Serial serial;
  private Console console;



}