package mutante;

import java.util.List;
import org.springframework.data.repository.CrudRepository;

// This will be AUTO IMPLEMENTED by Spring into a Bean called userRepository
// CRUD refers Create, Read, Update, Delete

public interface MutanteRepository extends CrudRepository<Mutante, Long> {
    Integer countByIsMutantValue(boolean value);  
    List<Mutante> findByDna(String []dna);
}