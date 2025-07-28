import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.stream.Collectors;
import javax.swing.*;


public class RestockMedicinePage extends JFrame {
    private JComboBox<String> productComboBox;
    private JTextField quantityField, searchField;
    private ArrayList<String[]> products;
    private ArrayList<String[]> filteredProducts;

    public RestockMedicinePage() {
        setTitle("Restock Medicine");
        setSize(500, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        BackgroundPanel panel = new BackgroundPanel("photo/home.jpg");
        panel.setLayout(null);
        setContentPane(panel);

        JLabel searchLabel = new JLabel("Search:");
        searchLabel.setBounds(30, 20, 80, 25);
        searchLabel.setForeground(Color.WHITE);
        panel.add(searchLabel);

        searchField = new JTextField();
        searchField.setBounds(110, 20, 300, 25);
        panel.add(searchField);

        JLabel selectLabel = new JLabel("Select Product:");
        selectLabel.setBounds(30, 60, 120, 25);
        selectLabel.setForeground(Color.WHITE);
        panel.add(selectLabel);

        productComboBox = new JComboBox<>();
        productComboBox.setBounds(150, 60, 300, 25);
        panel.add(productComboBox);

        JLabel quantityLabel = new JLabel("Quantity to Add:");
        quantityLabel.setBounds(30, 100, 120, 25);
        quantityLabel.setForeground(Color.WHITE);
        panel.add(quantityLabel);

        quantityField = new JTextField();
        quantityField.setBounds(150, 100, 300, 25);
        panel.add(quantityField);

        JButton restockBtn = createRoundedButton("Restock");
        restockBtn.setBounds(180, 160, 140, 40);
        restockBtn.addActionListener(e -> restockProduct());
        panel.add(restockBtn);

        JButton backBtn = createRoundedButton("Back");
        backBtn.setBounds(20, 220, 100, 40);
        backBtn.addActionListener(e -> {
            new OwnerDashboard();
            dispose();
        });
        panel.add(backBtn);

        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) { filterProducts(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { filterProducts(); }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { filterProducts(); }
        });

        loadProducts();
        setVisible(true);
    }

    private void loadProducts() {
        products = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader("products.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length >= 5) {
                    products.add(parts);
                }
            }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error loading products: " + ex.getMessage());
        }
        filteredProducts = new ArrayList<>(products);
        updateComboBox();
    }

    private void filterProducts() {
        String searchText = searchField.getText().trim().toLowerCase();
        filteredProducts = (ArrayList<String[]>) products.stream()
                .filter(p -> p[0].toLowerCase().contains(searchText))
                .collect(Collectors.toList());
        updateComboBox();
    }

    private void updateComboBox() {
        productComboBox.removeAllItems();
        for (String[] p : filteredProducts) {
            productComboBox.addItem(p[0]);
        }
    }

    private void restockProduct() {
        int selectedIndex = productComboBox.getSelectedIndex();
        if (selectedIndex == -1) {
            JOptionPane.showMessageDialog(this, "Please select a product.");
            return;
        }

        String qtyText = quantityField.getText().trim();
        int addQty;
        try {
            addQty = Integer.parseInt(qtyText);
            if (addQty <= 0) {
                JOptionPane.showMessageDialog(this, "Quantity must be a positive number.");
                return;
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid quantity format.");
            return;
        }

        String[] selectedProduct = filteredProducts.get(selectedIndex);
        int currentQty;
        try {
            currentQty = Integer.parseInt(selectedProduct[1]);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error reading current quantity.");
            return;
        }

        int newQty = currentQty + addQty;
        selectedProduct[1] = String.valueOf(newQty);

        for (int i = 0; i < products.size(); i++) {
            if (products.get(i)[0].equals(selectedProduct[0])) {
                products.set(i, selectedProduct);
                break;
            }
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("products.txt"))) {
            for (String[] p : products) {
                writer.write(String.join("|", p));
                writer.newLine();
            }
            JOptionPane.showMessageDialog(this, "Product restocked successfully!");
            quantityField.setText("");
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error saving products: " + ex.getMessage());
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
                g2.setColor(getModel().isRollover() ? hoverColor : getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
                g2.dispose();
                super.paintComponent(g);
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
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

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

    public static class BackgroundPanel extends JPanel {
        private Image backgroundImage;

        public BackgroundPanel(String imagePath) {
            try {
                backgroundImage = new ImageIcon(imagePath).getImage();
            } catch (Exception e) {
                System.err.println("Could not load background image: " + imagePath);
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (backgroundImage != null) {
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(RestockMedicinePage::new);
    }
}