package mutante;

class TestCase {

    String[] dna;
    boolean expectedResult;
    String name;

    public TestCase(String name, String[] dna, boolean result) {
        this.name = name;
        this.dna = dna;
        this.expectedResult = result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Name     : ").append(name).append("\n");
        sb.append("Expected : ").append(expectedResult);

        return sb.toString();
    }
}
