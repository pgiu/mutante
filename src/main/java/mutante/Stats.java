package mutante;

/**
 *
 * @author pablo
 */
public class Stats {

    private long count_mutant_dna;
    private long count_human_dna;
    private double ratio;

    public Stats() {
    }

    /**
     * Crea una nueva estadística
     *
     * @param countHumanDNA numero de humanos
     * @param countMutantDNA numero de mutantes
     */
    public Stats(long countHumanDNA, long countMutantDNA) {
        this.count_mutant_dna = countMutantDNA;
        this.count_human_dna = countHumanDNA;
    }

    /**
     * Devuelve la razón de mutantes del total de ADNs en la base de datos.
     *
     * @return
     */
    public double getRatio() {
        long total = count_human_dna + count_mutant_dna;
        if (total == 0) {
            return 0.0;
        }

        return 1.0 * count_mutant_dna / total;
    }

    /**
     *
     * @return
     */
    public long getCount_mutant_dna() {
        return count_mutant_dna;
    }

    /**
     *
     * @return
     */
    public long getCount_human_dna() {
        return count_human_dna;
    }

}
