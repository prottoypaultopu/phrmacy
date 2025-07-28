import java.awt.*;
import javax.swing.*;


public class CustomerDashboard extends JFrame {
    public CustomerDashboard() {
        setTitle("Customer Dashboard");
        setSize(600, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        BackgroundPanel panel = new BackgroundPanel("photo/2.jpg");
        panel.setLayout(null);

        JLabel titleLabel = new JLabel("Customer Dashboard");
        titleLabel.setBounds(180, 20, 300, 40);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(Color.black);
        panel.add(titleLabel);

        // Button 1: Order Product
        JButton orderProductBtn = createRoundedButton("Order Product");
        orderProductBtn.setBounds(200, 100, 200, 40);
        orderProductBtn.addActionListener(e -> {
            new OrderProductPage();  // You'll need to create this page for ordering
            dispose();
        });
        panel.add(orderProductBtn);

        // Button 2: Check Availability
        JButton checkAvailabilityBtn = createRoundedButton("Check Availability");
        checkAvailabilityBtn.setBounds(200, 160, 200, 40);
        checkAvailabilityBtn.addActionListener(e -> {
            new CheckAvailabilityPage();  // You'll need to create this page for availability checking
            dispose();
        });
        panel.add(checkAvailabilityBtn);

        JButton backButton = createRoundedButton("Back");
        backButton.setBounds(10, 300, 100, 35);
        backButton.addActionListener(e -> {
            new LoginPage();
            dispose();
        });
        panel.add(backButton);

        add(panel);
        setVisible(true);
    }

    private JButton createRoundedButton(String text) {
        Color normalColor = new Color(0, 102, 204);
        Color hoverColor = new Color(51, 153, 255); // Lighter blue for hover

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

        // Add hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(hoverColor);
            }

            @Override
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
}