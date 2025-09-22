import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import javax.swing.*;

public class OwnerViewOrdersPage extends JFrame {
    private DefaultListModel<String> orderListModel;
    private JList<String> orderList;

    public OwnerViewOrdersPage() {
        setTitle("Customer Orders (Owner View)");
        setSize(600, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        BackgroundPanel panel = new BackgroundPanel("photo/home.jpg");
        panel.setLayout(null);
        setContentPane(panel);

        JLabel titleLabel = new JLabel("Customer Orders");
        titleLabel.setBounds(200, 20, 300, 30);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        panel.add(titleLabel);

        orderListModel = new DefaultListModel<>();
        orderList = new JList<>(orderListModel);
        orderList.setFont(new Font("Monospaced", Font.PLAIN, 13));
        orderList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(orderList);
        scrollPane.setBounds(50, 70, 500, 280);
        panel.add(scrollPane);

        JButton fulfillBtn = createRoundedButton("Mark as Delivered");
        fulfillBtn.setBounds(180, 370, 200, 35);
        fulfillBtn.addActionListener(e -> markOrderDelivered());
        panel.add(fulfillBtn);

        JButton backBtn = createRoundedButton("Back");
        backBtn.setBounds(10, 10, 80, 30);
        backBtn.addActionListener(e -> {
            new OwnerDashboard();
            dispose();
        });
        panel.add(backBtn);

        loadOrders();

        setVisible(true);
    }

    private void loadOrders() {
        File file = new File("customer_orders.txt");
        if (!file.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            StringBuilder block = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().equals("--- ORDER ---")) {
                    if (block.length() > 0) {
                        orderListModel.addElement(block.toString());
                        block.setLength(0);
                    }
                }
                block.append(line).append("\n");
            }
            if (block.length() > 0) {
                orderListModel.addElement(block.toString());
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error reading orders: " + e.getMessage());
        }
    }

    private void markOrderDelivered() {
        String selected = orderList.getSelectedValue();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Please select an order to mark as delivered.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure to deliver this order?");
        if (confirm != JOptionPane.YES_OPTION) return;

        // Update product quantities before marking as delivered
        updateProductQuantities(selected);

        try (BufferedWriter bw = new BufferedWriter(new FileWriter("delivered_orders.txt", true))) {
            // Update status to DELIVERED in the order before saving
            String deliveredOrder = selected.replace("Status: PENDING", "Status: DELIVERED");
            bw.write(deliveredOrder);
            bw.newLine();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error saving to delivered orders.");
            return;
        }

        orderListModel.removeElement(selected);
        saveRemainingOrders();
        JOptionPane.showMessageDialog(this, "Order marked as delivered and quantities updated.");
    }

    // New method to update product quantities when order is delivered
    private void updateProductQuantities(String orderDetails) {
        ArrayList<Product> products = loadProducts();

        String[] lines = orderDetails.split("\n");

        // Parse the order details to extract product names and quantities
        for (String line : lines) {
            // Look for lines like "ProductName x5 = $25.00"
            if (line.contains(" x") && line.contains(" = $")) {
                try {
                    String productPart = line.substring(0, line.indexOf(" x"));
                    String quantityPart = line.substring(line.indexOf(" x") + 2, line.indexOf(" = $"));

                    String productName = productPart.trim();
                    int quantity = Integer.parseInt(quantityPart.trim());

                    // Find and update the product
                    for (Product product : products) {
                        if (product.getName().equals(productName)) {
                            int newQuantity = product.getQuantity() - quantity;
                            product.setQuantity(Math.max(0, newQuantity)); // Ensure quantity doesn't go below 0
                            break;
                        }
                    }
                } catch (Exception e) {
                    System.err.println("Error parsing line: " + line + " - " + e.getMessage());
                }
            }
        }

        // Save updated products back to file
        saveProducts(products);
    }

    // Load products from file
    private ArrayList<Product> loadProducts() {
        ArrayList<Product> products = new ArrayList<>();
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
        return products;
    }

    // Save products to file
    private void saveProducts(ArrayList<Product> products) {
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

    private void saveRemainingOrders() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("customer_orders.txt"))) {
            for (int i = 0; i < orderListModel.size(); i++) {
                bw.write(orderListModel.get(i));
                if (!orderListModel.get(i).endsWith("\n")) {
                    bw.newLine();
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error updating customer orders.");
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
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 30, 30);
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

    // Product class for handling product data
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(OwnerViewOrdersPage::new);
    }
}
