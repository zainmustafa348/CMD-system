import java.util.ArrayList;
import java.util.Collections;

/**
 * Manages the state of a single diagnostic test session.
 *
 * Demonstrates: encapsulation, ArrayList, Collections.shuffle,
 *               defensive copies, IllegalArgumentException.
 */
public class TestSession {

    private static final int TIME_PER_PLATE = 13; // seconds per plate

    // ── Private state ────────────────────────────────────────────────────────
    private final String           patientName;
    private final ArrayList<Plate> plates;
    private final ArrayList<Integer> answers; // -1 = skipped / unanswered
    private int  currentIndex;
    private long startTimeMillis;
    private boolean active;

    // ── Constructor ──────────────────────────────────────────────────────────
    public TestSession(String patientName) {
        if (patientName == null || patientName.trim().isEmpty()) {
            throw new IllegalArgumentException("Patient name cannot be empty.");
        }
        this.patientName    = patientName.trim();
        this.plates         = buildPlateList();
        Collections.shuffle(this.plates);              // random order each run
        this.answers        = new ArrayList<>(Collections.nCopies(plates.size(), -1));
        this.currentIndex   = 0;
        this.active         = false;
    }

    // ── Build the fixed plate set ────────────────────────────────────────────
    private ArrayList<Plate> buildPlateList() {
        ArrayList<Plate> list = new ArrayList<>();
        list.add(new Plate("plate_2.jpg",   2, Plate.ColorCategory.GENERAL,   2));
        list.add(new Plate("plate_6.jpg",   6, Plate.ColorCategory.PROTAN,    6));
        list.add(new Plate("plate_7.jpg",   7, Plate.ColorCategory.GENERAL,   7));
        list.add(new Plate("plate_8.jpg",   8, Plate.ColorCategory.PROTAN,    8));
        list.add(new Plate("plate_26.jpg", 26, Plate.ColorCategory.DEUTAN,   26));
        list.add(new Plate("plate_26b.jpg",26, Plate.ColorCategory.DEUTAN,  261));
        list.add(new Plate("plate_35.jpg", 35, Plate.ColorCategory.PROTAN,   35));
        list.add(new Plate("plate_48.jpg", 48, Plate.ColorCategory.GENERAL,  48));
        return list;
    }

    // ── Session lifecycle ─────────────────────────────────────────────────────
    public void begin() {
        startTimeMillis = System.currentTimeMillis();
        active = true;
    }

    // ── Navigation ────────────────────────────────────────────────────────────
    public Plate getCurrentPlate() {
        return (currentIndex < plates.size()) ? plates.get(currentIndex) : null;
    }

    public void recordAnswer(int answer) {
        if (currentIndex < answers.size()) {
            answers.set(currentIndex, answer);
        }
        advance();
    }

    public void skipCurrent() {
        recordAnswer(-1);
    }

    private void advance() {
        currentIndex++;
        if (currentIndex >= plates.size()) {
            active = false;
        }
    }

    // ── Query ─────────────────────────────────────────────────────────────────
    public boolean isCompleted()     { return currentIndex >= plates.size(); }
    public boolean isActive()        { return active;          }
    public int     getCurrentIndex() { return currentIndex;    }
    public int     getTotalPlates()  { return plates.size();   }
    public int     getTimePerPlate() { return TIME_PER_PLATE;  }
    public String  getPatientName()  { return patientName;     }

    /** Returns a defensive copy so callers cannot mutate session state. */
    public ArrayList<Plate>   getPlates()  { return new ArrayList<>(plates);  }
    public ArrayList<Integer> getAnswers() { return new ArrayList<>(answers); }

    public long getElapsedSeconds() {
        if (startTimeMillis == 0) return 0;
        return (System.currentTimeMillis() - startTimeMillis) / 1000;
    }

    public int getCorrectCount() {
        int count = 0;
        for (int i = 0; i < plates.size(); i++) {
            int ans = (i < answers.size()) ? answers.get(i) : -1;
            if (ans != -1 && plates.get(i).checkAnswer(ans)) count++;
        }
        return count;
    }
}
