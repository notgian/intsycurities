package solver;

import java.util.Comparator;

class HeuristicsComparator implements Comparator<GameState> {
    @Override
    public int compare(GameState s1, GameState s2) {
        return Double.compare(s1.calculateHeuristic(), s2.calculateHeuristic());
    }
}
