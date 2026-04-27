import javax.swing.*;
import javax.swing.border.AbstractBorder;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;

/**
 * Color Blindness Diagnostic System — Main Application
 *
 * Entry point and JFrame host for all panels.
 * Demonstrates: inner classes, CardLayout navigation, inheritance (BasePanel),
 *               anonymous classes, static factory methods, Swing Timer.
 *
 * Authors : MCS OOP Lab Group
 * Version : 2.0
 */
public class Main extends JFrame {

    // ── Theme constants (shared by all inner panels) ─────────────────────────
    static final Color C_BG      = new Color(10,  10,  26);
    static final Color C_BG2     = new Color(45,  10,  62);
    static final Color C_ACCENT  = new Color(127, 119, 221);
    static final Color C_ACCENT2 = new Color(83,  74,  183);
    static final Color C_TEXT    = Color.WHITE;
    static final Color C_SUB     = new Color(175, 169, 236, 200);
    static final Color C_GREEN   = new Color(72,  199, 116);
    static final Color C_AMBER   = new Color(255, 200,  60);
    static final Color C_RED     = new Color(239,  83,  80);

    // ── Application state ────────────────────────────────────────────────────
    private final CardLayout layout = new CardLayout();
    private final JPanel     root   = new JPanel(layout);
    private TestSession      session;                    // set on login
    private TestPanel        activeTestPanel;            // kept to stop timer on exit

    // ── Constructor ──────────────────────────────────────────────────────────
    public Main() {
        super("Color Blindness Diagnostic System — MCS");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(840, 700);
        setLocationRelativeTo(null);
        setResizable(false);
        root.setBackground(C_BG);
        add(root);
        navigate("login");
        setVisible(true);
    }

    // ── Navigation (replaces active panel) ───────────────────────────────────
    void navigate(String screen) {
        // Stop any running test timer before replacing the panel
        if (activeTestPanel != null) {
            activeTestPanel.stopTimer();
            activeTestPanel = null;
        }

        root.removeAll();

        switch (screen) {
            case "login":
                root.add(new LoginPanel(), "s");
                break;
            case "instructions":
                root.add(new InstructionsPanel(), "s");
                break;
            case "test":
                activeTestPanel = new TestPanel();
                root.add(activeTestPanel, "s");
                break;
            case "results":
                GradeResult result = GradeCalculator.analyze(session);
                root.add(new ResultsPanel(result), "s");
                break;
        }

        layout.show(root, "s");
        root.revalidate();
        root.repaint();
    }

    // ═════════════════════════════════════════════════════════════════════════
    // SHARED HELPERS (static — usable from inner panels)
    // ═════════════════════════════════════════════════════════════════════════

    static JTextField styledField(int cols) {
        JTextField f = new JTextField(cols) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                    RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(26, 16, 96, 150));
                g2.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 10, 10);
                super.paintComponent(g);
                g2.dispose();
            }
        };
        f.setOpaque(false);
        f.setForeground(Color.WHITE);
        f.setCaretColor(Color.WHITE);
        f.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        f.setBorder(BorderFactory.createCompoundBorder(
            new RoundBorder(new Color(127, 119, 221, 100), 1, 10),
            BorderFactory.createEmptyBorder(8, 14, 8, 14)));
        return f;
    }

    static JButton styledButton(String text, Color from, Color to) {
        JButton b = new JButton(text) {
            private boolean hovered;
            {
                setOpaque(false);
                setContentAreaFilled(false);
                setBorderPainted(false);
                addMouseListener(new MouseAdapter() {
                    public void mouseEntered(MouseEvent e) { hovered = true;  repaint(); }
                    public void mouseExited (MouseEvent e) { hovered = false; repaint(); }
                });
            }
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                    RenderingHints.VALUE_ANTIALIAS_ON);
                Color f2 = hovered ? from.brighter() : from;
                Color t2 = hovered ? to.brighter()   : to;
                g2.setPaint(new GradientPaint(0, 0, f2, getWidth(), getHeight(), t2));
                g2.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 12, 12);
                super.paintComponent(g);
                g2.dispose();
            }
        };
        b.setForeground(Color.WHITE);
        b.setFont(new Font("Segoe UI", Font.BOLD, 14));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setPreferredSize(new Dimension(200, 42));
        return b;
    }

    // ═════════════════════════════════════════════════════════════════════════
    // BASE PANEL — gradient background + label helpers
    // All screen panels inherit from this class (demonstrates inheritance).
    // ═════════════════════════════════════════════════════════════════════════
    abstract class BasePanel extends JPanel {

        BasePanel() { setOpaque(false); }

        @Override protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setPaint(new GradientPaint(0, 0, C_BG, getWidth(), getHeight(), C_BG2));
            g2.fillRect(0, 0, getWidth(), getHeight());
            g2.dispose();
        }

        JLabel heading(String text, int size) {
            JLabel l = new JLabel(text, JLabel.CENTER);
            l.setFont(new Font("Segoe UI", Font.BOLD, size));
            l.setForeground(C_TEXT);
            return l;
        }

        JLabel subLabel(String text) {
            JLabel l = new JLabel(text, JLabel.CENTER);
            l.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            l.setForeground(C_SUB);
            return l;
        }
    }

    // ═════════════════════════════════════════════════════════════════════════
    // ROUND BORDER  — reusable border for cards and inputs
    // ═════════════════════════════════════════════════════════════════════════
    static class RoundBorder extends AbstractBorder {
        private final Color color;
        private final float thickness;
        private final int   radius;

        RoundBorder(Color color, float thickness, int radius) {
            this.color     = color;
            this.thickness = thickness;
            this.radius    = radius;
        }

        @Override public void paintBorder(Component c, Graphics g,
                                          int x, int y, int w, int h) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.setStroke(new BasicStroke(thickness));
            g2.draw(new RoundRectangle2D.Float(x + thickness/2, y + thickness/2,
                    w - thickness, h - thickness, radius, radius));
            g2.dispose();
        }

        @Override public Insets getBorderInsets(Component c) {
            return new Insets(4, 4, 4, 4);
        }
    }

    // ═════════════════════════════════════════════════════════════════════════
    // LOGIN PANEL
    // ═════════════════════════════════════════════════════════════════════════
    class LoginPanel extends BasePanel {

        LoginPanel() {
            setLayout(new GridBagLayout());

            JPanel card = buildCard();
            card.setPreferredSize(new Dimension(420, 430));
            card.setBorder(BorderFactory.createEmptyBorder(40, 50, 50, 50));

            // Widgets
            JLabel title = heading("CBD System", 30);
            title.setAlignmentX(CENTER_ALIGNMENT);

            JLabel sub = subLabel("Color Blindness Diagnostic  ·  Military College of Signals");
            sub.setAlignmentX(CENTER_ALIGNMENT);

            JLabel nameLabel = subLabel("Enter Patient / Cadet Name");
            nameLabel.setAlignmentX(CENTER_ALIGNMENT);

            JTextField nameField = styledField(20);
            nameField.setMaximumSize(new Dimension(320, 44));
            nameField.setAlignmentX(CENTER_ALIGNMENT);

            JLabel errLabel = new JLabel(" ", JLabel.CENTER);
            errLabel.setForeground(C_RED);
            errLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            errLabel.setAlignmentX(CENTER_ALIGNMENT);

            JButton startBtn = styledButton("Begin Assessment", C_ACCENT2, C_ACCENT);
            startBtn.setMaximumSize(new Dimension(320, 44));
            startBtn.setAlignmentX(CENTER_ALIGNMENT);

            ActionListener login = e -> {
                String name = nameField.getText().trim();
                if (name.isEmpty()) {
                    errLabel.setText("Please enter a name to continue.");
                    nameField.setBorder(BorderFactory.createCompoundBorder(
                        new RoundBorder(C_RED, 1.5f, 10),
                        BorderFactory.createEmptyBorder(8, 14, 8, 14)));
                    return;
                }
                try {
                    session = new TestSession(name);
                    navigate("instructions");
                } catch (IllegalArgumentException ex) {
                    errLabel.setText(ex.getMessage());
                }
            };
            startBtn.addActionListener(login);
            nameField.addActionListener(login);          // Enter key submits

            nameField.addFocusListener(new FocusAdapter() {
                @Override public void focusGained(FocusEvent e) {
                    errLabel.setText(" ");
                    nameField.setBorder(BorderFactory.createCompoundBorder(
                        new RoundBorder(new Color(127, 119, 221, 150), 1, 10),
                        BorderFactory.createEmptyBorder(8, 14, 8, 14)));
                }
            });

            // Layout inside card
            card.add(Box.createVerticalStrut(6));
            card.add(title);
            card.add(Box.createVerticalStrut(8));
            card.add(sub);
            card.add(Box.createVerticalStrut(44));
            card.add(nameLabel);
            card.add(Box.createVerticalStrut(10));
            card.add(nameField);
            card.add(Box.createVerticalStrut(6));
            card.add(errLabel);
            card.add(Box.createVerticalStrut(20));
            card.add(startBtn);

            add(card);
        }
    }

    // ═════════════════════════════════════════════════════════════════════════
    // INSTRUCTIONS PANEL
    // ═════════════════════════════════════════════════════════════════════════
    class InstructionsPanel extends BasePanel {

        InstructionsPanel() {
            setLayout(new BorderLayout(0, 0));
            setBorder(BorderFactory.createEmptyBorder(40, 70, 40, 70));

            // Header
            JPanel header = new JPanel();
            header.setOpaque(false);
            header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
            JLabel h = heading("Test Instructions", 26);
            h.setAlignmentX(CENTER_ALIGNMENT);
            JLabel s = subLabel("Ishihara Color Vision Assessment — "
                    + session.getTotalPlates() + " plates  ·  "
                    + session.getTimePerPlate() + "s per plate");
            s.setAlignmentX(CENTER_ALIGNMENT);
            header.add(h);
            header.add(Box.createVerticalStrut(6));
            header.add(s);

            // Instructions list
            String[] lines = {
                "1.   You will be shown " + session.getTotalPlates()
                        + " Ishihara color plates presented in random order.",
                "2.   Each plate is displayed for up to " + session.getTimePerPlate()
                        + " seconds — a countdown timer is shown at the top.",
                "3.   Type the number you see inside the plate and press Submit (or Enter).",
                "4.   If you cannot identify any number, click Skip.",
                "5.   The timer resets automatically each time you advance to the next plate.",
                "6.   Answer honestly — do not guess — for accurate diagnostic results.",
                "7.   A detailed report will be shown immediately after the final plate."
            };

            JPanel listPanel = new JPanel();
            listPanel.setOpaque(false);
            listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
            listPanel.setBorder(BorderFactory.createEmptyBorder(26, 10, 26, 10));

            for (String line : lines) {
                JLabel l = new JLabel(line);
                l.setForeground(C_TEXT);
                l.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                l.setBorder(BorderFactory.createEmptyBorder(8, 0, 8, 0));
                listPanel.add(l);
            }

            // Buttons
            JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
            btnRow.setOpaque(false);
            JButton back  = styledButton("Back", new Color(50,50,70), new Color(70,70,100));
            JButton start = styledButton("Start Test →", C_ACCENT2, C_ACCENT);
            back.addActionListener(e  -> navigate("login"));
            start.addActionListener(e -> { session.begin(); navigate("test"); });
            btnRow.add(back);
            btnRow.add(start);

            add(header,    BorderLayout.NORTH);
            add(listPanel, BorderLayout.CENTER);
            add(btnRow,    BorderLayout.SOUTH);
        }
    }

    // ═════════════════════════════════════════════════════════════════════════
    // TEST PANEL  — core diagnostic screen
    // ═════════════════════════════════════════════════════════════════════════
    class TestPanel extends BasePanel {

        private final JLabel       plateImage  = new JLabel("", JLabel.CENTER);
        private final JLabel       timerLabel  = new JLabel("13", JLabel.CENTER);
        private final JLabel       progressLbl = new JLabel("", JLabel.RIGHT);
        private final JProgressBar progressBar = new JProgressBar(0, session.getTotalPlates());
        private final JTextField   answerField;
        private javax.swing.Timer  countdown;
        private int                secondsLeft;
        private boolean            stopped     = false;

        TestPanel() {
            setLayout(new BorderLayout(0, 12));
            setBorder(BorderFactory.createEmptyBorder(14, 20, 14, 20));

            answerField = styledField(10);
            styleProgressBar();

            add(buildTopBar(),    BorderLayout.NORTH);
            add(plateImage,       BorderLayout.CENTER);
            add(buildBottomBar(), BorderLayout.SOUTH);

            loadCurrentPlate();
        }

        // ── Build sub-panels ─────────────────────────────────────────────────
        private JPanel buildTopBar() {
            JPanel p = new JPanel(new BorderLayout(8, 0));
            p.setOpaque(false);

            JLabel nameTag = new JLabel("  " + session.getPatientName());
            nameTag.setForeground(C_SUB);
            nameTag.setFont(new Font("Segoe UI", Font.PLAIN, 13));

            timerLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
            timerLabel.setForeground(C_TEXT);

            progressLbl.setForeground(C_SUB);
            progressLbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));

            p.add(nameTag,     BorderLayout.WEST);
            p.add(timerLabel,  BorderLayout.CENTER);
            p.add(progressLbl, BorderLayout.EAST);
            p.add(progressBar, BorderLayout.SOUTH);
            return p;
        }

        private JPanel buildBottomBar() {
            JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 14, 8));
            p.setOpaque(false);

            JLabel prompt = new JLabel("What number do you see?");
            prompt.setForeground(C_SUB);
            prompt.setFont(new Font("Segoe UI", Font.ITALIC, 14));

            answerField.setPreferredSize(new Dimension(110, 38));

            JButton submit = styledButton("Submit", C_ACCENT2, C_ACCENT);
            submit.setPreferredSize(new Dimension(110, 38));

            JButton skip = styledButton("Skip", new Color(50,50,70), new Color(70,70,100));
            skip.setPreferredSize(new Dimension(90, 38));

            submit.addActionListener(e -> submitAnswer());
            skip.addActionListener(e   -> { session.skipCurrent(); next(); });
            answerField.addActionListener(e -> submitAnswer());

            p.add(prompt);
            p.add(answerField);
            p.add(submit);
            p.add(skip);
            return p;
        }

        private void styleProgressBar() {
            progressBar.setForeground(C_ACCENT);
            progressBar.setBackground(new Color(255, 255, 255, 30));
            progressBar.setPreferredSize(new Dimension(0, 6));
            progressBar.setBorderPainted(false);
            progressBar.setStringPainted(false);
        }

        // ── Logic ────────────────────────────────────────────────────────────
        private void submitAnswer() {
            String raw = answerField.getText().trim();
            if (raw.isEmpty()) {
                session.skipCurrent();
                next();
                return;
            }
            try {
                int val = Integer.parseInt(raw);
                session.recordAnswer(val);
                next();
            } catch (NumberFormatException ex) {
                answerField.setBorder(BorderFactory.createCompoundBorder(
                    new RoundBorder(C_RED, 1.5f, 10),
                    BorderFactory.createEmptyBorder(8, 14, 8, 14)));
            }
        }

        private void next() {
            stopTimer();
            if (session.isCompleted()) navigate("results");
            else loadCurrentPlate();
        }

        private void loadCurrentPlate() {
            Plate p = session.getCurrentPlate();
            if (p == null) { navigate("results"); return; }

            int idx   = session.getCurrentIndex();
            int total = session.getTotalPlates();
            progressLbl.setText("Plate " + (idx + 1) + " of " + total + "  ");
            progressBar.setValue(idx);

            ImageIcon icon = loadImage(p.getFileName());
            if (icon != null) {
                plateImage.setIcon(icon);
                plateImage.setText("");
            } else {
                plateImage.setIcon(null);
                plateImage.setText("<html><center><font color='#AFA9EC' size='5'>"
                        + "Image not found: " + p.getFileName()
                        + "<br><br><small>Place images in the same folder as Main.java</small>"
                        + "</font></center></html>");
            }

            resetInputField();
            answerField.requestFocusInWindow();
            startTimer();
        }

        private void resetInputField() {
            answerField.setText("");
            answerField.setBorder(BorderFactory.createCompoundBorder(
                new RoundBorder(new Color(127, 119, 221, 100), 1, 10),
                BorderFactory.createEmptyBorder(8, 14, 8, 14)));
        }

        // ── Timer ────────────────────────────────────────────────────────────
        private void startTimer() {
            secondsLeft = session.getTimePerPlate();
            updateTimerDisplay();
            countdown = new javax.swing.Timer(1000, e -> {
                if (stopped) return;
                secondsLeft--;
                updateTimerDisplay();
                if (secondsLeft <= 0) {
                    session.skipCurrent();
                    next();
                }
            });
            countdown.start();
        }

        void stopTimer() {
            stopped = true;
            if (countdown != null && countdown.isRunning()) countdown.stop();
        }

        private void updateTimerDisplay() {
            timerLabel.setText(secondsLeft + "s");
            if      (secondsLeft <= 4) timerLabel.setForeground(C_RED);
            else if (secondsLeft <= 8) timerLabel.setForeground(C_AMBER);
            else                       timerLabel.setForeground(C_TEXT);
        }

        // ── Image loading (tries multiple paths) ─────────────────────────────
        private ImageIcon loadImage(String fileName) {
            // 1. Classpath (works when images are in same dir as .class)
            URL url = getClass().getResource(fileName);
            if (url != null) return scale(new ImageIcon(url));

            url = getClass().getResource("/" + fileName);
            if (url != null) return scale(new ImageIcon(url));

            // 2. Filesystem relative paths (works from different working dirs)
            String[] candidates = {
                fileName,
                "main/" + fileName,
                "src/main/" + fileName
            };
            for (String path : candidates) {
                File f = new File(path);
                if (f.exists()) return scale(new ImageIcon(f.getAbsolutePath()));
            }
            return null;
        }

        private ImageIcon scale(ImageIcon icon) {
            Image img = icon.getImage().getScaledInstance(560, 420, Image.SCALE_SMOOTH);
            return new ImageIcon(img);
        }
    }

    // ═════════════════════════════════════════════════════════════════════════
    // RESULTS PANEL
    // ═════════════════════════════════════════════════════════════════════════
    class ResultsPanel extends BasePanel {

        ResultsPanel(GradeResult result) {
            setLayout(new BorderLayout(0, 0));
            setBorder(BorderFactory.createEmptyBorder(24, 36, 24, 36));

            add(buildHeader(result), BorderLayout.NORTH);
            add(buildTable(),        BorderLayout.CENTER);
            add(buildFooter(result), BorderLayout.SOUTH);
        }

        // ── Header: grade badge + summary ────────────────────────────────────
        private JPanel buildHeader(GradeResult r) {
            JPanel p = new JPanel();
            p.setOpaque(false);
            p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));

            JLabel title = heading("Assessment Complete", 24);
            title.setAlignmentX(CENTER_ALIGNMENT);

            JLabel nameTag = subLabel("Patient: " + session.getPatientName()
                    + "   ·   Duration: " + session.getElapsedSeconds() + "s");
            nameTag.setAlignmentX(CENTER_ALIGNMENT);

            // Circular grade badge (custom-painted JLabel)
            Color badgeColor = gradeColor(r.getGrade());
            JLabel badge = new JLabel(r.getGrade(), JLabel.CENTER) {
                @Override protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                        RenderingHints.VALUE_ANTIALIAS_ON);
                    // filled translucent circle
                    g2.setColor(new Color(badgeColor.getRed(),
                                          badgeColor.getGreen(),
                                          badgeColor.getBlue(), 35));
                    g2.fillOval(2, 2, getWidth()-4, getHeight()-4);
                    // outline
                    g2.setColor(badgeColor);
                    g2.setStroke(new BasicStroke(3));
                    g2.drawOval(4, 4, getWidth()-8, getHeight()-8);
                    super.paintComponent(g);
                    g2.dispose();
                }
            };
            badge.setFont(new Font("Segoe UI", Font.BOLD, 42));
            badge.setForeground(badgeColor);
            badge.setPreferredSize(new Dimension(90, 90));
            badge.setMaximumSize(new Dimension(90, 90));
            badge.setAlignmentX(CENTER_ALIGNMENT);

            JLabel score = heading(r.getCorrect() + " / " + r.getTotal()
                    + " correct   (" + String.format("%.1f", r.getAccuracy()) + "%)", 17);
            score.setAlignmentX(CENTER_ALIGNMENT);

            JLabel sev = new JLabel("Severity: " + r.getSeverity()
                    + "   |   Type: " + r.getDeficiencyType(), JLabel.CENTER);
            sev.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            sev.setForeground(severityColor(r.getSeverity()));
            sev.setAlignmentX(CENTER_ALIGNMENT);

            p.add(title);
            p.add(Box.createVerticalStrut(4));
            p.add(nameTag);
            p.add(Box.createVerticalStrut(14));
            p.add(badge);
            p.add(Box.createVerticalStrut(10));
            p.add(score);
            p.add(Box.createVerticalStrut(4));
            p.add(sev);
            p.add(Box.createVerticalStrut(14));
            return p;
        }

        // ── Plate-by-plate breakdown table ───────────────────────────────────
        private JScrollPane buildTable() {
            ArrayList<Plate>   plates  = session.getPlates();
            ArrayList<Integer> answers = session.getAnswers();

            String[] cols = {"Plate #", "Category", "Your Answer", "Correct Answer", "Result"};
            Object[][] data = new Object[plates.size()][5];

            for (int i = 0; i < plates.size(); i++) {
                Plate  pl  = plates.get(i);
                int    ans = (i < answers.size()) ? answers.get(i) : -1;
                boolean ok = (ans != -1) && pl.checkAnswer(ans);

                data[i][0] = pl.getPlateNumber();
                data[i][1] = pl.getCategory().getDisplayName();
                data[i][2] = (ans == -1) ? "—  Skipped" : String.valueOf(ans);
                data[i][3] = pl.getCorrectAnswer();
                data[i][4] = ok ? "✓ Correct" : (ans == -1 ? "— Skipped" : "✗ Incorrect");
            }

            JTable table = new JTable(data, cols) {
                @Override public boolean isCellEditable(int r, int c) { return false; }
            };

            // Custom cell renderer for colour-coded Result column
            DefaultTableCellRenderer resultRenderer = new DefaultTableCellRenderer() {
                @Override public Component getTableCellRendererComponent(
                        JTable t, Object val, boolean sel, boolean foc, int row, int col) {
                    super.getTableCellRendererComponent(t, val, sel, foc, row, col);
                    String v = val.toString();
                    if (v.startsWith("✓"))      setForeground(C_GREEN);
                    else if (v.startsWith("—")) setForeground(C_AMBER);
                    else                        setForeground(C_RED);
                    return this;
                }
            };

            Color tableBg = new Color(15, 12, 40);
            resultRenderer.setBackground(tableBg);
            resultRenderer.setHorizontalAlignment(SwingConstants.CENTER);

            DefaultTableCellRenderer centeredRenderer = new DefaultTableCellRenderer();
            centeredRenderer.setBackground(tableBg);
            centeredRenderer.setForeground(C_TEXT);
            centeredRenderer.setHorizontalAlignment(SwingConstants.CENTER);

            table.getColumnModel().getColumn(4).setCellRenderer(resultRenderer);
            for (int c = 0; c < 4; c++) {
                table.getColumnModel().getColumn(c).setCellRenderer(centeredRenderer);
            }
            table.getColumnModel().getColumn(0).setPreferredWidth(60);
            table.getColumnModel().getColumn(1).setPreferredWidth(220);
            table.getColumnModel().getColumn(4).setPreferredWidth(100);

            table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            table.setRowHeight(28);
            table.setBackground(tableBg);
            table.setForeground(C_TEXT);
            table.setGridColor(new Color(127, 119, 221, 50));
            table.setShowVerticalLines(false);
            table.setSelectionBackground(new Color(127, 119, 221, 80));
            table.setSelectionForeground(Color.WHITE);
            table.setFocusable(false);

            table.getTableHeader().setBackground(new Color(26, 16, 96));
            table.getTableHeader().setForeground(C_TEXT);
            table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
            table.getTableHeader().setPreferredSize(new Dimension(0, 32));

            JScrollPane sp = new JScrollPane(table);
            sp.setOpaque(false);
            sp.getViewport().setBackground(tableBg);
            sp.setBorder(new RoundBorder(new Color(127, 119, 221, 60), 1, 8));
            return sp;
        }

        // ── Footer: recommendation + action buttons ───────────────────────────
        private JPanel buildFooter(GradeResult r) {
            JPanel p = new JPanel();
            p.setOpaque(false);
            p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));

            JLabel rec = new JLabel("<html><center>" + r.getRecommendation() + "</center></html>",
                    JLabel.CENTER);
            rec.setForeground(C_SUB);
            rec.setFont(new Font("Segoe UI", Font.ITALIC, 13));
            rec.setAlignmentX(CENTER_ALIGNMENT);
            rec.setBorder(BorderFactory.createEmptyBorder(12, 0, 14, 0));

            JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
            btnRow.setOpaque(false);
            JButton again = styledButton("Take Test Again", C_ACCENT2, C_ACCENT);
            JButton exit  = styledButton("Exit", new Color(50,50,70), new Color(70,70,100));
            again.addActionListener(e -> navigate("login"));
            exit.addActionListener(e  -> System.exit(0));
            btnRow.add(again);
            btnRow.add(exit);

            p.add(rec);
            p.add(btnRow);
            return p;
        }

        // ── Colour helpers ───────────────────────────────────────────────────
        private Color gradeColor(String g) {
            switch (g) {
                case "A": return C_GREEN;
                case "B": return new Color(100, 215, 145);
                case "C": return C_AMBER;
                case "D": return new Color(255, 150,  50);
                default:  return C_RED;
            }
        }

        private Color severityColor(String s) {
            switch (s) {
                case "Normal":   return C_GREEN;
                case "Mild":     return C_AMBER;
                case "Moderate": return new Color(255, 140, 40);
                default:         return C_RED;
            }
        }
    }

    // ═════════════════════════════════════════════════════════════════════════
    // SHARED CARD BUILDER (glassmorphism card panel)
    // ═════════════════════════════════════════════════════════════════════════
    static JPanel buildCard() {
        JPanel card = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                    RenderingHints.VALUE_ANTIALIAS_ON);
                // translucent fill
                g2.setColor(new Color(255, 255, 255, 14));
                g2.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 18, 18);
                // glowing border
                g2.setColor(new Color(127, 119, 221, 90));
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawRoundRect(1, 1, getWidth()-3, getHeight()-3, 18, 18);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        card.setOpaque(false);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        return card;
    }

    // ═════════════════════════════════════════════════════════════════════════
    // ENTRY POINT
    // ═════════════════════════════════════════════════════════════════════════
    public static void main(String[] args) {
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");
        SwingUtilities.invokeLater(Main::new);
    }
}
