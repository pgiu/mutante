package mutante;

import java.util.List;
import org.springframework.data.repository.CrudRepository;

public interface MutanteRepository extends CrudRepository<Mutante, Long> {
    Integer countByIsMutantValue(boolean value);  
    List<Mutante> findByDna(String []dna);
}