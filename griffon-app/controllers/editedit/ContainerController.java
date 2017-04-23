package editedit;

import griffon.core.artifact.GriffonController;
import griffon.inject.MVCMember;
import griffon.metadata.ArtifactProviderFor;
import griffon.transform.Threading;
import griffon.util.CollectionUtils;
import org.codehaus.griffon.runtime.core.artifact.AbstractGriffonController;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;

import static griffon.util.GriffonNameUtils.isBlank;

@ArtifactProviderFor(GriffonController.class)
public class ContainerController extends AbstractGriffonController {
    @MVCMember @Nonnull
    private ContainerModel model;
    @MVCMember @Nonnull
    private ContainerView view;

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

    public void autoSave() {
        EditorController controller = resolveEditorController();
        if (controller != null) {
            controller.autoSaveFile();
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

    @Nullable
    private EditorController resolveEditorController() {
        if (!isBlank(model.getMvcIdentifier())) {
            return getApplication().getMvcGroupManager()
                .findController(model.getMvcIdentifier(), EditorController.class);
        }
        return null;
    }
}