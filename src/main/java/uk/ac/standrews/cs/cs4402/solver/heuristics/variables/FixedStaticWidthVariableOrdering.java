package uk.ac.standrews.cs.cs4402.solver.heuristics.variables;

import uk.ac.standrews.cs.cs4402.solver.graphDataModel.BinaryCSPGraph;
import uk.ac.standrews.cs.cs4402.solver.graphDataModel.VarNode;

import java.util.ArrayList;
import java.util.List;
//TODO: graph width. howto in non-np time?
public class FixedStaticWidthVariableOrdering extends VariableOrderingHeuristic {
    protected List<VarNode> varOrderList;
    public FixedStaticWidthVariableOrdering(BinaryCSPGraph bcsp){
        long start = System.nanoTime();
        setup_time_us =(System.nanoTime()-start)/1000;
    }
    protected List<VarNode> calcWidth(BinaryCSPGraph bcsp){
        List<VarNode> ordered = new ArrayList<>(bcsp.getVarCnt());
        return ordered;
    }
    @Override
    public VarNode getNextVal(BinaryCSPGraph bcsp) {
        long start = System.nanoTime();
        VarNode ret = varOrderList.stream().filter(varNode -> varNode.getDomain().size()>1).findFirst().get();
        compute_time_us +=(System.nanoTime()-start)/1000;
        return ret;
    }
}
