package fi.minedu.oiva.backend.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.Arrays;

@Configuration
public class ShowProfileInfo {
    @Autowired
    private Environment env;

    @PostConstruct
    public void verifyProductionConfigExists() throws IOException {
        System.out.println("Active: " + Arrays.toString(env.getActiveProfiles()));
        System.out.println("Default: " + Arrays.toString(env.getDefaultProfiles()));
    }
}
