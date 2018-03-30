package uk.ac.standrews.cs.cs4402.solver.graphDataModel;

import edu.uci.ics.jung.algorithms.layout.*;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.visualization.BasicVisualizationServer;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import org.jgrapht.alg.util.Pair;
import uk.ac.standrews.cs.cs4402.solver.dataModel.BinaryCSP;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class BinaryCSPGraph {
    private Graph<VarNode, ConstraintEdge> graph;
    private List<VarNode> variables;
    public BinaryCSPGraph(){
        graph = new DirectedSparseGraph<>();
    }
    public static BinaryCSPGraph buildGraph(BinaryCSP bcsp){
        BinaryCSPGraph bcspg = new BinaryCSPGraph();
        List<VarNode> vars = bcsp.getVarNodes();
        bcspg.variables = vars;
        for(VarNode vn : vars){
            bcspg.graph.addVertex(vn);
        }
        for(int i = 0; i < vars.size(); i++){
            List<Pair<Integer, ConstraintEdge>> edges = bcsp.getOutgoingEdgesForVarNode(i);
            VarNode vn1 = vars.get(i);
            for(Pair<Integer, ConstraintEdge> e : edges){
                VarNode vn2 = vars.get(e.getFirst());
                bcspg.graph.addEdge(e.getSecond(), vn1, vn2);
            }
        }
        return bcspg;
    }

    public void draw(){
        Layout<VarNode, ConstraintEdge> layout = new CircleLayout<>(graph);
        layout.setSize(new Dimension(900,900)); // sets the initial size of the space
        // The BasicVisualizationServer<V,E> is parameterized by the edge types
        BasicVisualizationServer<VarNode, ConstraintEdge> vv =
                new BasicVisualizationServer<VarNode, ConstraintEdge>(layout);
        vv.setPreferredSize(new Dimension(950,950)); //Sets the viewing area size

        vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller());
        vv.getRenderContext().setEdgeLabelTransformer(new ToStringLabeller());

        JFrame frame = new JFrame("Current Solver State");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.getContentPane().add(vv);
        frame.pack();
        frame.setVisible(true);
    }

    protected void reviseArcs(){ //forall nodes
        Set<VarNode> changedVars = new HashSet<>(variables);
    }
    public void reviseArcs(int varIndex){ //for specified node and propagate changes
        Set<VarNode> changedVars = new HashSet<>();
        changedVars.add(getNode(varIndex));
        boolean nodeChange = true;
        while(nodeChange) {
            nodeChange = false;
            //pick node in set
            for (VarNode vn : changedVars.toArray(new VarNode[0])) {//workaround loop, seems not to care that changedVars changed during iteration
                //foreach edge
                Collection<ConstraintEdge> edges = graph.getOutEdges(vn);
                for (ConstraintEdge ce : edges) {
                    //identify impossible values at targets of outgoing edge
                    Set<Integer> rmValues = getUnreachableDomainSubset(vn, graph.getOpposite(vn, ce), ce);
                    //remove if found
                    if (rmValues.size() > 0) {
                        VarNode tgt = graph.getOpposite(vn, ce);
                        for (Integer i : rmValues) {
                            tgt.prune(i);
                        }
                        //add target to set of changed nodes
                        nodeChange = nodeChange || changedVars.add(tgt);
                    }
                }
            }
        }
    }
    protected Set<Integer> getUnreachableDomainSubset(VarNode src, VarNode target, ConstraintEdge ce){
        Set<Integer> srcDomain = src.getDomain();
        Set<Integer> targetDomain = target.getDomain();

        Set<Integer> allowedDomain = new HashSet<>();
        for(Pair<Integer, Integer> tuple : ce.tuples){
            if(srcDomain.contains(tuple.getFirst())){
                allowedDomain.add(tuple.getSecond());
            }
        }

        return targetDomain.parallelStream().filter(i -> !allowedDomain.contains(i)).collect(Collectors.toSet());
    }

    public void pruneFromVariableDomain(int varIndex, int value){
        getNode(varIndex).prune(value);
    }
    public VarNode getNode(int varIndex){
        return variables.get(varIndex);
    }
}
