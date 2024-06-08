public class Carte {
    String titlu;
    String autor;
    String gen;
    int nrPagini;

    public Carte(String titlu, String autor, String gen, int nrPagini) {
        this.titlu = titlu;
        this.autor = autor;
        this.gen = gen;
        this.nrPagini = nrPagini;
    }

    @Override
    public String toString() {
        return "Carte{" +
                "titlu='" + titlu + '\'' +
                ", autor='" + autor + '\'' +
                ", gen='" + gen + '\'' +
                ", nrPagini=" + nrPagini +
                '}';
    }
}
