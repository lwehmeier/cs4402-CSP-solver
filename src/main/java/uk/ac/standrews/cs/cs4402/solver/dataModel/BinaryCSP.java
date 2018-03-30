package uk.ac.standrews.cs.cs4402.solver.dataModel;

import org.jgrapht.alg.util.Pair;
import uk.ac.standrews.cs.cs4402.solver.graphDataModel.ConstraintEdge;
import uk.ac.standrews.cs.cs4402.solver.graphDataModel.VarNode;

import java.util.* ;

public final class BinaryCSP {
  private int[][] domainBounds ;
  private ArrayList<BinaryConstraint> constraints ;
  
  public BinaryCSP(int[][] db, ArrayList<BinaryConstraint> c) {
    domainBounds = db ;
    constraints = c ;
  }
  
  public String toString() {
    StringBuffer result = new StringBuffer() ;
    result.append("CSP:\n") ;
    for (int i = 0; i < domainBounds.length; i++)
      result.append("Var "+i+": "+domainBounds[i][0]+" .. "+domainBounds[i][1]+"\n") ;
    for (BinaryConstraint bc : constraints)
      result.append(bc+"\n") ;
    return result.toString() ;
  }
  public List<VarNode> getVarNodes(){
    List nodes = new ArrayList<VarNode>();
    for(int[] domain : domainBounds){
      nodes.add(new VarNode(domain[0], domain[1]));
    }
    return nodes;
  }

  public List<Pair<Integer, ConstraintEdge>> getOutgoingEdgesForVarNode(int varIndex){
    List edges = new ArrayList<Pair<Integer, ConstraintEdge>>();
    for(BinaryConstraint bc : constraints){
      if(bc.getFirstVar() == varIndex ) {
        edges.add(new Pair<>(bc.getSecondVar(),
                new ConstraintEdge(bc.getTuples())));
      }
      if(bc.getSecondVar() == varIndex ) {
        edges.add(new Pair<>(bc.getFirstVar(),
                new ConstraintEdge(bc.getTuples())));
      }
    }
    return edges;
  }
  
  public int getNoVariables() {
    return domainBounds.length ;
  }
  
  public int getLB(int varIndex) {
    return domainBounds[varIndex][0] ;
  }
  
  public int getUB(int varIndex) {
    return domainBounds[varIndex][1] ;
  }
  
  public ArrayList<BinaryConstraint> getConstraints() {
    return constraints ;
  }
}
