/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mutante;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author pablo
 */
public class StatsTest {

    public StatsTest() {
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

    @Test
    public void testEmptyStat() {
        Stats s = new Stats(0, 0);

        assertEquals(s.getCount_human_dna(), 0);
        assertEquals(s.getCount_mutant_dna(), 0);
        // el valor por defecto para getRatio tiene que ser 0.0
        assertEquals(s.getRatio(), 0.0, 0.0);

    }

    @Test
    public void testSomeValue() {
        int mutante = 100;
        int humano = 123;
        Stats s = new Stats(humano, mutante);
        double r = 1.0 * mutante / (mutante + humano);

        assertEquals(s.getCount_human_dna(), humano);
        assertEquals(s.getCount_mutant_dna(), mutante);
        // el valor por defecto para getRatio tiene que ser 0.0
        assertEquals(s.getRatio(), r, 1e-6);

    }
}
