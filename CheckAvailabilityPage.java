import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import javax.swing.*;


public class CheckAvailabilityPage extends JFrame {
    private JTextField searchField;
    private JLabel resultLabel;
    private JButton searchBtn;

    private ArrayList<Product> products;

    public CheckAvailabilityPage() {
        setTitle("Check Product Availability");
        setSize(600, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        BackgroundPanel panel = new BackgroundPanel("photo/2.jpg");
        panel.setLayout(null);

        JLabel titleLabel = new JLabel("Check Availability");
        titleLabel.setBounds(180, 20, 250, 40);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.BLACK);
        panel.add(titleLabel);

        searchField = new JTextField();
        searchField.setBounds(150, 80, 200, 30);
        panel.add(searchField);

        searchBtn = createRoundedButton("Search");
        searchBtn.setBounds(370, 80, 100, 30);
        panel.add(searchBtn);

        resultLabel = new JLabel("");
        resultLabel.setBounds(150, 130, 400, 30);
        panel.add(resultLabel);

        JButton backBtn = createRoundedButton("Back");
        backBtn.setBounds(10, 320, 100, 35);
        backBtn.addActionListener(e -> {
            new CustomerDashboard();
            dispose();
        });
        panel.add(backBtn);

        add(panel);
        setVisible(true);

        loadProducts();

        searchBtn.addActionListener(e -> searchProduct());
    }

    private void loadProducts() {
        products = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader("products.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length >= 3) {
                    String name = parts[0];
                    int qty = Integer.parseInt(parts[1]);
                    double price = Double.parseDouble(parts[2]);
                    products.add(new Product(name, qty, price));
                }
            }
        } catch (IOException e) {
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
                resultLabel.setText("Product: " + p.getName() + " | Available quantity: " + p.getQuantity());
                return;
            }
        }
        resultLabel.setText("Product not found.");
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
            backgroundImage = new ImageIcon(imagePath).getImage();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
    }

    class Product {
        private String name;
        private int quantity;
        private double price;

        public Product(String name, int quantity, double price) {
            this.name = name;
            this.quantity = quantity;
            this.price = price;
        }

        public String getName() { return name; }
        public int getQuantity() { return quantity; }
        public double getPrice() { return price; }
    }

}