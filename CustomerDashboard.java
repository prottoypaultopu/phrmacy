import javax.swing.*;
import java.awt.*;

public class CustomerDashboard extends JFrame {

    public CustomerDashboard() {
        setTitle("Customer Dashboard");
        setSize(600, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        JPanel panel = new BackgroundPanel("photo/home.jpg");
        panel.setLayout(null);

        JLabel titleLabel = new JLabel("Customer Dashboard");
        titleLabel.setBounds(180, 20, 300, 40);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        panel.add(titleLabel);

        JButton searchOrderBtn = createRoundedButton("Search & Order Medicine");
        searchOrderBtn.setBounds(180, 100, 240, 40);
        searchOrderBtn.addActionListener(e -> {
            new SearchOrderPage();
            dispose();
        });
        panel.add(searchOrderBtn);

        JButton checkAvailabilityBtn = createRoundedButton("Check Availability");
        checkAvailabilityBtn.setBounds(180, 160, 240, 40);
        checkAvailabilityBtn.addActionListener(e -> {
            new CheckAvailabilityPage();
            dispose();
        });
        panel.add(checkAvailabilityBtn);

        JButton trackOrderBtn = createRoundedButton("Track My Orders");
        trackOrderBtn.setBounds(180, 220, 240, 40);
        trackOrderBtn.addActionListener(e -> {
            new OrderTrackingPage();
            dispose();
        });
        panel.add(trackOrderBtn);

        JButton logoutBtn = createRoundedButton("Logout");
        logoutBtn.setBounds(180, 280, 240, 40);
        logoutBtn.addActionListener(e -> {
            new LoginPage();
            dispose();
        });
        panel.add(logoutBtn);

        add(panel);
        setVisible(true);
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
                super.paintComponent(g);
                g2.dispose();
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
        SwingUtilities.invokeLater(CustomerDashboard::new);
    }
}
