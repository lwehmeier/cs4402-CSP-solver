package uk.ac.standrews.cs.cs4402.solver.graphDataModel;

import org.jgrapht.alg.util.Pair;

import java.util.*;

public class ConstraintEdge {
    protected Set<Pair<Integer, Integer>> tuples;
    protected Map<Integer, HashSet<Integer>> targetValues;
    public ConstraintEdge(Set<Pair<Integer, Integer>> tuples){
        this.tuples = tuples;
        this.targetValues=new HashMap<>();
        //create map of start value and all reachable values in hashset for faster access
        for(Pair<Integer, Integer> tuple : tuples){
            Integer left = tuple.getFirst();
            if(!targetValues.keySet().contains(left)) {
                targetValues.put(left, new HashSet<>());
                for (Pair<Integer, Integer> tuple2 : tuples) {
                    if (left == tuple2.getFirst()) {
                        targetValues.get(left).add(tuple2.getSecond());
                    }
                }
            }
        }
    }
    protected ConstraintEdge(){}
    public ConstraintEdge(List<Pair<Integer, Integer>> tuples){
        this(new HashSet<>(tuples));
    }
    protected boolean satisfiesConstraint(Integer start, Integer end) {
        if (targetValues.keySet().contains(start)){
            return targetValues.get(start).contains(end);
        }
        return false;
    }
    public Set<Integer> reachableTargetDomain(VarNode start, VarNode target){
        HashSet<Integer> targetDomain = new HashSet<>();
        for(Integer src : start.getDomain()){
            for(Integer tgt : target.getDomain()){
                if(satisfiesConstraint(src, tgt) && !targetDomain.contains(tgt)){
                    targetDomain.add(tgt);
                }
            }
        }
        return targetDomain;
    }
    @Override
    public String toString(){
        StringBuffer result = new StringBuffer() ;
        for (Pair<?,?> bt : tuples)
            result.append(bt+",") ;
        return result.toString() ;
    }
}
