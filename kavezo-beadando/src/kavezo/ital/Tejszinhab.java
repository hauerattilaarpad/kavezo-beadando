package kavezo.ital;

public class Tejszinhab extends ItalDiszito {
    public Tejszinhab(Ital ital) { super(ital); }
    @Override public String getLeiras() { return becsomagolt.getLeiras() + " + tejszínhab"; }
    @Override public int getAr() { return becsomagolt.getAr() + 250; }
}
