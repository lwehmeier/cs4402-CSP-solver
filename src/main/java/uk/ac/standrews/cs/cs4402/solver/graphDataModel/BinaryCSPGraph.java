package uk.ac.standrews.cs.cs4402.solver.graphDataModel;

import edu.uci.ics.jung.algorithms.layout.*;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.visualization.BasicVisualizationServer;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse;
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
    private Stack<Map<VarNode, Set<Integer>>> pruneSteps = new Stack<>();
    private Map<VarNode, Set<Integer>> currentPruneOp = new HashMap<>();

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
        VisualizationViewer<VarNode, ConstraintEdge> vv =
                new VisualizationViewer<VarNode, ConstraintEdge>(layout);
        vv.setPreferredSize(new Dimension(950,950)); //Sets the viewing area size
        DefaultModalGraphMouse gm = new DefaultModalGraphMouse();
        gm.setMode(ModalGraphMouse.Mode.TRANSFORMING);
        vv.setGraphMouse(gm);
        vv.getRenderingHints().remove(RenderingHints.KEY_ANTIALIASING);

        vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller());
        vv.getRenderContext().setEdgeLabelTransformer(new ToStringLabeller());

        JFrame frame = new JFrame("Current Solver State");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.getContentPane().add(vv);
        frame.pack();
        frame.setVisible(true);
    }

    public void reviseArcs(){ //forall nodes
        //TODO: make more efficient
        for(int n = 0; n < graph.getVertexCount(); n++){
            reviseArcs(n);
        }
    }
    public void reviseArcs(int varIndex){ //for specified node and propagate changes
        Set<VarNode> changedVars = new HashSet<>();
        changedVars.add(getNode(varIndex));
        boolean nodeChange = true;
        System.out.println("reviseArcs called for Node index " + Integer.toString(varIndex));
        while(nodeChange) {
            nodeChange = false;
            //pick node in set
            for (VarNode vn : changedVars.toArray(new VarNode[0])) {//workaround loop, seems not to care that changedVars changed during iteration
                changedVars.remove(vn);
                //foreach edge
                System.out.println("\tRevising arcs for Node " + vn.getName());
                Collection<ConstraintEdge> edges = graph.getOutEdges(vn);
                for (ConstraintEdge ce : edges) {
                    System.out.println("\t\tChecking edge to " + graph.getOpposite(vn, ce).getName());
                    //identify impossible values at targets of outgoing edge
                    Set<Integer> rmValues = getUnreachableDomainSubset(vn, graph.getOpposite(vn, ce), ce);
                    //remove if found
                    if (rmValues.size() > 0) {
                        System.out.print("\t\t\tRemoving variables from target domain: ");
                        System.out.println(rmValues);
                        VarNode tgt = graph.getOpposite(vn, ce);
                        for (Integer i : rmValues) {
                            pruneFromVariableDomain(tgt, i);
                        }
                        //add target to set of changed nodes
                        boolean addStatus = changedVars.add(tgt);
                        nodeChange = nodeChange || addStatus;
                        if(addStatus){
                            System.out.println("\t\t\tNode "+tgt.getName()+ "'s domain was changed, adding it to list of nodes to perform arc revision from");
                        }
                    }
                }
            }
        }
        //assert variables.parallelStream().allMatch(varNode -> varNode.getDomain().size()>=1);
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
        pruneFromVariableDomain(getNode(varIndex), value);
    }
    protected void pruneFromVariableDomain(VarNode var, int value){
        if(var.prune(value)){
            Set<Integer> is = currentPruneOp.get(var);
            if(is == null){
                is = new HashSet<>();
                currentPruneOp.put(var, is);
            }
            is.add(value);
            if(var.getDomain().size()==0){
                throw new NoSolutionException();
            }
        }

    }
    public void push(){
        //System.out.println("BCSPGraph: push()");
        pruneSteps.push(currentPruneOp);
        currentPruneOp = new HashMap<>();
    }
    public boolean pop(){
        //System.out.println("BCSPGraph: pop()");
        if(pruneSteps.size()==0){
            return false;
        }
        undoCurrentPrune();
        currentPruneOp = pruneSteps.pop();
        /*for(VarNode vn : currentPruneOp.keySet()){
            for(Integer val : currentPruneOp.get(vn)){
                vn.extendDomain(val);
            }
        }*/
        return true;
    }
    public boolean undoCurrentPrune(){
        //System.out.println("BCSPGraph: rollback()");
        for(VarNode vn : currentPruneOp.keySet()){
            for(Integer val : currentPruneOp.get(vn)){
                vn.extendDomain(val);
            }
        }
        currentPruneOp = new HashMap<>();
        return true;
    }
    public void emptyStack(){
        pruneSteps = new Stack<>();
    }
    public VarNode getNode(int varIndex){
        return variables.get(varIndex);
    }
    public Set<Integer> getVarDomain(int varIndex){
        return getNode(varIndex).getDomain();
    }
    public int getVarCnt(){
        return graph.getVertexCount();
    }
    public List<VarNode> getNodes(){
        return variables;
    }
    public String stateToString(){
        String ret = "<html>";
        for(VarNode vn : variables){
            ret+=vn.toString()+"<br>";
        }
        return ret;
    }
    protected boolean solved(){
        for(int var = 0; var < getVarCnt(); var++){
            if(getVarDomain(var).size()!=1){
                return false;
            }
        }
        return true;
    }
    public Map<Integer, Integer> getAssignments(){
        if(!solved()) {
            return null;
        }
        Map<Integer, Integer> asnm = new HashMap<>();
        for(VarNode vn : variables){
            asnm.put(vn.getId(), vn.getDomain().iterator().next());
        }
        return asnm;
    }
}
