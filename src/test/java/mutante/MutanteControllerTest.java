package mutante;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.util.ArrayList;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertEquals;
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
import org.junit.Before;

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

    @Before
    public void cleanDatabase() {

        String url = "http://localhost:" + port + "/deleteall";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<>("{}", headers);
        ResponseEntity response = this.restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

    }

    /**
     * Estos casos de prueba son los mismos que se prueban en los tests
     * unitarios de la clase mutante, pero ahora usando el servidor para hacer
     * las consultas.
     */
    @Test
    public void runAllCases() throws Exception {
        MutanteTest mt = new MutanteTest();

        runCases(mt.getAllCases());
    }

    private void runCases(ArrayList<TestCase> cases) throws Exception {
        String url = "http://localhost:" + port + "/mutant";

        // set headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        for (TestCase t : cases) {

            String dnaAsJson = getDNAAsJson(t.dna);
            if (dnaAsJson.length() > 100) {
                log.info("ADN (primeros 100 chars): " + dnaAsJson.substring(0, 100));
            } else {
                log.info("ADN: " + dnaAsJson);
            }
            HttpEntity<String> entity = new HttpEntity<>(dnaAsJson, headers);

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
            "~asldkjf qer sdfñ asjdf ksadfñjsdaf",};

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

    @Test
    public void testStats() throws Exception {
        Stats s = this.restTemplate.getForObject("http://localhost:" + port + "/stats", Stats.class);
        assertEquals(s.getCount_human_dna(), 0);
        assertEquals(s.getCount_mutant_dna(), 0);
        assertEquals(s.getRatio(), 0.0, 1e-5);
    }

    @Test
    public void testStatsAfterQuery() throws Exception {
        log.info("Test Stats after query");
        cleanDatabase();
        Stats s;
        s = this.restTemplate.getForObject("http://localhost:" + port + "/stats", Stats.class);
        assertEquals(0, s.getCount_human_dna());
        assertEquals(0, s.getCount_mutant_dna());
        assertEquals(0.0, s.getRatio(), 1e-5);

        MutanteTest mt = new MutanteTest();
        
        // Correr todos los casos de mutantes
        log.info("Test: Mutantes");
        runCases(mt.getTrueCases());
        s = this.restTemplate.getForObject("http://localhost:" + port + "/stats", Stats.class);
        assertEquals(mt.getTrueCases().size(), s.getCount_mutant_dna());
        assertEquals(0, s.getCount_human_dna());

        // Correr todos los casos de humanos
        log.info("Test: Humanos");
        runCases(mt.getFalseCases());
        s = this.restTemplate.getForObject("http://localhost:" + port + "/stats", Stats.class);
        assertEquals(mt.getTrueCases().size(), s.getCount_mutant_dna());
        assertEquals(mt.getFalseCases().size(), s.getCount_human_dna());
    }

    @Test
    public void testCount() throws Exception {
        String count = this.restTemplate.getForObject("http://localhost:" + port + "/count", String.class);
        assertEquals(Integer.parseInt(count), 0);
    }

    @Test
    public void testLoaderIO() throws Exception {
        String result = this.restTemplate.getForObject("http://localhost:" + port + "/loaderio-e75603c0b79140f14a285d9ab85e4518", String.class);
        assertEquals("loaderio-e75603c0b79140f14a285d9ab85e4518", result);
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
