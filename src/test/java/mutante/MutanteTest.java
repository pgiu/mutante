package mutante;

import java.util.ArrayList;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test unitarios para la clase Mutante.
 *
 * @author pablo
 */
public class MutanteTest {

    private final ArrayList<TestCase> trueCases;
    private final ArrayList<TestCase> falseCases;

    public ArrayList<TestCase> getTrueCases() {
        return trueCases;
    }

    public ArrayList<TestCase> getFalseCases() {
        return falseCases;
    }

    public ArrayList<TestCase> getAllCases() {
        ArrayList<TestCase> allCases = new ArrayList<>();
        allCases.addAll(trueCases);
        allCases.addAll(falseCases);
        return allCases;
    }

    public MutanteTest() {
        trueCases = new ArrayList<>();

        trueCases.add(new TestCase("5x5 D1", new String[]{
            "GTGGT",
            "GCTAG",
            "CGCTT",
            "TTGAT",
            "TGCGA"
        }, true));

        trueCases.add(new TestCase("4x4 1H 1V", new String[]{
            "CCCC",
            "CGTC",
            "CTCA",
            "CCAG"
        }, true));

        trueCases.add(new TestCase("4x4 2H 2V", new String[]{
            "CCGT",
            "CCCC",
            "GCCA",
            "CCAG"
        }, true));

        trueCases.add(new TestCase("4x4 3H 3V", new String[]{
            "CACT",
            "AGCC",
            "CCCC",
            "CCCG"
        }, true));

        trueCases.add(new TestCase("4x4 4H 4V", new String[]{
            "CAGC",
            "AGTC",
            "GTCC",
            "CCCC"
        }, true));

        trueCases.add(new TestCase("8x8 8H 8V", new String[]{
            "CAGTCAGT",
            "GTCAGTCA",
            "CAGTCAGT",
            "GTCAGTCG",
            "CAGTCAGA",
            "GTCAGTCA",
            "CAGTCAGA",
            "GTCGAAAA"
        }, true));

        trueCases.add(new TestCase("8x8 1H 1V", new String[]{
            "CCCCCAGT",
            "GTCAGTCA",
            "CAGTCAGT",
            "TTCAGTCG",
            "GAGTCAGA",
            "GTCAGTCA",
            "GAGTCAGG",
            "GTCCAGAA"
        }, true));

        trueCases.add(new TestCase("4x4 Diagonales", new String[]{
            "CAGC",
            "GCCA",
            "CCCT",
            "CTCC"
        }, true));

        trueCases.add(new TestCase("5x5 D1 D2", new String[]{
            "GAGGT",
            "CGTAG",
            "CCGTT",
            "TTCGA",
            "TGCCA"
        }, true));

        trueCases.add(new TestCase("5x5 otra", new String[]{
            "GAGGT",
            "TCTAA",
            "TGCTA",
            "TTCAA",
            "TGCGA"
        }, true));

        trueCases.add(new TestCase("4x4 todos iguales", new String[]{
            "AAAA",
            "AAAA",
            "AAAA",
            "AAAA"
        }, true));

        trueCases.add(new TestCase("4x4 4 iguales", new String[]{
            "AAAA",
            "GGGG",
            "TTTT",
            "CCCC"
        }, true));

        falseCases = new ArrayList<>();

        falseCases.add(new TestCase("1x1", new String[]{
            "C"
        }, false));

        falseCases.add(new TestCase("2x2", new String[]{
            "CG",
            "TA"
        }, false));

        falseCases.add(new TestCase("3x3", new String[]{
            "CGT",
            "TCT",
            "GGG"
        }, false));

        falseCases.add(new TestCase("8x8", new String[]{
            "CAGTCAGT",
            "GTCAGTCA",
            "CAGTCAGT",
            "GTCAGTCA",
            "CAGTCAGT",
            "GTCAGTCA",
            "CAGTCAGT",
            "GTCAGTCA"
        }, false));

        falseCases.add(new TestCase("4x4 1H", new String[]{
            "CCCC",
            "AGTC",
            "GTCA",
            "CCAG"
        }, false));

        falseCases.add(new TestCase("4x4 2H", new String[]{
            "CAGT",
            "CCCC",
            "GTCA",
            "CCAG"
        }, false));

        falseCases.add(new TestCase("4x4 3H", new String[]{
            "CAGT",
            "AGTC",
            "CCCC",
            "CCAG"
        }, false));

        falseCases.add(new TestCase("4x4 4H", new String[]{
            "CAGT",
            "AGTC",
            "GTCA",
            "CCCC"
        }, false));

        falseCases.add(new TestCase("4x4 1V", new String[]{
            "CAGT",
            "CGTC",
            "CTCA",
            "CGCA"
        }, false));

        falseCases.add(new TestCase("4x4 2V", new String[]{
            "CGGT",
            "GGTC",
            "TGCA",
            "CGCA"
        }, false));

        falseCases.add(new TestCase("4x4 3V", new String[]{
            "CATT",
            "GGTC",
            "CTTA",
            "CGTA"
        }, false));

        falseCases.add(new TestCase("4x4 4V", new String[]{
            "CAGT",
            "GGTT",
            "TTCT",
            "CGCT"
        }, false));

        falseCases.add(new TestCase("8x8 8V", new String[]{
            "CAGTCAGT",
            "GTCAGTCA",
            "CAGTCAGT",
            "GTCAGTCG",
            "CAGTCAGA",
            "GTCAGTCA",
            "CAGTCAGA",
            "GTCAGTCA"
        }, false));

        falseCases.add(new TestCase("8x8 8H", new String[]{
            "CAGTCAGT",
            "GTCAGTCA",
            "CAGTCAGT",
            "GTCAGTCG",
            "CAGTCAGA",
            "GTCAGTCA",
            "CAGTCAGG",
            "GTCCAAAA"
        }, false));

        for (int k = 100; k <= 1000; k = k * 10) {
            TestCase t = new TestCase(k + "x" + k, createBigFalseCase(k), false);
            falseCases.add(t);
        }
    }

    private String[] createBigFalseCase(int n) {
        char[] letters = {'C', 'A', 'G', 'T'};
        String[] result = new String[n];

        for (int r = 0; r < n; r++) {
            char[] line = new char[n];

            int i = 0;
            if (r % 2 == 0) {
                i = 2;
            }
            for (int c = 0; c < n; c++) {
                line[c] = letters[i];
                i = (i + 1) % letters.length;
            }
            result[r] = new String(line);
        }

        return result;
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test para isMutant
     */
    @Test
    public void testIsMutantNull() {
        System.out.println("entrada null");
        String[] dna = null;
        Mutante instance = new Mutante();
        boolean expResult = false;
        boolean result = instance.isMutant(dna);
        assertEquals(expResult, result);
    }

    @Test
    public void testIsMutantTrue() {
        System.out.println("Entradas verdaderas");
        runCases(trueCases);
    }

    @Test
    public void testIsMutantFalse() {
        System.out.println("Entradas falsas");
        runCases(falseCases);
    }

    private void runCases(ArrayList<TestCase> cases) {
        Mutante instance = new Mutante();

        for (TestCase t : cases) {
            System.out.println(t);
            boolean result = instance.isMutant(t.dna);
            assertEquals(t.expectedResult, result);
        }
    }

    /**
     * Test input size
     */
    @Test
    public void testIsInputValid() {
        System.out.println("isInputSizeValid");
        ArrayList<TestCase> malformedInput = new ArrayList<>();
        malformedInput.add(new TestCase("tamanio invalido", new String[]{
            "CAGT",
            "CCCC",
            "GTCA",
            "CCA"
        }, false));

        malformedInput.add(new TestCase("caracteres en minusculas", new String[]{
            "cgat",
            "ttta",
            "ggtt",
            "aacc"
        }, false));

        malformedInput.add(new TestCase("caracteres invalidos", new String[]{
            "abcd",
            "efgh",
            "ijkd",
            "mnop"
        }, false));

        malformedInput.add(new TestCase("3x4 tamanio invalido", new String[]{
            "abcd",
            "efgh",
            "ijkd"
        }, false));

        falseCases.add(new TestCase("null input",
                null,
                false));
        malformedInput.add(new TestCase("Tamano mayor que el maximo",
                createBigFalseCase(Mutante.MAX_DNA_SIZE + 1),
                false));
        runCases(malformedInput);
    }

}
