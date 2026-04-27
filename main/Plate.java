import java.util.Objects;

/**
 * Represents one Ishihara color plate used in the diagnostic test.
 *
 * Demonstrates: encapsulation, enum, method overriding (equals / hashCode / toString).
 */
public class Plate {

    // ── Color-deficiency category ────────────────────────────────────────────
    public enum ColorCategory {
        GENERAL("General Screening"),
        PROTAN ("Protan – Red Deficiency"),
        DEUTAN ("Deutan – Green Deficiency");

        private final String displayName;

        ColorCategory(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() { return displayName; }
    }

    // ── Private fields (encapsulation) ───────────────────────────────────────
    private final String        fileName;
    private final int           correctAnswer;
    private final ColorCategory category;
    private final int           plateNumber;

    // ── Constructor ──────────────────────────────────────────────────────────
    public Plate(String fileName, int correctAnswer, ColorCategory category, int plateNumber) {
        this.fileName      = fileName;
        this.correctAnswer = correctAnswer;
        this.category      = category;
        this.plateNumber   = plateNumber;
    }

    // ── Getters ──────────────────────────────────────────────────────────────
    public String        getFileName()      { return fileName;      }
    public int           getCorrectAnswer() { return correctAnswer; }
    public ColorCategory getCategory()      { return category;      }
    public int           getPlateNumber()   { return plateNumber;   }

    // ── Business logic ───────────────────────────────────────────────────────
    public boolean checkAnswer(int userInput) {
        return userInput == correctAnswer;
    }

    // ── Object overrides ─────────────────────────────────────────────────────
    @Override
    public String toString() {
        return String.format("Plate #%d [%s] — Answer: %d",
                plateNumber, category.getDisplayName(), correctAnswer);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Plate)) return false;
        return plateNumber == ((Plate) o).plateNumber;
    }

    @Override
    public int hashCode() {
        return Objects.hash(plateNumber);
    }
}
