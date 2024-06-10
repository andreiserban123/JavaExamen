public class Carte {
    String cota;
    String tilu;
    String autor;
    int an;

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Carte{");
        sb.append("cota='").append(cota).append('\'');
        sb.append(", tilu='").append(tilu).append('\'');
        sb.append(", autor='").append(autor).append('\'');
        sb.append(", an=").append(an);
        sb.append('}');
        return sb.toString();
    }

    public Carte() {
    }

    public Carte(String cota, String tilu, String autor, int an) {
        this.cota = cota;
        this.tilu = tilu;
        this.autor = autor;
        this.an = an;
    }
}
