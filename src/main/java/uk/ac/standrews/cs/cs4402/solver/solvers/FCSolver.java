package uk.ac.standrews.cs.cs4402.solver.solvers;

import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.algorithms.layout.TreeLayout;
import edu.uci.ics.jung.graph.DelegateTree;
import edu.uci.ics.jung.graph.Tree;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import org.apache.commons.lang3.StringUtils;
import uk.ac.standrews.cs.cs4402.solver.graphDataModel.BinaryCSPGraph;
import uk.ac.standrews.cs.cs4402.solver.graphDataModel.NoSolutionException;
import uk.ac.standrews.cs.cs4402.solver.graphDataModel.VarNode;
import com.google.common.base.Function;
import uk.ac.standrews.cs.cs4402.solver.heuristics.values.ValueOrderingHeuristic;
import uk.ac.standrews.cs.cs4402.solver.heuristics.variables.VariableOrderingHeuristic;

import javax.swing.*;
import java.awt.*;
import java.util.*;

public class FCSolver implements Solver {
    BinaryCSPGraph bcsp;

    private DelegateTree<String, String> searchTree = new DelegateTree<>();
    private String searchTreeActiveNode;
    private int searchNodeID=0;
    @Override
    public void setCSP(BinaryCSPGraph csp) {
        bcsp = csp;
        //bcsp.reviseArcs_simple(); // reduce variable domains if possible
        //bcsp.push(); //actually, don't do that in fwd checking
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

    protected void createGraphNode(String assignment){
        try {
            String newNode = bcsp.stateToString()+Integer.toString(searchNodeID++);
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
    public boolean solve(VariableOrderingHeuristic varH, ValueOrderingHeuristic valH, boolean slow) {
        if(solved()){ //edge case, csp is already solved
            return true;
        }
        if(slow) {
            try {
                Thread.sleep(250);
            } catch (InterruptedException ex) {
            }
        }
        VarNode currentCSPVar = varH.getNextVal(bcsp);

        int assignment = valH.getNextVal(currentCSPVar);

        boolean hasSolution = false;
        String searchTreeEdge = "";
        try {//assign left node, i.e. assign values to variable
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
                bcsp.reviseArcs_FC(currentCSPVar.getId());
            }
            finally {
                createGraphNode(searchTreeEdge);
            }
            if(solved()){
                hasSolution = true;
                return true;
            }
            hasSolution = solve(varH, valH, slow);//recurse
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
                    bcsp.reviseArcs_FC(currentCSPVar.getId());
                }
                finally {
                    createGraphNode(searchTreeEdge);
                }
                if (solved()) {
                    hasSolution = true;
                    return true;
                }
                hasSolution = solve(varH, valH, slow);//recurse
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
    public void displaySearchTree(boolean update) {

        Layout<String, String> layout = new TreeLayout<String, String>(searchTree, 130, 170);
        // The BasicVisualizationServer<V,E> is parameterized by the edge types
        VisualizationViewer<String, String> vv =
                new VisualizationViewer<String, String>(layout);
        vv.setPreferredSize(new Dimension(640,480)); //Sets the viewing area size
        DefaultModalGraphMouse gm = new DefaultModalGraphMouse();
        gm.setMode(ModalGraphMouse.Mode.TRANSFORMING);
        vv.setGraphMouse(gm);
        vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller());
        vv.getRenderContext().setEdgeLabelTransformer(s -> StringUtils.substringAfterLast(s, ":"));
        vv.getRenderingHints().remove(RenderingHints.KEY_ANTIALIASING);
        // Transformer maps the vertex number to a vertex property

        Function<String, Paint> vertexColour = new Function<String, Paint>(){
            public Paint apply(String input) {
                if(input.contains("{}")) {//empty domain
                    return Color.RED;
                }
                //if(input.equals("ROOT")){
                //    return Color.BLACK;
                //}
                if(input.equals(searchTreeActiveNode)) {//last node, either solution or problem unsolvable
                    if(!searchTreeActiveNode.contains("{}")){//no empty domains, ergo solution or current search node
                        if(searchTreeActiveNode.matches(".*\\{([0-9]+,)+[0-9]+,\\}.*")){
                            return Color.BLUE;
                        }
                        return Color.GREEN;
                    }
                }
                if(searchTree.getChildCount(input) < 2)
                    return Color.ORANGE;
                else
                    return Color.MAGENTA;
            }
        };
        vv.getRenderContext().setVertexFillPaintTransformer(vertexColour);
        JFrame frame = new JFrame("Solver search tree");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.getContentPane().add(vv);
        frame.pack();
        frame.setVisible(true);
        if(update) {
            new Thread(() -> {
                int nVert = searchTree.getVertexCount();
                double lastZoom = 1.0;
                while (frame.isDisplayable()) {
                    try {
                        Thread.sleep(200);
                        if(searchTree.getVertexCount()!=nVert) {
                            nVert = searchTree.getVertexCount();
                            Layout cl = vv.getGraphLayout();
                            int treeHeight = ((Tree<String, String>) cl.getGraph()).getHeight() + 1;
                            Layout l = new TreeLayout<String, String>(searchTree, 100, 100);
                            vv.setGraphLayout(l);
                                /*ScalingControl scaler = new CrossoverScalingControl();
                                double zoom = (double)frame.getHeight() / (100 * treeHeight);
                                if(zoom < 1) {
                                    zoom = 1.2- (lastZoom - zoom);
                                    lastZoom = zoom;
                                    Point2D zoomPos = vv.getCenter();
                                    zoomPos = new Point2D.Double(zoomPos.getX()*0.75, zoomPos.getY()*0.75);
                                    scaler.scale(vv, (float) (zoom), zoomPos);
                                }
                                */
                        }
                    } catch (InterruptedException ex) {
                    }
                }
            })
                    .start();
        }
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
