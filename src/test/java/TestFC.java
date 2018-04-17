import org.junit.Assert;
import org.junit.Test;
import uk.ac.standrews.cs.cs4402.solver.cli.CLI;
import uk.ac.standrews.cs.cs4402.solver.graphDataModel.BinaryCSPGraph;
import uk.ac.standrews.cs.cs4402.solver.graphDataModel.VarNode;
import uk.ac.standrews.cs.cs4402.solver.heuristics.values.ValueOrderingHeuristic;
import uk.ac.standrews.cs.cs4402.solver.heuristics.variables.VariableOrderingHeuristic;
import uk.ac.standrews.cs.cs4402.solver.solvers.Solver;

public class TestFC {
    @Test
    public void runFC_langford2_3() {
        VarNode.reset();
        System.setProperty("java.util.concurrent.ForkJoinPool.common.parallelism", "4");
            String args[] = new String[9];
            args[0] = "--algorithm";
            args[1] = "fc";
            args[2] = "--var-heuristic";
            args[3] = "min-width";
            args[4] = "--val-heuristic";
            args[5] = "magnitude-desc";
            args[6] = "-F";
            args[7] = "./langfords2_3.csp";
            args[8]="--json";
            //args[8] = "--slow";

        CLI cli = new CLI(args);
        cli.parse();
        BinaryCSPGraph bcsp = cli.getBcspg();
        Solver solver = cli.getSolver();
        solver.setCSP(bcsp);
        VariableOrderingHeuristic varH = cli.getVarh();
        ValueOrderingHeuristic valH = cli.getValh();

        boolean SAT = solver.solve(varH, valH, false);
        assert SAT == true;
        Integer assignments[] = solver.getAssignments().values().toArray(new Integer[0]);
        Integer expected[] = new Integer[]{3,5,1,4,2,6};
        Assert.assertArrayEquals(expected , assignments );
    }

    @Test
    public void runFC_langford2_4() {
        VarNode.reset();
        System.setProperty("java.util.concurrent.ForkJoinPool.common.parallelism", "4");
        String args[] = new String[9];
        args[0] = "--algorithm";
        args[1] = "fc";
        args[2] = "--var-heuristic";
        args[3] = "min-width";
        args[4] = "--val-heuristic";
        args[5] = "magnitude-desc";
        args[6] = "-F";
        args[7] = "./langfords2_4.csp";
        args[8]="--json";
        //args[8] = "--slow";

        CLI cli = new CLI(args);
        cli.parse();
        BinaryCSPGraph bcsp = cli.getBcspg();
        Solver solver = cli.getSolver();
        solver.setCSP(bcsp);
        VariableOrderingHeuristic varH = cli.getVarh();
        ValueOrderingHeuristic valH = cli.getValh();

        boolean SAT = solver.solve(varH, valH, false);
        assert SAT == true;
        Integer assignments[] = solver.getAssignments().values().toArray(new Integer[0]);
        Integer expected[] = new Integer[]{5,7,1,4,2,6,3,8};
        Assert.assertArrayEquals(expected , assignments );
    }
    @Test
    public void runFC_10Queens() {
        VarNode.reset();
        System.setProperty("java.util.concurrent.ForkJoinPool.common.parallelism", "4");
        String args[] = new String[9];
        args[0] = "--algorithm";
        args[1] = "fc";
        args[2] = "--var-heuristic";
        args[3] = "min-width";
        args[4] = "--val-heuristic";
        args[5] = "magnitude-desc";
        args[6] = "-F";
        args[7] = "./10Queens.csp";
        args[8]="--json";
        //args[8] = "--slow";

        CLI cli = new CLI(args);
        cli.parse();
        BinaryCSPGraph bcsp = cli.getBcspg();
        Solver solver = cli.getSolver();
        solver.setCSP(bcsp);
        VariableOrderingHeuristic varH = cli.getVarh();
        ValueOrderingHeuristic valH = cli.getValh();

        boolean SAT = solver.solve(varH, valH, false);
        assert SAT == true;
        Integer assignments[] = solver.getAssignments().values().toArray(new Integer[0]);
        Integer expected[] = new Integer[]{3,6,8,1,5,0,2,4,7,9};
        Assert.assertArrayEquals(expected , assignments );
    }
}
