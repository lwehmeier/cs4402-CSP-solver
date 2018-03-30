package uk.ac.standrews.cs.cs4402.solver;

import uk.ac.standrews.cs.cs4402.solver.dataModel.BinaryCSP;
import uk.ac.standrews.cs.cs4402.solver.graphDataModel.BinaryCSPGraph;
import uk.ac.standrews.cs.cs4402.solver.input.BinaryCSPReader;

public class Main {
    static String inputFile = "4Queens.csp";
    public static void main(String args[]){
        BinaryCSPReader reader = new BinaryCSPReader() ;
        System.out.println(reader.readBinaryCSP(inputFile)) ;

        BinaryCSP bcsp = reader.readBinaryCSP(inputFile);
        BinaryCSPGraph bcspg = BinaryCSPGraph.buildGraph(bcsp);

        bcspg.draw();
        bcspg.getNode(0).prune(2);
        bcspg.getNode(0).prune(0);
        bcspg.reviseArcs(0);
        bcspg.getNode(3).prune(1);
        bcspg.getNode(3).prune(2);
        bcspg.reviseArcs(3);

    }
}
