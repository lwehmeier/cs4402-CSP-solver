package uk.ac.standrews.cs.cs4402.solver.heuristics.variables;

import uk.ac.standrews.cs.cs4402.solver.graphDataModel.BinaryCSPGraph;
import uk.ac.standrews.cs.cs4402.solver.graphDataModel.VarNode;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class FileStaticVariableOrdering extends VariableOrderingHeuristic{
    protected List<VarNode> varOrderList;
    public FileStaticVariableOrdering(BinaryCSPGraph bcsp){
        long start = System.nanoTime();
        int nNodes = bcsp.getVarCnt();
        varOrderList = new ArrayList<>(nNodes);
        for(Integer vn : readOrder(nNodes)){
            varOrderList.add(bcsp.getNode(vn));
        }
        setup_time_us =(System.nanoTime()-start)/1000;
    }
    Integer[] readOrder(int size){
        try {
            Scanner scanner = new Scanner(new File("varOrder.txt"));
            Integer[] order = new Integer[size];
            int i = 0;
            while (scanner.hasNextInt()) {
                order[i++] = scanner.nextInt();
            }
            return order;
        }
        catch (FileNotFoundException ex){
            ex.printStackTrace();
            System.exit(-1);
        }
        return null;
    }
    @Override
    public VarNode getNextVal(BinaryCSPGraph bcsp) {
        long start = System.nanoTime();
        VarNode ret = varOrderList.stream().filter(varNode -> varNode.getDomain().size()>1).findFirst().get();
        compute_time_us +=(System.nanoTime()-start)/1000;
        return ret;
    }
}
