package designPatterns;

import consts.MyConsts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

/*
the design pattern revolves around one idea (interface) that will be
implemented by many different concrete classes. Then the concrete classes
will have their own factories that will create instances of those
concrete classes that implement the interface (idea). All these factories will
be together in ONE factory (using an interface that will produce the given concrete object from
the correct factory)
 */
public class AbstractFactory extends DesignPatternObj {
    private String mainInterfaceName;
    private int totalFuncs;
    private String abstFactoryName;
    private String abstFactoryMethodName;
    final static Logger logger = LoggerFactory.getLogger("Abstract Factory");

    // get the information for the super class and sub classes
    public AbstractFactory(ArrayList<String> parameters,String path){
        // order of params: main interface, function amount, amount of subclasses and names of subclasses
        this.desPatParams = parameters;
        parseDesignPatternParams(this.desPatParams);
        // make the directory
        this.path = path + "/" + this.mainInterfaceName;
        MyConsts.createDir(this.path);

        // to have a name for the create function and abstract factory
        this.abstFactoryName = this.mainInterfaceName+"AbstractFactory";
        this.abstFactoryMethodName = "create"+ this.mainInterfaceName;
        this.subClassList = new ArrayList<>();
        logger.info("Abstract factory parameters acquired successfully");
    }

    public void createAbstractFactory(){
        // create interface for the product we are going to mass produce
        Container mainInterface = new Container("interface", this.mainInterfaceName, "",totalFuncs);
        mainInterface.setDirName(this.path);
        MyConsts.createContainerStub(mainInterface);

        // adding the function stubs
        for(int i = 0; i < mainInterface.functionAmount; i++){
            mainInterface.text += String.format(MyConsts.InterfaceFunctionGenericSig,i);
        }

        // ready to create main interface file
        MyConsts.createFile(mainInterface);
        //now create the subclasses
        createFactorySubclasses();
        //create factory interface
        createFactoryInterface();
        // create the factories for the concrete subclasses classes
        createFactoryForConcreteClasses();
        //create the main factory that will create the products from the different factories
        createInterfaceFactoryMaker();
        logger.info("Abstract factory for {} created successfully",this.mainInterfaceName);
    }

    /*
    creates the sub classes (products) created by factory and the interface that will be implemented by all factories
     */
    private void createFactorySubclasses(){
        String name = "";
        // 2 because of the position of the names starts at 2 in the design pattern params list
        for(int i = 2; i < this.desPatParams.size(); i++){
            ArrayList<String> subClassParams = new ArrayList<>();
            name = this.desPatParams.get(i);
            Container subClass = new Container("regular class",name,this.mainInterfaceName,this.totalFuncs);
            subClass.setImplement(true);
            subClass.setDirName(this.path);

            MyConsts.createContainerStub(subClass);
            // will need the override because implementing an interface
            for(int j = 0; j < this.totalFuncs; j++){
                subClass.text += MyConsts.OverrideRegularFunctionGenericSig;
                subClassParams.add(String.valueOf(j));
            }
            // now finalize this subclass
            subClassParams.add(subClass.name);
            subClass.text += MyConsts.ConstructorSig;
            subClass.formatTextTest(subClassParams);
            this.subClassList.add(subClass);

            MyConsts.createFile(subClass);
        }
    }

    /*
    creates the factory that returns an instance of the main interface
     */
    private void createFactoryInterface(){
        Container factoryInterface = new Container("interface",this.abstFactoryName,"",1);
        ArrayList<String> params = new ArrayList<>();
        params.add(this.mainInterfaceName);
        params.add(this.abstFactoryMethodName);
        factoryInterface.setDirName(this.path);

        //now make the text for this interface and create the file
        MyConsts.createContainerStub(factoryInterface);
        factoryInterface.text += MyConsts.InterfaceFuncWReturnAndNameSig;
        factoryInterface.formatTextTest(params);
        MyConsts.createFile(factoryInterface);
    }

    /*
    create factory interfaces based on the concrete classes made
    and implement the factory interface
     */
    private void createFactoryForConcreteClasses(){
        // 2 because of the position of the names starts at 2 in the design pattern params list
        for(int i = 2; i < this.desPatParams.size(); i++){
            Container subFactory = new Container("regular class",this.desPatParams.get(i) + "Factory",this.abstFactoryName,1);
            subFactory.setImplement(true);
            subFactory.setDirName(this.path);

            MyConsts.createContainerStub(subFactory);
            subFactory.text += String.format(MyConsts.OverrideFunctionWReturnAndNameSig,this.mainInterfaceName,this.abstFactoryMethodName);
            subFactory.text += String.format(MyConsts.ReturnNewStub,this.desPatParams.get(i));
            // now make constructor
            subFactory.text += String.format(MyConsts.ConstructorSig,subFactory.name);
            MyConsts.createFile(subFactory);
        }
    }

    /*
    creates the factory that makes the abstract factories
    that will create an instance of a product of type main interface
    will only have 1 public static method that will create a product
    from the given factory
     */
    private void createInterfaceFactoryMaker(){
        Container mainFactory = new Container("regular class",this.mainInterfaceName+"Factory","",1);
        // implements and extends are to be set to false since this is going to be a regular class
        mainFactory.setDirName(this.path);

        MyConsts.createContainerStub(mainFactory);
        //create function stub
        mainFactory.text += "\t"+ MyConsts.PublicStatic + this.mainInterfaceName + " " + this.abstFactoryMethodName;
        mainFactory.text += String.format("(%s %s){\n\treturn %s.%s();\n\t}\n",this.abstFactoryName,"af","af",this.abstFactoryMethodName);
        MyConsts.createFile(mainFactory);
    }

    @Override
    public void parseDesignPatternParams(ArrayList<String> paramList) {
        this.mainInterfaceName = paramList.get(0);
        this.totalFuncs = Integer.parseInt(paramList.get(1));
    }

    public String getMainInterfaceName(){ return this.mainInterfaceName;}
    public int getTotalFunctions(){ return this.totalFuncs;}
}