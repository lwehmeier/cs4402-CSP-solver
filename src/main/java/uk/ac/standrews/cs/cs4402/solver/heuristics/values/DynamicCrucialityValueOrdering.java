package uk.ac.standrews.cs.cs4402.solver.heuristics.values;

import edu.uci.ics.jung.graph.Graph;
import org.jgrapht.alg.util.Pair;
import uk.ac.standrews.cs.cs4402.solver.graphDataModel.BinaryCSPGraph;
import uk.ac.standrews.cs.cs4402.solver.graphDataModel.ConstraintEdge;
import uk.ac.standrews.cs.cs4402.solver.graphDataModel.VarNode;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class DynamicCrucialityValueOrdering extends ValueOrderingHeuristic{
    BinaryCSPGraph bcsp;
    public DynamicCrucialityValueOrdering(BinaryCSPGraph bcsp){
        this.bcsp = bcsp;
    }
    @Override
    public int getNextVal(VarNode vn) {
        long start = System.nanoTime();
        Set<Integer> domain = vn.getDomain();
        int next = domain.stream().
                sorted((e1, e2) -> calcCruciality(vn, e1).compareTo(calcCruciality(vn, e2))).
                findFirst().get();
        compute_time_us +=(System.nanoTime()-start)/1000;
        return next;
    }
    protected Double calcCruciality(VarNode vn, Integer assignment){
        Collection<VarNode> future = bcsp.getGraph(key).getNeighbors(vn).stream()
                .filter(varNode -> varNode.getDomain().size()>1).collect(Collectors.toList());
        int szLost = getConflictScore(vn, assignment, future);
        int szPreAssignment = calcDomainSize(bcsp.getGraph(key).getNeighbors(vn));
        return ((double)szLost)/((double)szPreAssignment);
    }
    protected Integer calcDomainSize(Collection<VarNode> nodes){
        int score=0;
        for(VarNode vn : nodes){
            score+=vn.getDomain().size();
        }
        return score;
    }
    protected Integer getConflictScore(VarNode vn, Integer value, Collection<VarNode> future){
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
