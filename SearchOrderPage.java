import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class SearchOrderPage extends JFrame {
    private JTextField searchField, quantityField, nameField, contactField, addressField, transactionField;
    private JComboBox<String> productComboBox, paymentComboBox;
    private JTable orderTable;
    private DefaultTableModel tableModel;
    private ArrayList<Product> products;
    private ArrayList<Product> filteredProducts;
    private JButton addToCartBtn, orderBtn, cancelBtn;

    public SearchOrderPage() {
        setTitle("Search & Order Medicine");
        setSize(750, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        BackgroundPanel panel = new BackgroundPanel("photo/home.jpg");
        panel.setLayout(null);
        setContentPane(panel);

        // Search
        JLabel searchLabel = new JLabel("Search Medicine:");
        searchLabel.setBounds(30, 20, 120, 25);
        panel.add(searchLabel);

        searchField = new JTextField();
        searchField.setBounds(150, 20, 200, 25);
        panel.add(searchField);

        // Product ComboBox
        productComboBox = new JComboBox<>();
        productComboBox.setBounds(150, 55, 200, 25);
        panel.add(productComboBox);

        // Quantity
        JLabel quantityLabel = new JLabel("Quantity:");
        quantityLabel.setBounds(30, 90, 100, 25);
        panel.add(quantityLabel);

        quantityField = new JTextField();
        quantityField.setBounds(150, 90, 100, 25);
        panel.add(quantityField);

        addToCartBtn = createRoundedButton("Add to Cart");
        addToCartBtn.setBounds(270, 90, 170, 30);
        panel.add(addToCartBtn);

        // Order Table
        tableModel = new DefaultTableModel(new String[]{"Name", "Quantity"}, 0);
        orderTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(orderTable);
        scrollPane.setBounds(30, 140, 500, 150);
        panel.add(scrollPane);

        JLabel nameLabel = new JLabel("Name:");
        nameLabel.setBounds(30, 310, 100, 25);
        panel.add(nameLabel);

        nameField = new JTextField();
        nameField.setBounds(150, 310, 200, 25);
        panel.add(nameField);

        JLabel contactLabel = new JLabel("Contact:");
        contactLabel.setBounds(30, 345, 100, 25);
        panel.add(contactLabel);

        contactField = new JTextField();
        contactField.setBounds(150, 345, 200, 25);
        panel.add(contactField);

        JLabel addressLabel = new JLabel("Address:");
        addressLabel.setBounds(30, 380, 100, 25);
        panel.add(addressLabel);

        addressField = new JTextField();
        addressField.setBounds(150, 380, 200, 25);
        panel.add(addressField);

        JLabel paymentLabel = new JLabel("Payment Method:");
        paymentLabel.setBounds(30, 415, 120, 25);
        panel.add(paymentLabel);

        paymentComboBox = new JComboBox<>(new String[]{"Cash on Delivery", "Mobile Payment"});
        paymentComboBox.setBounds(150, 415, 200, 25);
        panel.add(paymentComboBox);

        JLabel transactionLabel = new JLabel("Transaction ID:");
        transactionLabel.setBounds(30, 450, 120, 25);
        panel.add(transactionLabel);

        transactionField = new JTextField();
        transactionField.setBounds(150, 450, 200, 25);
        transactionField.setEnabled(false);
        panel.add(transactionField);

        paymentComboBox.addActionListener(e -> {
            boolean isOnline = paymentComboBox.getSelectedItem().equals("Mobile Payment");
            transactionField.setEnabled(isOnline);
        });

        orderBtn = createRoundedButton("Order Now");
        orderBtn.setBounds(600, 500, 120, 35);
        panel.add(orderBtn);

        cancelBtn = createRoundedButton("Cancel");
        cancelBtn.setBounds(150, 500, 100, 40);
        panel.add(cancelBtn);

        JButton backBtn = createRoundedButton("Back");
        backBtn.setBounds(30, 500, 100, 40);
        panel.add(backBtn);

        loadProducts();
        filteredProducts = new ArrayList<>(products);
        updateComboBox();

        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { filterProducts(); }
            public void removeUpdate(DocumentEvent e) { filterProducts(); }
            public void changedUpdate(DocumentEvent e) { filterProducts(); }
        });

        addToCartBtn.addActionListener(e -> addToCart());

        orderBtn.addActionListener(e -> orderNowUnified());

        cancelBtn.addActionListener(e -> {
            quantityField.setText("");
            searchField.setText("");
            tableModel.setRowCount(0);
            nameField.setText("");
            contactField.setText("");
            addressField.setText("");
            transactionField.setText("");
            paymentComboBox.setSelectedIndex(0);
            updateComboBox();
        });

        backBtn.addActionListener(e -> {
            new CustomerDashboard();
            dispose();
        });

        productComboBox.addActionListener(e -> updateResultLabel());

        setVisible(true);
    }

    // --- Modified Unified Order Method (NO quantity reduction) ---
    private void orderNowUnified() {
        if (tableModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Cart is empty!");
            return;
        }

        String name = nameField.getText().trim();
        String contact = contactField.getText().trim();
        String address = addressField.getText().trim();
        String payment = (String) paymentComboBox.getSelectedItem();
        String transaction = transactionField.getText().trim();

        if (name.isEmpty() || contact.isEmpty() || address.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in your details!");
            return;
        }

        if (payment.equals("Mobile Payment") && transaction.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter Transaction ID for online payment!");
            return;
        }

        // Validate that all items are still in stock
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            String nameP = tableModel.getValueAt(i, 0).toString();
            int qty = Integer.parseInt(tableModel.getValueAt(i, 1).toString());
            Product product = products.stream().filter(p -> p.getName().equals(nameP)).findFirst().get();

            if (qty > product.getQuantity()) {
                JOptionPane.showMessageDialog(this, "Not enough stock for " + nameP + ". Available: " + product.getQuantity());
                return;
            }
        }

        double total = 0;
        StringBuilder bill = new StringBuilder();
        bill.append("-----prova and naim store----\n");

        // Calculate total and create bill (DO NOT reduce quantities yet)
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            String nameP = tableModel.getValueAt(i, 0).toString();
            int qty = Integer.parseInt(tableModel.getValueAt(i, 1).toString());
            Product product = products.stream().filter(p -> p.getName().equals(nameP)).findFirst().get();

            double price = product.getPrice();
            total += price * qty;
            // DO NOT reduce quantity here: product.setQuantity(product.getQuantity() - qty);

            bill.append(nameP).append(" x").append(qty).append(" = $").append(String.format("%.2f", qty * price)).append("\n");
        }

        bill.append("--------------------------\n");
        bill.append("Total: $").append(String.format("%.2f", total)).append("\n");
        bill.append("Customer: ").append(name).append("\n");
        bill.append("Contact: ").append(contact).append("\n");
        bill.append("Address: ").append(address).append("\n");
        bill.append("Payment: ").append(payment).append("\n");
        if (payment.equals("Mobile Payment")) {
            bill.append("Transaction ID: ").append(transaction).append("\n");
        }
        bill.append("Date: ").append(java.time.LocalDate.now()).append("\n");
        bill.append("Status: PENDING\n"); // Add status to track order

        // DO NOT save products here since quantities haven't changed
        // saveProducts(); // Remove this line

        saveCustomerOrder(name, contact, bill.toString());

        JTextArea textArea = new JTextArea(bill.toString());
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(400, 300));
        JOptionPane.showMessageDialog(this, scrollPane, "Order Summary", JOptionPane.INFORMATION_MESSAGE);

        tableModel.setRowCount(0);
        quantityField.setText("");
        searchField.setText("");
        nameField.setText("");
        contactField.setText("");
        addressField.setText("");
        transactionField.setText("");
        paymentComboBox.setSelectedIndex(0);
        updateComboBox();
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

    private void saveCustomerOrder(String name, String contact, String details) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("customer_orders.txt", true))) {
            writer.write("--- ORDER ---\n");
            writer.write(details);
            writer.newLine();
        } catch (IOException e) {
            System.err.println("Error saving order: " + e.getMessage());
        }
    }

    private void filterProducts() {
        String text = searchField.getText().trim().toLowerCase();
        filteredProducts = products.stream()
                .filter(p -> p.getName().toLowerCase().contains(text) || p.getCompany().toLowerCase().contains(text))
                .collect(Collectors.toCollection(ArrayList::new));
        updateComboBox();
    }

    private void updateComboBox() {
        productComboBox.removeAllItems();
        for (Product p : filteredProducts) {
            productComboBox.addItem(p.getName());
        }
        if (!filteredProducts.isEmpty()) {
            productComboBox.setSelectedIndex(0);
        }
    }

    private void updateResultLabel() { }

    private void addToCart() {
        int selectedIndex = productComboBox.getSelectedIndex();
        if (selectedIndex < 0) return;

        String qtyText = quantityField.getText().trim();
        if (qtyText.isEmpty()) return;

        int qty;
        try {
            qty = Integer.parseInt(qtyText);
            if (qty <= 0) return;
        } catch (NumberFormatException ex) {
            return;
        }

        Product selectedProduct = filteredProducts.get(selectedIndex);
        if (qty > selectedProduct.getQuantity()) {
            JOptionPane.showMessageDialog(this, "Not enough stock.");
            return;
        }

        tableModel.addRow(new Object[]{selectedProduct.getName(), qty});
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

        return button;
    }

    private static class BackgroundPanel extends JPanel {
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
        SwingUtilities.invokeLater(SearchOrderPage::new);
    }
}
