package editedit;

import griffon.core.artifact.GriffonController;
import griffon.inject.MVCMember;
import griffon.metadata.ArtifactProviderFor;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import org.codehaus.griffon.runtime.core.artifact.AbstractGriffonController;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.Map;

import static org.apache.commons.io.FileUtils.readFileToString;
import static org.apache.commons.io.FileUtils.writeStringToFile;

@ArtifactProviderFor(GriffonController.class)
public class EditorController extends AbstractGriffonController {
    @MVCMember @Nonnull
    private EditorModel model;
    @MVCMember @Nonnull
    private EditorView view;

    @FXML
    protected TextArea editor;

    @Override
    public void mvcGroupInit(@Nonnull Map<String, Object> args) {
        model.setDocument((Document) args.get("document"));
        runOutsideUI(() -> {
            try {
                final String content = readFileToString(model.getDocument().getFile());
                runInsideUIAsync(() -> model.getDocument().setContents(content));
            } catch (IOException e) {
                getLog().warn("Can't open file", e);
            }
        });
    }

    public void saveFile() {
        try {
            writeStringToFile(model.getDocument().getFile(), view.getEditor().getText());
            runInsideUIAsync(() -> model.getDocument().setContents(view.getEditor().getText()));
        } catch (IOException e) {
            getLog().warn("Can't save file", e);
        }
    }

    public void closeFile() {
        destroyMVCGroup(getMvcGroup().getMvcId());
    }

    public String getFileText() {
        try {
            return view.getEditor().getText();
        } catch (Exception e) {
            getLog().warn("Can't get file text", e);
        }
        return "";
    }

    public void selectText(int from, int to) {
        try {
            editor.selectRange(from, to);
        }
        catch (Exception e) {
            getLog().warn("Error selecting text", e);
        }
    }
}