public class Specializare {
    int cod_specializare;
    int nr_locuri;

    public Specializare() {
    }

    public Specializare(int cod_specializare, int nr_locuri) {
        this.cod_specializare = cod_specializare;
        this.nr_locuri = nr_locuri;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Specializare{");
        sb.append("cod_specializare=").append(cod_specializare);
        sb.append(", nr_locuri=").append(nr_locuri);
        sb.append('}');
        return sb.toString();
    }
}
