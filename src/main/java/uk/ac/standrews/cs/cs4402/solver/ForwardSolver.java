package uk.ac.standrews.cs.cs4402.solver;

import edu.uci.ics.jung.algorithms.layout.CircleLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.algorithms.layout.TreeLayout;
import edu.uci.ics.jung.graph.DelegateTree;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.visualization.BasicVisualizationServer;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import edu.uci.ics.jung.visualization.renderers.DefaultEdgeLabelRenderer;
import org.apache.commons.lang3.StringUtils;
import uk.ac.standrews.cs.cs4402.solver.graphDataModel.BinaryCSPGraph;
import uk.ac.standrews.cs.cs4402.solver.graphDataModel.ConstraintEdge;
import uk.ac.standrews.cs.cs4402.solver.graphDataModel.NoSolutionException;
import uk.ac.standrews.cs.cs4402.solver.graphDataModel.VarNode;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public class ForwardSolver implements Solver {
    BinaryCSPGraph bcsp;

    private DelegateTree<String, String> searchTree = new DelegateTree<>();
    private String searchTreeActiveNode;
    @Override
    public void setCSP(BinaryCSPGraph csp) {
        bcsp = csp;
        bcsp.reviseArcs(); // reduce variable domains if possible
        bcsp.push();
        searchTree = new DelegateTree<>();
        searchTreeActiveNode="ROOT";
        searchTree.addVertex(searchTreeActiveNode);
    }

    protected boolean solved(){
        for(int var = 0; var < bcsp.getVarCnt(); var++){
            if(bcsp.getVarDomain(var).size()!=1){
                return false;
            }
        }
        return true;
    }
    public void backtrack(){

    }

    protected void createGraphNode(String assignment){
        try {
            String newNode = bcsp.stateToString();
            //System.out.println("Created new Child: " + newNode + ", parent: " + searchTreeActiveNode + " for assignment: " + assignment);
            searchTree.addChild(searchTreeActiveNode+":"+newNode+":"+assignment, searchTreeActiveNode, newNode);
            searchTreeActiveNode = newNode;
        }
        catch (IllegalArgumentException ex){
            ex.printStackTrace();
            throw ex;
        }
    }
    @Override
    public boolean step() {
        //assign first domain item to first possible variable
        List<VarNode> vars = bcsp.getNodes();
        Optional<VarNode> res = vars.stream().filter(varNode -> varNode.getDomain().size()>1).findFirst();
        if(!res.isPresent()){
            System.out.println("???");
        }
        VarNode currentCSPVar = res.get(); //only select nodes with domain >1 value

        int assignment = currentCSPVar.getDomain().toArray(new Integer[1])[0];

        boolean hasSolution = false;
        String searchTreeEdge = "";
        try {//assign left node, i.e. assign value to variable
            bcsp.push();
            Integer domain[] = currentCSPVar.getDomain().toArray(new Integer[0]);//create copy to avoid
            // concurrent modification exception as crappy java can't handle removal from Set while iterating
            // Srsly, it's a managed language. It should be able to dynamically modify iterators..
            for(int val : domain) { // i.e. remove everything from domain that's not assignment
                if(val != assignment) {
                    bcsp.pruneFromVariableDomain(currentCSPVar.getId(), val);
                }
            }
            searchTreeEdge = currentCSPVar.toString();
            try {
                bcsp.reviseArcs(currentCSPVar.getId());
            }
            finally {
                createGraphNode(searchTreeEdge);
            }
            if(solved()){
                hasSolution = true;
                return true;
            }
            hasSolution = step();//recurse
            return hasSolution;
        }
        catch (NoSolutionException ex){//try right node, i.e. remove assignment from left from variable domain
            try {
                bcsp.undoCurrentPrune();//reset changes made in try/left branch
                bcsp.pruneFromVariableDomain(currentCSPVar.getId(), assignment);
                //System.out.print("No Solution, traversing up from " + searchTreeActiveNode);
                searchTreeActiveNode = searchTree.getParent(searchTreeActiveNode);
                //System.out.println(" to parent node: " + searchTreeActiveNode);
                searchTreeEdge = currentCSPVar.toString();
                try {
                    bcsp.reviseArcs(currentCSPVar.getId());
                }
                finally {
                    createGraphNode(searchTreeEdge);
                }
                if (solved()) {
                    hasSolution = true;
                    return true;
                }
                hasSolution = step();//recurse
            }
            catch (NoSolutionException ex2){
                bcsp.pop();
                bcsp.undoCurrentPrune();
                //System.out.print("Alt branch failed at node " + searchTreeActiveNode);
                searchTreeActiveNode = searchTree.getParent(searchTreeActiveNode); //traverse up in our search tree. Only used for visualisation
                //System.out.println(", traversing up to parent node: " + searchTreeActiveNode);
                throw ex2;//pass on
            }
            return hasSolution;
        }//might throw exception if right node is unsat. Intended, this will traverse up until we reach an unexplored branch or top level
    }

    @Override
    public boolean isSAT() {
        return false;
    }

    @Override
    public void displaySearchTree() {

        Layout<String, String> layout = new TreeLayout<String, String>(searchTree, 130, 170);
        // The BasicVisualizationServer<V,E> is parameterized by the edge types
        VisualizationViewer<String, String> vv =
                new VisualizationViewer<String, String>(layout);
        vv.setPreferredSize(new Dimension(1000,800)); //Sets the viewing area size
        DefaultModalGraphMouse gm = new DefaultModalGraphMouse();
        gm.setMode(ModalGraphMouse.Mode.TRANSFORMING);
        vv.setGraphMouse(gm);
        vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller());
        vv.getRenderContext().setEdgeLabelTransformer(s -> StringUtils.substringAfterLast(s, ":"));

        JFrame frame = new JFrame("Solver search tree");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.getContentPane().add(vv);
        frame.pack();
        frame.setVisible(true);
    }

    @Override
    public Map<Integer, Integer> getAssignments() {
        return bcsp.getAssignments();
    }

    @Override
    public int getNumNodes() {
        return searchTree.getVertexCount();
    }
}
