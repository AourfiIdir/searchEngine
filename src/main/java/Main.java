import com.formdev.flatlaf.FlatDarkLaf;

import javax.swing.*;
import java.io.IOException;
import java.sql.*;

public class Main {
    public static void main(String []args) throws SQLException, IOException {
        try {
            // Set a modern dark theme
            UIManager.setLookAndFeel(new FlatDarkLaf());
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
        SwingUtilities.invokeLater(()->{
            try {
                new Gui();
            } catch (SQLException|IOException e) {
                throw new RuntimeException(e);
            }
        });

    }
}


