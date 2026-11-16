package kavezo.figyelo;

import kavezo.rendeles.Atadott;
import kavezo.rendeles.Rendeles;
import kavezo.rendeles.Torolt;

public class Kassza implements RendelesFigyelo {
    private int napiBevetel = 0;
    private int eladottRendelesek = 0;
    private int toroltRendelesek = 0;

    @Override
    public void allapotValtozott(Rendeles r) {
        if (r.getAllapot() instanceof Atadott) {
            napiBevetel += r.vegosszeg();
            eladottRendelesek++;
            System.out.println("  [Kassza] #" + r.getId() + " könyvelve: " + r.vegosszeg() + " Ft");
        } else if (r.getAllapot() instanceof Torolt) {
            toroltRendelesek++;
            System.out.println("  [Kassza] #" + r.getId() + " törölve, nincs bevétel.");
        }
    }

    public void napiZaras() {
        System.out.println("=== Napi zárás ===");
        System.out.println("Átadott rendelések: " + eladottRendelesek);
        System.out.println("Törölt rendelések:  " + toroltRendelesek);
        System.out.println("Bevétel:            " + napiBevetel + " Ft");
    }
}
