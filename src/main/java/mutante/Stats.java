package mutante;

public class Stats {

    private final long count_mutant_dna;
    private final long count_human_dna;
    private double ratio;

    public Stats(long countHumanDNA, long countMutantDNA) {
        this.count_mutant_dna = countMutantDNA;
        this.count_human_dna = countHumanDNA;
    }

    public double getRatio() {
        long total = count_human_dna + count_mutant_dna;
        if (total == 0) {
            return 0.0;
        }

        return 1.0 * count_mutant_dna / total;
    }

    public long getCount_mutant_dna() {
        return count_mutant_dna;
    }

    public long getCount_human_dna() {
        return count_human_dna;
    }

}
