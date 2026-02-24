package apptest.openapi;

import tools.jackson.dataformat.yaml.YAMLMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ActiveProfiles;
import se.sundsvall.garbage.Application;

import java.io.IOException;
import java.nio.file.Path;

import static java.nio.file.Files.writeString;
import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static net.javacrumbs.jsonunit.core.Option.IGNORING_ARRAY_ORDER;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.web.util.UriComponentsBuilder.fromPath;
import static se.sundsvall.dept44.util.ResourceUtils.asString;

@SpringBootTest(
        webEnvironment = RANDOM_PORT,
        classes = Application.class,
        properties = {
                "spring.main.banner-mode=off",
                "logging.level.se.sundsvall.dept44.payload=OFF"
        }
)
@ActiveProfiles("it")
@AutoConfigureWebTestClient
@AutoConfigureTestRestTemplate
class OpenApiSpecificationIT {

    private static final YAMLMapper YAML_MAPPER = new YAMLMapper();

    @Value("${openapi.name}")
    private String openApiName;

    @Value("${openapi.version}")
    private String openApiVersion;

    @Value("classpath:/openapi.yml")
    private Resource openApiResource;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void compareOpenApiSpecifications() throws IOException {
        final var existingOpenApiSpecification = asString(openApiResource);
        final var currentOpenApiSpecification = getCurrentOpenApiSpecification();

        writeString(Path.of("target/openapi.yml"), currentOpenApiSpecification);

        assertThatJson(toJson(currentOpenApiSpecification))
                .withOptions(IGNORING_ARRAY_ORDER)
                .whenIgnoringPaths("servers")
                .isEqualTo(toJson(existingOpenApiSpecification));
    }

    /**
     * Fetches and returns the current OpenAPI specification in YAML format.
     *
     * @return the current OpenAPI specification
     */
    private String getCurrentOpenApiSpecification() {
        final var uri = fromPath("/api-docs.yaml")
                .buildAndExpand(openApiName, openApiVersion)
                .toUri();

        return restTemplate.getForObject(uri, String.class);
    }

    /**
     * Attempts to convert the given YAML (no YAML-check...) to JSON.
     *
     * @param yaml the YAML to convert
     * @return a JSON string
     */
    private String toJson(final String yaml) {
        return YAML_MAPPER.readTree(yaml).toString();
    }

}
