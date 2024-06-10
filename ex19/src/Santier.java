public class Santier {
    int codSantier;
    String localitate;
    String strada;
    String obiectiv;
    double valoare;

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Santier{");
        sb.append("codSantier=").append(codSantier);
        sb.append(", localitate='").append(localitate).append('\'');
        sb.append(", strada='").append(strada).append('\'');
        sb.append(", obiectiv='").append(obiectiv).append('\'');
        sb.append(", valoare=").append(valoare);
        sb.append('}');
        return sb.toString();
    }
}
