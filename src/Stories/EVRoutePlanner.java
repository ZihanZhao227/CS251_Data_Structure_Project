package Stories;

import CoreUtils.UsefulContainers.iPair;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class EVRoutePlanner implements EVRoutePlannerInterface {
    @Override
    public List<Location> findEVRoute(String filename, int sourceId, int destId, double maxRange) {
//        try {
//            BufferedReader bf = new BufferedReader(new FileReader(filename));
//            //todo
//        } catch (IOException e) {
//
//        }
//        return null;
        List<Location> all = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line = br.readLine();

            if (line == null) return null;
            int N;
            try {
                N = Integer.parseInt(line.trim());
            } catch (NumberFormatException e) {
                return null;
            }
            for (int i = 0; i < N; i++) {
                line = br.readLine();
                if (line == null) break;

                //String[] tokv

                String[] tok = line.trim().split("\\s+");
                if (tok.length < 5) continue;
                try {
                    int id = Integer.parseInt(tok[0] );
                    int x = Integer.parseInt(tok[1] );
                    int y = Integer.parseInt(tok[2] );

                    LocationType type = LocationType.valueOf(tok[3] );

                    String name = tok[4];
                    all.add(new Location(id, new iPair(x, y), type, name) );
                } catch (Exception ignored) {
                }
            }
        } catch (IOException e) {
            return new ArrayList<>();
            // silently fail; no print, no exit
        }

        int n = all.size();
        if (sourceId < 0 || sourceId >= n || destId < 0 || destId >= n) return null;

        // Build list of valid nodes: source, dest, and charging stations
        List<Location> valid = new ArrayList<>();
        boolean[] isValid =new boolean[n];
        Location source =  all.get(sourceId);
        Location dest = all.get(destId);
        valid.add(source);





        isValid[sourceId] = true;
        if (destId != sourceId){
            valid.add(dest);

            isValid[destId] = true;
        }
        for (int i = 0; i < n; i++) {
            Location loc = all.get(i);
            if (i == sourceId || i == destId) continue;




            if (loc.type == LocationType.CHARGING_STATION) {
                valid.add(loc);
                isValid[i] = true;
            }
        }

        int m = valid.size();
        // Map index in valid -> original index for distance calc
        int[] originalIdx = new int[m];
        for (int i = 0; i < m; i++) {
            originalIdx[i] = valid.get(i).id;
        }

        // Dijkstra on valid nodes
        double[] dist = new double[m];
        int[] prev = new int[m];

        boolean[] visited = new boolean[m];
        for (int i = 0; i < m; i++) {
            dist[i] = Double.POSITIVE_INFINITY;
            prev[i] = -1;
        }
        int srcPos = -1, dstPos = -1;
        for (int i = 0; i < m; i++) {
            if (valid.get(i).id == sourceId) srcPos = i;
            if (valid.get(i).id == destId) dstPos = i;
        }
        if (srcPos == -1 || dstPos == -1) return null;

        dist[srcPos] = 0.0;
        MinHeap heap = new MinHeap(m);
        heap.insert(srcPos, 0.0);

        while (!heap.isEmpty()) {
            int u = heap.pollMin();
            if (u == dstPos) break;
            if (visited[u]) continue;
            visited[u] = true;

            Location lu = valid.get(u);
            for (int v = 0; v < m; v++) {
                if (visited[v]) continue;
                if (u == v) continue;
                Location lv = valid.get(v);
                double d = calculateDistance(lu, lv);
                if (d > maxRange) continue;
                double nd = dist[u] + d;
                if (nd + 1e-12 < dist[v]) {
                    dist[v] = nd;
                    prev[v] = u;
                    heap.insertOrUpdate(v, nd);
                }
            }
        }

        if (Double.isInfinite(dist[dstPos])) {
            return null;
        }

        // Reconstruct path
        List<Location> path = new ArrayList<>();
        int cur = dstPos;
        while (cur != -1) {
            path.add(0, valid.get(cur));
            cur = prev[cur];
        }
        return path;

    }

    /**
     * Helper method to calculate Euclidean distance between two locations
     */
    private double calculateDistance(Location a, Location b) {
        return Math.hypot(a.coordinates.a - b.coordinates.a,
                a.coordinates.b - b.coordinates.b);
    }
    private static class MinHeap {
        private final int[] heap; // stores positions
        private final double[] key;
        private final int[] posInHeap; // reverse lookup
        private int size;

        public MinHeap(int capacity) {
            heap = new int[capacity + 1];
            key = new double[capacity + 1];
            posInHeap = new int[capacity + 1];
            for (int i = 0; i <= capacity; i++) posInHeap[i] = -1;
            size = 0;
        }

        public boolean isEmpty() {
            return size == 0;
        }

        public void insert(int idx, double k) {
            if (posInHeap[idx] != -1) return; // already present
            size++;
            heap[size] = idx;
            key[size] = k;
            posInHeap[idx] = size;
            siftUp(size);
        }

        public void insertOrUpdate(int idx, double k) {
            if (posInHeap[idx] == -1) {
                insert(idx, k);
            } else {
                int i = posInHeap[idx];
                if (k < key[i]) {
                    key[i] = k;
                    siftUp(i);
                }
            }
        }

        public int pollMin() {
            if (size == 0) return -1;
            int res = heap[1];
            swap(1, size);
            posInHeap[res] = -1;
            size--;
            siftDown(1);
            return res;
        }

        private void siftUp(int i) {
            while (i > 1) {
                int parent = i / 2;
                if (key[i] < key[parent]) {
                    swap(i, parent);
                    i = parent;
                } else break;
            }
        }

        private void siftDown(int i) {
            while (true) {
                int left = 2 * i;
                int right = left + 1;
                int smallest = i;
                if (left <= size && key[left] < key[smallest]) smallest = left;
                if (right <= size && key[right] < key[smallest]) smallest = right;
                if (smallest != i) {
                    swap(i, smallest);
                    i = smallest;
                } else break;
            }
        }

        private void swap(int i, int j) {
            double tmpK = key[i];
            key[i] = key[j];
            key[j] = tmpK;
            int tmpH = heap[i];
            heap[i] = heap[j];
            heap[j] = tmpH;
            posInHeap[heap[i]] = i;
            posInHeap[heap[j]] = j;
        }
    }

}