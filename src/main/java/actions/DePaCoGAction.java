package actions;

import com.intellij.ide.IdeView;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;

public class DePaCoGAction extends com.intellij.openapi.actionSystem.AnAction {
    final static Logger logger = LoggerFactory.getLogger("DePaCoGAction");
    public static Project[] proj;

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        if(createDesignPattern(e)){
            // when everything was valid and created check the file

            // get the psi elements from the project src directory
            PsiElement[] psiElements = getPsiElements(e);           // BREAK HERE
            for(PsiElement psi: psiElements){

                // the children within the next dir (in this case the first dir it finds it was car)
                // then it would show all the files within that dir
                PsiElement[] pchild = psi.getChildren();
                System.out.println(pchild.length);
            }
        }
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
    private boolean createDesignPattern(AnActionEvent e){
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
                    prompt.createSelectedDesignPattern(selectedDesPat,paramsList,path);

                    return true;
                }
            }
        }
        return false;
    }

    /*
    gets the elements from the src directory (my target directory)
     */
    private PsiElement[] getPsiElements(AnActionEvent e){
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
}
