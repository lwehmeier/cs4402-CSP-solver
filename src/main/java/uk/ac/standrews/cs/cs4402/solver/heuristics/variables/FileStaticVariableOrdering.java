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
        int nNodes = bcsp.getVarCnt();
        varOrderList = new ArrayList<>(nNodes);
        for(Integer vn : readOrder(nNodes)){
            varOrderList.add(bcsp.getNode(vn));
        }
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
        return varOrderList.stream().filter(varNode -> varNode.getDomain().size()>1).findFirst().get();
    }
}
