package editedit;

import griffon.core.artifact.GriffonController;
import griffon.inject.MVCMember;
import griffon.metadata.ArtifactProviderFor;
import griffon.transform.Threading;
import griffon.util.CollectionUtils;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
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
    @MVCMember @Nonnull
    private ContainerModel model;
    @MVCMember @Nonnull
    private ContainerView view;

    @FXML
    private TextField findTextField;

    private static String textToFind = null;

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
        EditorController controller = resolveEditorController();
        if (controller == null) {
            return;
        }

//        Platform.runLater(new Runnable() {
//            @Override
//            public void run() {
//                openFindWindow();
//            }
//        });
        runInsideUIAsync(() -> openFindWindow());
    }

    @Nullable
    private EditorController resolveEditorController() {
        if (!isBlank(model.getMvcIdentifier())) {
            return getApplication().getMvcGroupManager()
                .findController(model.getMvcIdentifier(), EditorController.class);
        }
        return null;
    }

    public void openFindWindow() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/editedit/findreplace.fxml"));
            Parent root = (Parent) fxmlLoader.load();
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.DECORATED);
            stage.setTitle("Find & Replace");
            stage.setScene(new Scene(root));
            stage.show();
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
        replaceAll();
    }

    private void replaceAll() {
        System.out.println("This is replace all");
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
}