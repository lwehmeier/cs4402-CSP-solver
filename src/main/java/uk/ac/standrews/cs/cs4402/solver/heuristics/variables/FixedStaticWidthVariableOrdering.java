package uk.ac.standrews.cs.cs4402.solver.heuristics.variables;

import uk.ac.standrews.cs.cs4402.solver.graphDataModel.BinaryCSPGraph;
import uk.ac.standrews.cs.cs4402.solver.graphDataModel.VarNode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

//TODO: graph width. howto in non-np time?
public class FixedStaticWidthVariableOrdering extends VariableOrderingHeuristic {
    protected List<VarNode> varOrderList;
    public FixedStaticWidthVariableOrdering(BinaryCSPGraph bcsp){
        long start = System.nanoTime();
        VarNode tmp[]=new VarNode[bcsp.getVarCnt()];
        int i=bcsp.getVarCnt();
        for(VarNode vn : calcWidth(bcsp)){
            i--;
            tmp[i]=vn;
        }
        varOrderList= Arrays.asList(tmp);
        setup_time_us =(System.nanoTime()-start)/1000;
    }
    protected List<VarNode> calcWidth(BinaryCSPGraph bcsp){
        List<VarNode> ordered = new ArrayList<>(bcsp.getVarCnt());
        Collection<VarNode> nodes = bcsp.getNodes().stream().collect(Collectors.toList());//clone
        int k=0;
        while(!nodes.isEmpty()){
            k++;
            for(VarNode vn : nodes.toArray(new VarNode[0])) {//work around dynamic iterator change
                int nodelinkage = 0;
                for(VarNode vn1 : bcsp.getGraph(key).getNeighbors(vn)){
                    if(nodes.contains(vn1)){
                        nodelinkage++;
                    }
                }
                if(nodelinkage<=k){
                    nodes.remove(vn);
                    ordered.add(vn);
                }
            }
        }
        assert ordered.size() == bcsp.getVarCnt();
        return ordered;
    }
    @Override
    public VarNode getNextVal(BinaryCSPGraph bcsp) {
        long start = System.nanoTime();
        VarNode ret = varOrderList.stream().filter(varNode -> varNode.getDomain().size()>1).findFirst().get();
        compute_time_us +=(System.nanoTime()-start)/1000;
        return ret;
    }
}
