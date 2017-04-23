package editedit;

import griffon.core.artifact.GriffonController;
import griffon.inject.MVCMember;
import griffon.metadata.ArtifactProviderFor;
import org.codehaus.griffon.runtime.core.artifact.AbstractGriffonController;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import static editedit.ConfigHolder.getProperty;
import static org.apache.commons.io.FileUtils.readFileToString;
import static org.apache.commons.io.FileUtils.writeStringToFile;

@ArtifactProviderFor(GriffonController.class)
public class EditorController extends AbstractGriffonController {
    @MVCMember @Nonnull
    private EditorModel model;
    @MVCMember @Nonnull
    private EditorView view;

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

    public void autoSaveFile() {
        int msec = Integer.parseInt(ConfigHolder.getProperty("autoSave"))*60000;

        Timer t = new Timer();
        t.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    writeStringToFile(model.getDocument().getFile(), view.getEditor().getText());
                    runInsideUIAsync(() -> model.getDocument().setContents(view.getEditor().getText()));
                } catch (IOException e) {
                    getLog().warn("Can't save file", e);
                }
            }
        }, 0, msec);
    }

    public void closeFile() {
        destroyMVCGroup(getMvcGroup().getMvcId());
    }
}