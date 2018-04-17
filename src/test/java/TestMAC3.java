import org.junit.Assert;
import org.junit.Test;
import uk.ac.standrews.cs.cs4402.solver.cli.CLI;
import uk.ac.standrews.cs.cs4402.solver.graphDataModel.BinaryCSPGraph;
import uk.ac.standrews.cs.cs4402.solver.graphDataModel.VarNode;
import uk.ac.standrews.cs.cs4402.solver.heuristics.values.ValueOrderingHeuristic;
import uk.ac.standrews.cs.cs4402.solver.heuristics.variables.VariableOrderingHeuristic;
import uk.ac.standrews.cs.cs4402.solver.solvers.Solver;

public class TestMAC3
{

    @Test
    public void runMAC3() {
        VarNode.reset();
        System.setProperty("java.util.concurrent.ForkJoinPool.common.parallelism", "4");
        String args[] = new String[9];
        args[0] = "--algorithm";
        args[1] = "mac3";
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
    public void runMAC3_langford2_4() {
        VarNode.reset();
        System.setProperty("java.util.concurrent.ForkJoinPool.common.parallelism", "4");
        String args[] = new String[9];
        args[0] = "--algorithm";
        args[1] = "mac3";
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
    public void runMAC3_10Queens() {
        VarNode.reset();
        System.setProperty("java.util.concurrent.ForkJoinPool.common.parallelism", "4");
        String args[] = new String[9];
        args[0] = "--algorithm";
        args[1] = "mac3";
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

    @Test
    public void runMAC3_langfords3_9() {
        VarNode.reset();
        System.setProperty("java.util.concurrent.ForkJoinPool.common.parallelism", "4");
        String args[] = new String[9];
        args[0] = "--algorithm";
        args[1] = "mac3";
        args[2] = "--var-heuristic";
        args[3] = "min-width";
        args[4] = "--val-heuristic";
        args[5] = "magnitude-desc";
        args[6] = "-F";
        args[7] = "./langfords3_9.csp";
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
        Integer expected[] = new Integer[]{23,25,27,15,18,21,1,5,9,2,7,12,8,14,20,10,17,24,3,11,19,4,13,22,6,16,26};
        Assert.assertArrayEquals(expected , assignments );
    }

    /**
     * variable var1 0 : (0, 42)
     * variable var3 2 : (4, 10)
     * variable var2 1 : {1,2,4}
     * constraint c1 (var1, var2) : {(1,1),(42,42),(1,2)}
     * constraint c2 (var2, var3) : {<}
     **/
    @Test
    public void runMAC3_combinedTupleIntrinsics() {
        VarNode.reset();
        System.setProperty("java.util.concurrent.ForkJoinPool.common.parallelism", "4");
        String args[] = new String[9];
        args[0] = "--algorithm";
        args[1] = "mac3";
        args[2] = "--var-heuristic";
        args[3] = "min-width";
        args[4] = "--val-heuristic";
        args[5] = "magnitude-desc";
        args[6] = "-f";
        args[7] = "../../test.bcsp";
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
        Integer expected[] = new Integer[]{1,2,10};
        Assert.assertArrayEquals(expected , assignments );
    }
}
