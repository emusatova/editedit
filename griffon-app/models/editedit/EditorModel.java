package editedit;

import griffon.core.artifact.GriffonModel;
import griffon.inject.MVCMember;
import griffon.metadata.ArtifactProviderFor;
import org.codehaus.griffon.runtime.core.artifact.AbstractGriffonModel;

@ArtifactProviderFor(GriffonModel.class)
public class EditorModel extends AbstractGriffonModel {
    @MVCMember
    private Document document;

    public Document getDocument() {
        return document;
    }

    public void setDocument(Document document) {
        firePropertyChange("document", this.document, this.document = document);
    }
}