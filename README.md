# CS251 Data Structures Project 4  

## Overview  
This project was completed as part of **CS 251: Data Structures** at Purdue University.  
It focuses on implementing advanced data structures and graph algorithms entirely from scratch in **Java 17**, without relying on Java’s built-in tree or graph libraries.  

The project has three major components:  
1. **ScapeGoat Tree with Range Queries** – a self-balancing binary search tree with efficient range query support.  
2. **WorldBuildingManager (Monstrous Road Minimizer)** – an MST-based algorithm to minimize total road length when connecting cities.  
3. **EVRoutePlanner (Electric Voyage Planner)** – a shortest-path algorithm ensuring electric vehicles can travel within battery range using charging stations.  

---

## Features  

### 1. ScapeGoat Tree with Range Queries  
- Implements `ScapeGoatTree<K,V>` with support for:  
  - Insertion, deletion, and lookup of key–value pairs.  
  - Automatic rebalancing using the scapegoat strategy.  
  - Efficient handling of duplicate keys and deletions.  
- `ScapeGoatIntKey<V>` adds:  
  - `getRange(int start, int end)` method to return values with keys in `[start, end]` in **O(log n + r)** time (where *r* is result size).  
- Balanced using rebuild operations that construct a perfectly balanced BST from inorder traversal.  

### 2. WorldBuildingManager (Minimum Road Network)  
- Reads a list of cities with coordinates and connects them with **minimum total road length**.  
- Uses **Minimum Spanning Tree (MST)** algorithms to compute optimal road placement.  
- Ensures exactly `C − 1` roads for `C` cities, minimizing construction costs.  

### 3. EVRoutePlanner (Electric Vehicle Routing)  
- Computes the shortest path from a source city to a destination while respecting EV battery range.  
- Only allows stops at **charging stations** between source and destination.  
- If no valid route exists within constraints, returns `"NO_PATH"`.  
- Efficiently handles up to `10^5` locations.  

---

## Technical Details  
- **Language**: Java 17  
- **Constraints**:  
  - No usage of `TreeMap`, `TreeSet`, or other built-in balanced trees.  
  - Allowed only `java.util.List`, `ArrayList`, and streams where explicitly specified.  
- **Complexity Goals**:  
  - `getRange`: `O(log n + r)`  
  - Insert/Delete: `O(log n)` amortized  
  - MST and EV routing: optimized for large input constraints.  

---

## Example Usage  

### ScapeGoatIntKey Example
```java
ScapeGoatIntKey<String> tree = new ScapeGoatIntKey<>();
tree.add(10, "A");
tree.add(20, "B");
tree.add(30, "C");
System.out.println(tree.getRange(15, 30)); // Output: ["B", "C"]
```

### WorldBuildingManager Example
```java
//Input file:
4
0 0
0 3
3 0
3 3

//Output (total road length):
9.000000
```

### EVRoutePlanner Example

- Input file with cities and charging stations, maxRange = 75 →
- Valid route: 0 → 1 → 2 → 5 → 6 → 9
- Total distance = 250.000

## Skills Demonstrated

- Advanced self-balancing trees (ScapeGoat Tree implementation).

- Graph algorithms: Minimum Spanning Tree (Kruskal/Prim) and shortest-path with constraints.

- Algorithm optimization for large-scale input (up to 2×10^6 insertions, 10^5 nodes).

- Software engineering best practices: modular design, JUnit testing, interface-based programming.

## How to Run

- Compile with Java 17:
```java
javac src/CoreUtils/*.java src/Stories/*.java
```

- Run test files (sample):
```java
java src/Stories/WorldBuildingManager input.txt
java src/Stories/EVRoutePlanner map.txt 0 9 75.0
```
