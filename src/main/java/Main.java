import ilog.concert.IloException;
import org.apache.commons.cli.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

/**
 * Created by javlon on 29.11.17.
 */
public class Main {
    public static void main(String[] args) throws IloException, ParseException, FileNotFoundException {
        Options options = new Options();
        options.addOption("h", "print this message");
        options.addOption("f", true, "input file");
        options.addOption("b", false, "run base mode");
        options.addOption("t", true, "number of threads");
        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = parser.parse(options, args);
        if (!cmd.hasOption("f")) {
            System.err.println("Input file name is required!");
            return;
        }
        if(cmd.hasOption("b"))
            base(cmd);
        else
            median(cmd);

    }

    public static void base(CommandLine cmd) throws FileNotFoundException, IloException {
        List<int[][]> p = new ArrayList<>();
        Scanner sc = new Scanner(new File(cmd.getOptionValue("f")));
        while (sc.hasNextLine()){
            String line = sc.nextLine();
            int[] matching = Arrays.stream(line.split(" ")).mapToInt(Integer::valueOf).toArray();
            int[][] matchingPair = new int[matching.length/2][2];
            for (int i = 0; i < matching.length / 2; i++) {
                matchingPair[i][0] = matching[2*i];
                matchingPair[i][1] = matching[2*i+1];
            }
            p.add(matchingPair);
        }
        SimpleAlgorithm simpleAlgorithm = new SimpleAlgorithm();
        simpleAlgorithm.cplexSolution(p.toArray(new int[p.size()][p.get(0).length][2]), cmd.hasOption("t") ? Integer.parseInt(cmd.getOptionValue("t")) : 1);
    }

    public static void median(CommandLine cmd) throws FileNotFoundException, IloException {
        List<int[][]> p = new ArrayList<>();
        Scanner sc = new Scanner(new File(cmd.getOptionValue("f")));
        while (sc.hasNextLine()){
            sc.nextLine();
            String line = sc.nextLine().trim();
            int[] matching = Arrays.stream(line.split("\\s+")).mapToInt(Integer::valueOf).toArray();
            int[][] matchingPair = new int[matching.length][2];
            for (int i = 0; i < matching.length; i++) {
                int left = 2 * Math.abs(matching[i]) + (matching[i] > 0 ? 0 : -1);
                int right = 2 * Math.abs(matching[(i + 1) % matching.length]) + (matching[(i + 1) % matching.length] > 0 ? -1 : 0);
                matchingPair[i][0] = left;
                matchingPair[i][1] = right;
            }
            p.add(matchingPair);
        }
        File file = new File(cmd.getOptionValue("f"));
        PrintStream out = new PrintStream(new FileOutputStream(file.getParent() + "/cplex_solver_output.txt"));
        System.setOut(out);
        SimpleAlgorithm simpleAlgorithm = new SimpleAlgorithm();
        simpleAlgorithm.cplexSolution(p.toArray(new int[p.size()][p.get(0).length][2]), cmd.hasOption("t") ? Integer.parseInt(cmd.getOptionValue("t")) : 1);
    }
}