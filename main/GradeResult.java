/**
 * Immutable data class holding the fully-calculated test results.
 *
 * Demonstrates: immutability, encapsulation, toString().
 */
public class GradeResult {

    private final int    correct;
    private final int    total;
    private final double accuracy;
    private final String grade;
    private final String severity;
    private final String deficiencyType;
    private final String recommendation;

    // ── Constructor (package-visible; created only by GradeCalculator) ───────
    GradeResult(int correct, int total, double accuracy,
                String grade, String severity,
                String deficiencyType, String recommendation) {
        this.correct        = correct;
        this.total          = total;
        this.accuracy       = accuracy;
        this.grade          = grade;
        this.severity       = severity;
        this.deficiencyType = deficiencyType;
        this.recommendation = recommendation;
    }

    // ── Getters ──────────────────────────────────────────────────────────────
    public int    getCorrect()        { return correct;        }
    public int    getTotal()          { return total;          }
    public double getAccuracy()       { return accuracy;       }
    public String getGrade()          { return grade;          }
    public String getSeverity()       { return severity;       }
    public String getDeficiencyType() { return deficiencyType; }
    public String getRecommendation() { return recommendation; }

    @Override
    public String toString() {
        return String.format("Score: %d/%d (%.1f%%) | Grade: %s | %s | %s",
                correct, total, accuracy, grade, severity, deficiencyType);
    }
}
