package kavezo.ital;

public class Szirup extends ItalDiszito {
    private final String iz;

    public Szirup(Ital ital, String iz) {
        super(ital);
        this.iz = iz;
    }

    @Override public String getLeiras() { return becsomagolt.getLeiras() + " + " + iz + " szirup"; }
    @Override public int getAr() { return becsomagolt.getAr() + 200; }
}
