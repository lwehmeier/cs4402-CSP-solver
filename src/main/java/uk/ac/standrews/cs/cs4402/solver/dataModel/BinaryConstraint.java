package uk.ac.standrews.cs.cs4402.solver.dataModel;

import org.jgrapht.alg.util.Pair;

import java.util.* ;
import java.util.stream.Collectors;

public final class BinaryConstraint {
  private int firstVar, secondVar ;
  private List<Pair<Integer, Integer>> tuples ;
  
  public BinaryConstraint(int fv, int sv, ArrayList<BinaryTuple> t) {
    firstVar = fv ;
    secondVar = sv ;
    tuples = t.parallelStream().map(binaryTuple -> binaryTuple.toPair()).collect(Collectors.toList()); ;
  }
  public java.util.List<Pair<Integer, Integer>> getTuples() {
    return tuples;
  }
  public int getFirstVar(){
    return firstVar;
  }
  public int getSecondVar(){
    return secondVar;
  }

  public String toString() {
    StringBuffer result = new StringBuffer() ;
    result.append("c("+firstVar+", "+secondVar+")\n") ;
    for (Pair<?,?> bt : tuples)
      result.append(bt+"\n") ;
    return result.toString() ;
  }
  
  // SUGGESTION: You will want to add methods here to reason about the constraint
}
