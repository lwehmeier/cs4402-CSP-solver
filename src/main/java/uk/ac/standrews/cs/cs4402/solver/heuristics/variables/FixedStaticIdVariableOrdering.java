package uk.ac.standrews.cs.cs4402.solver.heuristics.variables;

import uk.ac.standrews.cs.cs4402.solver.graphDataModel.BinaryCSPGraph;
import uk.ac.standrews.cs.cs4402.solver.graphDataModel.VarNode;

import java.util.List;
import java.util.Optional;

public class FixedStaticIdVariableOrdering extends VariableOrderingHeuristic{
    @Override
    public VarNode getNextVal(BinaryCSPGraph bcsp) {
        List<VarNode> vars = bcsp.getNodes();
        Optional<VarNode> res = vars.stream()
                .filter(varNode -> varNode.getDomain().size()>1)
                .sorted((varNode, t1) -> new Integer(varNode.getId()).compareTo(t1.getId()))
                .findFirst();
        return res.get();
    }
}
