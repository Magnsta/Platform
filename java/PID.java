public class PID {
  private double P;
  private double I;
  private double D;
  private double error;
  private double lastError;
  private double integratedError;
  private double output;

  public PID(double P, double I, double D) {
    this.P = P;
    this.I = I;
    this.D = D;
    this.lastError = 0;
    this.integratedError = 0;
  }

  public synchronized double calculatePID(double is, double want) {
    System.out.println("calculating pid:cis: " + is+ "  want:  " +want);
    error = is - want;
    integratedError += error;
    output += P * error;
    output += integratedError * I;
    output += (error - lastError) * D;
    lastError = error;
    return output;

  }
}
