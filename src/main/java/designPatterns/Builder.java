package designPatterns;

import consts.MyConsts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

/*
has a main class that has certain mandatory attributes
and then some optional attributes (all private). The main class only has
getter functions for the attributes. Its constructor is
private, takes in an instance of the nested class, and only
accessible through the pubic static nested class (the builder).
The nested static class has the same mandatory AND optional
attributes and its public constructor that sets the mandatory attributes.
Has functions to set the optional attributes and returns the builder itself (this).
Has a function called build that creates the new instance of the outer class


take user input for whats needed. then make the main class stub along with the variables
then make getters and private constructor. Then make the public static nested class with the
same variables, its public constructor, setters then build function
 */
public class Builder extends DesignPatternObj {
    private String mainClassName;
    private int mandatoryAttributeAmount;
    private int optionalAttributeAmount;
    private String[] mandatoryAttributeTypes;
    private String[] optionalAttributeTypes;
    final static Logger logger = LoggerFactory.getLogger("Builder");

    public Builder(ArrayList<String> parameters,String path){
        this.desPatParams = parameters;
        parseDesignPatternParams(this.desPatParams);

        // make the directory
        this.path = path + "/" + this.mainClassName;
        MyConsts.createDir(this.path);
        logger.info("Builder parameters acquired successfully");
    }

    public void createBuilder(){
        //create main class
        int totalFunctionsNeeded = this.mandatoryAttributeAmount + this.optionalAttributeAmount;
        Container mainClass = new Container("regular class",this.mainClassName,"",totalFunctionsNeeded);
        makeMainClassStub(mainClass);
    }

    private void makeMainClassStub(Container mainClass){
        mainClass.setDirName(this.path);
        MyConsts.createContainerStub(mainClass);

        // add in attributes
        MyConsts.makeVariableStubs(this.mandatoryAttributeTypes,mainClass,true);
        MyConsts.makeVariableStubs(this.optionalAttributeTypes,mainClass,true);

        // make getters for attributes mandatory then optional
        int counter = 0;
        for(String s: this.mandatoryAttributeTypes){
            mainClass.text += String.format(MyConsts.GetterFuncSig,s,counter);
            mainClass.text += MyConsts.ReturnNullStub;
            counter++;
        }
        for(String s: this.optionalAttributeTypes){
            mainClass.text += String.format(MyConsts.GetterFuncSig,s,counter);
            mainClass.text += MyConsts.ReturnNullStub;
            counter++;
        }

        //private constructor
        mainClass.text += String.format(MyConsts.PrivateConstructorSig,mainClass.name);

        // now make nested static class
        Container nestedStaticClass = createNestedStaticClass();

        // merge the nested class text with the main class'
        mainClass.text += nestedStaticClass.text;
        MyConsts.createFile(mainClass);
    }

    /*
    builds the text for the nested static class
     */
    private Container createNestedStaticClass(){
        Container nestedClass = new Container("static class",this.mainClassName +"Builder","",4);
        MyConsts.createContainerStub(nestedClass);
        //add the same attributes to this nested class
        MyConsts.makeVariableStubs(this.mandatoryAttributeTypes,nestedClass,true);
        MyConsts.makeVariableStubs(this.optionalAttributeTypes,nestedClass,true);

        // setters for the optional attributes
        int counter = 0;
        for(String s: this.optionalAttributeTypes){
            nestedClass.text += String.format(MyConsts.SetterWRetOptionSig,nestedClass.name,counter,s);
            nestedClass.text += String.format(MyConsts.ReturnSomethingStub,"this",counter,s);
            counter++;
        }

        // now do build function
        nestedClass.text += String.format(MyConsts.FunctionWReturnAndNameSig,this.mainClassName,"build");
        nestedClass.text += String.format(MyConsts.ReturnSomethingStub,"new " + this.mainClassName + "(this)");

        // now public constructor
        nestedClass.text += String.format(MyConsts.ConstructorSig,nestedClass.name);

        // add the last } needed for this nested class
        nestedClass.text += "}\n";
        return nestedClass;
    }

    /*
    order or params: main class name, mandatory variable amount,
    optional amount, variable types (mandatory + optional)
     */
    @Override
    public void parseDesignPatternParams(ArrayList<String> paramList) {
        this.mainClassName = paramList.get(0);
        this.mandatoryAttributeAmount = Integer.parseInt(paramList.get(1));
        this.optionalAttributeAmount = Integer.parseInt(paramList.get(2));
        this.mandatoryAttributeTypes = new String[this.mandatoryAttributeAmount];
        this.optionalAttributeTypes = new String[this.optionalAttributeAmount];

        // get the mandatory attribute types
        int index = 3;
        for(int i = 0; i < this.mandatoryAttributeAmount; i++){
            mandatoryAttributeTypes[i] = paramList.get(index);
            index++;
        }

        // now get the optional
        for(int i = 0; i < this.optionalAttributeAmount; i++){
            this.optionalAttributeTypes[i] = paramList.get(index);
            index++;
        }
    }

    public String getMainClassName(){ return this.mainClassName;}
    public int getMandatoryAttributeAmount(){ return this.mandatoryAttributeAmount;}
    public int getOptionalAttributeAmount(){ return this.optionalAttributeAmount;}
    public String[] getMandatoryAttributeTypes(){ return this.mandatoryAttributeTypes;}
    public String[] getOptionalAttributeTypes(){ return this.optionalAttributeTypes;}
}
