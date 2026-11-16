package kavezo.rendeles;

import kavezo.figyelo.RendelesFigyelo;
import kavezo.ital.Ital;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Rendeles {
    private static int kovetkezoId = 1;

    private final int id;
    private final String vendegNev;
    private final List<Ital> tetelek = new ArrayList<>();
    private final List<RendelesFigyelo> figyelok = new ArrayList<>();
    private RendelesAllapot allapot = new Felveve();

    public Rendeles(String vendegNev) {
        this.id = kovetkezoId++;
        this.vendegNev = vendegNev;
    }


    public void feliratkozik(RendelesFigyelo f) { figyelok.add(f); }
    public void leiratkozik(RendelesFigyelo f) { figyelok.remove(f); }

    private void ertesit() {
        for (RendelesFigyelo f : List.copyOf(figyelok)) {
            f.allapotValtozott(this);
        }
    }

    public void tetelHozzaad(Ital ital) { allapot.tetelHozzaad(this, ital); }
    public void tovabblep() { allapot.tovabblep(this); }
    public void torol() { allapot.torol(this); }

    void tetelFelvetele(Ital ital) { tetelek.add(ital); }

    void allapotValtas(RendelesAllapot uj) {
        System.out.println("  #" + id + ": " + allapot.nev() + " -> " + uj.nev());
        this.allapot = uj;
        ertesit();
    }


    public int vegosszeg() {
        return tetelek.stream().mapToInt(Ital::getAr).sum();
    }

    public int getId() { return id; }
    public String getVendegNev() { return vendegNev; }
    public RendelesAllapot getAllapot() { return allapot; }
    public List<Ital> getTetelek() { return Collections.unmodifiableList(tetelek); }

    @Override
    public String toString() {
        return "#" + id + " " + vendegNev + " [" + allapot.nev() + "] " + tetelek.size()
                + " tétel, " + vegosszeg() + " Ft";
    }
}
