# CompSt 751 Group Project

## Name

Java Sortledton

## People

Siddhi Vilas Dube, ,
Abdulla Fazil, ,
Jesse Tessmer, ,
Dustin Underwood, @moleodonuts, underw33@uwm.edu
Sri Moulika Yetukuri, @moulika006, yetukuri@uwm.edu

## Paper

Sortledton: a Universal Graph Data Structure
https://dl.acm.org/doi/10.14778/3514061.3514065

### Scope

We will implement the Sortledton graph data structure in Java, as described in the paper. The focus will be on handling both small neighborhoods using headless power-of-two-sized vectors and large neighborhoods using unrolled skip lists implemented by extending ConcurrentSkipListSet&lt;E&gt;. We will test the efficiency of our implementation using Graphalytics.

We will exclude:

- Memory management
  - This will eliminate a memory precaching function described in the paper due to limitations of the Java language.
- Parallelization:
  - Read-write latches on the Sortledton DS will be removed.
  - The “group mutual exclusion” advanced locking mechanism described in the referenced Unrolled Skip Lists paper.

### Artifacts found

- Graphalytics: We found a Java implementation of Graphalytics at Graphalytics <https://github.com/ldbc/ldbc_graphalytics/tree/main> . This will be used for benchmarking our Sortledton implementation.
- Sortledton (C++ Implementation): We located the C++ implementation of Sortledton at <https://gitlab.db.in.tum.de/per.fuchs/sortledton> . While we are implementing the project in Java, we may reference this implementation for comparison or potential insights into certain design elements.
- Skip list class in java.util.concurrent.ConcurrentSkipListSet&lt;E&gt;: Java.util includes a skip list class, which we intend to extend to convert it to an _unrolled_ skip list. [ConcurrentSkipListSet (Java Platform SE 8 ) (oracle.com)](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/ConcurrentSkipListSet.html) This will require:
  - creating a new Node class to handle (static) arrays
  - overriding methods such as add( ), remove( ), and contains( )
  - implementing new methods, namely split( ), merge( ), and rebalance( ), described in _Concurrent Unrolled Skiplist_ (Platz et al., 2019)

### Comparison of the authors' implementation to our implementation

The authors' implementation of Sortledton in C++ allowed for fine-grained manual memory management and included parallelization through read-write latches, enabling concurrent graph updates. They managed small neighborhoods with dynamic arrays and large neighborhoods using a custom implementation of unrolled skip lists, optimizing for both memory usage and performance in high-degree vertices.

In our implementation, built in Java, we rely on automatic memory management via the garbage collector, simplifying the process but potentially increasing memory overhead. We are not implementing parallelization, and for neighborhood management, we use headless power-of-two-sized vectors for small neighborhoods and an unrolled skip list, created by extending ConcurrentSkipListSet&lt;E&gt;, for large ones to avoid building it from scratch. Performance testing will be done using Graphalytics, similar to the authors, but without deep customization, which was aimed at a close comparison to the Teseo data structure.


## Plan

### Main classes

- SortledtonGraph: The core class representing the graph. It will manage the adjacency list using the adjacency index map (Map&lt;ID, Set<ID&gt;).
- NeighborhoodSet: This class will handle small neighborhoods using headless power-of-two-sized vectors and large neighborhoods using unrolled skip lists. It will dynamically switch between the two based on neighborhood size.
- Edge: A class representing edges between vertices. It will manage edge properties (e.g., weight, direction).

### Helper classes

- Vertex: A class representing a vertex in the graph, which stores vertex-specific information such as the vertex ID and a reference to its neighborhood.
- ConcurrentSkipListSetHelper: A helper class that wraps and customizes ConcurrentSkipListSet&lt;E&gt; for large neighborhoods to behave as unrolled skip lists.

### Test Suites

Primary Unit Tests

- Description: This suite will focus on validating core operations of the Sortledton graph structure, such as vertex insertion, edge insertion, edge deletion, and neighborhood traversal. We will test edge cases like inserting duplicate edges, removing non-existent edges, and handling empty neighborhoods.

Invariant Tests

- Description: This suite will check the well-formedness of the data structure after each operation. It will validate invariants such as the correctness of the neighborhood ordering, whether the correct data structure (dynamic array or unrolled skip list) is being used based on neighborhood size, and that no corruption occurs during operations.

Graphalytics Efficiency Testing

- Description: This suite will evaluate the performance of our implementation by running standard Graphalytics benchmarks using the Java-based Graphalytics tool from [GitHub](https://github.com/ldbc/ldbc_graphalytics). We will compare the efficiency of small neighborhood handling (via dynamic arrays) against large neighborhoods (using unrolled skip lists). We will also benchmark the overall graph performance and compare it against the authors’ results.

#### Functionality testing


## Status

#### 13/October/2024
As of now, we have completed the design phase and identified the core components to implement. We have found a C++ implementation of Sortledton from [GitLab](https://gitlab.db.in.tum.de/per.fuchs/sortledton) that will serve as a reference, although we are focusing on the Java implementation. Unit and invariant testing will begin after the base implementation is complete.





