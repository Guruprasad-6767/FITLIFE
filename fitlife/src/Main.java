import fitlife.logic.BMITracker;
import fitlife.core.CalorieTracker;
import fitlife.core.StepsTracker;
import fitlife.core.WaterTracker;
import fitlife.core.Tracker;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

public class Main {
	// Minimal CLI to exercise app features
	public static void main(String[] args) {
		try (Scanner sc = new Scanner(System.in)) {
			while (true) {
				System.out.println();
				System.out.println("1) Log meal");
				System.out.println("2) Log steps");
				System.out.println("3) Log water");
				System.out.println("4) BMI calculator");
				System.out.println("5) Weekly summary");
				System.out.println("6) Exit");
				System.out.print("Choose: ");
				String choice = sc.nextLine().trim();
				if (choice.equals("6")) {
					System.out.println("Exiting.");
					break;
				}
				switch (choice) {
					case "1":
						logMeal(sc);
						break;
					case "2":
						logSteps(sc);
						break;
					case "3":
						logWater(sc);
						break;
					case "4":
						runBMI(sc);
						break;
					case "5":
						weeklySummary(sc);
						break;
					default:
						System.out.println("Invalid choice.");
				}
			}
		}
	}

	// helpers to avoid NumberFormatException
	private static Integer parseIntOrNull(String s) {
		if (s == null) return null;
		s = s.trim();
		if (s.isEmpty()) return null;
		try {
			return Integer.parseInt(s);
		} catch (NumberFormatException ex) {
			return null;
		}
	}

	private static Double parseDoubleOrNull(String s) {
		if (s == null) return null;
		s = s.trim();
		if (s.isEmpty()) return null;
		try {
			return Double.parseDouble(s);
		} catch (NumberFormatException ex) {
			return null;
		}
	}

	private static void logMeal(Scanner sc) {
		try {
			System.out.print("Meal name: ");
			String name = sc.nextLine().trim();
			if (name.isEmpty()) {
				System.out.println("Meal name is required.");
				return;
			}
			System.out.print("Category (optional): ");
			String category = sc.nextLine().trim();
			System.out.print("Quantity in grams (number): ");
			Double qtyD = parseDoubleOrNull(sc.nextLine());
			if (qtyD == null) {
				System.out.println("Invalid quantity. Please enter a numeric value.");
				return;
			}
			double qty = qtyD;
			System.out.print("Provide calories for this quantity? (y/N): ");
			String giveCal = sc.nextLine().trim().toLowerCase();
			Integer calories = null;
			if (giveCal.equals("y") || giveCal.equals("yes")) {
				System.out.print("Calories (kcal) for this quantity: ");
				calories = parseIntOrNull(sc.nextLine());
				if (calories == null) {
					System.out.println("Invalid calories value.");
					return;
				}
			}
			System.out.print("Use custom date? (yyyy-MM-dd) or leave empty for today: ");
			String dateInput = sc.nextLine().trim();
			CalorieTracker ct;
			if (!dateInput.isEmpty()) {
				try {
					LocalDate d = LocalDate.parse(dateInput);
					if (calories != null) ct = new CalorieTracker(d, name, qty, calories, category);
					else ct = new CalorieTracker(d, name, qty, category);
				} catch (DateTimeParseException ex) {
					System.out.println("Invalid date, using today.");
					if (calories != null) ct = new CalorieTracker(name, qty, calories, category);
					else ct = new CalorieTracker(name, qty, category);
				}
			} else {
				if (calories != null) ct = new CalorieTracker(name, qty, calories, category);
				else ct = new CalorieTracker(name, qty, category);
			}
			ct.saveToFile();
			System.out.println("Meal logged: " + ct.getDataAsString());
		} catch (IOException ex) {
			System.out.println("Error logging meal: " + ex.getMessage());
		}
	}

	private static void logSteps(Scanner sc) {
		try {
			System.out.print("Date (yyyy-MM-dd) or leave empty for today: ");
			String date = sc.nextLine().trim();
			System.out.print("Steps (integer): ");
			Integer steps = parseIntOrNull(sc.nextLine());
			if (steps == null) {
				System.out.println("Invalid steps value. Please enter an integer.");
				return;
			}
			String dateIso = date.isEmpty() ? LocalDate.now().toString() : date;
			StepsTracker.logDaily(dateIso, steps);
			System.out.println("Steps logged for " + dateIso + ": " + steps);
		} catch (IOException ex) {
			System.out.println("Error logging steps: " + ex.getMessage());
		}
	}

	private static void logWater(Scanner sc) {
		try {
			System.out.print("Date (yyyy-MM-dd) or leave empty for today: ");
			String date = sc.nextLine().trim();
			System.out.print("Liters (e.g. 1.5): ");
			Double liters = parseDoubleOrNull(sc.nextLine());
			if (liters == null) {
				System.out.println("Invalid liters value. Please enter a number (e.g. 1.5).");
				return;
			}
			String dateIso = date.isEmpty() ? LocalDate.now().toString() : date;
			WaterTracker.logDaily(dateIso, liters);
			System.out.println("Water logged for " + dateIso + ": " + String.format("%.2f", liters) + " L");
		} catch (IOException ex) {
			System.out.println("Error logging water: " + ex.getMessage());
		}
	}

	private static void runBMI(Scanner sc) {
		// validate each numeric input to avoid NumberFormatException
		try {
			System.out.print("Age (years, optional - press Enter to skip): ");
			Integer age = parseIntOrNull(sc.nextLine());
			System.out.print("Height in meters (e.g. 1.75): ");
			Double height = parseDoubleOrNull(sc.nextLine());
			if (height == null) {
				System.out.println("Invalid height. Please enter a number in meters (e.g. 1.75).");
				return;
			}
			System.out.print("Weight in kg (e.g. 70): ");
			Double weight = parseDoubleOrNull(sc.nextLine());
			if (weight == null) {
				System.out.println("Invalid weight. Please enter a number in kg (e.g. 70).");
				return;
			}
			BMITracker bmi = (age != null && age > 0) ? new BMITracker(age, height, weight) : new BMITracker(height, weight);
			double val = bmi.calculate();
			System.out.printf("BMI: %.2f%n", val);
			System.out.println("Category: " + bmi.getBMICategory());
		} catch (IllegalArgumentException ex) {
			System.out.println("Error calculating BMI: " + ex.getMessage());
		}
	}

	private static void weeklySummary(Scanner sc) {
		System.out.print("Start date for week (yyyy-MM-dd): ");
		String start = sc.nextLine().trim();
		if (start.isEmpty()) start = LocalDate.now().toString();
		String report = Tracker.generateWeeklySummary(start);
		System.out.println(report);
	}
}
