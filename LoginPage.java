import java.awt.*;
import javax.swing.*;


public class LoginPage extends JFrame {
    public LoginPage() {
        setTitle("Medicine Shop Login");
        setSize(600, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        BackgroundPanel panel = new BackgroundPanel("photo/home.jpg");
        panel.setLayout(null);

        JLabel welcomeLabel = new JLabel("--Welcome to Medicine Shop--");
        welcomeLabel.setBounds(105, 50, 700, 100);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 28));
        welcomeLabel.setForeground(Color.black);
        panel.add(welcomeLabel);

        JButton ownerBtn = createRoundedButton("Login as Owner");
        ownerBtn.setBounds(200, 150, 200, 40);
        panel.add(ownerBtn);

        JButton customerBtn = createRoundedButton("Login as Customer");
        customerBtn.setBounds(200, 220, 200, 40);
        panel.add(customerBtn);

        ownerBtn.addActionListener(e -> {
            JPasswordField passwordField = new JPasswordField();
            Object[] message = {
                    "Enter Owner Password:", passwordField
            };
            int option = JOptionPane.showConfirmDialog(this, message, "Owner Login", JOptionPane.OK_CANCEL_OPTION);
            if (option == JOptionPane.OK_OPTION) {
                char[] passwordChars = passwordField.getPassword();
                String password = new String(passwordChars);
                if (password.equals("topu")) {
                    new OwnerDashboard();
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Wrong password!");
                }
            }
        });


        customerBtn.addActionListener(e -> {
            new CustomerDashboard();
            dispose();
        });

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
        public BackgroundPanel(String path) {
            backgroundImage = new ImageIcon(path).getImage();
        }
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.drawImage(backgroundImage,0,0,getWidth(),getHeight(),this);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(LoginPage::new);
    }
}