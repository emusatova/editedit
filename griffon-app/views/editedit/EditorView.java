package editedit;

import griffon.core.artifact.GriffonView;
import griffon.inject.MVCMember;
import griffon.metadata.ArtifactProviderFor;
import javafx.fxml.FXML;
import javafx.scene.control.Tab;
import javafx.scene.control.TextArea;
import org.codehaus.griffon.runtime.javafx.artifact.AbstractJavaFXGriffonView;

import javax.annotation.Nonnull;
import java.util.Objects;

@ArtifactProviderFor(GriffonView.class)
public class EditorView extends AbstractJavaFXGriffonView {
    @MVCMember @Nonnull
    private EditorModel model;
    @MVCMember @Nonnull
    private ContainerView parentView;
    @MVCMember @Nonnull
    private String tabName;

    @FXML
    private TextArea editor;

    private Tab tab;

    @Override
    public void initUI() {
        tab = new Tab(tabName);
        tab.setId(getMvcGroup().getMvcId());
        tab.setContent(loadFromFXML());
        parentView.getTabGroup().getTabs().add(tab);

        editor.prefHeightProperty().bind(parentView.getTabGroup().heightProperty());
        editor.prefWidthProperty().bind(parentView.getTabGroup().widthProperty());

        model.getDocument().addPropertyChangeListener("contents", (e) -> editor.setText((String) e.getNewValue()));

        editor.textProperty().addListener((observable, oldValue, newValue) ->
            model.getDocument().setDirty(!Objects.equals(editor.getText(), model.getDocument().getContents())));
    }

    public TextArea getEditor() {
        return editor;
    }

    @Override
    public void mvcGroupDestroy() {
        runInsideUISync(() -> parentView.getTabGroup().getTabs().remove(tab));
    }
}