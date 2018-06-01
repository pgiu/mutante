package mutante;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

enum Direction {
    DIR_DOWN, DIR_RIGHT, DIR_DIAG_DOWN, DIR_DIAG_UP
}

enum Start {
    TOP, LEFT, BOTTOM
}

class SearchStrategy {

    final Start start;
    final Direction direction;

    public SearchStrategy(Start start, Direction direction) {
        this.start = start;
        this.direction = direction;
    }

    public Start getStart() {
        return start;
    }

    public Direction getDirection() {
        return direction;
    }

}

class DnaIterator {

    private final String[] dna;
    private final int n;
    private int row, col;
    private int rowStart, colStart;
    Start start;
    Direction direction;

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getCol() {
        return col;
    }

    public void setCol(int col) {
        this.col = col;
    }

    public DnaIterator(String[] dna, Start start, Direction direction) {
        this.dna = dna;
        this.n = dna.length;
        this.row = 0;
        this.col = 0;
        this.start = start;
        this.direction = direction;
        this.rowStart = 0;
        this.colStart = 0;
        if (start == Start.BOTTOM && direction == Direction.DIR_DIAG_UP) {
            this.col = 1;
            this.colStart = 1;
        }

        if (start == Start.TOP && direction == Direction.DIR_DIAG_DOWN) {
            this.col = 1;
            this.colStart = 1;
        }

    }

    /**
     * @return false cuando hay una nueva dimensión disponible
     */
    public boolean nextStartDimension(Start start) {
        if (start == Start.TOP || start == Start.BOTTOM) {
            row = (start == Start.TOP) ? 0 : n - 1;
            colStart++;
            col = colStart;
            return colStart < n;
            //TODO que pasa si asigno col a un valor mayor a N y alguien llama getValue()
        } else if (start == Start.LEFT) {
            col = 0;
            rowStart++;
            row = rowStart;
            return rowStart < n;
        }
        return false;
    }

    /**
     * Devuelve el valor en (row, col)
     */
    Character getValue() {
        return dna[row].charAt(col);
    }

    /**
     * Devuelve el valor de la siguiente posición en la dirección pedida.
     *
     * @return el valor o null, si está fuera de rango
     */
    Character getNext(Direction direction) {
        switch (direction) {
            case DIR_RIGHT:
                if (col < n - 1) {
                    col++;
                    return getValue();
                }
                break;
            case DIR_DOWN:
                if (row < n - 1) {
                    row++;
                    return getValue();
                }
                break;
            case DIR_DIAG_DOWN:
                if (row < n - 1 && col < n - 1) {
                    row++;
                    col++;
                    return getValue();
                }
                break;
            case DIR_DIAG_UP:
                if (row > 0 && col < n - 1) {
                    row--;
                    col++;
                    return getValue();
                }
                break;
        }
        return null;
    }

    @Override
    public String toString() {
        return "DnaIterator{" + "row=" + row + ", col=" + col + '}';
    }
}

@Entity
public class Mutante {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Column(length = 1000000)
    private String[] dna;

    private boolean isMutantValue;

    @Transient
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    /**
     * Largo de la cadena para ser considerada mutante.
     */
    private static final int SEQUENCE_LENGTH = 4;

    /**
     * Cantidad de secuencias de tamaño SEQUENCE_LENGTH que tiene que haber para
     * ser considerado mutante.
     */
    private static final int MUTANT_SEQUENCE_COUNT = 2;

    /* Set de caracteres válidos en la entrada */
    private static final Set<Character> VALID_CHARACTERS = new HashSet<>(Arrays.asList('A', 'T', 'C', 'G'));

    /**
     * Funcion principal de la clase. Se encarga de verificar si el ADN que se
     * le pasa, corresponde al de un humano o al de un mutante. Para ser
     * considerado
     *
     * @param dna el ADN a verificar
     */
    boolean isMutant(String[] dna) {

        this.dna = dna;
        isMutantValue = false;

        // Validamos la entrada
        if (!isInputValid(dna)) {
            log.debug("Entrada invalida");
            return false;
        }

        // Buscar si es mutante o no
        this.isMutantValue = searchInAllDirections(dna);

        return isMutantValue;
    }

    boolean searchInAllDirections(String[] dna) {

        SearchStrategy[] strategies = new SearchStrategy[]{
            new SearchStrategy(Start.TOP, Direction.DIR_DOWN),
            new SearchStrategy(Start.LEFT, Direction.DIR_RIGHT),
            new SearchStrategy(Start.TOP, Direction.DIR_DIAG_DOWN),
            new SearchStrategy(Start.LEFT, Direction.DIR_DIAG_DOWN),
            new SearchStrategy(Start.LEFT, Direction.DIR_DIAG_UP),
            new SearchStrategy(Start.BOTTOM, Direction.DIR_DIAG_UP)};

        int count = 0;
        for (SearchStrategy s : strategies) {
            count += searchUsingStrategy(dna, s.start, s.direction, count);
            if (count >= MUTANT_SEQUENCE_COUNT) {
                return true;
            }
        }
        return false;
    }

    /**
     * Hace la búsqueda empezando por una dimension y buscando en una única
     * dimensión.
     *
     * @param dna
     * @param startDimension
     * @param direction
     * @param curCount
     * @return el número de secuencias de largo mayor a SEQUENCE_LENGTH
     */
    int searchUsingStrategy(String[] dna, Start startDimension, Direction direction, int curCount) {

        DnaIterator di = new DnaIterator(dna, startDimension, direction);
        int sequenceCount = 0;

        do {
            int counter = 1;
            Character prevChar = di.getValue();

            Character curChar;
            while ((curChar = di.getNext(direction)) != null) {

                if (curChar.equals(prevChar)) {
                    counter++;
                } else {
                    prevChar = curChar;
                    counter = 1;
                }

                if (counter == SEQUENCE_LENGTH) {
                    sequenceCount++;
                    if (curCount + sequenceCount >= 2) {
                        return sequenceCount;
                    }
                    // Reiniciamos la búsqueda y consumimos un valor dummy
                    counter = 1;
                    di.getNext(direction);
                    prevChar = di.getNext(direction);
                }
            }
        } while (di.nextStartDimension(startDimension));

        return sequenceCount;
    }

    /**
     * Verifica que tanto la forma y el contenido del ADN de entrada sean
     * válidos.
     *
     * @param dna el ADN a verificar
     * @return true si tiene dimensiones validas (matriz cuadrada) y el
     * contenido es únicamente componentes válidos (A, T, C, G)
     */
    boolean isInputValid(String[] dna) {
        return isInputSizeValid(dna) && isInputContentValid(dna);
    }

    /**
     * Verifica que la matriz de entrada tenga dimensiones correctas.
     *
     * @param dna la matriz de entrada con el DNA del candidato
     */
    boolean isInputSizeValid(String[] dna) {
        if (dna == null) {
            return false;
        }

        if (dna.length == 0) {
            return false;
        }

        int nrows = dna.length;

        // No tiene sentido buscar en una matriz que tenga menos que el tamaño mínimo
        if (nrows < SEQUENCE_LENGTH) {
            return false;
        }

        // Verifica que todas las filas tengan igual largo
        for (String row : dna) {
            if (row == null || row.length() != nrows) {
                return false;
            }
        }

        return true;
    }

    /**
     * Verifica que todos los components de la matriz de entrada sean válidos.
     * Time complexity: O(n)
     *
     * @param dna el ADN de entrada
     * @return true si es válido o false en caso contrario
     */
    boolean isInputContentValid(String[] dna) {
        for (String s : dna) {
            for (int k = 0; k < s.length(); k++) {
                char c = s.charAt(k);
                if (!VALID_CHARACTERS.contains(c)) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean isIsMutantValue() {
        return isMutantValue;
    }

}
