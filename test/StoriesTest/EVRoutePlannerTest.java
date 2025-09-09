package StoriesTest;

import Stories.EVRoutePlannerInterface;
import Stories.EVRoutePlannerInterface.Location;
import Stories.EVRoutePlanner;
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
 * Tests {@link EVRoutePlanner}.
 */
@Timeout(value = 2000, unit = TimeUnit.MILLISECONDS)
public class EVRoutePlannerTest {
    private static final String prefix = "test/StoriesTest/evRouteFiles/";
    private static final String[] subdirs = { "sample", "manual" };
    private static final String inputSuffix = "in";
    private static final String answerSuffix = "out";

    private final EVRoutePlannerInterface planner = new EVRoutePlanner();

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

    @DisplayName("File-based tests for EV Route Planning")
    @ParameterizedTest(name = "{index}: {0}")
    @MethodSource("testFileProvider")
    void runFiles(File file) {
        String inputFile = file.getPath();
        String ansFile = inputFile.substring(0, inputFile.length() - inputSuffix.length()) + answerSuffix;

        // Read test parameters from answer file
        int sourceId, destId;
        double maxRange, expectedDistance;
        List<Integer> expectedPath = new ArrayList<>();

        try (BufferedReader bf = new BufferedReader(new FileReader(ansFile))) {
            String[] params = bf.readLine().split(" ");
            sourceId = Integer.parseInt(params[0]);
            destId = Integer.parseInt(params[1]);
            maxRange = Double.parseDouble(params[2]);
            expectedDistance = Double.parseDouble(params[3]);

            // Read expected path
            String pathLine = bf.readLine();
            if (!pathLine.equals("NO_PATH")) {
                String[] pathIds = pathLine.split(" ");
                for (String id : pathIds) {
                    expectedPath.add(Integer.parseInt(id));
                }
            }
        } catch (IOException e) {
            throw new AssertionError("GRADER ERROR:: ANSWER FILE NOT FOUND:: \"" + file.getName() + "\"", e);
        }

        // Run student solution
        List<Location> result;
        try {
            result = planner.findEVRoute(inputFile, sourceId, destId, maxRange);
        } catch (Exception e) {
            throw new AssertionError("Error calling findEVRoute(\"" + file.getName() + "\"): " + e.getMessage(), e);
        }

        // Verify answer
        if (expectedPath.isEmpty()) {
            assertNull(result, "Expected no path but got: " + result);
        } else {
            assertNotNull(result, "Expected a path but got null");
            assertEquals(expectedPath.size(), result.size(),
                    "Path length mismatch. Expected " + expectedPath.size() + " but got " + result.size());

            // Verify path matches expected
            for (int i = 0; i < expectedPath.size(); i++) {
                assertEquals(expectedPath.get(i).intValue(), result.get(i).id,
                        "Path mismatch at position " + i);
            }

            // Verify all charging stations are within range
            verifyValidPath(result, maxRange);

            // Verify total distance (within tolerance)
            double actualDistance = calculatePathDistance(result);
            assertEquals(expectedDistance, actualDistance, 0.001,
                    "Path distance incorrect. Expected " + expectedDistance + " but got " + actualDistance);
        }
    }

    /**
     * Verifies that the path is valid (all segments are within maxRange)
     */
    private void verifyValidPath(List<Location> path, double maxRange) {
        for (int i = 0; i < path.size() - 1; i++) {
            Location current = path.get(i);
            Location next = path.get(i + 1);

            // Check if we need charging between current and next
            double distance = calculateDistance(current, next);

            // If it's too far, there must be a charging station
            if (distance > maxRange) {
                // Check if current is a charging station or source
                if (i > 0 && current.type != EVRoutePlannerInterface.LocationType.CHARGING_STATION) {
                    fail("Invalid path: segment from " + current.name + " to " + next.name +
                            " exceeds max range " + maxRange + " (distance: " + distance + ")");
                }
            }
        }
    }

    /**
     * Calculates total distance of a path
     */
    private double calculatePathDistance(List<Location> path) {
        double total = 0;
        for (int i = 0; i < path.size() - 1; i++) {
            total += calculateDistance(path.get(i), path.get(i + 1));
        }
        return total;
    }

    /**
     * Helper to calculate distance between two locations
     */
    private double calculateDistance(Location a, Location b) {
        return Math.hypot(a.coordinates.a - b.coordinates.a,
                a.coordinates.b - b.coordinates.b);
    }
}