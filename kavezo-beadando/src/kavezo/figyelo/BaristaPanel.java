package kavezo.figyelo;

import kavezo.ital.Ital;
import kavezo.rendeles.Keszul;
import kavezo.rendeles.Rendeles;

public class BaristaPanel implements RendelesFigyelo {
    @Override
    public void allapotValtozott(Rendeles r) {
        if (r.getAllapot() instanceof Keszul) {
            System.out.println("  [Barista] Elkészítendő: #" + r.getId());
            for (Ital i : r.getTetelek()) {
                System.out.println("      - " + i.getLeiras());
            }
        }
    }
}
