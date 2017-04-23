package editedit;

import griffon.core.artifact.GriffonModel;
import griffon.metadata.ArtifactProviderFor;
import org.codehaus.griffon.runtime.core.artifact.AbstractGriffonModel;

@ArtifactProviderFor(GriffonModel.class)
public class ContainerModel extends AbstractGriffonModel {
    private static final String MVC_IDENTIFIER = "mvcIdentifier";
    private final DocumentModel documentModel = new DocumentModel();
    private String mvcIdentifier;

    public ContainerModel() {
        addPropertyChangeListener(MVC_IDENTIFIER, (e) -> {
            Document document = null;
            if (e.getNewValue() != null) {
                EditorModel model = getApplication().getMvcGroupManager().getModel(mvcIdentifier, EditorModel.class);
                document = model.getDocument();
            } else {
                document = new Document();
            }
            documentModel.setDocument(document);
        });
    }

    public String getMvcIdentifier() {
        return mvcIdentifier;
    }

    public void setMvcIdentifier(String mvcIdentifier) {
        firePropertyChange(MVC_IDENTIFIER, this.mvcIdentifier, this.mvcIdentifier = mvcIdentifier);
    }

    public DocumentModel getDocumentModel() {
        return documentModel;
    }
}