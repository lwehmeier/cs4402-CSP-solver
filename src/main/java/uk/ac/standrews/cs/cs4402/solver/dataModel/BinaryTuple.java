package uk.ac.standrews.cs.cs4402.solver.dataModel;

import org.jgrapht.alg.util.Pair;

/**
 * Assumes tuple tuples are integers
 */
public final class BinaryTuple {
  private int val1, val2 ;
  
  public BinaryTuple(int v1, int v2) {
    val1 = v1 ;
    val2 = v2 ;
  }
  
  public String toString() {
    return "<"+val1+", "+val2+">" ;
  }
  
  public boolean matches(int v1, int v2) {
    return (val1 == v1) && (val2 == v2) ;
  }

  public Pair<Integer, Integer> toPair(){
    return new Pair<Integer, Integer>(val1, val2);
  }
  public Pair<Integer, Integer> toReversePair(){
    return new Pair<Integer, Integer>(val2, val1);
  }
}
