package kavezo.ital;

public class Tej extends ItalDiszito {
    public Tej(Ital ital) { super(ital); }
    @Override public String getLeiras() { return becsomagolt.getLeiras() + " + tej"; }
    @Override public int getAr() { return becsomagolt.getAr() + 150; }
}
