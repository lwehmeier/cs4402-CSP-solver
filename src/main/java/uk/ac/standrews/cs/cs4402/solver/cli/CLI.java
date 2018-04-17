package uk.ac.standrews.cs.cs4402.solver.cli;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import uk.ac.standrews.cs.cs4402.solver.dataModel.BinaryCSP;
import uk.ac.standrews.cs.cs4402.solver.graphDataModel.BinaryCSPGraph;
import uk.ac.standrews.cs.cs4402.solver.heuristics.values.*;
import uk.ac.standrews.cs.cs4402.solver.heuristics.variables.*;
import uk.ac.standrews.cs.cs4402.solver.input.BinaryCSPReader;
import uk.ac.standrews.cs.cs4402.solver.solvers.FCSolver;
import uk.ac.standrews.cs.cs4402.solver.solvers.MACSolverAC25;
import uk.ac.standrews.cs.cs4402.solver.solvers.MACSolverAC3;
import uk.ac.standrews.cs.cs4402.solver.solvers.Solver;
import uk.standrews.cs.cs4402.dsl.dSL.CSP;

public class CLI {
    private String[] args = null;
    private Options options = new Options();
    private BinaryCSP legacy_bcsp=null;
    private CSP bcsp=null;
    private BinaryCSPGraph bcspg=null;
    private ValueOrderingHeuristic valh = null;
    private VariableOrderingHeuristic varh = null;
    private Solver solver =null;
    private boolean slow=false;
    private boolean outputJson = false;

    public CLI(String[] args) {
        this.args = args;
        options.addOption("h", "help", false, "show help.");
        options.addOption("a", "algorithm", true, "Solver algorithm (FC, MAC25 MAC3 MAC31)");
        options.addOption("v", "var-heuristic", true, "Variable heuristic. brelaz," +
                "domain-size, dom-deg, file-static, var-id, min-width, degree, cardinality");
        options.addOption("V", "val-heuristic", true, "Value heuristic. magnitude," +
                " min-conflict, cruciality, promise");
        options.addOption("f", "file", true, "bcsp file");
        options.addOption("F", "file-deprecated", true, "old csp file using provided parser");
        options.addOption("j", "json", false, "output statistics in json format");
        options.addOption("s", "slow", false, "perform steps slowly step by step. Show in solver tree");
    }

    public void parse() {
        CommandLineParser parser = new BasicParser();
        CommandLine cmd = null;
        try {
            cmd = parser.parse(options, args);
            if (cmd.hasOption("h")) {
                help();
            }
            if (cmd.hasOption("f")) {
                bcsp = uk.standrews.cs.cs4402.dsl.Main.parse(cmd.getOptionValue("f"));
            } else if(cmd.hasOption("F")) {
                BinaryCSPReader reader = new BinaryCSPReader() ;
                legacy_bcsp = reader.readBinaryCSP(cmd.getOptionValue("F"));
            }else {
                help();
            }
            if(bcsp==null) {
                bcspg = BinaryCSPGraph.buildGraph(legacy_bcsp);
            }else {
                bcspg = BinaryCSPGraph.buildGraph(bcsp);
            }
            if (cmd.hasOption("v")) {
                switch (cmd.getOptionValue("v")){
                    case "brelaz":
                        varh = new DynamicBrelazVariableOrdering();
                        break;
                    case "domain-size":
                        varh = new DynamicDomainVariableOrdering();
                        break;
                    case "dom-deg":
                        varh = new DynamicDomDegVariableOrder();
                        break;
                    case "file-static":
                        varh = new FileStaticVariableOrdering(getBcspg());
                        break;
                    case "var-id":
                        varh = new FixedStaticIdVariableOrdering();
                        break;
                    case "min-width":
                        varh = new FixedStaticWidthVariableOrdering(getBcspg());
                        break;
                    case "degree":
                        varh = new FixedStaticDegreeVariableOrdering();
                        break;
                    case "cardinality":
                        varh = new FixedStaticCardinalityVariableOrdering(getBcspg());
                        break;
                    default:
                        System.err.println("unsupported variable heuristic.");
                        help();
                }
            } else {
                help();
            }
            if (cmd.hasOption("V")) {
                switch (cmd.getOptionValue("V")) {
                    case "magnitude-asc":
                        valh = new FixedStaticMagnitudeValueOrdering(true);
                        break;
                    case "magnitude-desc":
                        valh = new FixedStaticMagnitudeValueOrdering(false);
                        break;
                    case "min-conflict":
                        valh = new DynamicMinConflictsValueOrdering(getBcspg());
                        break;
                    case "cruciality":
                        valh = new DynamicCrucialityValueOrdering(getBcspg());
                        break;
                    case "promise":
                        valh = new DynamicPromiseValueOrdering(getBcspg());
                        break;
                    default:
                        System.err.println("unsupported value heuristic.");
                        help();
                }
            } else {
                help();
            }
            if (cmd.hasOption("a")) {
                switch (cmd.getOptionValue("a")) {
                    case "fc":
                        solver = new FCSolver();
                        break;
                    case "mac25":
                        solver = new MACSolverAC25();
                        break;
                    case "mac3":
                        solver = new MACSolverAC3();
                        break;
                    case "mac31":
                        throw new NotImplementedException();
                    default:
                        System.err.println("unsupported solver algorithm.");
                        help();
                }
            } else {
                help();
            }
            if (cmd.hasOption("j")) {
                outputJson = true;
            }
            if (cmd.hasOption("s")) {
                slow = true;
            }

        } catch (ParseException e) {
            help();
        }
    }

    private void help() {
        // This prints out some help
        HelpFormatter formatter = new HelpFormatter();

        formatter.printHelp("Main", options);
        System.exit(0);
    }

    public BinaryCSPGraph getBcspg() {
        return bcspg;
    }

    public ValueOrderingHeuristic getValh() {
        return valh;
    }

    public VariableOrderingHeuristic getVarh() {
        return varh;
    }

    public Solver getSolver() {
        return solver;
    }

    public boolean isOutputJson() {
        return outputJson;
    }

    public boolean isSlow() {
        return slow;
    }
}