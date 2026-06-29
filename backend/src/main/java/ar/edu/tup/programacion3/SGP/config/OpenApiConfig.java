package ar.edu.tup.programacion3.SGP.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;

/**
 * Configuracion de OpenAPI para generar la documentacion de la API.
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(
                        new Info()
                                .title("API de Gestión de Pedidos")
                                .description(
                                        "Esta API permite administrar productos, categorías, usuarios y pedidos.")
                                .version("1.0.0-beta.1")
                                .contact(
                                        new Contact()
                                                .name("Cristian Lahoz")
                                                .email("m415xs@gmail.com")
                                                .url("https://github.com/m415x"))
                                .license(
                                        new License()
                                                .name("GPL-3.0")
                                                .url("https://www.gnu.org/licenses/gpl-3.0.html")));
    }
}
