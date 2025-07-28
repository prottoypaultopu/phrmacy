import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import javax.swing.*;

public class OrderProductPage extends JFrame {
    private JTextField searchField;
    private JLabel resultLabel;
    private JTextField quantityField;
    private JButton orderBtn;

    private ArrayList<Product> products;

    public OrderProductPage() {
        setTitle("Order Product");
        setSize(600, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        BackgroundPanel panel = new BackgroundPanel("photo/2.jpg");
        panel.setLayout(null);

        JLabel titleLabel = new JLabel("Order Product");
        titleLabel.setBounds(200, 20, 200, 40);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.BLACK);
        panel.add(titleLabel);

        searchField = new JTextField();
        searchField.setBounds(150, 80, 200, 30);
        panel.add(searchField);

        JButton searchBtn = createRoundedButton("Search");
        searchBtn.setBounds(370, 80, 100, 30);
        panel.add(searchBtn);

        resultLabel = new JLabel("");
        resultLabel.setBounds(150, 130, 400, 30);
        panel.add(resultLabel);

        JLabel quantityLabel = new JLabel("Quantity:");
        quantityLabel.setBounds(150, 180, 100, 30);
        panel.add(quantityLabel);

        quantityField = new JTextField();
        quantityField.setBounds(230, 180, 100, 30);
        panel.add(quantityField);

        orderBtn = createRoundedButton("Place Order");
        orderBtn.setBounds(150, 230, 180, 40);
        orderBtn.setEnabled(false);
        panel.add(orderBtn);

        JButton backBtn = createRoundedButton("Back");
        backBtn.setBounds(10, 320, 100, 35);
        backBtn.addActionListener(e -> {
            new CustomerDashboard();
            dispose();
        });
        panel.add(backBtn);

        setContentPane(panel);
        setVisible(true);

        loadProducts();

        searchBtn.addActionListener(e -> searchProduct());
        orderBtn.addActionListener(e -> placeOrder());
    }

    private void loadProducts() {
        products = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader("products.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length >= 3) {
                    String name = parts[0].trim();
                    int qty = Integer.parseInt(parts[1].trim());
                    double price = Double.parseDouble(parts[2].trim());
                    String expiry = parts.length > 3 ? parts[3].trim() : "";
                    String company = parts.length > 4 ? parts[4].trim() : "";
                    products.add(new Product(name, qty, price, expiry, company));
                }
            }
        } catch (IOException | NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Error loading products: " + e.getMessage());
        }
    }

    private void searchProduct() {
        String search = searchField.getText().trim().toLowerCase();
        if (search.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a product name to search.");
            return;
        }
        for (Product p : products) {
            if (p.getName().toLowerCase().equals(search)) {
                resultLabel.setText("Found: " + p.getName() + " | Price: " + p.getPrice() + " | Stock: " + p.getQuantity());
                orderBtn.setEnabled(true);
                return;
            }
        }
        resultLabel.setText("Product not found.");
        orderBtn.setEnabled(false);
    }

    private void placeOrder() {
        String search = searchField.getText().trim().toLowerCase();
        String qtyText = quantityField.getText().trim();

        if (qtyText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter quantity.");
            return;
        }

        int orderQty;
        try {
            orderQty = Integer.parseInt(qtyText);
            if (orderQty <= 0) {
                JOptionPane.showMessageDialog(this, "Quantity must be positive.");
                return;
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid quantity.");
            return;
        }

        for (Product p : products) {
            if (p.getName().toLowerCase().equals(search)) {
                if (p.getQuantity() < orderQty) {
                    JOptionPane.showMessageDialog(this, "Not enough stock.");
                    return;
                }

                JPanel orderPanel = new JPanel(new GridLayout(0, 2, 10, 10));
                JTextField nameField = new JTextField();
                JTextField addressField = new JTextField();
                JTextField contactField = new JTextField();
                String[] payments = {"bKash", "Nagad", "Rocket", "Others"};
                JComboBox<String> paymentBox = new JComboBox<>(payments);
                JTextField transactionField = new JTextField();

                orderPanel.add(new JLabel("Customer Name:"));
                orderPanel.add(nameField);
                orderPanel.add(new JLabel("Address:"));
                orderPanel.add(addressField);
                orderPanel.add(new JLabel("Contact Number:"));
                orderPanel.add(contactField);
                orderPanel.add(new JLabel("Payment Method:"));
                orderPanel.add(paymentBox);
                orderPanel.add(new JLabel("Transaction ID:"));
                orderPanel.add(transactionField);

                int result = JOptionPane.showConfirmDialog(this, orderPanel, "Enter Order Details", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

                if (result == JOptionPane.OK_OPTION) {
                    String customerName = nameField.getText().trim();
                    String address = addressField.getText().trim();
                    String contact = contactField.getText().trim();
                    String paymentMethod = (String) paymentBox.getSelectedItem();
                    String transactionId = transactionField.getText().trim();

                    if (customerName.isEmpty() || address.isEmpty() || contact.isEmpty() || transactionId.isEmpty()) {
                        JOptionPane.showMessageDialog(this, "Please fill in all fields.");
                        return;
                    }

                    p.setQuantity(p.getQuantity() - orderQty);
                    saveProducts();

                    try (BufferedWriter bw = new BufferedWriter(new FileWriter("customer_orders.txt", true))) {
                        String orderLine = "Customer: " + customerName
                                + " | Product: " + p.getName()
                                + " | Qty: " + orderQty
                                + " | Total: " + (orderQty * p.getPrice())
                                + " | Address: " + address
                                + " | Contact: " + contact
                                + " | Payment: " + paymentMethod
                                + " | TxnID: " + transactionId;
                        bw.write(orderLine);
                        bw.newLine();
                    } catch (IOException e) {
                        JOptionPane.showMessageDialog(this, "Error saving order: " + e.getMessage());
                    }

                    JOptionPane.showMessageDialog(this, "Order placed successfully!");
                    quantityField.setText("");
                    resultLabel.setText("");
                    orderBtn.setEnabled(false);
                    searchField.setText("");
                }
                return;
            }
        }
        JOptionPane.showMessageDialog(this, "Product not found.");
    }

    private void saveProducts() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("products.txt"))) {
            for (Product p : products) {
                String line = p.getName() + "|" + p.getQuantity() + "|" + p.getPrice()
                        + "|" + p.getExpiry() + "|" + p.getCompany();
                bw.write(line);
                bw.newLine();
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error saving products: " + e.getMessage());
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

    class BackgroundPanel extends JPanel {
        private Image backgroundImage;

        public BackgroundPanel(String imagePath) {
            try {
                backgroundImage = new ImageIcon(imagePath).getImage();
            } catch (Exception e) {
                System.err.println("Could not load background image: " + imagePath);
                backgroundImage = null;
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (backgroundImage != null) {
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            } else {
                setBackground(Color.LIGHT_GRAY);
            }
        }
    }

    class Product {
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
}
