package uk.ac.standrews.cs.cs4402.solver.heuristics.variables;

import uk.ac.standrews.cs.cs4402.solver.graphDataModel.BinaryCSPGraph;
import uk.ac.standrews.cs.cs4402.solver.graphDataModel.VarNode;

public abstract class VariableOrderingHeuristic {
    public abstract VarNode getNextVal(BinaryCSPGraph bcsp);
    //fixing java deficiencies...
    //https://stackoverflow.com/questions/182278/is-there-a-way-to-simulate-the-c-friend-concept-in-java
    public static final class Key { private Key() {} }
    protected static final Key key = new Key();
}
