package actions;

import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.uiDesigner.core.AbstractLayout;
import com.intellij.util.ui.GridBag;
import consts.MyConsts;
import designPatterns.AbstractFactory;
import designPatterns.Builder;
import designPatterns.FactoryMethod;
import designPatterns.Template;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

/*
this will contain the main window of DePaCoG
will display the selections and allow user to choose
a design pattern to be created
 */
public class DePaCoGPromptWindow extends DialogWrapper {
    private String mainPrompt = String.format("***Select a Design Pattern***\n" +
        "%-15d Abstract factory\n%-15d Builder\n%-15d Factory Method\n%-15d Template %n" +
        "Enter the design pattern number you want or exit",1,2,3,4);
    private JPanel mainPanel;
    private JTextArea prompt;
    private JTextArea userSelection;
    private String selection;
    // maps the design pattern to a number
    HashMap<String,String> desPatMap;
    HashMap<String,String> desPatPrompt;

    final static Logger logger = LoggerFactory.getLogger("DePaCoGPromptWindow");

    // for the main window
    protected DePaCoGPromptWindow() {
        super(true);
        createDesignPatternHashmaps();
        mainPanel = new JPanel(new GridBagLayout());
        prompt = new JTextArea(mainPrompt);
        prompt.setEditable(false);
        userSelection = new JTextArea();
        init();
        setTitle("Main Window");
    }

    /*
    will ask the user to input all the necessary
    information to create the design pattern. Info
    will be taken in as a cvs and parsed out
     */
    protected DePaCoGPromptWindow(String designPattern){
        super(true);
        createDesignPatternHashmaps();
        mainPanel = new JPanel(new GridBagLayout());
        prompt = new JTextArea(MyConsts.PluginInstructions + desPatPrompt.get(designPattern));
        prompt.setEditable(false);
        userSelection = new JTextArea();
        init();
        setTitle("Prompt Window for: " + desPatMap.get(designPattern));
    }


    /*
    set the dimensions and items on the main window
     */
    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        GridBag gridBag = new GridBag();
        gridBag.setDefaultInsets(0,0, AbstractLayout.DEFAULT_VGAP,AbstractLayout.DEFAULT_HGAP);
        gridBag.setDefaultWeightX(2);
        gridBag.setDefaultPaddingX(20);
        gridBag.setDefaultPaddingY(20);
        gridBag.setDefaultFill(GridBagConstraints.HORIZONTAL);

        mainPanel.setPreferredSize(new Dimension(300,300));
        mainPanel.add(prompt,gridBag.nextLine().next().weightx(.03));
        mainPanel.add(userSelection,gridBag.nextLine().next().weightx(.03));

        return mainPanel;
    }

    @Override
    protected void doOKAction() {
        super.doOKAction();
        selection = userSelection.getText();
    }

    public String getSelection(){return this.selection;}

    private void createDesignPatternHashmaps(){
        this.desPatMap = new HashMap<>();
        this.desPatPrompt = new HashMap<>();
        desPatMap.put("1","Abstract Factory");
        desPatMap.put("2","Builder");
        desPatMap.put("3","Factory Method");
        desPatMap.put("4","Template");

        desPatPrompt.put("1",MyConsts.PluginAbstractFactoryInstructions);
        desPatPrompt.put("2",MyConsts.PluginBuilderInstructions);
        desPatPrompt.put("3",MyConsts.PluginFactoryInstructions);
        desPatPrompt.put("4",MyConsts.PluginTemplateInstructions);
    }

    public boolean isValidSelection(String selection){
        // checks that the selected value is within range
        if(Integer.parseInt(selection) > 4 || Integer.parseInt(selection) < 1){
            logger.warn("Invalid option **{}** was selected",selection);
            return false;
        }
        return true;
    }

    /*
    will take in the user input and create the selected design pattern
    1 abstract factory          2 builder   3 factory method    8 template

    path is passed in to know where to create the files
     */
    protected void createSelectedDesignPattern(String selection,ArrayList<String> parameters,String path){
        try{
            switch(selection){
                case "1":
                    new AbstractFactory(parameters,path).createAbstractFactory();
                    break;
                case "2":
                    new Builder(parameters,path).createBuilder();
                    break;
                case "3":
                    new FactoryMethod(parameters,path).createFactoryMethod();
                    break;
                case "4":
                    new Template(parameters,path).createTemplate();
                    break;
                default:
                    logger.warn("Selected number {} yields a design pattern in config set to true but not in switch statement",selection);
            }
        }catch(Exception e){
            logger.error("***Unable to create {} design pattern***",desPatMap.get(selection));
        }
    }
}
