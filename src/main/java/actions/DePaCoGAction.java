package actions;

import com.intellij.ide.IdeView;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import designPatterns.Container;
import designPatterns.DesignPatternObj;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Set;

public class DePaCoGAction extends com.intellij.openapi.actionSystem.AnAction {
    final static Logger logger = LoggerFactory.getLogger("DePaCoGAction");

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        createDesignPattern(e);
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        super.update(e);
    }

    private ArrayList<String> createParameterList(String inputs){
        String[] params = inputs.split(",");
        return new ArrayList<>(Arrays.asList(params));
    }

    /*
    create design pattern files, if successful
    check files for clashing
     */
    private void createDesignPattern(AnActionEvent e){
        DePaCoGPromptWindow mainWindow = new DePaCoGPromptWindow();
        String selectedDesPat = "";
        // when user makes selection
        if(mainWindow.showAndGet()){
            selectedDesPat = mainWindow.getSelection();

            //if valid continue with next prompts else dont
            if(mainWindow.isValidSelection(selectedDesPat)){
                // choose directory
                FileChooserDescriptor fc = new FileChooserDescriptor(false,true,false,false,false,false);
                fc.setTitle("Choose Directory");
                fc.setDescription("Choose the directory you want to store files");
                String path = FileChooser.chooseFile(fc,e.getProject(),null).getCanonicalPath();
                logger.info("Chosen path was {}",path);

                //open window to get info
                DePaCoGPromptWindow prompt = new DePaCoGPromptWindow(selectedDesPat);
                if(prompt.showAndGet()){
                    ArrayList<String> paramsList = createParameterList(prompt.getSelection());

                    //create the selected design pattern
                    DesignPatternObj designPatObj = prompt.createSelectedDesignPattern(selectedDesPat,paramsList,path);
                    if(designPatObj == null){
                        logger.error("Design Pattern Object null");
                        return;
                    }

                    // check for clashes after file has been created
                    ArrayList<String> filteredParamsList = getImportantDesPatParams(designPatObj,selectedDesPat);

                    // != means there are clashes
                    // put in log as error and display a message
                    Hashtable<String,ArrayList<String>> clashHashTable = iterateThroughPsiElements(e,filteredParamsList);
                    if(clashHashTable != null){                            //BREAKKKKK
                        String errMsg = createNameClashErr(clashHashTable);
                        logger.error(errMsg);
                        Messages.showMessageDialog(e.getProject(),errMsg,"ERROR: Name Clash",Messages.getErrorIcon());
                    }
                    else{
                        logger.info("***No name clash with {}***",designPatObj.getDesPatParams().get(0));
                    }
                }
            }
        }
    }

    /*
    gets the elements from the src directory (my target directory)
    must have clicked on the src directory before using the plugin
     */
    private PsiElement[] getPsiElementsFromSrc(AnActionEvent e){
        // gets the contents from the ide
        IdeView view = e.getData(LangDataKeys.IDE_VIEW);

        // gets all the directories from the view (in this case it would only be the project dir from my tests)
        PsiDirectory[] dir = view.getDirectories();
        for(PsiDirectory d: dir){
            if(d.getName().compareTo("src") == 0){
                return d.getChildren();
            }
        }
        return null;
    }

    /*
    iterates through all the projects PsiElements (project files)

    returns the ArrayList from the nameClashes()
    null if nothing (we're good)
    !null if something was clashing
     */
    public Hashtable<String,ArrayList<String>> iterateThroughPsiElements(AnActionEvent e, ArrayList<String> filteredList){
        // will store the clashes and their locations
        // make name key and location value
        Hashtable<String,ArrayList<String>> table1 = new Hashtable<>();

        // get the psi elements from the project src directory
        // in other words, get the directories located in src
        PsiElement[] psiElements = getPsiElementsFromSrc(e);           // BREAK HERE
        if(psiElements == null){
            return null;
        }
        for(PsiElement psi: psiElements){
            // the children within the next dir (in this case the first dir it finds it was car)
            // then it would show all the files within that dir
            PsiElement[] subDirs = psi.getChildren();
            for(PsiElement fileFromDir: subDirs){
                String fileText = fileFromDir.getText();

                //check for clashes
                ArrayList<String> nameClashList = nameClashes(fileText,filteredList);
                if(nameClashList != null){
                    for(String s : nameClashList){
                        // if the key does not exist yet create array
                        if(table1.get(s) == null){
                            table1.put(s,new ArrayList<>());
                        }
                        table1.get(s).add(fileFromDir.getContainingFile().getName() + " from " + fileFromDir.getContainingFile().getParent().getName());
                    }
                }
            }
        }

        // check if table is populated (with clashes)
        if(!table1.isEmpty()){
            return table1;
        }
        return null;
    }

    /*
    filters out the important names from the input parameters
    according to the design pattern. Builder does not use subclasses
    so it was excluded from using subclasses
     */
    private ArrayList<String> getImportantDesPatParams(DesignPatternObj desPatObj,String selectedDesPat){
        ArrayList<String> filteredList = new ArrayList<>();
        filteredList.add(desPatObj.getDesPatParams().get(0));
        if(selectedDesPat.compareTo("2") != 0){
            for(Container c: desPatObj.getSubClassList()){
                filteredList.add(c.name);
            }
        }
        return filteredList;
    }

    /*
    checks the current file text for same names
    uses the important names from the user input for the new files
    using only the important names, defaults can be assumed that will be changed

    returns null if nothing
    else a populated ArrayList with the names that clash
     */
    public ArrayList<String> nameClashes(String fileString,ArrayList<String> filteredList){
        ArrayList<String> repeats = new ArrayList<>();

        for(String fromList: filteredList){    //BREAK HERE
//------------------------------------------------------------------------------------------------------------
             //THIS WORKS PERFECTLY ONLY ISSUE IS THAT IT PICKS UP COMMENTS....
//            //check for repeats from the current file
//            for(String s:fileString.split(" ")){
//                if(s.compareTo(fromList) == 0){
//                    repeats.add(fromList);
//                }
//            }
//--------------------------------------------------------------------------------------------------------

            // FIX FOR COMMENT ISSUE
            //------------------------------------------------------------------------
            String[] fileStrings = fileString.split(" ");
            int pos = 0;
            while(pos != fileStrings.length - 1){
                if(fileStrings[pos].compareTo(fromList) == 0){
                    repeats.add(fromList);
                }

                // a comment was found so ignore and go to the end of it
                else if(fileStrings[pos].compareTo("//") == 0){
                    pos = getToEndOfComment(pos,fileStrings);
                    if(pos == -1){
                        break;
                    }
                }
                pos++;
            }
            //---------------------------------------------------------------------------
        }
        if(repeats.size() != 0)
            return repeats;

        return null;
    }

    /*
    if a comment was found then go up to the end of it to start checking again
     */
    private int getToEndOfComment(int pos,String[] fileStrings){
        while(pos != fileStrings.length -1){
            if(fileStrings[pos].compareTo("\n") == 0){
                return pos;
            }
            pos++;
        }
        return -1;
    }

    /*
    creates the error message according to what is given from the hashtable
     */
    private String createNameClashErr(Hashtable<String,ArrayList<String>> clashHashTable){
        String errMsg = "The following names are repeated in the following files based on selected design pattern and parameters.\n" +
                "This may lead to other repeated files, please look at those along with default names!\n" +
                "***This was added to the log***\n";

        // get the keys
        Set<String> names = clashHashTable.keySet();
        for(String keyName: names){
            for(String location: clashHashTable.get(keyName)){
                errMsg += keyName + " located in " + location + "\n";
            }
        }

        return errMsg;
    }
}
