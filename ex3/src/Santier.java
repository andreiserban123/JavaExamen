public class Santier {
    int codSantier;
    String localitate;
    String strada;
    String obiectiv;
    float valoare;

    public Santier(int codSantier, String localitate, String strada, String obiectiv, float valoare) {
        this.codSantier = codSantier;
        this.localitate = localitate;
        this.strada = strada;
        this.obiectiv = obiectiv;
        this.valoare = valoare;
    }

    @Override
    public String toString() {
        return "Santier{" +
                "codSantier=" + codSantier +
                ", localitate='" + localitate + '\'' +
                ", strada='" + strada + '\'' +
                ", obiectiv='" + obiectiv + '\'' +
                ", valoare=" + valoare +
                '}';
    }
}
