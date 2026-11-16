package kavezo.figyelo;

import kavezo.rendeles.Kesz;
import kavezo.rendeles.Rendeles;

public class PultKijelzo implements RendelesFigyelo {
    @Override
    public void allapotValtozott(Rendeles r) {
        if (r.getAllapot() instanceof Kesz) {
            System.out.println("  [Kijelző] *** #" + r.getId() + " (" + r.getVendegNev()
                    + ") rendelése elkészült, átvehető a pultnál! ***");
        }
    }
}
