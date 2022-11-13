import FunctionAsAService.Function;
import FunctionAsAService.Memory.Memory;
import Simulation.FaaSSimulation;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.util.LinkedList;
import java.util.List;
import java.util.StringJoiner;
import org.jetbrains.annotations.NotNull;

/**
 * Starts a server with args[0] = path to csv of the form
 * FunctionID_f,AvgServiceTimeMilliseconds,Invocations30Days
 */
public class Main {

  private static final int M = 400; // 4000 MB (4GB)

  public static void main(String[] args) throws IOException {
    // Set up objects for simulation
    File file = new File("trace-final.csv");
    Memory memory = new Memory(M);
    List<Function> functions = parseCSV(file);
    // Create a new simulation
    memory.fillMemory(functions); // A6
    FaaSSimulation sim = new FaaSSimulation(memory, functions);
    sim.runSim();
    // Print function statistics
    String statistics = collectFunctionStatistics(functions);
    // Save to file
    File pathToFunctionResultsCSV = new File(file.getAbsoluteFile().getParentFile(),
        "trace-function-results.csv");
    printToFile(pathToFunctionResultsCSV, statistics);
  }

  private static void printToFile(File file, String content) throws IOException {
    Files.writeString(file.toPath(), content);
  }

  private static String collectFunctionStatistics(List<Function> functions) {
    StringBuilder sb = new StringBuilder();
    sb.append("FunctionID,Requests,ColdStarts,Invocations,Promotions,Completions,Rejections\n");
    for (Function f : functions) {
      StringJoiner sj = new StringJoiner(",");
      sj.add(Integer.toString(f.getFunctionID()));
      sj.add(Integer.toString(f.getRequests()));
      sj.add(Integer.toString(f.getColdStarts()));
      sj.add(Integer.toString(f.getInvocations()));
      sj.add(Integer.toString(f.getPromotions()));
      sj.add(Integer.toString(f.getCompletions()));
      sj.add(Integer.toString(f.getRejectsion()));
      sb.append(sj).append("\n");
    }
    return sb.toString();
  }

  private static @NotNull List<Function> parseCSV(File fp) throws IOException {
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
