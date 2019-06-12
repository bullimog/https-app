
package app.config;
import app.connectors.HttpsConnector;
import app.connectors.HttpsConnectorImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//Defines Beans, which will be found and autowired.

@Configuration
//@SpringBootApplication is in higher-level package, so this bean will be scanned automatically.

public class ControllerDependencies{ //} implements WebMvcConfigurer {
    @Bean
    public HttpsConnector getHttpConnection() {
        String certificatesTrustStorePath = "client-truststore.jks";
        System.setProperty("javax.net.ssl.trustStore", certificatesTrustStorePath);
        System.setProperty("javax.net.ssl.trustStorePassword", "password");
        return new HttpsConnectorImpl();
    }
}