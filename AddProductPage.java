import java.awt.*;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import javax.swing.*;


public class AddProductPage extends JFrame {
    private JTextField nameField, quantityField, priceField, expiryField, companyField;

    public AddProductPage() {
        setTitle("Add Product");
        setSize(500, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        BackgroundPanel panel = new BackgroundPanel("photo/home.jpg");
        panel.setLayout(null);

        JLabel titleLabel = new JLabel("Add Product");
        titleLabel.setBounds(170, 10, 150, 30);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        titleLabel.setForeground(Color.WHITE);
        panel.add(titleLabel);

        addLabelAndField(panel, "Name:", nameField = new JTextField(), 60);
        addLabelAndField(panel, "Quantity:", quantityField = new JTextField(), 100);
        addLabelAndField(panel, "Price (per piece):", priceField = new JTextField(), 140);
        addLabelAndField(panel, "Expiry Date:", expiryField = new JTextField(), 180);
        addLabelAndField(panel, "Company:", companyField = new JTextField(), 220);

        JButton addBtn = createRoundedButton("Add Product");
        addBtn.setBounds(170, 270, 150, 40);
        addBtn.addActionListener(e -> addProduct());
        panel.add(addBtn);

        JButton backBtn = createRoundedButton("Back");
        backBtn.setBounds(10, 10, 80, 30);
        backBtn.addActionListener(e -> {
            new OwnerDashboard();
            dispose();
        });
        panel.add(backBtn);

        setContentPane(panel);
        setVisible(true);
    }

    private void addLabelAndField(JPanel panel, String labelText, JTextField textField, int y) {
        JLabel label = new JLabel(labelText);
        label.setBounds(50, y, 120, 30);
        label.setForeground(Color.BLACK);
        panel.add(label);

        textField.setBounds(160, y, 250, 30);
        panel.add(textField);
    }

    private void addProduct() {
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
            int qty = Integer.parseInt(quantity);
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

            nameField.setText("");
            quantityField.setText("");
            priceField.setText("");
            expiryField.setText("");
            companyField.setText("");
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error saving product: " + ex.getMessage());
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

    class BackgroundPanel extends JPanel {
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
        SwingUtilities.invokeLater(AddProductPage::new);
    }
}
