package com.fooddelivery.ui.customer;

import com.fooddelivery.model.Restaurant;
import com.fooddelivery.service.RestaurantService;
import com.fooddelivery.ui.UITheme;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.function.Consumer;

/**
 * Customer-facing restaurant discovery panel.
 * Allows searching by name / cuisine and shows cards for each restaurant.
 */
public class RestaurantListPanel extends JPanel {

    private final Consumer<Restaurant> onSelect;
    private JTextField   searchField;
    private JComboBox<String> cuisineFilter;
    private JPanel       cardsPanel;

    public RestaurantListPanel(Consumer<Restaurant> onSelect) {
        this.onSelect = onSelect;
        setLayout(new BorderLayout(0, 0));
        setBackground(UITheme.BG);
        buildUI();
        loadRestaurants(RestaurantService.getInstance().getAll());
    }

    private void buildUI() {
        // ── Top bar ──────────────────────────────────────────────────────────
        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        topBar.setBackground(UITheme.SECONDARY);

        JLabel title = new JLabel("  🍽  Restaurants");
        title.setFont(UITheme.FONT_TITLE);
        title.setForeground(Color.WHITE);

        searchField = UITheme.textField(20);
        searchField.setToolTipText("Search by name or cuisine…");

        List<String> cuisines = RestaurantService.getInstance().getAllCuisineTypes();
        cuisines.add(0, "All Cuisines");
        cuisineFilter = new JComboBox<>(cuisines.toArray(new String[0]));
        cuisineFilter.setFont(UITheme.FONT_BODY);

        JButton searchBtn = UITheme.primaryButton("Search");
        searchBtn.addActionListener(e -> doSearch());
        searchField.addActionListener(e -> doSearch());

        topBar.add(title);
        topBar.add(Box.createHorizontalStrut(20));
        topBar.add(new JLabel("  "));
        topBar.add(searchField);
        topBar.add(cuisineFilter);
        topBar.add(searchBtn);
        add(topBar, BorderLayout.NORTH);

        // ── Cards area ───────────────────────────────────────────────────────
        cardsPanel = new JPanel();
        cardsPanel.setLayout(new BoxLayout(cardsPanel, BoxLayout.Y_AXIS));
        cardsPanel.setBackground(UITheme.BG);

        JScrollPane scroll = new JScrollPane(cardsPanel);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        add(scroll, BorderLayout.CENTER);
    }

    private void doSearch() {
        String query   = searchField.getText().trim();
        String cuisine = (String) cuisineFilter.getSelectedItem();
        List<Restaurant> results;

        if (query.isEmpty() && (cuisine == null || cuisine.equals("All Cuisines"))) {
            results = RestaurantService.getInstance().getAll();
        } else if (!query.isEmpty()) {
            results = RestaurantService.getInstance().search(query);
        } else {
            results = RestaurantService.getInstance().filterByCuisine(cuisine);
        }
        loadRestaurants(results);
    }

    private void loadRestaurants(List<Restaurant> restaurants) {
        cardsPanel.removeAll();
        if (restaurants.isEmpty()) {
            JLabel empty = new JLabel("No restaurants found.", SwingConstants.CENTER);
            empty.setFont(UITheme.FONT_HEADING);
            empty.setForeground(UITheme.TEXT_MUTED);
            cardsPanel.add(empty);
        } else {
            for (Restaurant r : restaurants) {
                cardsPanel.add(buildCard(r));
                cardsPanel.add(Box.createVerticalStrut(8));
            }
        }
        cardsPanel.revalidate();
        cardsPanel.repaint();
    }

    private JPanel buildCard(Restaurant r) {
        JPanel card = new JPanel(new BorderLayout(10, 0));
        card.setBackground(UITheme.CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 4, 0, 0,
                r.isCurrentlyOpen() ? UITheme.SUCCESS : UITheme.DANGER),
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BORDER_COLOR),
                BorderFactory.createEmptyBorder(14, 16, 14, 16))));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Left info
        JPanel info = new JPanel(new GridLayout(4, 1, 0, 2));
        info.setOpaque(false);

        JLabel nameLabel = new JLabel(r.getName());
        nameLabel.setFont(UITheme.FONT_HEADING);
        nameLabel.setForeground(UITheme.TEXT_MAIN);

        JLabel cuisineLabel = UITheme.mutedLabel(r.getCuisineType()
            + (r.getDescription() != null ? "  ·  " + r.getDescription() : ""));

        JLabel ratingLabel = new JLabel(UITheme.starRating(r.getRating())
            + "  (" + r.getTotalRatings() + " ratings)");
        ratingLabel.setFont(UITheme.FONT_SMALL);
        ratingLabel.setForeground(UITheme.STAR_COLOR);

        JLabel statusLabel = new JLabel(r.isCurrentlyOpen() ? "● Open" : "● Closed");
        statusLabel.setFont(UITheme.FONT_SMALL);
        statusLabel.setForeground(r.isCurrentlyOpen() ? UITheme.SUCCESS : UITheme.DANGER);

        info.add(nameLabel);
        info.add(cuisineLabel);
        info.add(ratingLabel);
        info.add(statusLabel);
        card.add(info, BorderLayout.CENTER);

        // Right meta
        JPanel meta = new JPanel(new GridLayout(3, 1, 0, 4));
        meta.setOpaque(false);
        meta.setPreferredSize(new Dimension(180, 80));

        meta.add(UITheme.mutedLabel("Min order: " + (int) r.getMinOrderAmount() + " BDT"));
        meta.add(UITheme.mutedLabel("Delivery: " + r.getEstimatedDeliveryMinutes() + " min"));
        JButton viewBtn = UITheme.primaryButton("View Menu →");
        viewBtn.addActionListener(e -> onSelect.accept(r));
        meta.add(viewBtn);
        card.add(meta, BorderLayout.EAST);

        card.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                card.setBackground(new Color(0xFFF8F4, false));
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                card.setBackground(UITheme.CARD_BG);
            }
            public void mouseClicked(java.awt.event.MouseEvent e) {
                onSelect.accept(r);
            }
        });

        return card;
    }

    public void refresh() {
        loadRestaurants(RestaurantService.getInstance().getAll());
    }
}
