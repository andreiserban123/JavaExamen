public class Carte {
    String cotaCarte;
    String titlu;
    String autor;
    int an;

    @Override
    public String toString() {
        return "Carte{" +
                "cotaCarte='" + cotaCarte + '\'' +
                ", titlu='" + titlu + '\'' +
                ", autor='" + autor + '\'' +
                ", an=" + an +
                '}';
    }

    public Carte() {
    }

    public Carte(String cotaCarte, String titlu, String autor, int an) {
        this.cotaCarte = cotaCarte;
        this.titlu = titlu;
        this.autor = autor;
        this.an = an;
    }
}
