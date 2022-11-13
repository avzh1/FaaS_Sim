import FunctionAsAService.Function;
import FunctionAsAService.Memory.Memory;
import Simulation.FaaSSimulation;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import org.jetbrains.annotations.NotNull;

/**
 * Starts a server with args[0] = path to csv of the form
 * FunctionID_f,AvgServiceTimeMilliseconds,Invocations30Days
 */
public class Main {

  private static final int M = 40; // 4000 MB (4GB)

  public static void main(String[] args) throws IOException {
    // Set up objects for simulation
    String pathToCSV = "trace-final.csv"; //args[0];
    Memory memory = new Memory(M);
    List<Function> functions = parseCSV(pathToCSV);
    // Create a new simulation
    memory.fillMemory(functions); // A6
    FaaSSimulation sim = new FaaSSimulation(memory, functions);
    sim.runSim();
    // Print function statistics
    printStatistics(functions);
  }

  private static void printStatistics(List<Function> functions) {
    for (Function f : functions) {
      System.out.println(
          f.getFunctionID() + ": " + "(Rs:" + f.getRequests() + ", Colds:" + f.getColdStarts()
              + ", Is:"
              + f.getInvocations() + ", Ps:" + f.getPromotions() + ", Cs:" + f.getCompletions()
              + ")"
              + "(Rej:" + f.getRejectsion() + ")");
    }
  }

  private static @NotNull List<Function> parseCSV(String path) throws IOException {
    File fp = new File(path);
    FileReader fr = new FileReader(fp);
    BufferedReader br = new BufferedReader(fr);
    List<Function> functions = new LinkedList<>();

    String line = br.readLine(); // ignore the heading (assume correct format)
    while ((line = br.readLine()) != null) {
      String[] split = line.split(","); // split the csv into columns
      Function f = new Function(Integer.parseInt(split[0]), Integer.parseInt(split[1]),
          Integer.parseInt(split[2]));
      functions.add(f);
    }

    fr.close();
    return functions;
  }
}
