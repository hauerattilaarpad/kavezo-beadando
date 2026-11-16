package kavezo.rendeles;

import kavezo.ital.Ital;

public class Felveve extends AlapAllapot {
    @Override public String nev() { return "Felvéve"; }

    @Override
    public void tetelHozzaad(Rendeles r, Ital ital) {
        r.tetelFelvetele(ital);
    }

    @Override
    public void tovabblep(Rendeles r) {
        if (r.getTetelek().isEmpty()) {
            throw new IllegalStateException("Üres rendelést nem lehet elkészíteni.");
        }
        r.allapotValtas(new Keszul());
    }

    @Override
    public void torol(Rendeles r) {
        r.allapotValtas(new Torolt());
    }
}
