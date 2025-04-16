package controller;

import db.DatabaseUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;

public class RegisterFormController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Label statusLabel;

    @FXML
    public void handleRegister(ActionEvent event) {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        if (!password.equals(confirmPassword)) {
            statusLabel.setText("Passwords do not match!");
            return;
        }

        try (Connection conn = DatabaseUtil.connect()) {
            String hashed = BCrypt.hashpw(password, BCrypt.gensalt());

            String query = "INSERT INTO users (username, password) VALUES (?, ?)";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, username);
            ps.setString(2, hashed);
            ps.executeUpdate();

            statusLabel.setText("Account created! Redirecting...");
            loadScene("/view/LoginForm.fxml");
        } catch (SQLIntegrityConstraintViolationException e) {
            statusLabel.setText("Username already exists!");
        } catch (Exception e) {
            e.printStackTrace();
            statusLabel.setText("Registration failed.");
        }
    }

    @FXML
    public void goToLogin(ActionEvent event) {
        loadScene("/view/LoginForm.fxml");
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

