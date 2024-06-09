public class Departament {
    public int codDepartament;
    public String numire;
    public Double buget;
    public String manager;

    public Departament() {
    }

    public Departament(int codDepartament, String numire, Double buget, String manager) {
        this.codDepartament = codDepartament;
        this.numire = numire;
        this.buget = buget;
        this.manager = manager;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Departament{");
        sb.append("codDepartament=").append(codDepartament);
        sb.append(", numire='").append(numire).append('\'');
        sb.append(", buget=").append(buget);
        sb.append(", manager='").append(manager).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
