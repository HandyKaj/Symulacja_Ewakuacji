Symulacja ewakuacji budynku podczas pożaru

1. Typy agentów
W modelu wyróżniono pięć typów agentów reprezentujących osoby w budynku oraz służby
ratunkowe. Każdy agent podejmuje działania na podstawie lokalnych informacji o otoczeniu
    — widocznych sąsiadach, pobliskim ogniu i znajomości planu ewakuacji.

Typ agenta Atrybuty i zachowanie
  Spokojny:
    • Analizuje otoczenie i wybiera najkrótszą drogę do wyjścia
    • Omija ogniska pożaru; nie wpada w panikę
    • Może pomagać rannym (jeśli pełni rolę altruisty)
  Panikujący 
    • Porusza się chaotycznie; ignoruje oznakowanie wyjść
    • Blokuje korytarze; wzmacnia efekt tłumu
    • Poziom paniki rośnie w pobliżu ognia i innych panikujących
  Altruista 
    • Jak Spokojny, lecz celowo poszukuje rannych w pobliżu
    • Spowalnia własną ewakuację, aby pomóc innemu agentowi
    • Po podjęciu rannego: oboje poruszają się z połową prędkości
  Ranny 
    • Porusza się z połową normalnej prędkości
    • Wymaga pomocy altruisty do skutecznej ewakuacji
    • Powstaje w wyniku kontaktu z ogniem lub efektu tłumu
  Strażak 
    • Wchodzi do budynku po N tickach od wybuchu pożaru
    • Tłumi ogień na sąsiadujących polach
    • Prowadzi rannych do najbliższego wyjścia

2. Atrybuty agentów
  Mieszkańcy (Spokojny / Panikujący / Altruista / Ranny)
    • pozycja: współrzędne (x, y) na siatce 2D
    • typ: Spokojny / Panikujący / Altruista / Ranny
    • prędkość: liczba pól na tick — Spokojny: 1; Ranny: 0,5
    • panika: wartość 0–100; rośnie przy ogniu i zagęszczeniu; maleje na otwartej
  przestrzeni
    • stan: W budynku / Ewakuowany / Ranny / Martwy
    • niesiony: czy agent jest prowadzony przez Altruistę
  Strażak
    • pozycja: współrzędne na siatce
    • zasięg gaszenia: liczba pól wokół objętych działaniem
    • skuteczność: o ile jednostek zmniejsza intensywność ognia na tick
  Ogień (element środowiska)
    • intensywność: wartość 0–100 na danym polu; rośnie co tick
    • prędkość rozprzestrzeniania: zależy od materiałów w strefie budynku
    • blokada: jeśli intensywność > próg, pole staje się nieprzejezdne

3. Możliwe interakcje między agentami
  Agent – Ogień
    • Wejście agenta na pole z ogniem (intensywność > próg) powoduje przejście w stan
    Ranny
    • Poziom paniki agentów rośnie proporcjonalnie do odległości od ognia
    Agent – Agent (efekt tłumu)
    • Gdy na jednym polu znajduje się więcej niż X agentów, prędkość ruchu spada
    • Panikujący agent może zarazić paniką spokojnego (prawdopodobieństwo zależy od
    parametru)
  Altruista – Ranny
    • Altruista w sąsiedztwie rannego może go zabrać — oboje poruszają się z prędkością
    0,5 pola/tick
    • Po dojściu do wyjścia oboje są ewakuowani i usuwani z mapy
  Strażak – Ogień
    • Strażak na polu sąsiednim z ogniem redukuje jego intensywność co tick
    • Strażak może odblokować wcześniej nieprzejezdne pole

4. Założenia dotyczące środowiska symulacji (plansza)
  Środowisko symulacji jest reprezentowane jako dwuwymiarowa siatka pól (grid) — rzut jednej
  kondygnacji budynku. Mapa składa się z następujących elementów:
  Element mapy Kolor Opis
  Pokoje / biura Szary Miejsca startowe agentów; ruch możliwy
  Korytarze Biały Główne trasy ewakuacji; szybki ruch
  Wyjście awaryjne Zielony Cel ewakuacji — agent wychodzący usuwany z mapy
  Schody Żółty Połączenie między piętrami (rozszerzenie)
  Ściana Czarny Nieprzejezdna; blokuje ruch i ogień
  Ogień Czerwony Pole z aktywnym pożarem; intensywność rośnie co tick

  Przyjęto następujące założenia ogólne:
    • Pożar startuje w losowym miejscu i rozprzestrzenia się co tick na sąsiednie pola
    • Część wyjść może zostać zablokowana przez ogień w trakcie symulacji
    • Budynek ma jedno piętro w wersji podstawowej; rozszerzenie: wielopiętrowy
    budynek ze schodami
    
5. Reguły działania agentów w kroku symulacji
  W każdym kroku symulacji agenci wykonują zestaw działań według ustalonego schematu:
    1. planowanie trasy: Spokojny wybiera najkrótszą ścieżkę; Panikujący porusza się
    losowo
    2. sprawdzenie ruchu: pole docelowe nie może być ścianą ani zablokowane ogniem
    3. ruch: agent przesuwa się na nowe pole (lub zostaje, jeśli ruch niemożliwy)
    4. aktualizacja paniki: rośnie przy ogniu/zagęszczeniu; maleje przy otwartej przestrzeni
    5. interakcja Altruista–Ranny: podjęcie lub kontynuacja prowadzenia rannego agenta
    6. rozprzestrzenianie ognia: intensywność rośnie; ogień przechodzi na sąsiednie pola
    7. działanie strażaków: redukcja intensywności ognia w zasięgu strażaków
    8. warunek ewakuacji/śmierci: agent na polu Wyjście: ewakuowany; na polu z ogniem:
    Ranny/Martwy
    
6. Parametry modelu
  Symulacja jest kontrolowana przez zestaw parametrów, które mogą być zmieniane w trakcie
  eksperymentów:
    • Liczba agentów i ich podział na typy (% spokojnych, panikujących, altruistów,
    rannych)
    • Miejsce i liczba początkowych ognisk pożaru
    • Prędkość rozprzestrzeniania ognia (jednostki intensywności na tick)
    • Próg intensywności ognia blokujący przejście
    • Liczba i rozmieszczenie wyjść awaryjnych
    • Opóźnienie przybycia strażaków (ticki od wybuchu pożaru)
    • Próg zagęszczenia powodujący efekt tłumu
    • Prawdopodobieństwo zarażenia paniką przy kontakcie
    
7. Warunki początkowe symulacji
  Na początku symulacji:
  • Agenci są losowo rozmieszczeni w pokojach i biurach budynku
  • Każdemu agentowi przypisywany jest typ zachowania zgodnie z parametrami
  podziału
  • Ogień startuje w jednym lub kilku losowych polach (nie przy wyjściach)
  • Strażacy nie są obecni na starcie — pojawiają się po N tickach
  
8. Losowość w modelu
  Element losowości występuje w kilku aspektach symulacji:
    • Początkowe rozmieszczenie agentów w pomieszczeniach
    • Lokalizacja pierwszego ogniska pożaru
    • Kierunek ruchu agentów Panikujących
    • Wynik kontaktu (czy Spokojny zarazi się paniką)
    • Moment i typ wystąpienia zdarzeń losowych
    
9. Zdarzenia losowe
  • Zawalenie korytarza: losowe pole-korytarz staje się ścianą, blokując trasę ewakuacji
  • Eksplozja: nagły wzrost intensywności ognia w promieniu N pól
  • Zablokowanie wyjścia: jedno z wyjść zostaje zamknięte przez ogień
  • Panika masowa: X% spokojnych agentów przechodzi w stan Panikujący
  
10. Metryki obserwowane w symulacji
  W trakcie działania modelu analizowane mogą być następujące wskaźniki:
    • Odsetek ewakuowanych agentów (główna miara sukcesu symulacji)
    • Średni czas ewakuacji (w tickach)
    • Liczba rannych i zabitych według typu agenta
    • Wąskie gardła — pola na mapie z największym zagęszczeniem w czasie symulacji
    • Efektywność altruistów (liczba rannych uratowanych przez agentów tego typu)
    • Zasięg pożaru na końcu symulacji (odsetek spalonej powierzchni budynku)
    
11. Rozważane scenariusze symulacji
  W zależności od wartości parametrów model może prowadzić do różnych wyników ewakuacji:
    • Optymalne warunki: wszystkie wyjścia otwarte, mało panikujących — maksymalna
    przeżywalność
    • Zablokowane wyjście: główne wyjście zajęte przez ogień — agenci muszą znaleźć
    alternatywną drogę
    • Wysoki odsetek panikujących: duże zagęszczenie w korytarzach, wysoka
    śmiertelność
    • Agresywny pożar: szybkie rozprzestrzenianie ognia — czas ewakuacji staje się
    krytyczny
    • Efekt altruistów: wysoka przeżywalność rannych, niższa ogólna prędkość ewakuacji
    
12. Ograniczenia modelu
  • Brak modelowania dymu — jedynie ogień jako bezpośrednie zagrożenie
  • Agenci nie znają pełnego układu budynku — nawigacja wyłącznie przez lokalne
  informacje
  • Nie modeluje się zmęczenia fizycznego wpływającego na prędkość ruchu
  • Jedno piętro w wersji podstawowej (rozbudowa: wielopiętrowy budynek ze schodami)
  • Wszyscy agenci tego samego typu mają identyczne parametry — brak
  indywidualnego zróżnicowania
