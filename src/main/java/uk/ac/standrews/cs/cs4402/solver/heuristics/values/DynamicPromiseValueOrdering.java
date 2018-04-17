package uk.ac.standrews.cs.cs4402.solver.heuristics.values;

import edu.uci.ics.jung.graph.Graph;
import org.jgrapht.alg.util.Pair;
import uk.ac.standrews.cs.cs4402.solver.graphDataModel.BinaryCSPGraph;
import uk.ac.standrews.cs.cs4402.solver.graphDataModel.ConstraintEdge;
import uk.ac.standrews.cs.cs4402.solver.graphDataModel.VarNode;

import java.util.*;
import java.util.stream.Collectors;

public class DynamicPromiseValueOrdering extends ValueOrderingHeuristic {
    BinaryCSPGraph bcsp;
    public DynamicPromiseValueOrdering(BinaryCSPGraph bcsp){
        this.bcsp = bcsp;
    }
    @Override
    public int getNextVal(VarNode vn) {
        long start = System.nanoTime();
        Set<Integer> domain = vn.getDomain();
        List<VarNode> future = bcsp.getGraph(key).getNeighbors(vn).stream()
                .filter(varNode -> varNode.getDomain().size()>1)
                .collect(Collectors.toList());
        int next = domain.stream().
                sorted((e1, e2) -> getLEFTSize(vn, e2, future).compareTo(getLEFTSize(vn, e1, future))). //maximize score
                findFirst().get();
        compute_time_us +=(System.nanoTime()-start)/1000;
        return next;
    }
    protected Integer getLEFTSize(VarNode src, Integer assignment, Collection<VarNode> future){
        int score = 0;
        for(VarNode vn : future){
            score += getReachableDomainSubsetSize(src, assignment, vn, bcsp.getGraph(key).findEdge(src, vn));
        }
        return score;
    }
    protected Integer getReachableDomainSubsetSize(VarNode src, Integer assignment, VarNode target, ConstraintEdge ce){
        Set<Integer> srcDomain = new HashSet<>();
        srcDomain.add(assignment);
        Set<Integer> targetDomain = target.getDomain();
        Set<Integer> allowedDomain = new HashSet<>();
        for(Pair<Integer, Integer> tuple : bcsp.getEdgeTuples(key, ce)){
            if(srcDomain.contains(tuple.getFirst())){
                allowedDomain.add(tuple.getSecond());
            }
        }
        return (int) targetDomain.parallelStream().filter(i -> allowedDomain.contains(i)).count();
    }
}
