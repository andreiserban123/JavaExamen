public class Program {
    int codProgram;
    String denumire;
    int nrLocuri;

    public Program(int codProgram, String denumire, int nrLocuri) {
        this.codProgram = codProgram;
        this.denumire = denumire;
        this.nrLocuri = nrLocuri;
    }

    @Override
    public String toString() {
        return "Program{" +
                "codProgram=" + codProgram +
                ", denumire='" + denumire + '\'' +
                ", nrLocuri=" + nrLocuri +
                '}';
    }
}
