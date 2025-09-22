import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;



public class OwnerDashboard extends JFrame {
    private JLabel lowStockLabel;

    public OwnerDashboard() {
        setTitle("Owner Dashboard");
        setSize(600, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        BackgroundPanel panel = new BackgroundPanel("photo/home.jpg");
        panel.setLayout(null);
        setContentPane(panel);

        JLabel titleLabel = new JLabel("Welcome, Owner");
        titleLabel.setBounds(173, 20, 300, 40);
        titleLabel.setFont(new Font("Verdana", Font.BOLD, 26));
        titleLabel.setForeground(new Color(255, 255, 255));
        panel.add(titleLabel);

        int y = 80;
        int spacing = 50;
        int btnWidth = 250;
        int btnHeight = 40;

        JButton viewOrdersBtn = createRoundedButton("View Orders");
        viewOrdersBtn.setBounds(170, y, btnWidth, btnHeight);
        viewOrdersBtn.addActionListener(e -> {
            new OwnerViewOrdersPage();
            dispose();
        });
        panel.add(viewOrdersBtn);
        JButton viewOfflineSalesBtn = createRoundedButton("Offline Sales");
        viewOfflineSalesBtn.setBounds(170, y += spacing, btnWidth, btnHeight);
        viewOfflineSalesBtn.addActionListener(e -> {
            new OfflineSalesReportPage();
            dispose();
        });
        panel.add(viewOfflineSalesBtn);

        JButton viewDeliveredBtn = createRoundedButton("Delivered Orders");
        viewDeliveredBtn.setBounds(170, y += spacing, btnWidth, btnHeight);
        viewDeliveredBtn.addActionListener(e -> {
            new ViewDeliveredOrdersPage();
            dispose();
        });
        panel.add(viewDeliveredBtn);

        JButton restockBtn = createRoundedButton("Restock Medicine");
        restockBtn.setBounds(170, y += spacing, btnWidth, btnHeight);
        restockBtn.addActionListener(e -> {
            new RestockMedicinePage();
            dispose();
        });
        panel.add(restockBtn);

        JButton sellBtn = createRoundedButton("Sell Product");
        sellBtn.setBounds(170, y += spacing, btnWidth, btnHeight);
        sellBtn.addActionListener(e -> {
            new SellProductPage();
            dispose();
        });
        panel.add(sellBtn);

        JButton viewAllBtn = createRoundedButton("View All Medicines");
        viewAllBtn.setBounds(170, y += spacing, btnWidth, btnHeight);
        viewAllBtn.addActionListener(e -> {
            new ViewAllMedicinesPage();
            dispose();
        });
        panel.add(viewAllBtn);

        JButton addProductBtn = createRoundedButton(" Add Product");
        addProductBtn.setBounds(170, y += spacing, btnWidth, btnHeight);
        addProductBtn.addActionListener(e -> showAddProductForm(panel));
        panel.add(addProductBtn);

        JButton logoutBtn = createRoundedButton("Logout");
        logoutBtn.setBounds(450, 400, 120, 30);
        logoutBtn.setFont(new Font("Arial", Font.BOLD, 14));
        logoutBtn.addActionListener(e -> {
            new LoginPage();
            dispose();
        });
        panel.add(logoutBtn);

        lowStockLabel = new JLabel();
        lowStockLabel.setBounds(50, 420, 500, 30);
        lowStockLabel.setForeground(Color.RED);
        lowStockLabel.setFont(new Font("Arial", Font.BOLD, 14));
        panel.add(lowStockLabel);

        checkLowStock();

        setVisible(true);
    }

    private void showAddProductForm(JPanel panel) {
        panel.removeAll();

        JTextField nameField = new JTextField();
        JTextField quantityField = new JTextField();
        JTextField priceField = new JTextField();
        JTextField expiryField = new JTextField();
        JTextField companyField = new JTextField();

        addLabelAndField(panel, "Name:", nameField, 60);
        addLabelAndField(panel, "Quantity:", quantityField, 100);
        addLabelAndField(panel, "Price (per piece):", priceField, 140);
        addLabelAndField(panel, "Expiry Date:", expiryField, 180);
        addLabelAndField(panel, "Company:", companyField, 220);

        JButton addProductBtn = createRoundedButton("Add Product");
        addProductBtn.setBounds(170, 270, 250, 40);
        addProductBtn.addActionListener(e -> addProduct(nameField, quantityField, priceField, expiryField, companyField));
        panel.add(addProductBtn);

        JButton backBtn = createRoundedButton("Back to Dashboard");
        backBtn.setBounds(170, 320, 250, 40);
        backBtn.addActionListener(e -> {
            new OwnerDashboard();
            dispose();
        });
        panel.add(backBtn);

        panel.revalidate();
        panel.repaint();
    }

    private void addProduct(JTextField nameField, JTextField quantityField, JTextField priceField, JTextField expiryField, JTextField companyField) {
        String name = nameField.getText().trim();
        String quantity = quantityField.getText().trim();
        String price = priceField.getText().trim();
        String expiry = expiryField.getText().trim();
        String company = companyField.getText().trim();

        if (name.isEmpty() || quantity.isEmpty() || price.isEmpty() || expiry.isEmpty() || company.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields.");
            return;
        }

        try {
            int qty = Integer.parseInt(quantity);    //string k convert kortechi
            double pr = Double.parseDouble(price);
            if (qty <= 0 || pr <= 0) {
                JOptionPane.showMessageDialog(this, "Quantity and Price must be positive numbers.");
                return;
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid number format for quantity or price.");
            return;
        }

        String productLine = name + "|" + quantity + "|" + price + "|" + expiry + "|" + company;

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("products.txt", true))) {
            writer.write(productLine);
            writer.newLine();
            JOptionPane.showMessageDialog(this, "Product added successfully!");

            nameField.setText("");   //buffer clear kortechi
            quantityField.setText("");
            priceField.setText("");
            expiryField.setText("");
            companyField.setText("");
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error saving product: " + ex.getMessage());
        }
    }

    private void addLabelAndField(JPanel panel, String labelText, JTextField textField, int y) {
        JLabel label = new JLabel(labelText);
        label.setBounds(50, y, 120, 30);
        label.setForeground(Color.BLACK);
        panel.add(label);

        textField.setBounds(160, y, 250, 30);
        panel.add(textField);
    }

    private void checkLowStock() {
        ArrayList<String> lowStockProducts = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader("products.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length >= 2) {
                    try {
                        int qty = Integer.parseInt(parts[1].trim());
                        if (qty < 10) {
                            lowStockProducts.add(parts[0]);
                        }
                    } catch (NumberFormatException ignored) {
                    }
                }
            }
        } catch (IOException ignored) {
        }

        if (!lowStockProducts.isEmpty()) {
            lowStockLabel.setText("Low stock alert: " + String.join(", ", lowStockProducts));
        } else {
            lowStockLabel.setText("");
        }
    }

    private JButton createRoundedButton(String text) {
        Color normalColor = new Color(0, 102, 204);
        Color hoverColor = new Color(51, 153, 255);

        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
                super.paintComponent(g);
                g2.dispose();
            }

            @Override
            protected void paintBorder(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getForeground());
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 30, 30);
                g2.dispose();
            }
        };

        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setOpaque(false);
        button.setForeground(Color.WHITE);
        button.setBackground(normalColor);
        button.setFont(new Font("Arial", Font.BOLD, 16));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(hoverColor);
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(normalColor);
            }
        });

        return button;
    }

    static class BackgroundPanel extends JPanel {
        private Image backgroundImage;

        public BackgroundPanel(String imagePath) {
            backgroundImage = new ImageIcon(imagePath).getImage();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
    }
}
