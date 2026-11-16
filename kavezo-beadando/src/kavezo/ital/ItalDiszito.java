package kavezo.ital;

public abstract class ItalDiszito implements Ital {
    protected final Ital becsomagolt;

    protected ItalDiszito(Ital becsomagolt) {
        this.becsomagolt = becsomagolt;
    }

    @Override public String getLeiras() { return becsomagolt.getLeiras(); }
    @Override public int getAr() { return becsomagolt.getAr(); }
}
