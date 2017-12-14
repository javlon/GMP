import ilog.concert.IloException;
import org.apache.commons.cli.*;

import java.io.File;
import java.io.FileNotFoundException;
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
        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = parser.parse(options, args);
        if (!cmd.hasOption("f")) {
            System.err.println("Input file name is required!");
            return;
        }
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
        simpleAlgorithm.cplexSolution(p.toArray(new int[p.size()][p.get(0).length][2]));
    }
}