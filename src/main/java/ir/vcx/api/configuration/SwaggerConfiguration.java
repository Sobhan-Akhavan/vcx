package ir.vcx.api.configuration;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Sobhan at 8/10/2023 - VCX
 */

@Configuration
public class SwaggerConfiguration {

    @Value("${server.version.code}")
    private String SERVER_VERSION_CODE;
    @Value("${server.version.name}")
    private String SERVER_VERSION_NAME;
    @Value("${swagger.title}")
    private String SWAGGER_TITLE;
    @Value("${swagger.host.url-list}")
    private List<String> SWAGGER_HOST_URL_LIST;
    @Value("${swagger.description}")
    private String SWAGGER_DESCRIPTION;

    @Bean
    public OpenAPI openAPIInfo() {
        return new OpenAPI()
                .components(new Components()
                        .addSecuritySchemes("Bearer", new SecurityScheme()
                                .type(SecurityScheme.Type.APIKEY)
                                .scheme("Bearer")
                                .name("Authorization")
                                .in(SecurityScheme.In.HEADER)))
                .info(new Info().title(SWAGGER_TITLE)
                        .description(SWAGGER_DESCRIPTION)
                        .version("v" + SERVER_VERSION_CODE + " - " + SERVER_VERSION_NAME)
                        .license(new License().name("Powered by VCX")))
                .servers(
                        SWAGGER_HOST_URL_LIST.stream().map(server ->
                                new Server().url(server)).collect(Collectors.toList())
                );
    }

    @Hidden
    @Controller()
    public static class Home {

        @GetMapping("/api/docs/**")
        public ModelAndView docs(ModelMap model, HttpServletRequest request) {

            String sharp = request.getServletPath().substring("/api/docs".length());
            String redirect;
            if (StringUtils.isNotEmpty(sharp) && !sharp.equals("/")) {
                redirect = "redirect:/swagger-ui/index.html" + "#" + sharp;
            } else {
                redirect = "redirect:/swagger-ui/index.html";
            }
            return new ModelAndView(redirect, model);

        }

        @GetMapping("/api/swagger-ui.html/**")
        public ModelAndView help(ModelMap model, HttpServletRequest request) {

            String sharp = request.getServletPath().substring("/api/swagger-ui.html".length());
            String redirect;
            if (StringUtils.isNotEmpty(sharp) && !sharp.equals("/")) {
                redirect = "redirect:/swagger-ui/index.html" + "#" + sharp;
            } else {
                redirect = "redirect:/swagger-ui/index.html";
            }
            return new ModelAndView(redirect, model);

        }

    }
}