package uk.ac.standrews.cs.cs4402.solver.heuristics.values;

import uk.ac.standrews.cs.cs4402.solver.graphDataModel.VarNode;

import java.util.Set;

public class FixedStaticMagnitudeValueOrdering extends ValueOrderingHeuristic{
    protected boolean orderAsc;
    public FixedStaticMagnitudeValueOrdering(boolean ascendingOrder){
        orderAsc = ascendingOrder;
    }
    @Override
    public int getNextVal(VarNode vn) {
        long start = System.nanoTime();
        Set<Integer> domain = vn.getDomain();
        int next = domain.stream().sorted((e1, e2) -> orderAsc?new Integer(e1).compareTo(e2):new Integer(e2).compareTo(e1)).
                findFirst().get();
        compute_time_us +=(System.nanoTime()-start)/1000;
        return next;
    }
}
