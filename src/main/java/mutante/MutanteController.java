package mutante;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.JacksonJsonParser;
import org.springframework.boot.json.JsonParseException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MutanteController {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private MutanteRepository mutanteRepository;

    /**
     * Servicio principal. Determina si el ADN que recibe corresponde al de un 
     * humano o un mutante. 
     * Recibe un objeto JSON en el body con el ADN en el siguiente formato: 
     * 
     *  {
     *      "dna" : ["ATGCGA","CAGTGC","TTATGT","AGAAGG","CCCCTA","TCACTG"]
     *  }
     * 
     * @note el verbo es POST
     * @param bodyContent el contenido del cuerpo del request
     * @return Status code 200-OK si el ADN corresponde al de un mutante o
     * 403-FORBIDDEN en caso contrario
     */
    @CrossOrigin
    @RequestMapping(value = "/mutant", method = POST)
    public ResponseEntity isMutant(@RequestBody String bodyContent) {

        try {
            log.error("Getting the request body");
            String[] dnaArray = getDNAFromRequestBody(bodyContent);
            log.error("Dna size: " + dnaArray.length);
            
            // Calcular si es un mutante y salvar el resultado en la base de datos
            Mutante m = new Mutante();
            List<Mutante> inMemory = mutanteRepository.findByDna(dnaArray);
            boolean isMutant;

            if (!inMemory.isEmpty()) {
                isMutant = inMemory.get(0).isIsMutantValue();
            } else {
                isMutant = m.isMutant(dnaArray);
                mutanteRepository.save(m);
            }

            // Si es un mutante, devolver 200-OK
            if (isMutant) {
                log.debug("Mutante: true");
                return ResponseEntity.ok(null);
            }

        } catch (JsonParseException ex) {
            log.error("Can't parse the body: " + ex.getLocalizedMessage());
        }

        // Si no es mutante o no se puede parsear el body, devolver 403-forbidden
        log.error("Mutante: false");
        
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
    }

    /**
     * Extrae el parametro 'dna' del body del mensaje.
     *
     * @param bodyContent el string con el archivo json recibido
     * @throws JsonParseException cuando no se puede parsear
     */
    String[] getDNAFromRequestBody(String bodyContent) throws JsonParseException {

        JacksonJsonParser jsonParser = new JacksonJsonParser();
        Map<String, Object> parsed = jsonParser.parseMap(bodyContent);

        if (!parsed.containsKey("dna")) {
            throw new JsonParseException();
        }

        List<String> dnaList = (List<String>) parsed.get("dna");

        // Convertir la lista en un array
        String[] dnaArray = dnaList.toArray(new String[]{});

        return dnaArray;
    }

    /**
     * Elimina todos los registros de base de datos.
     */
    @RequestMapping(value = "/deleteall", method = POST)
    public boolean deleteAll() {
        mutanteRepository.deleteAll();
        return true;
    }

    /**
     * Devuelve un json con las estadísticas. El formato es:
     * {"count_mutant_dna":40, "count_human_dna":100: "ratio":0.4}
     */
    @RequestMapping(value = "/stats", method = GET)
    public Stats stats() {

        long human = mutanteRepository.countByIsMutantValue(false);
        long mutant = mutanteRepository.countByIsMutantValue(true);
        return new Stats(human, mutant);
    }

    /**
     * Devuelve el numero total de ADNs guardados en la base de datos.
     */
    @RequestMapping(value = "/count", method = GET)
    public long getCount() {
        return mutanteRepository.count();
    }

    /**
     * Este es el token privado para hacer los tests en loader.io
     *
     * @return el token
     */
    @RequestMapping(value = "/loaderio-e75603c0b79140f14a285d9ab85e4518", method = GET)
    public String getLoaderToken() {
        return "loaderio-e75603c0b79140f14a285d9ab85e4518";
    }
}
