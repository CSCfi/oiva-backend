package fi.minedu.oiva.backend.template.extension;

import com.mitchellbosecke.pebble.loader.ClasspathLoader;
import com.mitchellbosecke.pebble.loader.DelegatingLoader;
import com.mitchellbosecke.pebble.loader.FileLoader;
import com.mitchellbosecke.pebble.loader.StringLoader;

import java.util.Arrays;

public class OivaTemplateLoader extends DelegatingLoader {

    public OivaTemplateLoader(final String prefix) {
        super(Arrays.asList(new FileLoader(), new ClasspathLoader()));
        setPrefix(prefix);
        setSuffix(".html");
    }
}
