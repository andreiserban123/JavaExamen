public enum Tip {
    vanzare(1), cumparare(-1);
    private int semn;

    Tip(int semn) {
        this.semn = semn;
    }

    public int getSemn() {
        return semn;
    }
}
