package fitlife;

import fitlife.core.CalorieTracker;
import fitlife.core.StepsTracker;
import fitlife.core.WaterTracker;
import fitlife.core.Tracker;
import fitlife.logic.BMITracker;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.time.LocalDate;

public class FitLifeGUI extends JFrame {

    public FitLifeGUI() {
        setTitle("FitLife");
        setSize(400, 350);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel(new GridLayout(6, 1, 10, 10));

        JButton mealBtn = new JButton("Log Meal");
        JButton stepsBtn = new JButton("Log Steps");
        JButton waterBtn = new JButton("Log Water");
        JButton bmiBtn = new JButton("BMI Calculator");
        JButton weeklyBtn = new JButton("Weekly Summary");
        JButton exitBtn = new JButton("Exit");

        mealBtn.addActionListener(e -> logMeal());
        stepsBtn.addActionListener(e -> logSteps());
        waterBtn.addActionListener(e -> logWater());
        bmiBtn.addActionListener(e -> runBMI());
        weeklyBtn.addActionListener(e -> showWeeklySummary());
        exitBtn.addActionListener(e -> System.exit(0));

        panel.add(mealBtn);
        panel.add(stepsBtn);
        panel.add(waterBtn);
        panel.add(bmiBtn);
        panel.add(weeklyBtn);
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
            showInfo("Meal logged successfully.");

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
            showInfo("Steps logged successfully.");

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
            showInfo("Water logged successfully.");

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

        JScrollPane scroll = new JScrollPane(area);
        scroll.setPreferredSize(new Dimension(380, 300));

        JOptionPane.showMessageDialog(this, scroll,
                "Weekly Summary", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showInfo(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Success",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error",
                JOptionPane.ERROR_MESSAGE);
    }
}
