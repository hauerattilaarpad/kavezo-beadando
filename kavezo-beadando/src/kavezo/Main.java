package kavezo;

import kavezo.figyelo.BaristaPanel;
import kavezo.figyelo.Kassza;
import kavezo.figyelo.PultKijelzo;
import kavezo.ital.*;
import kavezo.rendeles.Rendeles;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    private final List<Rendeles> rendelesek = new ArrayList<>();
    private final BaristaPanel barista = new BaristaPanel();
    private final PultKijelzo kijelzo = new PultKijelzo();
    private final Kassza kassza = new Kassza();
    private final Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        Main app = new Main();
        if (args.length > 0 && args[0].equalsIgnoreCase("demo")) {
            app.demo();
        } else {
            app.futtat();
        }
    }

    private Rendeles ujRendeles(String nev) {
        Rendeles r = new Rendeles(nev);
        r.feliratkozik(barista);
        r.feliratkozik(kijelzo);
        r.feliratkozik(kassza);
        rendelesek.add(r);
        return r;
    }

    private void futtat() {
        System.out.println("=== Kávézó rendeléskezelő ===");
        while (true) {
            System.out.println();
            System.out.println("1) Új rendelés");
            System.out.println("2) Ital hozzáadása rendeléshez");
            System.out.println("3) Rendelés továbbléptetése (elkészítés / kész / átadás)");
            System.out.println("4) Rendelés törlése");
            System.out.println("5) Rendelések listázása");
            System.out.println("6) Napi zárás");
            System.out.println("7) Demó futtatása");
            System.out.println("0) Kilépés");
            int v = szam("Választás: ");
            try {
                switch (v) {
                    case 1 -> {
                        System.out.print("Vendég neve: ");
                        Rendeles r = ujRendeles(sc.nextLine().trim());
                        System.out.println("Létrehozva: " + r);
                    }
                    case 2 -> {
                        Rendeles r = valaszt();
                        if (r != null) {
                            Ital i = italOsszeallitas();
                            r.tetelHozzaad(i);
                            System.out.println("Hozzáadva: " + i.getLeiras() + " (" + i.getAr() + " Ft)");
                        }
                    }
                    case 3 -> { Rendeles r = valaszt(); if (r != null) r.tovabblep(); }
                    case 4 -> { Rendeles r = valaszt(); if (r != null) r.torol(); }
                    case 5 -> listaz();
                    case 6 -> kassza.napiZaras();
                    case 7 -> demo();
                    case 0 -> { System.out.println("Viszlát!"); return; }
                    default -> System.out.println("Nincs ilyen menüpont.");
                }
            } catch (IllegalStateException e) {
                System.out.println("  HIBA: " + e.getMessage());
            }
        }
    }

    private Ital italOsszeallitas() {
        System.out.println("Alapital: 1) Espresso (590)  2) Tea (490)  3) Forró csoki (690)");
        Ital ital = switch (szam("Választás: ")) {
            case 2 -> new Tea();
            case 3 -> new ForroCsoki();
            default -> new Espresso();
        };
        while (true) {
            System.out.println("Jelenleg: " + ital.getLeiras() + " = " + ital.getAr() + " Ft");
            System.out.println("Feltét: 1) Tej (+150)  2) Szirup (+200)  3) Tejszínhab (+250)  4) Extra shot (+300)  0) Kész");
            switch (szam("Választás: ")) {
                case 1 -> ital = new Tej(ital);
                case 2 -> {
                    System.out.print("Szirup íze: ");
                    ital = new Szirup(ital, sc.nextLine().trim());
                }
                case 3 -> ital = new Tejszinhab(ital);
                case 4 -> ital = new ExtraShot(ital);
                case 0 -> { return ital; }
                default -> System.out.println("Nincs ilyen feltét.");
            }
        }
    }

    private Rendeles valaszt() {
        if (rendelesek.isEmpty()) {
            System.out.println("Még nincs rendelés.");
            return null;
        }
        listaz();
        int id = szam("Rendelés száma: ");
        for (Rendeles r : rendelesek) {
            if (r.getId() == id) return r;
        }
        System.out.println("Nincs ilyen rendelés.");
        return null;
    }

    private void listaz() {
        if (rendelesek.isEmpty()) System.out.println("Nincs rendelés.");
        for (Rendeles r : rendelesek) {
            System.out.println(r);
            for (Ital i : r.getTetelek()) {
                System.out.println("     - " + i.getLeiras() + "  " + i.getAr() + " Ft");
            }
        }
    }

    private int szam(String prompt) {
        while (true) {
            System.out.print(prompt);
            if (!sc.hasNextLine()) System.exit(0);
            String s = sc.nextLine().trim();
            try {
                return Integer.parseInt(s);
            } catch (NumberFormatException e) {
                System.out.println("Számot adj meg!");
            }
        }
    }

    // ------------------------------------------------------------- demó
    private void demo() {
        System.out.println("\n--- 1. DECORATOR: italok összeállítása feltétekkel ---");
        Ital anna1 = new Tejszinhab(new Szirup(new Tej(new Espresso()), "karamell"));
        Ital anna2 = new ExtraShot(new ExtraShot(new Espresso()));
        Ital bela1 = new Tejszinhab(new ForroCsoki());
        for (Ital i : List.of(anna1, anna2, bela1)) {
            System.out.println("  " + i.getLeiras() + " = " + i.getAr() + " Ft");
        }

        System.out.println("\n--- 2. STATE + OBSERVER: rendelések életciklusa ---");
        Rendeles anna = ujRendeles("Anna");
        anna.tetelHozzaad(anna1);
        anna.tetelHozzaad(anna2);
        Rendeles bela = ujRendeles("Béla");
        bela.tetelHozzaad(bela1);
        Rendeles cecil = ujRendeles("Cecil");
        cecil.tetelHozzaad(new Tea());

        anna.tovabblep();
        bela.tovabblep();
        cecil.torol();

        System.out.println("\n  Próbálkozás: tétel hozzáadása egy már készülő rendeléshez...");
        probal(() -> anna.tetelHozzaad(new Tea()));
        System.out.println("  Próbálkozás: készülő rendelés törlése...");
        probal(bela::torol);

        anna.tovabblep();
        anna.tovabblep();

        System.out.println("\n  Leiratkozás: a kijelző már nem figyeli Béla rendelését.");
        bela.leiratkozik(kijelzo);
        bela.tovabblep();
        bela.tovabblep();

        System.out.println("  Próbálkozás: átadott rendelés továbbléptetése...");
        probal(anna::tovabblep);

        System.out.println();
        listaz();
        kassza.napiZaras();
    }

    private void probal(Runnable muvelet) {
        try {
            muvelet.run();
        } catch (IllegalStateException e) {
            System.out.println("  HIBA: " + e.getMessage());
        }
    }
}
