package Stories;

import CoreUtils.UsefulContainers.iPair;

import java.util.List;
import java.util.Objects;

public interface WorldBuildingManagerInterface {
    /**
     * Contains a city edge identified by the coordinates of two cities
     */
    class CityEdge {
        public iPair city1, city2;

        //simple constructor
        public CityEdge(iPair city1, iPair city2) { this.city1 = city1; this.city2 = city2; }

        //default intellij-generated equals
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof CityEdge)) return false;
            CityEdge cityEdge = (CityEdge) o;
            return Objects.equals(city1, cityEdge.city1) && Objects.equals(city2, cityEdge.city2);
        }

        /**
         * This might be helpful for debugging. Default generated one.
         * @return string version of object
         */
        @Override
        public String toString() {
            return "CityEdge{" + "city1=" + city1 + ", city2=" + city2 + '}';
        }
    }

    /**
     * Selects roads per the specifications (minimum cost to connect all cities).
     *
     * @param filename file to read input from
     * @return list of roads, per the specifications
     */
    List<CityEdge> getMinimumConnectingRoads(String filename);
}
