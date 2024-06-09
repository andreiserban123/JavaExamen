public class Optiune {
    int cod_liceu;
    int cod_specializare;

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Optiune{");
        sb.append("cod_liceu=").append(cod_liceu);
        sb.append(", cod_specializare=").append(cod_specializare);
        sb.append('}');
        return sb.toString();
    }

    public Optiune() {
    }

    public Optiune(int cod_liceu, int cod_specializare) {
        this.cod_liceu = cod_liceu;
        this.cod_specializare = cod_specializare;
    }
}
