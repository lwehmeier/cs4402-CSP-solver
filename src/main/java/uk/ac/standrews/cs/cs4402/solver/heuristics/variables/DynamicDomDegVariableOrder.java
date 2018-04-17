package uk.ac.standrews.cs.cs4402.solver.heuristics.variables;

import edu.uci.ics.jung.graph.Graph;
import uk.ac.standrews.cs.cs4402.solver.graphDataModel.BinaryCSPGraph;
import uk.ac.standrews.cs.cs4402.solver.graphDataModel.ConstraintEdge;
import uk.ac.standrews.cs.cs4402.solver.graphDataModel.VarNode;

import java.util.List;
import java.util.stream.Collectors;

public class DynamicDomDegVariableOrder extends VariableOrderingHeuristic {
    protected VarNode getNextNode(BinaryCSPGraph bcsp){
        long start = System.nanoTime();
        Graph<VarNode, ConstraintEdge> graph = bcsp.getGraph(key);

        //don't loop, only interested in next node
        //while(varOrderList.size()<bcsp.getVarCnt()){
        List<VarNode> futureVars = bcsp.getNodes().stream().filter(varNode -> varNode.getDomain().size() > 1).collect(Collectors.toList());
        VarNode best = futureVars.stream().sorted((vn1, vn2) -> (new Double(vn1.getDomain().size()/calcDegree(vn1, bcsp, futureVars)).compareTo(vn2.getDomain().size()/calcDegree(vn2, bcsp, futureVars)))).findFirst().get();//lowest quotient first
        //}
        compute_time_us +=(System.nanoTime()-start)/1000;
        return best;
    }
    protected Double calcDegree(VarNode vn, BinaryCSPGraph bcsp, List<VarNode> futureNodes){ //in future subgraph
        Graph<VarNode, ConstraintEdge> graph = bcsp.getGraph(key);
        Integer score = 0;
        for(VarNode vn1 : graph.getNeighbors(vn)){
            if(futureNodes.contains(vn1)) {
                score++;
            }
        }
        return (double) score;
    }
    @Override
    public VarNode getNextVal(BinaryCSPGraph bcsp) {
        return getNextNode(bcsp);
    }
}
