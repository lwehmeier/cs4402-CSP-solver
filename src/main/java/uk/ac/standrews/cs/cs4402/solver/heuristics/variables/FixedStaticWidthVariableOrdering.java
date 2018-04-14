package uk.ac.standrews.cs.cs4402.solver.heuristics.variables;

import uk.ac.standrews.cs.cs4402.solver.graphDataModel.BinaryCSPGraph;
import uk.ac.standrews.cs.cs4402.solver.graphDataModel.VarNode;

import java.util.List;
//TODO: graph width. howto in non-np time?
public class FixedStaticWidthVariableOrdering extends VariableOrderingHeuristic {
    protected List<VarNode> varOrderList;
    public FixedStaticWidthVariableOrdering(BinaryCSPGraph bcsp){
    }
    protected Integer calcWidth(VarNode vn, BinaryCSPGraph bcsp){
        return null;
    }
    @Override
    public VarNode getNextVal(BinaryCSPGraph bcsp) {
        return varOrderList.stream().filter(varNode -> varNode.getDomain().size()>1).findFirst().get();
    }
}
