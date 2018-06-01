package mutante;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import org.springframework.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import static org.junit.Assert.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class MutanteControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private final Logger log = LoggerFactory.getLogger(this.getClass());
    ObjectMapper mapper;

    public MutanteControllerTest() {
        mapper = new ObjectMapper();
    }

    /**
     * Estos casos de prueba son los mismos que se prueban en los tests
     * unitarios de la clase mutante, pero ahora usando el servidor para hacer
     * las consultas.
     */
    @Test
    public void testCases() throws Exception {

        MutanteTest mt = new MutanteTest();
        String url = "http://localhost:" + port + "/mutant";

        // set headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        for (TestCase t : mt.getAllCases()) {

            log.debug("dna as json is " + getDNAAsJson(t.dna));
            HttpEntity<String> entity = new HttpEntity<>(getDNAAsJson(t.dna), headers);

            ResponseEntity response = this.restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

            if (t.expectedResult) {
                assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));
            } else {
                assertThat(response.getStatusCode(), equalTo(HttpStatus.FORBIDDEN));
            }
        }
    }

    /**
     * Estos casos de prueba prueban entradas invalidas.
     */
    @Test
    public void testMalformedInputs() throws Exception {

        String url = "http://localhost:" + port + "/mutant";

        String[] payloads = {
            "{}",
            "{\"dna\" : []}",
            "{\"dna\" : [\"ABCD\",\"FGH\"]}",
            "{\"dna\" : [\"cccc\",\"aaaa\",\"tttt\",\"gggg\"]}",
            "~asldkjf qer sdfñ asjdf ksadfñjsdaf",
        };

        // set headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        for (String payload : payloads) {

            HttpEntity<String> entity = new HttpEntity<>(payload, headers);
            log.error("Payload is " + payload);
            ResponseEntity response = this.restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

            assertThat(response.getStatusCode(), equalTo(HttpStatus.FORBIDDEN));
        }
    }

    /**
     * Formatea como JSON una secuencia de ADN.
     */
    private String getDNAAsJson(String[] dna) throws JsonProcessingException {
        StringBuilder sb = new StringBuilder();

        if (dna == null) {
            sb.append("{}");
        } else {
            sb.append("{ \"dna\" : ");
            sb.append(mapper.writeValueAsString(dna));
            sb.append("}");
        }
        return sb.toString();
    }
}
