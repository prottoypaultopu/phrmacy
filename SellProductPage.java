import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.stream.Collectors;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class SellProductPage extends JFrame {

    private JTextField searchField;
    private JComboBox<String> productComboBox;
    private JLabel resultLabel;
    private JTextField quantityField;
    private ArrayList<Product> products;
    private ArrayList<Product> filteredProducts;
    private JButton sellBtn;

    public SellProductPage() {
        setTitle("Sell Product");
        setSize(600, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        BackgroundPanel panel = new BackgroundPanel("photo/home.jpg");
        panel.setLayout(null);
        setContentPane(panel);

        JLabel searchLabel = new JLabel("Search Product:");
        searchLabel.setBounds(40, 30, 120, 25);
        searchLabel.setForeground(Color.WHITE);
        panel.add(searchLabel);

        searchField = new JTextField();
        searchField.setBounds(170, 30, 250, 25);
        panel.add(searchField);

        productComboBox = new JComboBox<>();
        productComboBox.setBounds(170, 65, 250, 25);
        productComboBox.setEditable(false);
        panel.add(productComboBox);

        resultLabel = new JLabel("");
        resultLabel.setBounds(40, 100, 500, 25);
        resultLabel.setForeground(Color.WHITE);
        panel.add(resultLabel);

        JLabel quantityLabel = new JLabel("Quantity:");
        quantityLabel.setBounds(40, 140, 120, 25);
        quantityLabel.setForeground(Color.WHITE);
        panel.add(quantityLabel);

        quantityField = new JTextField();
        quantityField.setBounds(170, 140, 100, 25);
        panel.add(quantityField);

        sellBtn = createRoundedButton("Sell");
        sellBtn.setBounds(290, 140, 120, 30);
        sellBtn.setEnabled(false);
        panel.add(sellBtn);

        JButton backBtn = createRoundedButton("Back");
        backBtn.setBounds(20, 320, 100, 40);
        panel.add(backBtn);

        // Load products
        loadProducts();
        filteredProducts = new ArrayList<>(products);
        updateComboBox();

        // Listeners
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { filterProducts(); }
            @Override
            public void removeUpdate(DocumentEvent e) { filterProducts(); }
            @Override
            public void changedUpdate(DocumentEvent e) { filterProducts(); }
        });

        productComboBox.addActionListener(e -> updateResultLabel());

        sellBtn.addActionListener(e -> sellProduct());

        backBtn.addActionListener(e -> {
            new OwnerDashboard();
            dispose();
        });

        setVisible(true);
    }

    private void loadProducts() {
        products = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader("products.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length >= 5) {
                    String name = parts[0].trim();
                    int qty = Integer.parseInt(parts[1].trim());
                    double price = Double.parseDouble(parts[2].trim());
                    String expiry = parts[3].trim();
                    String company = parts[4].trim();
                    products.add(new Product(name, qty, price, expiry, company));
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error loading products: " + e.getMessage());
        }
    }

    private void saveProducts() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("products.txt"))) {
            for (Product p : products) {
                String line = p.getName() + "|" + p.getQuantity() + "|" + p.getPrice() + "|" + p.getExpiry() + "|" + p.getCompany();
                bw.write(line);
                bw.newLine();
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error saving products: " + e.getMessage());
        }
    }

    private void filterProducts() {
        String text = searchField.getText().trim().toLowerCase();
        filteredProducts = products.stream()
                .filter(p -> p.getName().toLowerCase().contains(text))
                .collect(Collectors.toCollection(ArrayList::new));
        updateComboBox();
    }

    private void updateComboBox() {
        productComboBox.removeAllItems();
        for (Product p : filteredProducts) {
            productComboBox.addItem(p.getName());
        }
        if (filteredProducts.isEmpty()) {
            resultLabel.setText("No products found.");
            sellBtn.setEnabled(false);
        } else {
            productComboBox.setSelectedIndex(0);
            updateResultLabel();
        }
    }

    private void updateResultLabel() {
        int selectedIndex = productComboBox.getSelectedIndex();
        if (selectedIndex < 0 || filteredProducts.isEmpty()) {
            resultLabel.setText("");
            sellBtn.setEnabled(false);
            return;
        }
        Product selectedProduct = filteredProducts.get(selectedIndex);
        resultLabel.setText("Stock: " + selectedProduct.getQuantity() + " | Price: $" + selectedProduct.getPrice());
        sellBtn.setEnabled(selectedProduct.getQuantity() > 0);
    }

    private void sellProduct() {
        int selectedIndex = productComboBox.getSelectedIndex();
        if (selectedIndex < 0 || filteredProducts.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select a product.");
            return;
        }
        Product selectedProduct = filteredProducts.get(selectedIndex);

        String qtyText = quantityField.getText().trim();
        if (qtyText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter quantity.");
            return;
        }

        int sellQty;
        try {
            sellQty = Integer.parseInt(qtyText);
            if (sellQty <= 0) {
                JOptionPane.showMessageDialog(this, "Quantity must be positive.");
                return;
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid quantity.");
            return;
        }

        if (sellQty > selectedProduct.getQuantity()) {
            JOptionPane.showMessageDialog(this, "Not enough stock.");
            return;
        }

        // Update stock
        selectedProduct.setQuantity(selectedProduct.getQuantity() - sellQty);
        saveProducts();

        // Generate & show bill
        String bill = generateBill(selectedProduct, sellQty);
        JTextArea textArea = new JTextArea(bill);
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(400, 300));

        int option = JOptionPane.showConfirmDialog(this, scrollPane, "Invoice", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (option == JOptionPane.OK_OPTION) {
            quantityField.setText("");
            searchField.setText("");
            filteredProducts = new ArrayList<>(products);
            updateComboBox();
        }
    }

    private String generateBill(Product p, int qty) {
        StringBuilder sb = new StringBuilder();
        sb.append("------ INVOICE ------\n");
        sb.append("Product: ").append(p.getName()).append("\n");
        sb.append("Quantity Sold: ").append(qty).append("\n");
        sb.append("Price per unit: $").append(String.format("%.2f", p.getPrice())).append("\n");
        sb.append("---------------------\n");
        sb.append("Total Price: $").append(String.format("%.2f", qty * p.getPrice())).append("\n");
        sb.append("---------------------\n");
        sb.append("Thank you for your purchase!\n");
        sb.append("Date: ").append(java.time.LocalDate.now()).append("\n");
        return sb.toString();
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

    // Background panel with image
    private static class BackgroundPanel extends JPanel {
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
            if (backgroundImage != null)
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            else
                setBackground(Color.DARK_GRAY);
        }
    }

    static class Product {
        private String name;
        private int quantity;
        private double price;
        private String expiry;
        private String company;

        public Product(String name, int quantity, double price, String expiry, String company) {
            this.name = name;
            this.quantity = quantity;
            this.price = price;
            this.expiry = expiry;
            this.company = company;
        }

        public String getName() { return name; }
        public int getQuantity() { return quantity; }
        public double getPrice() { return price; }
        public String getExpiry() { return expiry; }
        public String getCompany() { return company; }

        public void setQuantity(int quantity) { this.quantity = quantity; }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(SellProductPage::new);
    }
}
