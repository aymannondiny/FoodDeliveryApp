package com.fooddelivery.ui;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

/**
 * Shared design constants for the Swing UI.
 * All colours, fonts, and common factory methods live here.
 */
public final class UITheme {

    // ── Palette ───────────────────────────────────────────────────────────────
    public static final Color PRIMARY      = new Color(0xFF6B35, false); // Orange
    public static final Color PRIMARY_DARK = new Color(0xCC5000, false);
    public static final Color SECONDARY    = new Color(0x2C3E50, false); // Dark navy
    public static final Color BG           = new Color(0xF5F5F5, false); // Light grey bg
    public static final Color CARD_BG      = Color.WHITE;
    public static final Color SUCCESS      = new Color(0x27AE60, false);
    public static final Color DANGER       = new Color(0xE74C3C, false);
    public static final Color TEXT_MAIN    = new Color(0x2C3E50, false);
    public static final Color TEXT_MUTED   = new Color(0x7F8C8D, false);
    public static final Color BORDER_COLOR = new Color(0xDEDEDE, false);
    public static final Color STAR_COLOR   = new Color(0xF39C12, false);

    // ── Fonts ─────────────────────────────────────────────────────────────────
    public static final Font FONT_TITLE   = new Font("Segoe UI", Font.BOLD,  22);
    public static final Font FONT_HEADING = new Font("Segoe UI", Font.BOLD,  15);
    public static final Font FONT_BODY    = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_SMALL   = new Font("Segoe UI", Font.PLAIN, 11);
    public static final Font FONT_BOLD    = new Font("Segoe UI", Font.BOLD,  13);

    private UITheme() {}

    // ── Factory Methods ───────────────────────────────────────────────────────

    /** Primary action button (orange). */
    public static JButton primaryButton(String text) {
        JButton btn = new JButton(text);
        btn.setBackground(PRIMARY);
        btn.setForeground(Color.WHITE);
        btn.setFont(FONT_BOLD);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setOpaque(true);
        return btn;
    }

    /** Secondary / outline button. */
    public static JButton secondaryButton(String text) {
        JButton btn = new JButton(text);
        btn.setBackground(Color.WHITE);
        btn.setForeground(SECONDARY);
        btn.setFont(FONT_BODY);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    /** Danger button (red). */
    public static JButton dangerButton(String text) {
        JButton btn = primaryButton(text);
        btn.setBackground(DANGER);
        return btn;
    }

    /** A label styled as a muted caption. */
    public static JLabel mutedLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(FONT_SMALL);
        lbl.setForeground(TEXT_MUTED);
        return lbl;
    }

    /** Standard titled text field. */
    public static JTextField textField(int cols) {
        JTextField tf = new JTextField(cols);
        tf.setFont(FONT_BODY);
        tf.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR),
            BorderFactory.createEmptyBorder(5, 8, 5, 8)));
        return tf;
    }

    /** Password field. */
    public static JPasswordField passwordField(int cols) {
        JPasswordField pf = new JPasswordField(cols);
        pf.setFont(FONT_BODY);
        pf.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR),
            BorderFactory.createEmptyBorder(5, 8, 5, 8)));
        return pf;
    }

    /** Rounded card border with padding. */
    public static Border cardBorder() {
        return BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(12, 14, 12, 14));
    }

    /** Standard section separator. */
    public static JSeparator separator() {
        JSeparator sep = new JSeparator();
        sep.setForeground(BORDER_COLOR);
        return sep;
    }

    /** Star rating display string (e.g. "★ 4.5"). */
    public static String starRating(double rating) {
        return String.format("★ %.1f", rating);
    }

    /** Apply FlatLaf-style defaults to the whole application (uses built-in Metal as fallback). */
    public static void applyLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}
        UIManager.put("Button.arc", 8);
        UIManager.put("Component.arc", 6);
        UIManager.put("Panel.background", BG);
    }
}
