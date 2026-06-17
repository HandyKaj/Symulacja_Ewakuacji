# Symulacja Ewakuacji Budynku podczas Pożaru

Symulacja agentowa napisana w Javie z wykorzystaniem programowania obiektowego. Program modeluje proces ewakuacji budynku podczas pożaru, uwzględniając różne typy uczestników zdarzenia oraz dynamiczne rozprzestrzenianie się ognia.

## Opis symulacji

Środowisko symulacji to dwuwymiarowa siatka (plansza) reprezentująca budynek, składająca się z korytarzy, pomieszczeń, ścian i wyjścia awaryjnego. Pożar startuje w losowym miejscu i rozprzestrzenia się na sąsiednie komórki z każdym tickiem symulacji, zwiększając swoją intensywność. Komórki o wysokiej intensywności ognia stają się nieprzejezdne.

W symulacji występuje pięć typów agentów:

- **Calm (Spokojny)** — wybiera najkrótszą drogę do wyjścia algorytmem BFS, omija ogień, podąża za liderem (strażakiem lub altruistą) jeśli jest w pobliżu.
- **Panicking (Panikujący)** — porusza się chaotycznie, zaraża paniką spokojnych agentów w swoim otoczeniu.
- **Altruist (Altruista)** — aktywnie poszukuje rannych w polu widzenia, prowadzi ich do wyjścia z obniżoną prędkością.
- **Injured (Ranny)** — porusza się z prędkością 0.5, wymaga pomocy do skutecznej ewakuacji.
- **Firefighter (Strażak)** — gasi ogień w swoim zasięgu, naprowadza rannych w kierunku wyjścia, posiada wyższą tolerancję na ogień.

Symulacja zbiera statystyki: liczbę ewakuowanych i zabitych agentów (łącznie oraz w podziale na typ), a także średni czas ewakuacji.

📄 Pełny, szczegółowy opis modelu (atrybuty agentów, interakcje, reguły kroku symulacji, parametry, ograniczenia) znajduje się w pliku [`opis_symulacji.md`](opis_symulacji.md).

## Wymagania

- Java 21 lub nowsza (JDK)
- Gradle (dołączony wrapper, nie wymaga osobnej instalacji)

## Quick start

1. Sklonuj repozytorium:
   ```
   git clone <adres-repozytorium>
   cd Symulacja_Ewakuacji
   ```

2. Uruchom aplikację za pomocą Gradle:
   ```
   ./gradlew run
   ```
   Na systemie Windows:
   ```
   gradlew.bat run
   ```

3. Po uruchomieniu okna aplikacji ustaw liczbę agentów każdego typu i kliknij **„Uruchom symulację”**.

## Sample run

Przykładowa konfiguracja do przetestowania:

| Typ agenta   | Liczba |
|--------------|--------|
| Calm         | 40     |
| Panicking    | 20     |
| Altruist     | 10     |
| Injured      | 6      |
| Firefighter  | 8      |

Po kliknięciu **„Uruchom symulację”**:

- Przycisk **▶ Tick** wykonuje jeden krok symulacji.
- Przycisk **⟳ Auto-run** uruchamia automatyczne odtwarzanie kolejnych ticków.
- Panel boczny wyświetla aktualne statystyki (liczba osób w budynku, rannych, ewakuowanych, zabitych).
- Wykresy na dole ekranu pokazują średni poziom paniki w czasie oraz statystyki ewakuacji/śmierci w podziale na typ agenta.

Symulacja zakończy się automatycznie, gdy wszyscy agenci zostaną ewakuowani lub zginą.

## Testy

Projekt zawiera testy jednostkowe (JUnit 5) pokrywające logikę ognia, planszy, agentów, konfiguracji i metryk. Aby je uruchomić:

```
./gradlew test
```

## Struktura projektu

```
src/main/java/po/simulation/
├── Main.java                 — punkt wejścia, GUI Swing
├── Simulation.java           — główna logika symulacji
├── agent/                    — klasy agentów (Calm, Panicking, Altruist, Injured, Firefighter)
├── board/                    — plansza i komórki
├── fire/                     — model ognia
├── config/                   — konfiguracja symulacji
├── metrics/                  — zbieranie statystyk
├── model/                    — enumy (AgentState, CellType)
└── ui/                       — komponenty interfejsu graficznego

src/test/java/po/simulation/  — testy jednostkowe
```
