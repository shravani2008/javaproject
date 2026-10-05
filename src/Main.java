import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import service.AuthenticationService;
import ui.LoginView;

public class Main {

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            AuthenticationService authenticationService =
                    new AuthenticationService();

            JFrame frame = new JFrame("SmartQueue");

            frame.setDefaultCloseOperation(
                    JFrame.EXIT_ON_CLOSE
            );

            new LoginView(
                    frame,
                    authenticationService
            ).show();
        });
    }
}