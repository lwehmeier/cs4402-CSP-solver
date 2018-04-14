package uk.ac.standrews.cs.cs4402.solver.heuristics.variables;

import uk.ac.standrews.cs.cs4402.solver.graphDataModel.BinaryCSPGraph;
import uk.ac.standrews.cs.cs4402.solver.graphDataModel.VarNode;

import java.util.List;
import java.util.Optional;

public class DynamicDomainVariableOrdering extends VariableOrderingHeuristic {
    @Override
    public VarNode getNextVal(BinaryCSPGraph bcsp) {
        List<VarNode> vars = bcsp.getNodes();
        Optional<VarNode> res = vars.stream()
                .filter(varNode -> varNode.getDomain().size()>1)
                .sorted((varNode, t1) -> new Integer(varNode.getDomain().size()).compareTo(t1.getDomain().size()))//order by domain size ASC
                .findFirst();
        return res.get();
    }
}
