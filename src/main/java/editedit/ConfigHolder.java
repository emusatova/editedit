package editedit;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public final class ConfigHolder {
    private static Properties props = null;

    private static void init() throws IOException
    {
        props = new Properties();
        props.load(new FileInputStream(new File("config.properties")));
    }

    public static String getProperty(String key) {
        try {
            if (props == null) {
                init();
            }
            return props.getProperty(key, null);
        } catch (Exception e) {
            //TODO log the problem?
            System.out.println(e.getMessage());
        }

        return null;
    }
}
