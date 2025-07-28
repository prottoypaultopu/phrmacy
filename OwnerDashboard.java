import java.awt.*;
import javax.swing.*;


public class OwnerDashboard extends JFrame {
    public OwnerDashboard() {
        setTitle("Owner Dashboard");
        setSize(600, 480);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        BackgroundPanel panel = new BackgroundPanel("photo/home.jpg");
        panel.setLayout(null);

        JLabel titleLabel = new JLabel("Owner Dashboard");
        titleLabel.setBounds(200, 20, 250, 40);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        panel.add(titleLabel);

        JButton addProductBtn = createRoundedButton("Add Product");
        addProductBtn.setBounds(200, 80, 200, 40);
        addProductBtn.addActionListener(e -> {
            new AddProductPage();
            dispose();
        });
        panel.add(addProductBtn);

        JButton viewOrdersBtn = createRoundedButton("View Orders");
        viewOrdersBtn.setBounds(200, 130, 200, 40);
        viewOrdersBtn.addActionListener(e -> {
            new OwnerViewOrdersPage();
            dispose();
        });
        panel.add(viewOrdersBtn);

        JButton sellProductBtn = createRoundedButton("Sell Product");
        sellProductBtn.setBounds(200, 180, 200, 40);
        sellProductBtn.addActionListener(e -> {
            new SellProductPage();
            dispose();
        });
        panel.add(sellProductBtn);

        JButton restockBtn = createRoundedButton("Restock Medicine");
        restockBtn.setBounds(200, 230, 200, 40);
        restockBtn.addActionListener(e -> {
            new RestockMedicinePage();
            dispose();
        });
        panel.add(restockBtn);

        JButton viewAllBtn = createRoundedButton("View All Medicines");
        viewAllBtn.setBounds(200, 280, 200, 40);
        viewAllBtn.addActionListener(e -> {
            new ViewAllMedicinesPage();
            dispose();
        });
        panel.add(viewAllBtn);

        JButton backBtn = createRoundedButton("Back");
        backBtn.setBounds(20, 400, 100, 40);
        backBtn.addActionListener(e -> {
            new LoginPage();
            dispose();
        });
        panel.add(backBtn);

        add(panel);
        setVisible(true);
    }

    private JButton createRoundedButton(String text) {
        Color normalColor = new Color(0, 102, 204);
        Color hoverColor = new Color(51, 153, 255);

        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                if (getModel().isArmed()) {
                    g.setColor(hoverColor.darker());
                } else if (getModel().isRollover()) {
                    g.setColor(hoverColor);
                } else {
                    g.setColor(getBackground());
                }
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
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

    // Background panel class
    public static class BackgroundPanel extends JPanel {
        private Image backgroundImage;

        public BackgroundPanel(String imagePath) {
            try {
                backgroundImage = new ImageIcon(imagePath).getImage();
            } catch (Exception e) {
                System.err.println("Image not found: " + imagePath);
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
        SwingUtilities.invokeLater(OwnerDashboard::new);
    }
}