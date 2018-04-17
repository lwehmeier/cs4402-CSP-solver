package uk.ac.standrews.cs.cs4402.solver.graphDataModel;


import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class ConstraintEdgeIntrinsic extends ConstraintEdge {
    private interface InsteadOfProperLambdaObjects{
        boolean satisfiesConstraint(Integer start, Integer end);
    }
    private InsteadOfProperLambdaObjects decider;
    private String symbol;
    private boolean inverted;

    public ConstraintEdgeIntrinsic(String symbol){
        this(symbol, false);
    }
    public ConstraintEdgeIntrinsic(String symbol, boolean invert){
        this.symbol = symbol;
        inverted=invert;
        switch (symbol){
            case "<":
                if(!invert)
                    decider = (start, end) -> start < end;
                else
                    decider = (start, end) -> (start > end);
                break;
            case "<=":
                if(!invert)
                    decider = (start, end) -> start <= end;
                else
                    decider = (start, end) -> (start >= end);
                break;
            case ">":
                if(!invert)
                    decider = (start, end) -> start > end;
                else
                    decider = (start, end) -> (start < end);
                break;
            case ">=":
                if(!invert)
                    decider = (start, end) -> start >= end;
                else
                    decider = (start, end) -> (start <= end);
                break;
            case "=":
                if(!invert)
                    decider = (start, end) -> start == end;
                else
                    decider = (start, end) -> (start == end);
                break;
            default:
                throw new org.apache.commons.lang3.NotImplementedException("Intrinsic "+symbol+" not implemented");
        }
    }
    @Override
    protected boolean satisfiesConstraint(Integer start, Integer end) {
        return decider.satisfiesConstraint(start, end);
    }
    @Override
    public String toString(){
        return inverted?"!"+symbol:symbol;
    }
}
