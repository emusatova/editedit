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

    public static String searchText = null;
    private static Boolean isSearchOpen = false;

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
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                openFindWindow();
            }
        });
    }

    public void openFindWindow() {
        EditorController controller = resolveEditorController();

        searchText = controller.getFileText();

        if (searchText == null || searchText.isEmpty()) {
            return;
        }

        if (isSearchOpen) {
            return;
        } else {
            isSearchOpen = true;
        }

        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/editedit/findreplace.fxml"));
            Parent root = (Parent) fxmlLoader.load();
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.DECORATED);
            stage.setTitle("ABC");
            stage.setScene(new Scene(root));
            stage.show();
            stage.setOnCloseRequest(event -> {
                isSearchOpen = false;
            });
        }
        catch (IOException e) {
            getLog().warn("Can't get file text", e);
        }
    }

    @FXML
    protected void handleFindButton(ActionEvent event) {
        findNext();
    }

    public void findNext() {
        System.out.println("This is find next");
    }

    @Nullable
    private EditorController resolveEditorController() {
        if (!isBlank(model.getMvcIdentifier())) {
            return getApplication().getMvcGroupManager()
                    .findController(model.getMvcIdentifier(), EditorController.class);
        }
        return null;
    }
}