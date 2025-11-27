package fitlife;

import fitlife.FitLifeGUI;

public class Main {
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            FitLifeGUI gui = new FitLifeGUI();
            gui.setVisible(true);
        });
    }
}
