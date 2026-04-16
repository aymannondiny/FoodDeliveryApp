package com.fooddelivery.ui.customer;

import com.fooddelivery.model.Restaurant;
import com.fooddelivery.ui.UITheme;
import com.fooddelivery.ui.customer.restaurants.RestaurantListController;
import com.fooddelivery.ui.customer.restaurants.viewmodel.RestaurantCardViewModel;
import com.fooddelivery.ui.customer.restaurants.viewmodel.RestaurantSearchViewModel;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.function.Consumer;

/**
 * Customer-facing restaurant discovery panel.
 * Delegates search/filter logic to RestaurantListController.
 */
public class RestaurantListPanel extends JPanel {

    private final RestaurantListController controller;
    private final Consumer<Restaurant> onSelect;

    private JTextField searchField;
    private JComboBox<String> cuisineFilter;
    private JPanel cardsPanel;

    public RestaurantListPanel(RestaurantListController controller,
                               Consumer<Restaurant> onSelect) {
        this.controller = controller;
        this.onSelect = onSelect;
        setLayout(new BorderLayout(0, 0));
        setBackground(UITheme.BG);
        buildUI();
        loadRestaurants(controller.loadAllRestaurants());
    }

    private void buildUI() {
        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        topBar.setBackground(UITheme.SECONDARY);

        JLabel title = new JLabel("  🍽  Restaurants");
        title.setFont(UITheme.FONT_TITLE);
        title.setForeground(Color.WHITE);

        searchField = UITheme.textField(20);
        searchField.setToolTipText("Search by name or cuisine…");

        RestaurantSearchViewModel searchViewModel = controller.loadSearchOptions();
        cuisineFilter = new JComboBox<>(searchViewModel.getCuisineOptions().toArray(new String[0]));
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

        cardsPanel = new JPanel();
        cardsPanel.setLayout(new BoxLayout(cardsPanel, BoxLayout.Y_AXIS));
        cardsPanel.setBackground(UITheme.BG);

        JScrollPane scroll = new JScrollPane(cardsPanel);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        add(scroll, BorderLayout.CENTER);
    }

    private void doSearch() {
        String query = searchField.getText().trim();
        String cuisine = (String) cuisineFilter.getSelectedItem();
        loadRestaurants(controller.search(query, cuisine));
    }

    private void loadRestaurants(List<RestaurantCardViewModel> restaurants) {
        cardsPanel.removeAll();

        if (restaurants.isEmpty()) {
            JLabel empty = new JLabel("No restaurants found.", SwingConstants.CENTER);
            empty.setFont(UITheme.FONT_HEADING);
            empty.setForeground(UITheme.TEXT_MUTED);
            cardsPanel.add(empty);
        } else {
            for (RestaurantCardViewModel vm : restaurants) {
                cardsPanel.add(buildCard(vm));
                cardsPanel.add(Box.createVerticalStrut(8));
            }
        }

        cardsPanel.revalidate();
        cardsPanel.repaint();
    }

    private JPanel buildCard(RestaurantCardViewModel vm) {
        JPanel card = new JPanel(new BorderLayout(10, 0));
        card.setBackground(UITheme.CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(
                        0, 4, 0, 0,
                        vm.isOpen() ? UITheme.SUCCESS : UITheme.DANGER
                ),
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(UITheme.BORDER_COLOR),
                        BorderFactory.createEmptyBorder(14, 16, 14, 16)
                )
        ));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, vm.hasClosureReason() ? 140 : 120));
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        int infoRows = vm.hasClosureReason() ? 5 : 4;
        JPanel info = new JPanel(new GridLayout(infoRows, 1, 0, 2));
        info.setOpaque(false);

        JLabel nameLabel = new JLabel(vm.getName());
        nameLabel.setFont(UITheme.FONT_HEADING);
        nameLabel.setForeground(UITheme.TEXT_MAIN);

        JLabel subtitleLabel = UITheme.mutedLabel(vm.getSubtitle());

        JLabel ratingLabel = new JLabel(vm.getRatingText());
        ratingLabel.setFont(UITheme.FONT_SMALL);
        ratingLabel.setForeground(UITheme.STAR_COLOR);

        JLabel statusLabel = new JLabel(vm.getStatusText());
        statusLabel.setFont(UITheme.FONT_SMALL);
        statusLabel.setForeground(vm.isOpen() ? UITheme.SUCCESS : UITheme.DANGER);

        info.add(nameLabel);
        info.add(subtitleLabel);
        info.add(ratingLabel);
        info.add(statusLabel);

        if (vm.hasClosureReason()) {
            JLabel reasonLabel = new JLabel("    ↳ " + vm.getClosureReason());
            reasonLabel.setFont(UITheme.FONT_SMALL);
            reasonLabel.setForeground(new Color(0xE67E22, false));
            info.add(reasonLabel);
        }

        card.add(info, BorderLayout.CENTER);

        JPanel meta = new JPanel(new GridLayout(3, 1, 0, 4));
        meta.setOpaque(false);
        meta.setPreferredSize(new Dimension(180, 80));

        meta.add(UITheme.mutedLabel(vm.getMinOrderText()));
        meta.add(UITheme.mutedLabel(vm.getDeliveryText()));

        JButton viewBtn = UITheme.primaryButton("View Menu →");
        viewBtn.addActionListener(e -> onSelect.accept(vm.getRestaurant()));
        meta.add(viewBtn);

        card.add(meta, BorderLayout.EAST);

        card.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                card.setBackground(new Color(0xFFF8F4, false));
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                card.setBackground(UITheme.CARD_BG);
            }

            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                onSelect.accept(vm.getRestaurant());
            }
        });

        return card;
    }

    public void refresh() {
        loadRestaurants(controller.loadAllRestaurants());
    }
}