import GUI.LANForm;

import java.awt.*;

public class Apllication {
    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                //LANForm frame = new LANForm();
                //frame.setVisible(true);
                LANForm.OpenForm();

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
