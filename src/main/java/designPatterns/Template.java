package designPatterns;

import consts.MyConsts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;


/*
provides a default way of doing something
example: to drive an older manual car
to drive an older manual car no matter what kind of make or model we need to:
    getInside()
    putKeyInIgnition()
    turnKey()
    pressClutch()
    putInfirstGear()
    releaseClutchAndGiveGas()

everything else is up to the driver but to begin this process has to be done
in this order so that the car moves, this process is absolute

plan:
ask for abstract class name
how many abstract functions, create
how many regular functions, create
how many absolute processes (public final methods)
how many functions in each absolute process
create abstract class
then how many subclasses, create and give abstract functions with override
 */
public class Template extends DesignPatternObj {
    private String mainAbstractClassName;
    private int totalAbstractFunctions;
    private int[] functionsInEachProcess;
    private int totalAbsoluteProcesses;
    final static Logger logger = LoggerFactory.getLogger("Template");

    public Template(ArrayList<String> parameters, String path){
        this.desPatParams = parameters;
        this.subClassList = new ArrayList<>();
        this.path = path + "/";
        parseDesignPatternParams(this.desPatParams);

        // make the directory
        MyConsts.createDir(this.path);
        logger.info("Template parameters acquired successfully");
    }


    public void createTemplate(){
        createMainAbstractClass();
        createSubclasses();
        logger.info("Template for {} successfully created",this.mainAbstractClassName);
    }

    private void createMainAbstractClass(){
        //start by making the main abstract class
        int overallTotalFunctions = this.totalAbstractFunctions + this.totalFuncs;
        Container mainAbstractClass = new Container("abstract class",this.mainAbstractClassName,"",overallTotalFunctions);
        mainAbstractClass.setDirName(this.path);
        MyConsts.createContainerStub(mainAbstractClass);

        // add abstract functions then regular functions
        for(int i = 0; i < this.totalAbstractFunctions; i++){
            mainAbstractClass.text += String.format(MyConsts.AbstractFunctionGenericSig,i);
        }
        for(int i = 0; i < this.totalFuncs; i++){
            mainAbstractClass.text += String.format(MyConsts.RegularFunctionGenericSig,i);
        }

        // absolute processes
        for(int i = 0; i < this.totalAbsoluteProcesses; i++){
            mainAbstractClass.text += String.format(MyConsts.FinalFunctionGenericSig,i);
            // get the stubs for the number of functions in each absolute process
            for(int x = 0; x < this.functionsInEachProcess[i];x++){
                mainAbstractClass.text += String.format(MyConsts.GenericFunctionCall,x);
            }
            // close the current absolute function
            mainAbstractClass.text += "\t}\n\n";
        }
        MyConsts.createFile(mainAbstractClass);
    }

    private void createSubclasses(){
        for(Container c: this.subClassList){
            MyConsts.createContainerStub(c);
            for(int i = 0; i < this.totalAbstractFunctions; i++){
                c.text += String.format(MyConsts.OverrideAbstractFunctionGenericSig,i);
            }
            c.text += String.format(MyConsts.ConstructorSig,c.name);
            MyConsts.createFile(c);
        }
    }

    /*
    order: main abstract class, abstract function amount, regular function amount,
    amount of absolute processes, amount of functions in each process, subclasses names
     */
    @Override
    public void parseDesignPatternParams(ArrayList<String> paramList){
        this.mainAbstractClassName = paramList.get(0);
        this.totalAbstractFunctions = Integer.parseInt(paramList.get(1));
        this.totalFuncs = Integer.parseInt(paramList.get(2));
        this.totalAbsoluteProcesses = Integer.parseInt(paramList.get(3));
        this.functionsInEachProcess = new int[this.totalAbsoluteProcesses];
        this.path = this.path + this.mainAbstractClassName;

        int index = 4;
        for(int i = 0; i < this.totalAbsoluteProcesses; i++){
            this.functionsInEachProcess[i] = Integer.parseInt(paramList.get(index));
            index++;
        }

        // get the names of the subclasses and create their containers to add to list
        for(int i = index; i < paramList.size(); i++){
            Container subClass = new Container("regular class",paramList.get(i),this.mainAbstractClassName,this.totalAbstractFunctions);
            subClass.setExtend(true);
            subClass.setDirName(this.path);
            this.subClassList.add(subClass);
        }
    }

    public String getMainAbstractClassName(){ return this.mainAbstractClassName;}
    public int getTotalAbstractFunctions(){return this.totalAbstractFunctions;}
    public int[] getFunctionsInEachProcess(){return this.functionsInEachProcess;}
    public int getTotalAbsoluteProcesses(){return this.totalAbsoluteProcesses;}
}
