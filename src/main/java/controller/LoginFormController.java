package controller;

import db.DatabaseUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.mindrot.jbcrypt.BCrypt;
import utils.UserSession;

import java.sql.*;

public class LoginFormController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label statusLabel;

    @FXML
    public void handleLogin(ActionEvent event) {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        try (Connection conn = DatabaseUtil.connect()) {
            String query = "SELECT * FROM users WHERE username = ?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();

            if (rs.next() && BCrypt.checkpw(password, rs.getString("password"))) {
                statusLabel.setText("Login successful!");

                // Store logged-in user
                UserSession.setUsername(username);

                // Load client chat UI
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/ClientForm.fxml"));
                Parent root = loader.load();

                // Pass username to ClientFormController
                ClientFormController controller = loader.getController();
                controller.setLoggedInUser(username);

                Stage stage = (Stage) usernameField.getScene().getWindow();
                stage.setScene(new Scene(root));
            } else {
                statusLabel.setText("Invalid credentials!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            statusLabel.setText("Error connecting to database.");
        }
    }


    @FXML
    public void goToRegister(ActionEvent event) {
        loadScene("/view/RegisterForm.fxml");
    }

    private void loadScene(String fxmlPath) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
