package uk.ac.standrews.cs.cs4402.solver.heuristics.values;

import edu.uci.ics.jung.graph.Graph;
import org.jgrapht.alg.util.Pair;
import uk.ac.standrews.cs.cs4402.solver.graphDataModel.BinaryCSPGraph;
import uk.ac.standrews.cs.cs4402.solver.graphDataModel.ConstraintEdge;
import uk.ac.standrews.cs.cs4402.solver.graphDataModel.VarNode;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Min Conflicts: only look at directly affected future variables, i.e. neighbours in CSP graph
 */
public class DynamicMinConflictsValueOrdering extends ValueOrderingHeuristic{
    BinaryCSPGraph bcsp;
    public DynamicMinConflictsValueOrdering(BinaryCSPGraph bcsp){
        this.bcsp = bcsp;
    }
    @Override
    public int getNextVal(VarNode vn) {
        Set<Integer> domain = vn.getDomain();
        List<VarNode> future = bcsp.getGraph(key).getNeighbors(vn).stream()
                .filter(varNode -> varNode.getDomain().size()>1)
                .collect(Collectors.toList());
        int next = domain.stream().
                sorted((e1, e2) -> getConflictScore(vn, e1, future).compareTo(getConflictScore(vn, e2, future))).
                findFirst().get();
        return next;
    }
    protected Integer getConflictScore(VarNode vn, Integer value, List<VarNode> future){
        int score = 0;
        Graph<VarNode, ConstraintEdge> graph = bcsp.getGraph(key);
        Set<Integer> newDomain = new HashSet<>();
        newDomain.add(value);
        for(VarNode vn1 : graph.getNeighbors(vn)){
            if(future.contains(vn1)){
                score+=getUnreachableDomainSubset(vn, newDomain,vn1, graph.findEdge(vn, vn1)).size();
            }
        }
        return score;
    }
    protected Set<Integer> getUnreachableDomainSubset(VarNode src, Set<Integer> srcDomain, VarNode target, ConstraintEdge ce){
        Set<Integer> targetDomain = target.getDomain();
        Set<Integer> allowedDomain = new HashSet<>();
        for(Pair<Integer, Integer> tuple : bcsp.getEdgeTuples(key, ce)){
            if(srcDomain.contains(tuple.getFirst())){
                allowedDomain.add(tuple.getSecond());
            }
        }
        return targetDomain.parallelStream().filter(i -> !allowedDomain.contains(i)).collect(Collectors.toSet());
    }
}
