package StoriesTest;

import Stories.WorldBuildingManagerInterface;
import Stories.WorldBuildingManagerInterface.CityEdge;
import Stories.WorldBuildingManager;
import CoreUtils.UsefulContainers.iPair;
import CoreUtilsTest.UsefulObjects.CorrectDisjointSet;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Named;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.*;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;

/**
 * Tests {@link WorldBuildingManager}.
 */
@Timeout(value = 2000, unit = TimeUnit.MILLISECONDS)
public class WorldBuildingManagerTest {
    private static final String prefix = "test/StoriesTest/worldBuildingFiles/";
    private static final String[] subdirs = { "sample", "manual" };
    private static final String inputSuffix = "in";
    private static final String answerSuffix = "out";

    private final WorldBuildingManagerInterface manager = new WorldBuildingManager();

    public static Stream<Arguments> testFileProvider() {
        List<Arguments> args = new ArrayList<>();
        for (String sub : subdirs) {
            File dir = new File(prefix + sub);
            if (!dir.isDirectory()) continue;
            for (File f : dir.listFiles((d, n) -> n.endsWith("." + inputSuffix))) {
                args.add(arguments(Named.of(f.getName(), f)));
            }
        }
        return args.stream();
    }

    @DisplayName("File-based tests for Story 7")
    @ParameterizedTest(name = "{index}: {0}")
    @MethodSource("testFileProvider")
    void runFiles(File file) {
        String inputFile = file.getPath();
        String ansFile = inputFile.substring(0, inputFile.length() - inputSuffix.length()) + answerSuffix;

        long startTime = System.currentTimeMillis();
        List<CityEdge> ans;
        try {
            ans = manager.getMinimumConnectingRoads(inputFile);
        } catch (Exception e) {
            throw new AssertionError("Error calling getMinimumConnectingRoads(\"" + file.getName() + "\": " + e.getMessage(), e);
        }

        double trueWeight;
        try (BufferedReader bf = new BufferedReader(new FileReader(ansFile))) {
            trueWeight = Double.parseDouble(bf.readLine());
        } catch (IOException e) {
            throw new AssertionError("GRADER ERROR:: ANSWER FILE NOT FOUND:: \"" + file.getName() + "\"", e);
        }

        Map<iPair, Integer> cityToIdx = new HashMap<>();
        double[][] adjMatrix = getAdjMatrix(inputFile, file.getName(), cityToIdx);

        assertNotNull(adjMatrix, "GRADER ERROR:: BAD INPUT FILE, NO EDGES RETURNED:: \"" + file.getName() + "\"");
        verifyAnswer(adjMatrix, cityToIdx, trueWeight, ans);
    }

    /**
     * Gets the edges from the input file
     * @param inputFile file to get edges from
     * @param fullFileName full file path for error printing
     * @return adjacency matrix or <code>null</code> if error
     */
    private double[][] getAdjMatrix(String inputFile, String fullFileName, Map<iPair, Integer> cityToIdx) {
        try (BufferedReader bf = new BufferedReader(new FileReader(inputFile))) {
            int numCities = Integer.parseInt(bf.readLine().split(" ")[0]);

            List<iPair> cities = new ArrayList<>(numCities);
            String[] line;
            for (int i = 0; i < numCities; i++) {
                line = bf.readLine().split(" ");
                var temp = new iPair(Integer.parseInt(line[0]), Integer.parseInt(line[1]));
                cities.add(temp);
                cityToIdx.put(temp, i);
            }

            double[][] adjMatrix = new double[numCities][numCities];
            for (int i = 0; i < cities.size(); i++) {
                iPair c = cities.get(i);
                for (int j = 0; j < cities.size(); j++) {
                    if (i != j) {
                        adjMatrix[i][j] = adjMatrix[j][i] = Math.hypot(c.a - cities.get(j).a, c.b - cities.get(j).b);
                    }
                }
            }

            return adjMatrix;
        } catch (IOException e) {
            throw new AssertionError("GRADER ERROR:: BAD INPUT FILE:: \"" + fullFileName + "\"", e);
        }
    }

    /**
     * Verifies the student answer is correct to 3 decimal places and that the graph is fully connected
     * @param adjMatrix graph adjacency matrix to get weights from
     * @param cityToIdx map from city coordinates to index in adjacency matrix
     * @param trueWeight correct MST weight
     * @param ans answer to check
     */
    private void verifyAnswer(double[][] adjMatrix, Map<iPair, Integer> cityToIdx, double trueWeight, List<CityEdge> ans) {
        int numCities = adjMatrix.length;
        assertEquals(numCities - 1, ans.size(),
                "Incorrect MST size, expected " + (numCities - 1) +
                        ", actual is " + ans.size());

        CorrectDisjointSet ds = new CorrectDisjointSet(numCities);
        double totWeight = 0;
        for (CityEdge edge : ans) {
            var idx1 = cityToIdx.get(edge.city1);
            var idx2 = cityToIdx.get(edge.city2);
            assertNotNull(idx1, "Bad edge: No edge from " + edge.city1.toString() + " to " + edge.city2.toString());

            ds.union(idx1, idx2);
            totWeight += adjMatrix[idx1][idx2];
        }

        long finalWeight = (long) (totWeight * 1000);
        long trueFinalWeight = (long) (trueWeight * 1000);
        assertEquals(trueFinalWeight, finalWeight,
                "Weight not correct to 3 decimal places, expected " +
                        trueWeight + ", actual is " + totWeight);
    }
}