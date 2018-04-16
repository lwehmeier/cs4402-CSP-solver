package uk.ac.standrews.cs.cs4402.solver;

import uk.ac.standrews.cs.cs4402.solver.cli.CLI;
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
    public static void main(String args[]){
        System.setProperty("java.util.concurrent.ForkJoinPool.common.parallelism", "4");
        if(args.length==0){
            args = new String[8];
            args[0]="--algorithm";
            args[1]="fc";
            args[2]="--var-heuristic";
            args[3]="cardinality";
            args[4]="--val-heuristic";
            args[5]="magnitude-desc";
            args[6]="-F";
            //args[7]="../test.bcsp";
            args[7]="cs4402.dsl.solver/langfords3_9.csp";
            //args[8]="--json";
        }


        long startTime, parseTime, solverSetupTime, solveTime;
        startTime = System.nanoTime();


        CLI cli = new CLI(args);
        cli.parse();
        boolean json = cli.isOutputJson();


        parseTime = System.nanoTime();


        BinaryCSPGraph bcsp = cli.getBcspg();
        if (!json && bcsp.getVarCnt() < 15)
            bcsp.draw();
        Solver solver = cli.getSolver();
        solver.setCSP(bcsp);
        boolean SAT = false;
        VariableOrderingHeuristic varH = cli.getVarh();
        ValueOrderingHeuristic valH = cli.getValh();


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
        if (!json && solver.getNumNodes() < 500)
            solver.displaySearchTree(false);
        if(!json) {
            System.out.println(SAT);
        }
        if (SAT && !json) {
            System.out.println("Assignments: ");
            for (Integer var : solver.getAssignments().keySet()) {
                System.out.print("Var_" + var.toString());
                System.out.println(": " + solver.getAssignments().get(var).toString());
            }
        }
        if(!json) {
            System.out.println("Read took " + Double.toString((parseTime - startTime) / 1000000) + "ms");
            System.out.println("Solver setup took " + Double.toString((solverSetupTime - parseTime) / 1000000) + "ms");
            System.out.println("Solving took " + Double.toString((solveTime - solverSetupTime) / 1000000) + "ms");
            System.out.println("SolverNodes: " + Integer.toString(solver.getNumNodes()));
        }
        else {
            System.out.print("{ \"parse_time\" : ");
            System.out.print(Double.toString((double)(parseTime - startTime) / 1000000));
            System.out.print(", \"setup_time\" : ");
            System.out.print(Double.toString((double)(solverSetupTime - parseTime) / 1000000));
            System.out.print(", \"solve_time\" : ");
            System.out.print(Double.toString((double)(solveTime - solverSetupTime) / 1000000));
            System.out.print(", \"solver_nodes\" : ");
            System.out.print(Integer.toString(solver.getNumNodes()));
            System.out.print(", \"csp_variables\" : ");
            System.out.print(Integer.toString(bcsp.getVarCnt()));
            System.out.print(", \"SAT\" : ");
            System.out.print(SAT?1:0);
            System.out.print("}\n");
        }
    }
}
