package uk.ac.standrews.cs.cs4402.solver.graphDataModel;

import edu.uci.ics.jung.algorithms.layout.*;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import org.apache.commons.lang3.NotImplementedException;
import org.jgrapht.alg.util.Pair;
import uk.ac.standrews.cs.cs4402.solver.dataModel.BinaryCSP;
import uk.ac.standrews.cs.cs4402.solver.dataModel.BinaryTuple;
import uk.ac.standrews.cs.cs4402.solver.heuristics.values.ValueOrderingHeuristic;
import uk.ac.standrews.cs.cs4402.solver.heuristics.variables.VariableOrderingHeuristic;
import uk.standrews.cs.cs4402.dsl.dSL.*;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class BinaryCSPGraph {
    private final static boolean debug = false;
    private Graph<VarNode, ConstraintEdge> graph;
    private Map<Integer, VarNode> variables;
    private Stack<Map<VarNode, Set<Integer>>> pruneSteps = new Stack<>();
    private Map<VarNode, Set<Integer>> currentPruneOp = new HashMap<>();
    private long arc_revision_time_us =0;
    public long getArc_revision_time_us() {
        return arc_revision_time_us;
    }
    public BinaryCSPGraph(){
        graph = new DirectedSparseGraph<>();
        variables = new HashMap<>();
    }
    public static BinaryCSPGraph buildGraph(BinaryCSP bcsp){
        BinaryCSPGraph bcspg = new BinaryCSPGraph();
        List<VarNode> vars = bcsp.getVarNodes();
        vars.stream().forEach(varNode -> bcspg.variables.put(varNode.getId(), varNode));
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
    protected static VarNode xtextToInternal(uk.standrews.cs.cs4402.dsl.dSL.VarNode vn){
        if(vn instanceof RangeVariable){
            RangeVariable var = (RangeVariable)vn;
            return new VarNode(var.getRange().getLower(), var.getRange().getUpper(), var.getVarID());
        }
        SetVariable var = (SetVariable) vn;
        Set<Integer> domain = new HashSet<>();
        for(int val : var.getSet().getValues()){
            domain.add(val);
        }
        return new VarNode(domain, var.getVarID());
    }
    protected static ConstraintEdge xtextToInternal(BinaryConstraint bc){
        if(bc instanceof BinaryTupleConstraint) {
            ConstraintEdge ce = new ConstraintEdge(((BinaryTupleConstraint) bc).getTuples().stream()
                    .map(bct -> new Pair<Integer, Integer>(bct.getLeft(), bct.getRight()))
                    .collect(Collectors.toList()));
            return ce;
        }else if(bc instanceof BinaryIntrinsicConstraint){
            ConstraintEdge ce = new ConstraintEdgeIntrinsic(((BinaryIntrinsicConstraint) bc).getIntrinsic().getSymbol());
            return ce;
        }
        else {
            throw new NotImplementedException("unsupported constraint type");
        }
    }
    protected static ConstraintEdge xtextToInternal_invertedEdge(BinaryConstraint bc){
        if(bc instanceof BinaryTupleConstraint) {
            ConstraintEdge ce = new ConstraintEdge(((BinaryTupleConstraint) bc).getTuples().stream()
                    .map(bct -> new Pair<Integer, Integer>(bct.getRight(), bct.getLeft()))
                    .collect(Collectors.toList()));
            return ce;
        }else if(bc instanceof BinaryIntrinsicConstraint){
            ConstraintEdge ce = new ConstraintEdgeIntrinsic(((BinaryIntrinsicConstraint) bc).getIntrinsic().getSymbol(), true);
            return ce;
        }
        else {
            throw new NotImplementedException("unsupported constraint type");
        }
    }
    public static BinaryCSPGraph buildGraph(CSP bcsp){
        BinaryCSPGraph bcspg = new BinaryCSPGraph();
        List<VarNode> vars = bcsp.getVariables().stream().map(varNode -> xtextToInternal(varNode)).collect(Collectors.toList());
        vars.stream().forEach(varNode -> bcspg.variables.put(varNode.getId(), varNode));
        for(VarNode vn : vars){
            bcspg.graph.addVertex(vn);
        }
        for(BinaryConstraint bc : bcsp.getConstraints()){
            VarNode vn1 = bcspg.getNode(bc.getVn1().getVarID());
            VarNode vn2 = bcspg.getNode(bc.getVn2().getVarID());
            ConstraintEdge ce = xtextToInternal(bc);
            bcspg.graph.addEdge(ce, vn1, vn2);
            ce = xtextToInternal_invertedEdge(bc);
            bcspg.graph.addEdge(ce, vn2, vn1);
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
    public void reviseArcs_AC25(){ //forall nodes
        //TODO: make more efficient
        for(int n = 0; n < graph.getVertexCount(); n++){
            reviseArcs_AC25(n);
        }
    }
    public void reviseArcs_AC3(){ //forall nodes
        for(int n = 0; n < graph.getVertexCount(); n++){
            reviseArcs_AC3(n);
        }
    }
    //implements AC2.5
    //check arcs to all nodes from changed node forall changed nodes
    //difference to AC3: it does check all outgoing arcs for a given node,
    //including the arc through which changes propagated to the node
    public void reviseArcs_AC25(int varIndex){ //for specified node and propagate changes
        long start = System.nanoTime();
        Set<VarNode> changedVars = new HashSet<>();
        changedVars.add(getNode(varIndex));
        boolean nodeChange = true;
        if(debug)
            System.out.println("reviseArcs_AC25 called for Node index " + Integer.toString(varIndex));
        while(nodeChange) {
            nodeChange = false;
            //pick node in set
            for (VarNode vn : changedVars.toArray(new VarNode[0])) {//workaround loop, seems not to care that changedVars changed during iteration
                changedVars.remove(vn);
                //foreach edge
                if(debug)
                    System.out.println("\tRevising arcs for Node " + vn.getName());
                Collection<ConstraintEdge> edges = graph.getOutEdges(vn);
                for (ConstraintEdge ce : edges) {
                    if(debug)
                        System.out.println("\t\tChecking edge to " + graph.getOpposite(vn, ce).getName());
                    //identify impossible values at targets of outgoing edge
                    Set<Integer> rmValues = getUnreachableDomainSubset(vn, graph.getOpposite(vn, ce), ce);
                    //remove if found
                    if (rmValues.size() > 0) {
                        if(debug) {
                            System.out.print("\t\t\tRemoving variables from target domain: ");
                            System.out.println(rmValues);
                        }
                        VarNode tgt = graph.getOpposite(vn, ce);
                        for (Integer i : rmValues) {
                            pruneFromVariableDomain(tgt, i);
                        }
                        //add target to set of changed nodes
                        boolean addStatus = changedVars.add(tgt);
                        nodeChange = nodeChange || addStatus;
                        if(addStatus){
                            if(debug)
                                System.out.println("\t\t\tNode "+tgt.getName()+ "'s domain was changed, adding it to list of nodes to perform arc revision from");
                        }
                    }
                }
            }
        }
        arc_revision_time_us +=(System.nanoTime()-start)/1000;
        //assert variables.parallelStream().allMatch(varNode -> varNode.getDomain().size()>=1);
    }
    //implements AC3
    public void reviseArcs_AC3(int varIndex){ //for specified node and propagate changes
        long start = System.nanoTime();
        Set<VarNode> changedVars = new HashSet<>();
        changedVars.add(getNode(varIndex));
        Set<VarNode> assignedVars = new HashSet<>();
        variables.values().stream().forEach(varNode -> {if(varNode.getDomain().size()==1){assignedVars.add(varNode);}});
        boolean nodeChange = true;
        if(debug)
            System.out.println("reviseArcs_AC3 called for Node index " + Integer.toString(varIndex));
        while(nodeChange) {
            nodeChange = false;
            //pick node in set
            for (VarNode vn : changedVars.toArray(new VarNode[0])) {//workaround loop, seems not to care that changedVars changed during iteration
                changedVars.remove(vn);
                //foreach edge
                if(debug)
                    System.out.println("\tRevising arcs for Node " + vn.getName());
                Collection<ConstraintEdge> edges = graph.getOutEdges(vn);
                for (ConstraintEdge ce : edges) {
                    if(debug)
                        System.out.println("\t\tChecking edge to " + graph.getOpposite(vn, ce).getName());
                    if(assignedVars.contains(graph.getOpposite(vn, ce))){
                        if(debug)
                            System.out.println("\t\t\tSkipping edge to " + graph.getOpposite(vn, ce).getName() + ", varNode already assigned");
                        continue;
                    }
                    //identify impossible values at targets of outgoing edge
                    Set<Integer> rmValues = getUnreachableDomainSubset(vn, graph.getOpposite(vn, ce), ce);
                    //remove if found
                    if (rmValues.size() > 0) {
                        if(debug) {
                            System.out.print("\t\t\tRemoving variables from target domain: ");
                            System.out.println(rmValues);
                        }
                        VarNode tgt = graph.getOpposite(vn, ce);
                        for (Integer i : rmValues) {
                            pruneFromVariableDomain(tgt, i);
                        }
                        //add target to set of changed nodes
                        boolean addStatus = changedVars.add(tgt);
                        nodeChange = nodeChange || addStatus;
                        if(addStatus){
                            if(debug)
                                System.out.println("\t\t\tNode "+tgt.getName()+ "'s domain was changed, adding it to list of nodes to perform arc revision from");
                        }
                    }
                }
            }
        }
        arc_revision_time_us +=(System.nanoTime()-start)/1000;
        //assert variables.parallelStream().allMatch(varNode -> varNode.getDomain().size()>=1);
    }
    //implements forward checking, single propagation solve
    public void reviseArcs_FC(int varIndex){ //for specified node and propagate changes
        long start = System.nanoTime();
        VarNode vn = getNode(varIndex);
        if(debug)
            System.out.println("reviseArcs_FC called for Node index " + Integer.toString(varIndex));
        if(debug)
            System.out.println("\tRevising arcs for Node " + vn.getName());
        Collection<ConstraintEdge> edges = graph.getOutEdges(vn);
        for (ConstraintEdge ce : edges) {
            if(debug)
                System.out.println("\t\tChecking edge to " + graph.getOpposite(vn, ce).getName());
            //identify impossible values at targets of outgoing edge
            Set<Integer> rmValues = getUnreachableDomainSubset(vn, graph.getOpposite(vn, ce), ce);
            //remove if found
            if (rmValues.size() > 0) {
                if(debug) {
                    System.out.print("\t\t\tRemoving variables from target domain: ");
                    System.out.println(rmValues);
                }
                VarNode tgt = graph.getOpposite(vn, ce);
                for (Integer i : rmValues) {
                    pruneFromVariableDomain(tgt, i);
                }
                if(graph.getOpposite(vn, ce).getDomain().size()==1){//sz 1 is basically an assignment. Let's check it NOW
                    if(debug)
                        System.out.println("\t\t\tTarget has only one remaining domain element, that's equal to an assignment. Checking this assignment now");
                    reviseArcs_FC(graph.getOpposite(vn, ce).getId());
                }
            }
        }
        arc_revision_time_us +=(System.nanoTime()-start)/1000;
        //assert variables.parallelStream().allMatch(varNode -> varNode.getDomain().size()>=1);
    }
    protected Set<Integer> getUnreachableDomainSubset(VarNode src, VarNode target, ConstraintEdge ce){
        Set<Integer> srcDomain = src.getDomain();
        Set<Integer> targetDomain = target.getDomain();

        Set<Integer> allowedDomain = ce.reachableTargetDomain(src, target);

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
    public Collection<VarNode> getNodes(){
        return variables.values();
    }
    public String stateToString(){
        String ret = "<html>";
        for(VarNode vn : variables.values()){
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
        for(VarNode vn : variables.values()){
            asnm.put(vn.getId(), vn.getDomain().iterator().next());
        }
        return asnm;
    }

    //fixing java deficiencies...
    //I need the friend keyword..
    //https://stackoverflow.com/questions/182278/is-there-a-way-to-simulate-the-c-friend-concept-in-java
    //AKA "signature security"
    public Graph<VarNode, ConstraintEdge> getGraph(ValueOrderingHeuristic.Key k){
        k.hashCode();
        return graph;
    }
    public Graph<VarNode, ConstraintEdge> getGraph(VariableOrderingHeuristic.Key k){
        k.hashCode();
        return graph;
    }
    public Set<Integer> getEdgeTargetDomain(ValueOrderingHeuristic.Key k, VarNode src, VarNode tgt, ConstraintEdge e){
        k.hashCode();
        return e.reachableTargetDomain(src, tgt);
    }
}
