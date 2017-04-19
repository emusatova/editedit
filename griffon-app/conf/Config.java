import griffon.util.AbstractMapResourceBundle;

import javax.annotation.Nonnull;
import java.util.Map;

import static griffon.util.CollectionUtils.map;
import static java.util.Arrays.asList;

public class Config extends AbstractMapResourceBundle {
    @Override
    protected void initialize(@Nonnull Map<String, Object> entries) {
        map(entries)
            .e("application", map()
                    .e("title", "EditEdit")
                    .e("startupGroups", asList("container"))
                    .e("autoShutdown", true)
            )
            .e("mvcGroups", map()
                    .e("container", map()
                            .e("model", "editedit.ContainerModel")
                            .e("view", "editedit.ContainerView")
                            .e("controller", "editedit.ContainerController")
                    )
                    .e("editedit", map()
                            .e("model", "editedit.EditorModel")
                            .e("view", "editedit.EditorView")
                            .e("controller", "editedit.EditorController")
                    )
            );
    }
}