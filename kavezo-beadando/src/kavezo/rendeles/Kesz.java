package kavezo.rendeles;

public class Kesz extends AlapAllapot {
    @Override public String nev() { return "Kész"; }

    @Override
    public void tovabblep(Rendeles r) {
        r.allapotValtas(new Atadott());
    }
}
