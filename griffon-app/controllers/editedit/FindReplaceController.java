package editedit;

import griffon.core.artifact.GriffonController;
import griffon.inject.MVCMember;
import griffon.metadata.ArtifactProviderFor;

import javax.annotation.Nonnull;

@ArtifactProviderFor(GriffonController.class)
public class FindReplaceController {
    @MVCMember
    @Nonnull
    private EditorModel model;
    @MVCMember @Nonnull
    private EditorView view;

    public void find() {}

    public void replace() {}
}
