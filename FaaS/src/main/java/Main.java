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

  private static final int M = 40; // 4000 MB (4GB)

  private static FaaSSimulation sim;

  public static void main(String[] args) throws IOException {
    // Set up objects for simulation
    File file = new File("trace-final.csv");
    Memory memory = new Memory(M);
    List<Function> functions = parseCSV(file);

    // Create a new simulation
    memory.fillMemory(functions); // A6
    sim = new FaaSSimulation(memory, functions, 33 * 24 * 60 * 60);
    sim.runSim();

    // Collect Statistics into String
    String functionStatistics = collectFunctionStatistics(functions);

    // Collect CSV version (to not overwrite old)
    File version_file = new File("version.txt");
    int CSV_version =
        Integer.parseInt(new String(Files.readAllBytes(version_file.toPath()))) + 1;

    // Save Statistics to file
    printToFile(version_file, String.valueOf(CSV_version));
    File pathToFunctionResultsCSV = new File(file.getAbsoluteFile().getParentFile(),
        "trace-function-results" + CSV_version + ".csv");
    printToFile(pathToFunctionResultsCSV, functionStatistics);

    // Print General Statistics to Console
    String systemStatistics = collectSystemStatistics(functions);
    System.out.println(systemStatistics);
  }

  private static String collectSystemStatistics(List<Function> functions) {
    StringBuilder sb = new StringBuilder();

    // Print time frame
    sb.append("Simulation Ran for: ").append(sim.getSimulationTime()).append("\n");

    double requests = 0, coldStarts = 0, promotions = 0, completions = 0, rejections = 0;

    for (Function f : functions) {
      requests += f.getRequests();
      coldStarts += f.getColdStarts();
      promotions += f.getPromotions();
      completions += f.getCompletions();
      rejections += f.getRejections();
    }

    sb.append("Total Requests: ").append(requests).append("\n");
    sb.append("Total ColdStarts: ").append(coldStarts).append("\n");
    sb.append("Total Promotions: ").append(promotions).append("\n");
    sb.append("Total Completions: ").append(completions).append("\n");
    sb.append("Total Rejections: ").append(rejections).append("\n");
    sb.append("---------\n");

    // C_ratio: probability that a request incurs a cold start
    double C_ratio = coldStarts / requests;
    sb.append("C_ratio: ").append(C_ratio).append("\n");
    // L_rate: the rate at which requests are lost
    double L_rate = rejections / sim.getSimulationTime();
    sb.append("L_rate: ").append(L_rate).append("\n");

    return sb.toString();
  }

  private static void printToFile(File file, String content) throws IOException {
    Files.writeString(file.toPath(), content);
  }

  private static String collectFunctionStatistics(List<Function> functions) {
    StringBuilder sb = new StringBuilder();
    sb.append("FunctionID,Requests,ColdStarts,Promotions,Completions,Rejections\n");
    for (Function f : functions) {
      StringJoiner sj = new StringJoiner(",");
      sj.add(Integer.toString(f.getFunctionID()));
      sj.add(Integer.toString(f.getRequests()));
      sj.add(Integer.toString(f.getColdStarts()));
      sj.add(Integer.toString(f.getPromotions()));
      sj.add(Integer.toString(f.getCompletions()));
      sj.add(Integer.toString(f.getRejections()));
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
