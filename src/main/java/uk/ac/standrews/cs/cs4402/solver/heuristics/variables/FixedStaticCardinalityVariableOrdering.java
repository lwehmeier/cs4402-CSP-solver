package uk.ac.standrews.cs.cs4402.solver.heuristics.variables;

import edu.uci.ics.jung.graph.Graph;
import uk.ac.standrews.cs.cs4402.solver.graphDataModel.BinaryCSPGraph;
import uk.ac.standrews.cs.cs4402.solver.graphDataModel.ConstraintEdge;
import uk.ac.standrews.cs.cs4402.solver.graphDataModel.VarNode;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class FixedStaticCardinalityVariableOrdering extends VariableOrderingHeuristic{
    protected List<VarNode> varOrderList;
    public FixedStaticCardinalityVariableOrdering(BinaryCSPGraph bcsp){
        long start = System.nanoTime();
        varOrderList = new ArrayList<>(bcsp.getVarCnt());
        VarNode current = bcsp.getNode(0);
        varOrderList.add(current);
        Graph<VarNode, ConstraintEdge> graph = bcsp.getGraph(key);

        while(varOrderList.size()<bcsp.getVarCnt()){
            Stream<VarNode> connectedNodes = graph.getNeighbors(current).stream()
                    .filter(varNode -> !varOrderList.contains(varNode));
            VarNode best = connectedNodes.sorted((vn1, vn2) -> calcCardinality(vn2, bcsp).compareTo(calcCardinality(vn1, bcsp))).findFirst().get();//invert order, we want highest card.
            varOrderList.add(best);
            current = best;
        }
        setup_time_us =(System.nanoTime()-start)/1000;
    }
    protected Integer calcCardinality(VarNode vn, BinaryCSPGraph bcsp){
        Graph<VarNode, ConstraintEdge> graph = bcsp.getGraph(key);
        Integer score = 0;
        for(VarNode vn1 : graph.getNeighbors(vn)){
            if(varOrderList.contains(vn1)){
                score++;
            }
        }
        return score;
    }
    @Override
    public VarNode getNextVal(BinaryCSPGraph bcsp) {
        long start = System.nanoTime();
        VarNode ret = varOrderList.stream().filter(varNode -> varNode.getDomain().size()>1).findFirst().get();
        compute_time_us +=(System.nanoTime()-start)/1000;
        return ret;
    }
}
