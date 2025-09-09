package Stories;

import CoreUtils.UsefulContainers.iPair;

import java.util.List;

public interface EVRoutePlannerInterface {
    /**
     * Enum representing different types of locations
     */
    enum LocationType {
        CHARGING_STATION,
        RESTAURANT,
        TOURIST_ATTRACTION,
        CITY,
        REGULAR_WAYPOINT
    }

    /**
     * Represents a location in the map with its type and coordinates
     */
    class Location {
        public final int id;
        public final iPair coordinates;
        public final LocationType type;
        public final String name;

        public Location(int id, iPair coordinates, LocationType type, String name) {
            this.id = id;
            this.coordinates = coordinates;
            this.type = type;
            this.name = name;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Location)) return false;
            Location location = (Location) o;
            return id == location.id;
        }

        @Override
        public String toString() {
            return "Location{id=" + id + ", name='" + name + "', type=" + type +
                    ", coords=" + coordinates + "}";
        }
    }

    /**
     * Represents a road segment between two locations
     */
    class RoadSegment {
        public final Location from;
        public final Location to;
        public final double distance;

        public RoadSegment(Location from, Location to, double distance) {
            this.from = from;
            this.to = to;
            this.distance = distance;
        }

        @Override
        public String toString() {
            return "RoadSegment{from=" + from.name + ", to=" + to.name +
                    ", distance=" + distance + "}";
        }
    }

    /**
     * Finds the shortest path from source to destination for an electric vehicle
     * that must visit charging stations to maintain battery charge.
     *
     * The EV has a maximum range of maxRange miles on a full charge and must
     * reach a charging station before the battery depletes. The algorithm should:
     * 1. Build a graph G' containing only charging stations (and source/destination)
     *    where edges exist only between stations within maxRange distance
     * 2. Find the shortest path in G' from source to destination
     * 3. Return the complete path including all intermediate charging stations
     *
     * @param filename file containing the map data
     * @param sourceId ID of the starting location
     * @param destId ID of the destination location
     * @param maxRange maximum distance the EV can travel on a full charge
     * @return list of locations representing the path from source to destination,
     *         or null if no valid path exists
     */
    List<Location> findEVRoute(String filename, int sourceId, int destId, double maxRange);
}