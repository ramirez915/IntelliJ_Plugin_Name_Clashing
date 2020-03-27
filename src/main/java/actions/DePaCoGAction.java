package actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;

public class DePaCoGAction extends com.intellij.openapi.actionSystem.AnAction {
    final static Logger logger = LoggerFactory.getLogger("DePaCoGAction");

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
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
                }
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
}
