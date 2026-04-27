import java.util.ArrayList;

/**
 * Analyzes a completed TestSession and produces a GradeResult.
 *
 * Demonstrates: static utility pattern, private constructor (no instantiation),
 *               algorithmic logic, switch statements.
 */
public class GradeCalculator {

    private GradeCalculator() { /* utility class — never instantiated */ }

    // ── Public API ────────────────────────────────────────────────────────────
    public static GradeResult analyze(TestSession session) {
        ArrayList<Plate>   plates  = session.getPlates();
        ArrayList<Integer> answers = session.getAnswers();
        int total = plates.size();

        int correct      = 0;
        int protanErrors = 0, protanTotal = 0;
        int deutanErrors = 0, deutanTotal = 0;

        for (int i = 0; i < total; i++) {
            Plate plate  = plates.get(i);
            int   answer = (i < answers.size()) ? answers.get(i) : -1;
            boolean ok   = (answer != -1) && plate.checkAnswer(answer);

            if (ok) {
                correct++;
            } else {
                switch (plate.getCategory()) {
                    case PROTAN: protanErrors++; break;
                    case DEUTAN: deutanErrors++; break;
                    default:                     break;
                }
            }

            switch (plate.getCategory()) {
                case PROTAN: protanTotal++; break;
                case DEUTAN: deutanTotal++; break;
                default:                    break;
            }
        }

        double accuracy     = (total > 0) ? (100.0 * correct / total) : 0.0;
        String grade        = gradeFor(accuracy);
        String severity     = severityFor(correct, total);
        String defType      = deficiencyFor(protanErrors, protanTotal,
                                            deutanErrors, deutanTotal,
                                            correct,      total);
        String recommendation = recommend(severity, defType);

        return new GradeResult(correct, total, accuracy, grade, severity, defType, recommendation);
    }

    // ── Private helpers ───────────────────────────────────────────────────────
    private static String gradeFor(double pct) {
        if (pct >= 87.5) return "A";
        if (pct >= 75.0) return "B";
        if (pct >= 62.5) return "C";
        if (pct >= 37.5) return "D";
        return "F";
    }

    private static String severityFor(int correct, int total) {
        double r = (double) correct / total;
        if (r >= 0.875) return "Normal";
        if (r >= 0.625) return "Mild";
        if (r >= 0.375) return "Moderate";
        return "Severe";
    }

    private static String deficiencyFor(int pe, int pt, int de, int dt,
                                         int correct, int total) {
        if ((double) correct / total >= 0.875) return "None";

        double pr = (pt > 0) ? (double) pe / pt : 0.0;
        double dr = (dt > 0) ? (double) de / dt : 0.0;

        if (pr > 0.5 && dr > 0.5) return "Mixed (Protan + Deutan)";
        if (pr > 0.5)              return "Protanopia / Protanomaly (Red-Deficient)";
        if (dr > 0.5)              return "Deuteranopia / Deuteranomaly (Green-Deficient)";
        return "Non-specific Color Deficiency";
    }

    private static String recommend(String severity, String defType) {
        if ("Normal".equals(severity)) {
            return "Your color vision appears normal. No further action is required.";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("A ").append(severity.toLowerCase())
          .append(" color vision deficiency has been detected. ");
        if (defType.contains("Protan")) {
            sb.append("You may have difficulty distinguishing red hues from green. ");
        } else if (defType.contains("Deutan")) {
            sb.append("You may have difficulty distinguishing green hues from red. ");
        } else if (defType.contains("Mixed")) {
            sb.append("Both red and green color channels appear affected. ");
        }
        sb.append("Please consult an ophthalmologist for a comprehensive clinical evaluation.");
        return sb.toString();
    }
}
