package solver;

import java.util.HashSet;

/*
 * Sort of container object to hold all pre-calculated map deadlocks.
 * Passed as a single reference all throughout the solver.
 */
public class DeadlockContext {
    private final HashSet<Integer> cornerDeadlocks;
    private final HashSet<Integer> twoBoxDeadlocks;
    // private final HashSet<Integer> fourBoxDeadlocks;

    public DeadlockContext(HashSet<Integer> cornerDeadlocks, HashSet<Integer> twoBoxDeadlocks) {
        this.cornerDeadlocks = cornerDeadlocks;
        this.twoBoxDeadlocks = twoBoxDeadlocks;
        // this.fourBoxDeadlocks= fourBoxDeadlocks;
    }

    // Getters for individual sets if your heuristic evaluation requires them
    public HashSet<Integer> getCornerDeadlocks() { return this.cornerDeadlocks; }
    public HashSet<Integer> getTwoBoxDeadlocks() { return this.twoBoxDeadlocks; }
    // public HashSet<Integer> getFourBoxDeadlocks() { return this.fourBoxDeadlocks; }
}
