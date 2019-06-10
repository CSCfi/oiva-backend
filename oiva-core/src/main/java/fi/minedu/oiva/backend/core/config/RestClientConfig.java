package fi.minedu.oiva.backend.core.config;

import org.glassfish.jersey.client.rx.RxClient;
import org.glassfish.jersey.client.rx.java8.RxCompletionStage;
import org.glassfish.jersey.client.rx.java8.RxCompletionStageInvoker;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestClientConfig {

    @Bean
    public RestTemplate getRestTemplate() {
        return new RestTemplate();
    }

    @Bean
    public RxClient<RxCompletionStageInvoker> getJerseyRxClient() {
        final RxClient<RxCompletionStageInvoker> client = RxCompletionStage.newClient();
        client.register(JacksonFeature.class);
        return client;
    }
}
