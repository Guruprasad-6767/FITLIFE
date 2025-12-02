import fitlife.core.CalorieTracker;
import fitlife.core.StepsTracker;
import fitlife.core.WaterTracker;
import fitlife.core.Tracker;
import fitlife.logic.BMITracker;
import fitlife.config.GeminiConfig;
import fitlife.ai.MetricsExtractor;
import fitlife.ai.GeminiAnalyzer;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class FitLifeGUI extends JFrame {

    public FitLifeGUI() {
        setTitle("FitLife - AI-Powered Health Tracker");
        setSize(400, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel(new GridLayout(8, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton mealBtn = new JButton("📝 Log Meal");
        JButton stepsBtn = new JButton("👟 Log Steps");
        JButton waterBtn = new JButton("💧 Log Water");
        JButton bmiBtn = new JButton("⚖️ BMI Calculator");
        JButton weeklyBtn = new JButton("📊 Weekly Summary");
        JButton aiBtn = new JButton("🤖 AI Health Analysis");
        JButton exitBtn = new JButton("❌ Exit");

        // Styling
        Font btnFont = new Font("Arial", Font.BOLD, 12);
        for (JButton btn : new JButton[]{mealBtn, stepsBtn, waterBtn, bmiBtn, weeklyBtn, aiBtn, exitBtn}) {
            btn.setFont(btnFont);
            btn.setFocusPainted(false);
            btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        }

        mealBtn.addActionListener(e -> logMeal());
        stepsBtn.addActionListener(e -> logSteps());
        waterBtn.addActionListener(e -> logWater());
        bmiBtn.addActionListener(e -> runBMI());
        weeklyBtn.addActionListener(e -> showWeeklySummary());
        aiBtn.addActionListener(e -> analyzeWithAI());
        exitBtn.addActionListener(e -> System.exit(0));

        panel.add(mealBtn);
        panel.add(stepsBtn);
        panel.add(waterBtn);
        panel.add(bmiBtn);
        panel.add(weeklyBtn);
        panel.add(aiBtn);
        panel.add(exitBtn);

        add(panel);
    }

    private void logMeal() {
        JTextField dateField = new JTextField();
        JTextField mealField = new JTextField();
        JTextField qtyField = new JTextField();
        JTextField categoryField = new JTextField();
        JTextField caloriesField = new JTextField();

        JPanel p = new JPanel(new GridLayout(0, 2, 5, 5));
        p.add(new JLabel("Date (yyyy-MM-dd, empty = today):"));
        p.add(dateField);
        p.add(new JLabel("Meal name:"));
        p.add(mealField);
        p.add(new JLabel("Category:"));
        p.add(categoryField);
        p.add(new JLabel("Quantity (grams):"));
        p.add(qtyField);
        p.add(new JLabel("Calories (optional):"));
        p.add(caloriesField);

        int res = JOptionPane.showConfirmDialog(this, p, "Log Meal",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (res != JOptionPane.OK_OPTION) return;

        String date = dateField.getText().trim();
        String name = mealField.getText().trim();
        String category = categoryField.getText().trim();
        String qty = qtyField.getText().trim();
        String cal = caloriesField.getText().trim();

        if (name.isEmpty() || qty.isEmpty()) {
            showError("Meal name and quantity are required.");
            return;
        }

        try {
            double grams = Double.parseDouble(qty);
            CalorieTracker ct;

            if (!date.isEmpty()) {
                LocalDate d = LocalDate.parse(date);
                if (!cal.isEmpty()) {
                    ct = new CalorieTracker(d, name, grams, Integer.parseInt(cal), category);
                } else {
                    ct = new CalorieTracker(d, name, grams, category);
                }
            } else {
                if (!cal.isEmpty()) {
                    ct = new CalorieTracker(name, grams, Integer.parseInt(cal), category);
                } else {
                    ct = new CalorieTracker(name, grams, category);
                }
            }

            ct.saveToFile();
            showInfo("✅ Meal logged successfully.");

        } catch (Exception ex) {
            showError("Error: " + ex.getMessage());
        }
    }

    private void logSteps() {
        JTextField dateField = new JTextField();
        JTextField stepsField = new JTextField();

        JPanel p = new JPanel(new GridLayout(0, 2, 5, 5));
        p.add(new JLabel("Date (yyyy-MM-dd, empty = today):"));
        p.add(dateField);
        p.add(new JLabel("Steps:"));
        p.add(stepsField);

        int res = JOptionPane.showConfirmDialog(this, p, "Log Steps",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (res != JOptionPane.OK_OPTION) return;

        try {
            int steps = Integer.parseInt(stepsField.getText().trim());
            String date = dateField.getText().trim();

            if (date.isEmpty()) date = LocalDate.now().toString();

            StepsTracker.logDaily(date, steps);
            showInfo("✅ Steps logged successfully.");

        } catch (Exception ex) {
            showError("Error: " + ex.getMessage());
        }
    }

    private void logWater() {
        JTextField dateField = new JTextField();
        JTextField litersField = new JTextField();

        JPanel p = new JPanel(new GridLayout(0, 2, 5, 5));
        p.add(new JLabel("Date (yyyy-MM-dd, empty = today):"));
        p.add(dateField);
        p.add(new JLabel("Liters (e.g., 1.5):"));
        p.add(litersField);

        int res = JOptionPane.showConfirmDialog(this, p, "Log Water",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (res != JOptionPane.OK_OPTION) return;

        try {
            double liters = Double.parseDouble(litersField.getText().trim());
            String date = dateField.getText().trim();

            if (date.isEmpty()) date = LocalDate.now().toString();

            WaterTracker.logDaily(date, liters);
            showInfo("✅ Water logged successfully.");

        } catch (Exception ex) {
            showError("Error: " + ex.getMessage());
        }
    }

    private void runBMI() {
        JTextField ageField = new JTextField();
        JTextField heightField = new JTextField();
        JTextField weightField = new JTextField();

        JPanel p = new JPanel(new GridLayout(0, 2, 5, 5));
        p.add(new JLabel("Age:"));
        p.add(ageField);
        p.add(new JLabel("Height (m):"));
        p.add(heightField);
        p.add(new JLabel("Weight (kg):"));
        p.add(weightField);

        int res = JOptionPane.showConfirmDialog(this, p, "BMI Calculator",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (res != JOptionPane.OK_OPTION) return;

        try {
            int age = Integer.parseInt(ageField.getText().trim());
            double height = Double.parseDouble(heightField.getText().trim());
            double weight = Double.parseDouble(weightField.getText().trim());

            BMITracker bmi = new BMITracker(age, height, weight);
            double value = bmi.calculate();

            showInfo("BMI: " + String.format("%.2f", value)
                    + "\nCategory: " + bmi.getBMICategory());

        } catch (Exception ex) {
            showError("Error: " + ex.getMessage());
        }
    }

    private void showWeeklySummary() {
        String start = JOptionPane.showInputDialog(this,
                "Start date (yyyy-MM-dd):", LocalDate.now().toString());

        if (start == null || start.trim().isEmpty()) return;

        String result = Tracker.generateWeeklySummary(start.trim());
        JTextArea area = new JTextArea(result);
        area.setEditable(false);
        area.setCaretPosition(0);
        area.setFont(new Font("Monospaced", Font.PLAIN, 11));

        JScrollPane scroll = new JScrollPane(area);
        scroll.setPreferredSize(new Dimension(550, 400));

        JOptionPane.showMessageDialog(this, scroll,
                "Weekly Summary", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * NEW: AI Health Analysis with Gemini LLM
     */
    private void analyzeWithAI() {
        // Check if API key is configured
        if (!GeminiConfig.isApiKeyConfigured()) {
            showError("API Configuration Error:\n\n" +
                    "GEMINI_API_KEY environment variable is not set.\n\n" +
                    "Please set it before using AI analysis:\n\n" +
                    "Windows: setx GEMINI_API_KEY \"your-api-key\"\n" +
                    "Mac/Linux: export GEMINI_API_KEY=\"your-api-key\"\n\n" +
                    "Get free API key at: https://aistudio.google.com/app/apikeys");
            return;
        }

        // Create input dialog
        JTextArea queryArea = new JTextArea(3, 40);
        queryArea.setLineWrap(true);
        queryArea.setWrapStyleWord(true);
        queryArea.setText("What would you like to know about your health?");

        JScrollPane queryScroll = new JScrollPane(queryArea);

        JPanel p = new JPanel(new BorderLayout(10, 10));
        p.add(new JLabel("Ask a specific health question:"), BorderLayout.NORTH);
        p.add(queryScroll, BorderLayout.CENTER);
        p.add(new JLabel("(e.g., 'Should I eat more protein?', 'Am I active enough?', 'Why am I tired?')"), BorderLayout.SOUTH);

        int res = JOptionPane.showConfirmDialog(this, p, "🤖 AI Health Analysis",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (res != JOptionPane.OK_OPTION) return;

        String userQuery = queryArea.getText().trim();
        if (userQuery.isEmpty() || userQuery.equals("What would you like to know about your health?")) {
            showError("Please enter a specific question");
            return;
        }

        // Show loading dialog
        JDialog loadingDialog = new JDialog(this, "Analyzing...", true);
        JLabel loadingLabel = new JLabel(
                "<html><center>" +
                "🤖 Calling Gemini AI for personalized analysis...<br>" +
                "This may take 3-5 seconds...<br><br>" +
                "Analyzing your health metrics...<br>" +
                "</center></html>"
        );
        loadingLabel.setHorizontalAlignment(SwingConstants.CENTER);
        loadingDialog.add(loadingLabel, BorderLayout.CENTER);
        loadingDialog.setSize(450, 180);
        loadingDialog.setLocationRelativeTo(this);

        // Run analysis in background thread
        new Thread(() -> {
            try {
                // Extract metrics (last 30 days)
                LocalDate endDate = LocalDate.now();
                LocalDate startDate = endDate.minusDays(29);
                Map<String, Object> metrics = MetricsExtractor.extractMetrics(startDate, endDate);

                // Call Gemini
                Map<String, Object> analysis = GeminiAnalyzer.analyzeUserHealth(metrics, userQuery);

                // Close loading dialog and display results
                loadingDialog.dispose();
                displayAnalysisResults(analysis, userQuery);

            } catch (Exception ex) {
                loadingDialog.dispose();
                showError("Analysis failed:\n" + ex.getMessage());
                ex.printStackTrace();
            }
        }).start();

        loadingDialog.setVisible(true);
    }

    /**
     * Display AI analysis results in a formatted dialog
     */
    private void displayAnalysisResults(Map<String, Object> analysis, String userQuery) {
        // Check for errors
        if (analysis.containsKey("error")) {
            showError("Analysis Error:\n" + analysis.get("error"));
            return;
        }

        StringBuilder result = new StringBuilder();
        result.append("═══════════════════════════════════════════════════════\n");
        result.append("             🤖 AI HEALTH ANALYSIS RESULTS              \n");
        result.append("═══════════════════════════════════════════════════════\n\n");

        result.append("YOUR QUESTION:\n");
        result.append("─────────────────────────────────────────────────────\n");
        result.append("\"").append(userQuery).append("\"\n\n");

        result.append("ANSWER:\n");
        result.append("─────────────────────────────────────────────────────\n");
        result.append(analysis.getOrDefault("answer", "N/A")).append("\n\n");

        result.append("KEY INSIGHTS:\n");
        result.append("─────────────────────────────────────────────────────\n");
        @SuppressWarnings("unchecked")
        List<String> insights = (List<String>) analysis.getOrDefault("insights", new java.util.ArrayList<>());
        if (insights.isEmpty()) {
            result.append("• No additional insights\n");
        } else {
            for (String insight : insights) {
                result.append("• ").append(insight).append("\n");
            }
        }
        result.append("\n");

        result.append("RECOMMENDATIONS:\n");
        result.append("─────────────────────────────────────────────────────\n");
        @SuppressWarnings("unchecked")
        List<String> recommendations = (List<String>) analysis.getOrDefault("recommendations", new java.util.ArrayList<>());
        if (recommendations.isEmpty()) {
            result.append("• No specific recommendations\n");
        } else {
            for (String rec : recommendations) {
                result.append("• ").append(rec).append("\n");
            }
        }
        result.append("\n");

        double confidence = ((Number) analysis.getOrDefault("confidence", 0.0)).doubleValue();
        result.append("─────────────────────────────────────────────────────\n");
        result.append("Analysis Confidence: ").append(String.format("%.0f%%", confidence * 100)).append("\n");
        result.append("═══════════════════════════════════════════════════════\n");

        JTextArea area = new JTextArea(result.toString());
        area.setEditable(false);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setFont(new Font("Monospaced", Font.PLAIN, 11));
        area.setCaretPosition(0);
        area.setMargin(new Insets(10, 10, 10, 10));

        JScrollPane scroll = new JScrollPane(area);
        scroll.setPreferredSize(new Dimension(600, 500));

        JOptionPane.showMessageDialog(this, scroll, "🤖 AI Analysis Results", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showInfo(String msg) {
        JOptionPane.showMessageDialog(this, msg, "✅ Success",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "❌ Error",
                JOptionPane.ERROR_MESSAGE);
    }
}