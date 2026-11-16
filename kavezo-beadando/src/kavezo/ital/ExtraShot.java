package kavezo.ital;

public class ExtraShot extends ItalDiszito {
    public ExtraShot(Ital ital) { super(ital); }
    @Override public String getLeiras() { return becsomagolt.getLeiras() + " + extra shot"; }
    @Override public int getAr() { return becsomagolt.getAr() + 300; }
}
