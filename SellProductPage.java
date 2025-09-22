import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;

public class SellProductPage extends JFrame {
    private JTextField searchField;
    private JComboBox<String> productComboBox;
    private JLabel resultLabel;
    private JTextField quantityField;
    private JTable orderTable;
    private DefaultTableModel tableModel;
    private ArrayList<Product> products;
    private ArrayList<Product> filteredProducts;
    private JButton addToCartBtn, sellBtn, cancelBtn;

    static class Customer {
        final String name;
        final String contact;

        Customer(String name, String contact) {
            this.name = name;
            this.contact = contact;
        }
    }

    public SellProductPage() {
        setTitle("Sell Products");
        setSize(700, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        BackgroundPanel panel = new BackgroundPanel("photo/home.jpg");
        panel.setLayout(null);
        setContentPane(panel);

        JLabel searchLabel = new JLabel("Search Product:");
        searchLabel.setBounds(30, 20, 120, 25);
        searchLabel.setForeground(Color.black);
        panel.add(searchLabel);

        searchField = new JTextField();
        searchField.setBounds(150, 20, 200, 25);
        panel.add(searchField);

        productComboBox = new JComboBox<>();
        productComboBox.setBounds(150, 55, 200, 25);
        panel.add(productComboBox);

        resultLabel = new JLabel("");
        resultLabel.setBounds(30, 90, 500, 25);
        resultLabel.setForeground(Color.WHITE);
        panel.add(resultLabel);

        JLabel quantityLabel = new JLabel("Quantity:");
        quantityLabel.setBounds(30, 125, 100, 25);
        quantityLabel.setForeground(Color.black);
        panel.add(quantityLabel);

        quantityField = new JTextField();
        quantityField.setBounds(150, 125, 100, 25);
        panel.add(quantityField);

        addToCartBtn = createRoundedButton("Add to Cart");
        addToCartBtn.setBounds(270, 125, 170, 30);
        panel.add(addToCartBtn);

        tableModel = new DefaultTableModel(new String[]{"Name", "Quantity"}, 0);
        orderTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(orderTable);
        scrollPane.setBounds(30, 170, 500, 150);
        panel.add(scrollPane);

        sellBtn = createRoundedButton("Sell All");
        sellBtn.setBounds(400, 340, 120, 35);
        panel.add(sellBtn);

        JButton backBtn = createRoundedButton("Back");
        backBtn.setBounds(30, 400, 100, 40);
        panel.add(backBtn);

        cancelBtn = createRoundedButton("Cancel");
        cancelBtn.setBounds(150, 400, 100, 40);
        panel.add(cancelBtn);

        loadProducts();
        filteredProducts = new ArrayList<>(products);
        updateComboBox();

        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                filterProducts();
            }

            public void removeUpdate(DocumentEvent e) {
                filterProducts();
            }

            public void changedUpdate(DocumentEvent e) {
                filterProducts();
            }
        });

        productComboBox.addActionListener(e -> updateResultLabel());
        addToCartBtn.addActionListener(e -> addToCart());
        sellBtn.addActionListener(e -> sellAll());

        cancelBtn.addActionListener(e -> {
            quantityField.setText("");//refresh kortechi
            searchField.setText("");
            tableModel.setRowCount(0);
            filteredProducts = new ArrayList<>(products);
            updateComboBox();
        });

        backBtn.addActionListener(e -> {
            new OwnerDashboard();
            dispose();
        });

        setVisible(true);
    }

    private void loadProducts() {
        products = new ArrayList<>();
        File f = new File("products.txt");
        if (!f.exists()) {
            try {
                f.createNewFile();
            } catch (IOException ignored) {
            }
        }
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
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
        if (!filteredProducts.isEmpty()) {
            productComboBox.setSelectedIndex(0);
            updateResultLabel();
        } else {
            resultLabel.setText("No product found");
        }
    }

    private void updateResultLabel() {
        int selectedIndex = productComboBox.getSelectedIndex();
        if (selectedIndex >= 0 && selectedIndex < filteredProducts.size()) {
            Product p = filteredProducts.get(selectedIndex);
            resultLabel.setText("Stock: " + p.getQuantity() + " | Price: " + p.getPrice());
        }
    }

    private void addToCart() {
        int selectedIndex = productComboBox.getSelectedIndex();
        if (selectedIndex < 0 || selectedIndex >= filteredProducts.size()) return;

        String qtyText = quantityField.getText().trim();
        if (qtyText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a quantity.");
            return;
        }

        int qty;
        try {
            qty = Integer.parseInt(qtyText);
            if (qty <= 0) {
                JOptionPane.showMessageDialog(this, "Quantity must be greater than 0.");
                return;
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid quantity.");
            return;
        }

        Product selectedProduct = filteredProducts.get(selectedIndex);
        if (qty > selectedProduct.getQuantity()) {
            JOptionPane.showMessageDialog(this, "Not enough stock.");
            return;
        }

        tableModel.addRow(new Object[]{selectedProduct.getName(), qty});
        quantityField.setText("");
    }

    private Optional<Customer> promptCustomerInfo() {
        JTextField nameField = new JTextField();
        JTextField contactField = new JTextField();

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(6, 6, 6, 6);

        gc.gridx = 0;
        gc.gridy = 0;
        gc.anchor = GridBagConstraints.EAST;
        form.add(new JLabel("Customer Name:"), gc);
        gc.gridx = 1;
        gc.gridy = 0;
        gc.anchor = GridBagConstraints.WEST;
        nameField.setColumns(16);
        form.add(nameField, gc);

        gc.gridx = 0;
        gc.gridy = 1;
        gc.anchor = GridBagConstraints.EAST;
        form.add(new JLabel("Contact Number:"), gc);
        gc.gridx = 1;
        gc.gridy = 1;
        gc.anchor = GridBagConstraints.WEST;
        contactField.setColumns(16);
        form.add(contactField, gc);

        while (true) {
            int result = JOptionPane.showConfirmDialog(
                    this, form, "Customer Info",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if (result != JOptionPane.OK_OPTION) {
                return Optional.empty();
            }

            String name = nameField.getText().trim();
            String contact = contactField.getText().trim();

            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter customer name.");
                continue;
            }
            if (contact.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter contact number.");
                continue;
            }
            if (!contact.matches("\\+?\\d[\\d\\s-]{5,}")) {
                JOptionPane.showMessageDialog(this, "Please enter a valid contact number.");
                continue;
            }

            return Optional.of(new Customer(name, contact));
        }
    }

    private void sellAll() {
        if (tableModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Cart is empty.");
            return;
        }

        // 🔥 One dialog for both fields
        Optional<Customer> customerOpt = promptCustomerInfo();
        if (!customerOpt.isPresent()) return; // user cancelled
        Customer customer = customerOpt.get();

        double total = 0;
        StringBuilder bill = new StringBuilder();
        bill.append("----- INVOICE -----\n");

        // Build invoice and deduct stock
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            String nameP = tableModel.getValueAt(i, 0).toString();
            int qty = Integer.parseInt(tableModel.getValueAt(i, 1).toString());

            // Find product by name in master list
            Product product = products.stream()
                    .filter(p -> p.getName().equals(nameP))
                    .findFirst()
                    .orElse(null);

            if (product == null) continue; // should not happen

            if (qty > product.getQuantity()) {
                JOptionPane.showMessageDialog(this,
                        "Insufficient stock for " + nameP + ". Skipped.");
                continue;
            }

            double price = product.getPrice();
            total += price * qty;
            product.setQuantity(product.getQuantity() - qty);

            bill.append(nameP)
                    .append(" x").append(qty)
                    .append(" = $").append(String.format("%.2f", qty * price))
                    .append("\n");
        }

        bill.append("-------------------\n");
        bill.append("Total: ").append(String.format("%.2f", total)).append("\n");
        bill.append("Customer: ").append(customer.name).append("\n");
        bill.append("Contact: ").append(customer.contact).append("\n");
        bill.append("Date: ").append(java.time.LocalDate.now()).append("\n");

        saveProducts();
        saveCustomerOrder(customer.name, customer.contact, bill.toString());

        JTextArea textArea = new JTextArea(bill.toString());
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(400, 300));
        JOptionPane.showMessageDialog(this, scrollPane, "Invoice", JOptionPane.INFORMATION_MESSAGE);

        // reset UI
        tableModel.setRowCount(0);
        quantityField.setText("");
        searchField.setText("");
        filteredProducts = new ArrayList<>(products);
        updateComboBox();
    }

    private void saveCustomerOrder(String name, String contact, String details) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("offline_sales.txt", true))) {
            writer.write("--- ORDER ---\n");
            writer.write(details);
            writer.newLine();
        } catch (IOException e) {
            System.err.println("Error saving order: " + e.getMessage());
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

    // ---- Product model ----
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

        public String getName() {
            return name;
        }

        public int getQuantity() {
            return quantity;
        }

        public double getPrice() {
            return price;
        }

        public String getExpiry() {
            return expiry;
        }

        public String getCompany() {
            return company;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }
    }

}
