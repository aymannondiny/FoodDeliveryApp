package com.fooddelivery.ui;

import com.fooddelivery.model.User;
import com.fooddelivery.service.AuthService;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

/**
 * Login / Registration panel shown before the main app.
 * Calls {@code onSuccess} with the authenticated User when login succeeds.
 */
public class LoginRegisterPanel extends JPanel {

    private final Consumer<User> onSuccess;

    private JTextField     emailField;
    private JPasswordField passField;
    private JTextField     nameField;
    private JTextField     phoneField;
    private JComboBox<User.Role> roleBox;
    private JTabbedPane    tabs;

    public LoginRegisterPanel(Consumer<User> onSuccess) {
        this.onSuccess = onSuccess;
        setBackground(UITheme.BG);
        setLayout(new GridBagLayout());
        buildUI();
    }

    private void buildUI() {
        JPanel card = new JPanel(new BorderLayout(0, 16));
        card.setBackground(UITheme.CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UITheme.BORDER_COLOR),
            BorderFactory.createEmptyBorder(30, 40, 30, 40)));

        // Header
        JLabel logo = new JLabel("🍔 FoodDelivery", SwingConstants.CENTER);
        logo.setFont(UITheme.FONT_TITLE);
        logo.setForeground(UITheme.PRIMARY);
        card.add(logo, BorderLayout.NORTH);

        tabs = new JTabbedPane();
        tabs.addTab("Login",    buildLoginTab());
        tabs.addTab("Register", buildRegisterTab());
        card.add(tabs, BorderLayout.CENTER);

        add(card);
    }

    private JPanel buildLoginTab() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(UITheme.CARD_BG);
        p.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(6, 4, 6, 4);
        gc.fill   = GridBagConstraints.HORIZONTAL;
        gc.weightx = 1;

        emailField = UITheme.textField(22);
        passField  = UITheme.passwordField(22);

        gc.gridx = 0; gc.gridy = 0; p.add(new JLabel("Email:"), gc);
        gc.gridy = 1; p.add(emailField, gc);
        gc.gridy = 2; p.add(new JLabel("Password:"), gc);
        gc.gridy = 3; p.add(passField, gc);

        JButton loginBtn = UITheme.primaryButton("Login");
        loginBtn.setPreferredSize(new Dimension(200, 36));
        loginBtn.addActionListener(e -> doLogin());
        gc.gridy = 4; gc.insets = new Insets(16, 4, 6, 4);
        p.add(loginBtn, gc);

        // Demo hint
        JLabel hint = UITheme.mutedLabel("Demo: rahim@example.com / password123");
        hint.setHorizontalAlignment(SwingConstants.CENTER);
        gc.gridy = 5; gc.insets = new Insets(4, 4, 4, 4);
        p.add(hint, gc);

        return p;
    }

    private JPanel buildRegisterTab() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(UITheme.CARD_BG);
        p.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(5, 4, 5, 4);
        gc.fill   = GridBagConstraints.HORIZONTAL;
        gc.weightx = 1;

        JTextField regName  = UITheme.textField(22);
        JTextField regEmail = UITheme.textField(22);
        JPasswordField regPass = UITheme.passwordField(22);
        JTextField regPhone = UITheme.textField(22);
        roleBox = new JComboBox<>(new User.Role[]{
            User.Role.CUSTOMER, User.Role.RESTAURANT_OWNER, User.Role.RIDER
        });
        roleBox.setFont(UITheme.FONT_BODY);

        String[][] rows = {{"Full Name:", null}, {"Email:", null},
                           {"Password:", null}, {"Phone:", null}, {"Role:", null}};
        Component[] fields = {regName, regEmail, regPass, regPhone, roleBox};

        for (int i = 0; i < rows.length; i++) {
            gc.gridy = i * 2;     p.add(new JLabel(rows[i][0]), gc);
            gc.gridy = i * 2 + 1; p.add(fields[i], gc);
        }

        JButton regBtn = UITheme.primaryButton("Create Account");
        regBtn.setPreferredSize(new Dimension(200, 36));
        regBtn.addActionListener(e -> {
            try {
                User user = AuthService.getInstance().register(
                    regName.getText(), regEmail.getText(),
                    new String(regPass.getPassword()),
                    regPhone.getText(),
                    (User.Role) roleBox.getSelectedItem()
                );
                JOptionPane.showMessageDialog(this,
                    "Account created! Please log in.", "Success",
                    JOptionPane.INFORMATION_MESSAGE);
                tabs.setSelectedIndex(0);
                emailField.setText(regEmail.getText());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "Registration Failed", JOptionPane.ERROR_MESSAGE);
            }
        });
        gc.gridy = 10; gc.insets = new Insets(16, 4, 6, 4);
        p.add(regBtn, gc);

        return p;
    }

    private void doLogin() {
        try {
            User user = AuthService.getInstance().login(
                emailField.getText(), new String(passField.getPassword())
            );
            onSuccess.accept(user);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                "Login Failed", JOptionPane.ERROR_MESSAGE);
        }
    }
}
