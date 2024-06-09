public class Angatat {
    int codAngajat;
    int codDepartament;
    String nume;
    double salariul;

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Angatat{");
        sb.append("codAngajat=").append(codAngajat);
        sb.append(", codDepartament=").append(codDepartament);
        sb.append(", nume='").append(nume).append('\'');
        sb.append(", salariul=").append(salariul);
        sb.append('}');
        return sb.toString();
    }

    public Angatat(int codAngajat, int codDepartament, String nume, double salariul) {
        this.codAngajat = codAngajat;
        this.codDepartament = codDepartament;
        this.nume = nume;
        this.salariul = salariul;
    }
}
