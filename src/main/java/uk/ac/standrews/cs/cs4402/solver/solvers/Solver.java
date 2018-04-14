package uk.ac.standrews.cs.cs4402.solver.solvers;

import uk.ac.standrews.cs.cs4402.solver.graphDataModel.BinaryCSPGraph;
import uk.ac.standrews.cs.cs4402.solver.heuristics.values.ValueOrderingHeuristic;
import uk.ac.standrews.cs.cs4402.solver.heuristics.variables.VariableOrderingHeuristic;

import java.util.Map;

public interface Solver {
    void setCSP(BinaryCSPGraph csp);
    boolean step(VariableOrderingHeuristic varH, ValueOrderingHeuristic valH, boolean slow);
    boolean isSAT();
    void displaySearchTree(boolean update);
    Map<Integer, Integer> getAssignments();
    int getNumNodes();
}
