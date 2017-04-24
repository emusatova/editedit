package editedit;

import griffon.core.GriffonApplication;
import griffon.core.artifact.GriffonController;
import griffon.inject.MVCMember;
import griffon.metadata.ArtifactProviderFor;
import griffon.transform.Threading;
import griffon.util.CollectionUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.codehaus.griffon.runtime.core.artifact.AbstractGriffonController;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;

import static griffon.util.GriffonNameUtils.isBlank;

@ArtifactProviderFor(GriffonController.class)
public class ContainerController extends AbstractGriffonController {
    @MVCMember
    @Nonnull
    private static ContainerModel model;
    @MVCMember
    @Nonnull
    private static ContainerView view;

    private static Stage findAndReplace;

    private static int newFileNum = 1;

    @FXML
    private TextField replaceTextField;

    @FXML
    private TextField findTextField;

    private static GriffonApplication griffonApplication;

    private static String textToFind = null;

    private static String textToReplace = null;

    @Threading(Threading.Policy.SKIP)
    public void create() {
        File file = new File("new" + newFileNum++);
        String mvcIdentifier = file.getName() + "-" + System.currentTimeMillis();
        createMVC("editedit", mvcIdentifier, CollectionUtils.<String, Object>map()
                .e("document", new Document(file, file.getName()))
                .e("tabName", file.getName()));

    }

    @Threading(Threading.Policy.SKIP)
    public void open() {
        File file = view.selectFile();
        if (file != null) {
            String mvcIdentifier = file.getName() + "-" + System.currentTimeMillis();
            createMVC("editedit", mvcIdentifier, CollectionUtils.<String, Object>map()
                    .e("document", new Document(file, file.getName()))
                    .e("tabName", file.getName()));
        }
    }

    public void save() {
        EditorController controller = resolveEditorController();
        if (controller != null) {
            controller.saveFile();
        }
    }

    public void close() {
        EditorController controller = resolveEditorController();
        if (controller != null) {
            controller.closeFile();
        }
    }

    public void quit() {
        getApplication().shutdown();
    }

    public void find() {

        griffonApplication = getApplication();
        runInsideUIAsync(this::openFindWindow);
    }

    public void replace(){
        find();
    }

    @Nullable
    private EditorController resolveEditorController() {
        if (!isBlank(model.getMvcIdentifier())) {
            if (griffonApplication != null)
                return griffonApplication.getMvcGroupManager()
                        .findController(model.getMvcIdentifier(), EditorController.class);
            else
                return getApplication().getMvcGroupManager()
                        .findController(model.getMvcIdentifier(), EditorController.class);
        }
        return null;
    }

    public void openFindWindow() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/editedit/findreplace.fxml"));
            Parent root = (Parent) fxmlLoader.load();
            Stage parent = (Stage) getApplication().getWindowManager().findWindow("mainWindow");
            findAndReplace = new Stage();
            findAndReplace.initOwner(parent);
            findAndReplace.setResizable(false);
            findAndReplace.initStyle(StageStyle.DECORATED);
            findAndReplace.setTitle("Find & Replace");
            findAndReplace.setScene(new Scene(root));
            findAndReplace.show();
        } catch (IOException e) {
            getLog().warn("Can't Find & Replace", e);
        }
    }

    @FXML
    protected void handleFindButton(ActionEvent event) {
        textToFind = findTextField.getText();
        findNext();

    }

    @FXML
    protected void handleReplaceButton(ActionEvent event) {
        textToFind = findTextField.getText();
        textToReplace = replaceTextField.getText();
        replaceText();
    }

    private void replaceText() {
        EditorController controller = resolveEditorController();
        if (controller == null) {
            return;
        }
        if (textToFind == null || textToFind.isEmpty() || textToReplace == null || textToReplace.isEmpty()) {
            return;
        }

        findNext();
        controller.replaceSelectedText(textToReplace);

    }

    public void findNext() {
        if (textToFind == null || textToFind.isEmpty()) {
            return;
        }

        EditorController controller = resolveEditorController();
        if (controller == null) {
            return;
        }

        String fileText = controller.getFileText();
        if (fileText == null || fileText.isEmpty()) {
            return;
        }

        try {
            int searchPosition = controller.getCursorPosition();
            int foundPosition = fileText.indexOf(textToFind, searchPosition);
            // if not found from current position, try to find from the beginning
            if (foundPosition == -1 && searchPosition != 0) {
                foundPosition = fileText.indexOf(textToFind, 0);
            }
            if (foundPosition != -1) {
                controller.selectText(foundPosition, foundPosition + textToFind.length());
            }
        } catch (Exception e) {
            getLog().warn("Error performing Find Next", e);
        }
    }

    public void handleEnterClick(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.ENTER) {
            TextField tf = (TextField) keyEvent.getSource();
            if (tf.getId().equals("findTextField")) {
                textToFind = findTextField.getText();
                findNext();
            } else {
                textToFind = findTextField.getText();
                textToReplace = replaceTextField.getText();
                replaceText();
            }
        }
    }

    public void handleCancelButton(ActionEvent actionEvent) {
        findAndReplace.close();
    }
}