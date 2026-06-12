# AmarBank

A Java **Swing** desktop banking application backed by **SQLite**.

The project is self-contained and needs **no build tool** (no Maven/Gradle).
A small launcher script downloads the SQLite JDBC driver into `lib/` if it is
missing, then compiles with `javac` and runs with `java`. The xerial
`sqlite-jdbc` jar bundles native libraries for every OS, so the same driver
works on macOS, Linux and Windows.

## Requirements

- **JDK 17 or newer** (`javac` and `java` on your `PATH`).
- `curl` or `wget` on macOS/Linux, or PowerShell on Windows (used once to
  download the driver). Internet access is only needed the first time.

## Run from the command line

macOS / Linux (or Git Bash on Windows):

```bash
./run.sh
```

Windows (Command Prompt / PowerShell):

```bat
run.bat
```

On first run the script downloads `lib/sqlite-jdbc.jar`, compiles the sources
into `out/`, and launches the Swing UI.

## Run in IntelliJ IDEA (any OS)

1. Clone the repository.
2. Run `./run.sh` (or `run.bat`) **once** so `lib/sqlite-jdbc.jar` is
   downloaded — the IntelliJ module references that jar.
3. In IntelliJ: **File -> Open** and select the project folder. The committed
   module (`AmarBank.iml`) is configured with `src/main/java` as the sources
   root and `lib/sqlite-jdbc.jar` as a library, on an OS-independent relative
   path.
4. Set the Project SDK to your installed JDK 17+
   (**File -> Project Structure -> Project**) if prompted.
5. Open `src/main/java/com/amarbank/Main.java` and click the green **Run** arrow.

## Default login

| Username | Password   |
| -------- | ---------- |
| `admin`  | `admin123` |

## Data storage

The SQLite database is created automatically at runtime in `db_file/amarbank.db`
(relative to the project root). The `lib/` and `db_file/` folders are
git-ignored, so each clone downloads its own driver and creates its own database.

## Project layout

```
src/main/java/com/amarbank/
├── Main.java              # entry point (launches the Swing UI)
├── db/                    # SQLite persistence
├── model/                 # account domain models
├── service/               # bank operations
├── ui/                    # Swing pages
├── util/                  # validators
└── exception/             # custom exceptions
```
