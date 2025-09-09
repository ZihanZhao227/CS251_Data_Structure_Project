package Stories;

import CoreUtils.UsefulContainers.iPair;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;






public class WorldBuildingManager implements WorldBuildingManagerInterface {

    @Override
    public List<CityEdge> getMinimumConnectingRoads(String filename) {

        List<CityEdge> mst = new ArrayList<>();
        List<iPair> pts = new ArrayList<>();

        // 读取所有城市坐标
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line = br.readLine(); // 城市数量
            if (line == null) return mst;
            int c;
            try {
                c = Integer.parseInt(line.trim());
            } catch (NumberFormatException e) {
                return mst;
            }
            for (int i = 0; i < c; i++) {
                line = br.readLine();
                if (line == null) break;
                String[] tok = line.trim().split("\\s+");
                if (tok.length < 2) continue;
                try {
                    int a = Integer.parseInt(tok[0]);
                    int b = Integer.parseInt(tok[1]);
                    pts.add(new iPair(a, b));
                } catch (NumberFormatException ignored) {
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to read input",e);
        }

        int n = pts.size();
        if (n <= 1) return mst;

        boolean[] inMST = new boolean[n];
        double[] minDistSq = new double[n];
        int[] parent = new int[n];

        for (int i = 0; i < n; i++){
            minDistSq[i] = Double.POSITIVE_INFINITY;
            parent[i] = -1;
        }
        minDistSq[0] = 0.0;

        for (int iter = 0; iter < n; iter++) {
            int u = -1;
            double bestSq = Double.POSITIVE_INFINITY;
            for (int i = 0; i < n; i++){
                if (!inMST[i] && minDistSq[i] <  bestSq) {
                    bestSq = minDistSq[i];
                    u = i;
                }
            }
            if (u == -1) break;
            inMST[u] = true;
            if (parent[u] != -1) {
                mst.add(new CityEdge(pts.get(parent[u]), pts.get(u)));
            }
            // 松弛
            iPair pu = pts.get(u);
            for (int v = 0; v < n; v++) {
                if (inMST[v]) continue;
                iPair pv = pts.get(v);

                double dx = pu.a -pv.a;
                double dy = pu.b- pv.b;
                double distSq = dx * dx +dy * dy;
                if (distSq < minDistSq[v]) {
                    minDistSq[v] = distSq;
                    parent[v] = u;
                }
            }
        }

        return mst;
    }

    // 欧几里得距离（真实距离，仅用于输出/求和）
    private static double euclidean(iPair a, iPair b) {
        double dx = a.a - b.a;
        double dy = a.b - b.b;
        return Math.hypot(dx, dy);
    }

    public static String totalWeightFormatted(List<CityEdge> edges) {
        double sum = 0.0;
        for (CityEdge e : edges) {


            sum += euclidean(e.city1, e.city2);
        }
        return String.format("%.6f", sum);
    }

    public static void main(String[] args) {
        if (args.length < 1) {
            //no print
            return;
        }
        WorldBuildingManager mgr = new WorldBuildingManager();
        List<CityEdge> mst = mgr.getMinimumConnectingRoads(args[0]);
    }
}
