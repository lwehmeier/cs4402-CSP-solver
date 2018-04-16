package uk.ac.standrews.cs.cs4402.solver;

import uk.ac.standrews.cs.cs4402.solver.dataModel.BinaryCSP;
import uk.ac.standrews.cs.cs4402.solver.graphDataModel.BinaryCSPGraph;
import uk.ac.standrews.cs.cs4402.solver.graphDataModel.NoSolutionException;
import uk.ac.standrews.cs.cs4402.solver.heuristics.values.*;
import uk.ac.standrews.cs.cs4402.solver.heuristics.variables.*;
import uk.ac.standrews.cs.cs4402.solver.input.BinaryCSPReader;
import uk.ac.standrews.cs.cs4402.solver.solvers.FCSolver;
import uk.ac.standrews.cs.cs4402.solver.solvers.MACSolverAC3;
import uk.ac.standrews.cs.cs4402.solver.solvers.Solver;
import uk.standrews.cs.cs4402.dsl.dSL.CSP;
import uk.standrews.cs.cs4402.dsl.dSL.VarNode;

public class Main {
    static String inputFile = "cs4402.dsl.solver/langfords2_4.csp";
    public static void main(String args[]){
        if(args.length==1){
            inputFile = args[0];
        }
//        try {
//            Thread.sleep(25000);
//            System.out.println("starting in 1s");
//            Thread.sleep(1000);
//        }catch (InterruptedException ex){}

        long startTime, readTime, graphBuildTime, solverSetupTime, solveTime;

        startTime = System.nanoTime();

        BinaryCSPReader reader = new BinaryCSPReader() ;
        //System.out.println(reader.readBinaryCSP(inputFile)) ;
        BinaryCSP bcsp = reader.readBinaryCSP(inputFile);
        CSP xtext_csp = uk.standrews.cs.cs4402.dsl.Main.parse();
        readTime = System.nanoTime();

        BinaryCSPGraph bcspg = BinaryCSPGraph.buildGraph(bcsp);
        BinaryCSPGraph xtext_bcspg = BinaryCSPGraph.buildGraph(xtext_csp);
        bcspg = xtext_bcspg;
        graphBuildTime = System.nanoTime();
        if (bcsp.getNoVariables() < 15)
            bcspg.draw();


        //Solver solver = new FCSolver();
        //Solver solver = new MACSolverAC25();
        Solver solver = new MACSolverAC3();
        solver.setCSP(bcspg);
        boolean SAT = false;
        //VariableOrderingHeuristic varH = new FixedStaticIdVariableOrdering();
        //VariableOrderingHeuristic varH = new FixedStaticDegreeVariableOrdering();
        //VariableOrderingHeuristic varH = new FixedStaticCardinalityVariableOrdering(bcspg);
        //VariableOrderingHeuristic varH = new FileStaticVariableOrdering(bcspg);
        //VariableOrderingHeuristic varH = new DynamicDomainVariableOrdering();
        //VariableOrderingHeuristic varH = new DynamicBrelazVariableOrdering();
        VariableOrderingHeuristic varH = new DynamicDomDegVariableOrder();


        //ValueOrderingHeuristic valH = new FixedStaticMagnitudeValueOrdering(false);
        //ValueOrderingHeuristic valH = new DynamicMinConflictsValueOrdering(bcspg);
        //ValueOrderingHeuristic valH = new DynamicCrucialityValueOrdering(bcspg);
        ValueOrderingHeuristic valH = new DynamicPromiseValueOrdering(bcspg);


        solverSetupTime = System.nanoTime();
        //solver.displaySearchTree(true);
        try {
            SAT = solver.step(varH,valH, false);
        } catch (NoSolutionException ex) {
            ex.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        solveTime = System.nanoTime();
        if (solver.getNumNodes() < 500)
            solver.displaySearchTree(false);
        System.out.println(SAT);
        if (SAT) {
            System.out.println("Assignments: ");
            for (Integer var : solver.getAssignments().keySet()) {
                System.out.print("Var_" + var.toString());
                System.out.println(": " + solver.getAssignments().get(var).toString());
            }
        }
        System.out.println("Read took " + Double.toString((readTime - startTime) / 1000000) + "ms");
        System.out.println("Graph building took " + Double.toString((graphBuildTime - readTime) / 1000000) + "ms");
        System.out.println("Solver setup took " + Double.toString((solverSetupTime - graphBuildTime) / 1000000) + "ms");
        System.out.println("Solving took " + Double.toString((solveTime - solverSetupTime) / 1000000) + "ms");

        System.out.println("SolverNodes: " + Integer.toString(solver.getNumNodes()));
    }
}
