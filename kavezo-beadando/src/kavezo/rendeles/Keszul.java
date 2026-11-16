package kavezo.rendeles;

public class Keszul extends AlapAllapot {
    @Override public String nev() { return "Készül"; }

    @Override
    public void tovabblep(Rendeles r) {
        r.allapotValtas(new Kesz());
    }
}
