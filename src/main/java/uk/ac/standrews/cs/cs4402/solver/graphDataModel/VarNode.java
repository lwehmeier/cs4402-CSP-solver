package uk.ac.standrews.cs.cs4402.solver.graphDataModel;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class VarNode {
    private Set<Integer> domain;
    private static int id=0;
    private int varID;
    private String name;
    public static void reset(){
        id=0;
    }
    private VarNode(){
        varID = id;
        name = Integer.toString(id++);
    }
    public VarNode(Set<Integer> domain){
        this();
        setDomain(domain);
    }
    public VarNode(List<Integer> domain){
        this();
        setDomain(new HashSet<Integer>(domain));
    }
    public VarNode(int lower, int upper){
        this();
        Set<Integer> domain = new HashSet<>();
        for(int i = lower; i <= upper ; i++){
            domain.add(i);
        }
        setDomain(domain);
    }
    public VarNode(Set<Integer> domain, int id){
        this(domain);
        this.varID=id;
        name = Integer.toString(varID);
    }
    public VarNode(List<Integer> domain, int id){
        this(domain);
        this.varID=id;
        name = Integer.toString(varID);
    }
    public VarNode(int lower, int upper, int id) {
        this(lower, upper);
        this.varID=id;
        name = Integer.toString(varID);
    }

    public Set<Integer> getDomain() {
        return domain;
    }

    public void setDomain(Set<Integer> domain) {
        this.domain = domain;
    }

    public boolean prune(Integer value){
        return domain.remove(value);
    }
    public int getId(){
        return varID;
    }
    public boolean extendDomain(Integer value){
        return domain.add(value);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        String s = "Var_"+getName()+"{";
        for(Integer i : domain){
            s+=i.toString()+",";
        }
        return s +"}";
    }
}
