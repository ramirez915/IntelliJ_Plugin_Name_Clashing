package designPatterns;

import java.util.ArrayList;

public abstract class DesignPatternObj {
    protected int totalFuncs;
    protected ArrayList<Container> subClassList;
    protected ArrayList<String> desPatParams;
    protected String path;                      // contains the path chosen by user to store the created files

    /*
     will parse out the parameters gotten from the user
     for the corresponding design pattern
     */
    public abstract void parseDesignPatternParams(ArrayList<String> paramList);

    public ArrayList<Container> getSubClassList(){ return subClassList;}
    public ArrayList<String> getDesPatParams(){ return desPatParams;}

}
