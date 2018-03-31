package uk.ac.standrews.cs.cs4402.solver;

import uk.ac.standrews.cs.cs4402.solver.graphDataModel.BinaryCSPGraph;

import java.util.Map;

public interface Solver {
    void setCSP(BinaryCSPGraph csp);
    boolean step();
    boolean isSAT();
    void displaySearchTree();
    Map<Integer, Integer> getAssignments();
}
