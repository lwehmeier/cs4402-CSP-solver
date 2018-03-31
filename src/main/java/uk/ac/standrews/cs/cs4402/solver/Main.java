package uk.ac.standrews.cs.cs4402.solver;

import uk.ac.standrews.cs.cs4402.solver.dataModel.BinaryCSP;
import uk.ac.standrews.cs.cs4402.solver.graphDataModel.BinaryCSPGraph;
import uk.ac.standrews.cs.cs4402.solver.graphDataModel.NoSolutionException;
import uk.ac.standrews.cs.cs4402.solver.input.BinaryCSPReader;

public class Main {
    static String inputFile = "6Queens.csp";
    public static void main(String args[]){
        BinaryCSPReader reader = new BinaryCSPReader() ;
        System.out.println(reader.readBinaryCSP(inputFile)) ;

        BinaryCSP bcsp = reader.readBinaryCSP(inputFile);
        BinaryCSPGraph bcspg = BinaryCSPGraph.buildGraph(bcsp);

        //bcspg.draw();
        /*bcspg.pruneFromVariableDomain(0, 3);
        bcspg.pruneFromVariableDomain(0, 0);
        bcspg.reviseArcs(0);
        //bcspg.push();
        bcspg.pruneFromVariableDomain(1, 0);
        bcspg.pruneFromVariableDomain(1, 3);
        bcspg.pruneFromVariableDomain(1, 2);
        bcspg.reviseArcs(1);
        //bcspg.push();
        //bcspg.pop();
        //bcspg.pop();
        */

        Solver solver = new ForwardSolver();
        solver.setCSP(bcspg);
        bcspg.push();
        boolean SAT = false;
        try {
            SAT = solver.step();
        }catch (NoSolutionException ex){
            ex.printStackTrace();
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
        solver.displaySearchTree();
        System.out.println(SAT);
        if(SAT){
            System.out.println("Assignments: ");
            for(Integer var : solver.getAssignments().keySet()) {
                System.out.print("Var_"+var.toString());
                System.out.println(": " + solver.getAssignments().get(var).toString());
            }
        }
    }
}
