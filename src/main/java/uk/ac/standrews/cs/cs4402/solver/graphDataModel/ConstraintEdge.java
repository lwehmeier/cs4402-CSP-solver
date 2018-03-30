package uk.ac.standrews.cs.cs4402.solver.graphDataModel;

import org.jgrapht.alg.util.Pair;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ConstraintEdge {
    protected Set<Pair<Integer, Integer>> tuples;
    public ConstraintEdge(Set<Pair<Integer, Integer>> tuples){
        this.tuples = tuples;
    }
    public ConstraintEdge(List<Pair<Integer, Integer>> tuples){
        this.tuples = new HashSet<>(tuples);
    }
    public void prune(Pair<Integer, Integer> tuple){
        tuples.removeIf(pair -> !(pair.getFirst()==tuple.getFirst() && pair.getSecond() == tuple.getSecond()));
    }
    @Override
    public String toString(){
        StringBuffer result = new StringBuffer() ;
        for (Pair<?,?> bt : tuples)
            result.append(bt+",") ;
        return result.toString() ;
    }
}
