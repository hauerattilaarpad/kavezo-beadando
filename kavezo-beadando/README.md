# Kávézó rendeléskezelő – Tervezési minták beadandó

Konzolos Java alkalmazás egy kávézó rendeléseinek kezelésére. A vendég italokat rendel,
amelyekhez tetszőleges feltéteket kérhet; a rendelés végigmegy egy életcikluson
(felvéve → készül → kész → átadva, vagy törölve), és minden változásról a megfelelő
szereplők (barista, pult kijelző, kassza) automatikusan értesülnek.

Alkalmazott minták:

| Minta | Kategória | Hol? |
|---|---|---|
| **Díszítő (Decorator)** | szerkezeti | `kavezo.ital` – italok és feltétek |
| **Állapot (State)** | viselkedési | `kavezo.rendeles` – rendelés életciklusa |
| **Megfigyelő (Observer)** | viselkedési | `kavezo.figyelo` + `Rendeles` – értesítések |

## Fordítás, futtatás

Java 17 vagy újabb kell (switch-kifejezések miatt).

```
javac -encoding UTF-8 -d out $(find src -name "*.java")
java -cp out kavezo.Main          # interaktív menü
java -cp out kavezo.Main demo     # automatikus bemutató
```

Windows PowerShellben a fordítás:
```
javac -encoding UTF-8 -d out (Get-ChildItem -Recurse src -Filter *.java).FullName
```
(Ha az ékezetek rosszul jelennek meg a konzolon: `chcp 65001`, illetve
`java -Dstdout.encoding=UTF-8 -cp out kavezo.Main`.)

## Szerkezet

```
src/kavezo/
├── Main.java                  konzolos menü + demó
├── ital/                      DECORATOR
│   ├── Ital.java              közös interfész (Component)
│   ├── Espresso, Tea, ForroCsoki   alapitalok (Concrete Component)
│   ├── ItalDiszito.java       alap díszítő (Base Decorator)
│   └── Tej, Szirup, Tejszinhab, ExtraShot   feltétek (Concrete Decorators)
├── rendeles/                  STATE (+ Observer publisher)
│   ├── Rendeles.java          Context + Publisher
│   ├── RendelesAllapot.java   állapot interfész
│   ├── AlapAllapot.java       közös ős: alapból minden tiltott
│   └── Felveve, Keszul, Kesz, Atadott, Torolt   konkrét állapotok
└── figyelo/                   OBSERVER
    ├── RendelesFigyelo.java   subscriber interfész
    └── BaristaPanel, PultKijelzo, Kassza   konkrét subscriberek
```

---

## 1. Díszítő (Decorator)

**Probléma.** Három alapital van (espresso, tea, forró csoki) és négy feltét (tej, szirup,
tejszínhab, extra shot). A feltétek tetszőlegesen kombinálhatók, akár többször is
(pl. dupla extra shot). Ha minden kombinációnak külön alosztályt csinálnánk
(`EspressoTejjelEsSziruppal`, …), az osztályok száma kombinatorikusan robbanna:
3 alapital × 2⁴ feltét-kombináció = már ismétlés nélkül is 48 osztály, ismétléssel végtelen.
Ez pontosan az előadás Decorator-példájának (értesítési alrendszerek kombinálása) helyzete.

**Miért nem elég egy sima mező/lista?** Egy `List<String> feltetek` + `switch` az ár
számításában minden új feltétnél a meglévő kód módosítását igényelné (sérül az
open/closed elv), és az ár- és leírás-logika egy helyre zsúfolódna (sérül az SRP).

**Megoldás.** Minden feltét egy díszítő, amely ugyanazt az `Ital` interfészt valósítja meg,
mint amit becsomagol, és a saját árát/leírását hozzáadja a becsomagolt italéhoz:

```java
Ital ital = new Tejszinhab(new Szirup(new Tej(new Espresso()), "karamell"));
ital.getAr();      // 590 + 150 + 200 + 250 = 1190
```

A kliens (rendelés, kassza) nem tudja, hogy díszített italt kezel – csak az `Ital`
interfészt látja. Az italt a felhasználó **futásidőben** rakja össze a menüben, réteget
rétegre húzva.

**Következmények.**
- Új feltét = egy új osztály, semmi meglévőt nem kell módosítani (open/closed).
- Minden feltét a saját felárát és leírását ismeri (single responsibility).
- Hátrány: egy rétegre utólag „rákérdezni” vagy kivenni egy közbülső díszítőt
  körülményes – itt nincs is rá szükség, mert a rendelés tétele összeállítás után nem változik.

## 2. Állapot (State)

**Probléma.** A rendelés minden műveletének viselkedése az állapotától függ:

| Állapot | Tétel hozzáadása | Továbblépés | Törlés |
|---|---|---|---|
| Felvéve | ✓ | → Készül (ha nem üres) | → Törölve |
| Készül | ✗ | → Kész | ✗ (már dolgoznak rajta) |
| Kész | ✗ | → Átadva | ✗ |
| Átadva | ✗ | ✗ | ✗ |
| Törölve | ✗ | ✗ | ✗ |

Ezt hagyományosan minden metódusban egy `switch (allapot)` oldaná meg; három művelet ×
öt állapot = 15 ág, szétszórva. Egy új állapot (pl. „Fizetésre vár”) felvételénél az
összes `switch`-et módosítani kellene, és könnyű egyet kihagyni. Az előadás szerint ez
pontosan a minta alkalmazási területe: előre definiált átmenetek, állapotonként eltérő
viselkedés, sok elágazás.

**Megoldás.** Minden állapot egy osztály a `RendelesAllapot` interfész mögött.
A `Rendeles` (Context) csak delegál, egyetlen állapotra vonatkozó `if` sincs benne:

```java
public void tovabblep() { allapot.tovabblep(this); }
```

Az `AlapAllapot` ősosztály alapból minden műveletet tilt (kivételt dob), így minden
állapot **csak azt írja felül, ami nála megengedett** – a végállapotok (`Atadott`,
`Torolt`) üres osztályok. Az átmeneteket maguk az állapotok végzik
(`r.allapotValtas(new Keszul())`).

**Következmények.**
- Az állapotfüggő viselkedés állapotonként egy osztályban van (single responsibility).
- Új állapot felvétele a többi állapot érintése nélkül, csak a szomszédos átmenet
  módosításával lehetséges.
- A tiltott műveletek önmagukban kezeltek (ld. a demóban a hibaüzeneteket).

## 3. Megfigyelő (Observer)

**Probléma.** A rendelés állapotváltozására többen, **különbözőképpen** kell reagáljanak:

- a **barista** akkor kíváncsi rá, amikor el kell kezdeni készíteni (Készül),
- a **pult kijelző** akkor, amikor elkészült (Kész) – a vendégnek szól,
- a **kassza** az átadáskor könyveli a bevételt, törléskor nem.

Ha a `Rendeles` maga hívná meg ezeket (`kassza.konyvel(...)`, `kijelzo.mutat(...)`),
szorosan függne minden konkrét osztálytól, és minden új érdekelt (pl. hűségpont-rendszer,
napló) a `Rendeles` módosításával járna. A másik út, hogy a szereplők folyamatosan
lekérdezik az állapotot (polling), erőforrás-pazarló – ez az előadás „boltba járkáló
vásárló” példája.

**Megoldás.** A `Rendeles` a Publisher: listában tartja a `RendelesFigyelo`
interfészt megvalósító feliratkozókat, és minden állapotváltáskor értesíti őket.
A feliratkozók maguk döntik el, mely eseményre reagálnak.

```java
r.feliratkozik(barista);
r.feliratkozik(kijelzo);
r.feliratkozik(kassza);
...
bela.leiratkozik(kijelzo);   // futásidőben le is iratkozhat
```

**Következmények.**
- A `Rendeles` nem ismeri a konkrét figyelőket, csak az interfészt.
- Új figyelő a `Rendeles` módosítása nélkül bevezethető (open/closed).
- A feliratkozás/leiratkozás futásidőben dinamikus (a demóban a kijelző leiratkozik
  Béla rendeléséről, és utána tényleg nem kap értesítést).
- Hátrány (ld. előadás): az értesítés sorrendje a feliratkozás sorrendjétől függ.

## A minták együttműködése

A State és az Observer egy ponton találkozik: a `Rendeles.allapotValtas()` metódusban.
Az állapotobjektum hajtja végre az átmenetet, a `Rendeles` pedig ezután értesíti a
figyelőket. A Decorator az italok szintjén működik: a rendelés tételei díszített
`Ital` objektumok, a kassza a `getAr()` hívással a teljes díszítési láncon végigszámolt
árat kapja.

```
  Felhasználó ──► Ital összeállítás (Decorator) ──► Rendeles.tetelHozzaad()
                                                          │ delegál
                                                          ▼
                                                  RendelesAllapot (State)
                                                          │ allapotValtas()
                                                          ▼
                                        értesítés ──► BaristaPanel / PultKijelzo / Kassza
                                                       (Observer)
```

## Miért nem más minták?

Az indokoltság miatt érdemes leírni, mit **nem** használtam szándékosan:

- **Egyke (Singleton) a kasszára** – a kasszából valóban egy van, de ez a `Main`-ben egy
  példánnyal egyszerűen megoldható és átadható; az egyke rejtett függőséget és nehezebb
  tesztelhetőséget hozna (ld. előadás).
- **Stratégia (Strategy) az árazásra** – egyetlen árazási mód van, a minta „overkill”
  lenne; az előadás is kiemeli, hogy kevés algoritmus esetén felesleges.
- **Gyártófüggvény/Absztrakt gyár az italokra** – az italok létrehozása egyszerű
  `new`, nincsenek összetartozó termékcsaládok.

## Kapcsolódás a SOLID elvekhez

- **S** – minden díszítő, állapot és figyelő egyetlen dologért felel.
- **O** – új feltét, új állapot vagy új figyelő a meglévő osztályok módosítása nélkül.
- **L** – egy díszített ital bárhol használható, ahol `Ital` kell.
- **I** – a `RendelesFigyelo` egyetlen metódusú, szűk interfész.
- **D** – a `Rendeles` absztrakciótól (`RendelesAllapot`, `RendelesFigyelo`, `Ital`)
  függ, nem konkrét osztályoktól.
