# Symulacja ewakuacji budynku podczas pożaru

## 1. Typy agentów

W modelu wyróżniono pięć typów agentów reprezentujących osoby w budynku oraz służby ratunkowe. Każdy agent podejmuje działania na podstawie lokalnych informacji o otoczeniu — widocznych sąsiadach (cztery pola: góra, dół, lewo, prawo) i znajomości planu ewakuacji poprzez algorytm BFS wyznaczający najkrótszą drogę do wyjścia.

| Typ agenta | Atrybuty i zachowanie |
|---|---|
| **Spokojny (Calm)** | Analizuje otoczenie i wybiera najkrótszą drogę do wyjścia algorytmem BFS. Omija ogniska pożaru. Podąża za strażakiem lub altruistą, jeśli znajdują się w sąsiedztwie. Gdy poziom paniki osiągnie 70, zaczyna poruszać się chaotycznie. |
| **Panikujący (Panicking)** | Porusza się losowo (z tasowaniem kierunków); ignoruje oznakowanie wyjść. Zaraża paniką spokojnych agentów w sąsiedztwie — zwiększa ich poziom paniki o 25. Startuje z wysokim poziomem paniki (85). |
| **Altruista (Altruist)** | Jak Spokojny, lecz aktywnie skanuje prostokątne pole widzenia (10 pól w przód, ±2 w bok) w poszukiwaniu rannych. Po znalezieniu rannego prowadzi go do wyjścia z prędkością 0,5 pola/tick. Po dojściu do wyjścia oboje są ewakuowani. |
| **Ranny (Injured)** | Porusza się z prędkością 0,5 pola/tick (ruch tylko co drugi tick). Od momentu utworzenia ma już stan Ranny i wymaga pomocy altruisty lub strażaka do skutecznej ewakuacji. |
| **Strażak (Firefighter)** | Pojawia się wraz z innymi agentami na starcie symulacji (liczba konfigurowana niezależnie). Tłumi ogień w zasięgu 2 pól wokół siebie, redukując jego intensywność o 25 jednostek na tick. Naprowadza sąsiadujących rannych w stronę wyjścia. Porusza się w kierunku innych agentów dopóki są dostępni, następnie ewakuuje się samodzielnie. Posiada wyższą tolerancję ognia podczas ruchu (ignoruje obecność ognia, blokuje go jedynie ściana). |

## 2. Atrybuty agentów

**Wszyscy agenci (klasa bazowa Agent)**
- pozycja: współrzędne (x, y) na siatce 2D
- prędkość: liczba pól na tick — 1,0 dla zdrowych agentów, 0,5 dla rannych
- panika: wartość 0–100; rośnie o 15 za każdą sąsiednią komórkę z ogniem, maleje o 2 co tick na otwartej przestrzeni
- stan: W budynku (IN_BUILDING) / Ewakuowany (EVACUATED) / Ranny (INJURED) / Martwy (DEAD) / Niesiony (CARRIED)
- licznik zablokowania: liczba ticków bez możliwości ruchu — po 30 tickach agent ginie z powodu uwięzienia

**Strażak (dodatkowe atrybuty)**
- zasięg gaszenia: 2 pola wokół strażaka
- skuteczność: redukuje intensywność ognia o 25 jednostek na tick

**Ogień (element środowiska)**
- intensywność: wartość 0–100 na danym polu; rośnie co tick
- prędkość rozprzestrzeniania: parametr konfigurowalny (domyślnie 0,1–0,3)
- próg blokady: domyślnie 50 — po przekroczeniu pole staje się nieprzejezdne
- próg śmiertelny: intensywność powyżej 90 powoduje śmierć agenta na tym polu
- próg zranienia: intensywność powyżej 50 powoduje przejście agenta w stan Ranny

## 3. Możliwe interakcje między agentami

**Agent – Ogień**
- Intensywność ognia powyżej 90 na polu agenta powoduje jego śmierć
- Intensywność powyżej 50 powoduje przejście agenta ze stanu W budynku w stan Ranny
- Poziom paniki rośnie proporcjonalnie do liczby sąsiednich komórek z ogniem

**Agent – Agent**
- Panikujący agent może zarazić paniką sąsiadującego Spokojnego — zwiększa jego panikę o 25
- Spokojny agent podąża za strażakiem lub altruistą znajdującym się w sąsiedztwie

**Altruista – Ranny**
- Altruista skanujący otoczenie znajduje rannego w zasięgu wzroku i przejmuje go (stan CARRIED)
- Oboje poruszają się razem z prędkością 0,5 pola/tick
- Po dojściu do wyjścia oboje są ewakuowani i usuwani z planszy

**Strażak – Ogień**
- Strażak redukuje intensywność ognia we wszystkich polach w zasięgu 2 pól co tick
- Po wygaszeniu ognia (intensywność ≤ 0) pole staje się znów przejezdne

**Strażak – Ranny**
- Strażak wykrywa sąsiadującego rannego i przesuwa go o jedno pole w stronę wyjścia (bez przenoszenia, jedynie naprowadzanie)

## 4. Założenia dotyczące środowiska symulacji (plansza)

Środowisko symulacji jest reprezentowane jako dwuwymiarowa siatka pól (domyślnie 120×70) — rzut jednej kondygnacji budynku.

| Element mapy | Typ w kodzie | Opis |
|---|---|---|
| Korytarze | CORRIDOR | Główne trasy ewakuacji; ruch możliwy |
| Pokoje / biura | ROOM | Miejsca startowe agentów; ruch możliwy |
| Wyjście awaryjne | EXIT | Cel ewakuacji — agent wchodzący jest usuwany z planszy |
| Ściana | WALL | Nieprzejezdna; blokuje ruch agentów i ognia |

Przyjęte założenia ogólne:
- Pożar startuje w losowym miejscu na planszy i rozprzestrzenia się co tick na sąsiednie pola z określonym prawdopodobieństwem
- Wyjście może zostać częściowo odcięte przez ogień, jeśli korytarz prowadzący do niego zostanie zablokowany
- Budynek ma jedno piętro — wewnętrzny układ ścian tworzy korytarze i pomieszczenia
- Agent zablokowany bez dostępnej drogi do wyjścia przez 30 ticków z rzędu ginie

## 5. Reguły działania agentów w kroku symulacji

W każdym kroku (ticku) symulacji wykonywane są następujące działania w ustalonej kolejności:

1. **Ruch agentów** — każdy żywy agent wykonuje swoją metodę step(): Spokojny i Altruista wybierają najkrótszą ścieżkę BFS, Panikujący porusza się losowo, Strażak gasi ogień i kieruje się do agentów lub wyjścia
2. **Sprawdzenie ruchu** — pole docelowe musi być przejezdne (nie ściana, nie zablokowane ogniem) i puste (lub wyjściem)
3. **Interakcja z ogniem** — sprawdzenie intensywności ognia na polu agenta: powyżej 90 → śmierć, powyżej 50 → stan Ranny
4. **Sprawdzenie uwięzienia** — jeśli agent nie ma dostępnej drogi do wyjścia, zwiększany jest licznik zablokowania; po 30 tickach agent ginie
5. **Rozprzestrzenianie ognia** — intensywność na każdym polu z ogniem rośnie; ogień przechodzi na sąsiednie pola z określonym prawdopodobieństwem
6. **Rejestracja ewakuacji** — agenci ze stanem Ewakuowany są zliczani w statystykach wg typu
7. **Usunięcie agentów** — ewakuowani i martwi agenci są usuwani z aktywnej listy symulacji
8. **Warunek końca** — symulacja zatrzymuje się, gdy lista agentów jest pusta

## 6. Parametry modelu (SimConfig)

Symulacja jest kontrolowana przez zestaw parametrów konfigurowalnych przed uruchomieniem:

- Liczba agentów każdego typu (Calm, Panicking, Altruist, Injured, Firefighter) — ustawiane w panelu konfiguracji
- Prędkość rozprzestrzeniania ognia (spreadSpeed)
- Prawdopodobieństwo zarażenia paniką przy kontakcie (panicSpreadChance)


## 7. Warunki początkowe symulacji

Na początku symulacji:
- Agenci są losowo rozmieszczeni na dostępnych, przejezdnych polach planszy (z maksymalnie 1000 próbami na agenta)
- Każdy agent jest tworzony przez fabrykę (AgentFactory) zgodnie z wybranym typem
- Ogień startuje w jednym losowym polu planszy, z marginesem od ścian,o początkowej intensywności 80
- Wszyscy agenci, włącznie ze strażakami, są obecni od pierwszego ticku symulacji

## 8. Losowość w modelu

Element losowości występuje w następujących aspektach symulacji:
- Początkowe rozmieszczenie wszystkich typów agentów na planszy
- Lokalizacja startowego ogniska pożaru
- Kierunek ruchu agentów Panikujących (tasowanie sąsiednich pól)
- Prawdopodobieństwo rozprzestrzenienia ognia na sąsiednie pole

## 9. Metryki obserwowane w symulacji (SimMetrics)

W trakcie działania modelu zbierane są następujące wskaźniki:
- Liczba ewakuowanych agentów (łącznie i w podziale na typ)
- Liczba zabitych agentów (łącznie i w podziale na typ)
- Średni czas ewakuacji (w tickach) — liczony jako suma ticków ewakuacji podzielona przez liczbę ewakuowanych
- Liczba agentów aktualnie w budynku oraz liczba rannych — dostępne w czasie rzeczywistym w panelu bocznym
- Średni poziom paniki w czasie — wizualizowany na wykresie liniowym (ostatnie 60 ticków)

## 10. Rozważane scenariusze symulacji

W zależności od wartości parametrów model prowadzi do różnych wyników ewakuacji:

- **Optymalne warunki** — wysoki odsetek Spokojnych, niska prędkość ognia, obecność strażaków i altruistów — maksymalna przeżywalność
- **Wysoki odsetek panikujących** — duże zagęszczenie chaotycznego ruchu w korytarzach, podwyższona śmiertelność wskutek blokowania dróg ewakuacji
- **Agresywny pożar** — wysoka prędkość rozprzestrzeniania ognia — czas ewakuacji staje się krytyczny, możliwe odcięcie dróg do wyjścia

## 11. Ograniczenia modelu

- Brak modelowania dymu — jedynie ogień jako bezpośrednie zagrożenie
- Agenci nawigują wyłącznie na podstawie globalnego algorytmu BFS do najbliższego wyjścia, bez ograniczeń percepcji typowych dla rzeczywistych warunków (dym, brak widoczności)
- Nie modeluje się zmęczenia fizycznego wpływającego na prędkość ruchu
- Jedno piętro budynku — typ komórki Schody zdefiniowany, ale nieaktywny w obecnej implementacji
- Wszyscy agenci tego samego typu mają identyczne parametry — brak indywidualnego zróżnicowania
- Strażacy nie przenoszą rannych fizycznie (jak Altruista), jedynie naprowadzają ich o jedno pole na tick