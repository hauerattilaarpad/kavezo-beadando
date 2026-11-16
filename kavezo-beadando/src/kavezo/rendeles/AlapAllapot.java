package kavezo.rendeles;

import kavezo.ital.Ital;

abstract class AlapAllapot implements RendelesAllapot {
    @Override
    public void tetelHozzaad(Rendeles r, Ital ital) {
        throw new IllegalStateException("'" + nev() + "' állapotban már nem adható hozzá tétel.");
    }

    @Override
    public void tovabblep(Rendeles r) {
        throw new IllegalStateException("'" + nev() + "' állapotból nincs továbblépés.");
    }

    @Override
    public void torol(Rendeles r) {
        throw new IllegalStateException("'" + nev() + "' állapotban a rendelés nem törölhető.");
    }

    @Override
    public String toString() { return nev(); }
}
