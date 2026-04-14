package com.fooddelivery.model;


public class MenuItemAddon {
    private String  id;
    private String  name;
    private double  extraPrice;  // 0.0 if free
    private boolean available;

    public MenuItemAddon() { this.available = true; }

    public MenuItemAddon(String id, String name, double extraPrice) {
        this();
        this.id         = id;
        this.name       = name;
        this.extraPrice = extraPrice;
    }

    @Override
    public String toString() {
        return name + (extraPrice > 0 ? " (+" + String.format("%.2f", extraPrice) + " BDT)" : " (free)");
    }

    // ── Getters & Setters ───────────────────────────────────────────────────
    public String  getId()                    { return id; }
    public void    setId(String id)           { this.id = id; }
    public String  getName()                  { return name; }
    public void    setName(String n)          { this.name = n; }
    public double  getExtraPrice()            { return extraPrice; }
    public void    setExtraPrice(double p)    { this.extraPrice = p; }
    public boolean isAvailable()              { return available; }
    public void    setAvailable(boolean a)    { this.available = a; }
}
