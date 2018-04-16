package uk.ac.standrews.cs.cs4402.solver.heuristics.variables;

import uk.ac.standrews.cs.cs4402.solver.graphDataModel.BinaryCSPGraph;
import uk.ac.standrews.cs.cs4402.solver.graphDataModel.VarNode;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class FixedStaticDegreeVariableOrdering extends VariableOrderingHeuristic{
    @Override
    public VarNode getNextVal(BinaryCSPGraph bcsp) {
        Collection<VarNode> vars = bcsp.getNodes();
        Optional<VarNode> res = vars.stream()
                .filter(varNode -> varNode.getDomain().size()>1)
                .sorted((varNode, t1) -> new Integer(bcsp.getGraph(key).outDegree(varNode)).compareTo(bcsp.getGraph(key).outDegree(varNode))*(-1)) //invert ordering, we want highest degree first
                .findFirst();
        return res.get();
    }
}
