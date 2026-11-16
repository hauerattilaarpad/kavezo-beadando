package kavezo.rendeles;

import kavezo.ital.Ital;

public interface RendelesAllapot {
    String nev();
    void tetelHozzaad(Rendeles r, Ital ital);
    void tovabblep(Rendeles r);
    void torol(Rendeles r);
}
