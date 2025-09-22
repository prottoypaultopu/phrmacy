import java.awt.*;
import java.io.*;
import javax.swing.*;

public class OfflineSalesReportPage extends JFrame {
    private DefaultListModel<String> salesListModel;
    private JList<String> salesList;

    public OfflineSalesReportPage() {
        setTitle("Offline Sales Report");
        setSize(600, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        BackgroundPanel panel = new BackgroundPanel("photo/home.jpg");
        panel.setLayout(null);
        setContentPane(panel);

        JLabel titleLabel = new JLabel("Offline Sales Report");
        titleLabel.setBounds(200, 20, 300, 30);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        panel.add(titleLabel);

        salesListModel = new DefaultListModel<>();
        salesList = new JList<>(salesListModel);
        salesList.setFont(new Font("Monospaced", Font.PLAIN, 13));
        salesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(salesList);
        scrollPane.setBounds(50, 70, 500, 280);
        panel.add(scrollPane);

        JButton backBtn = createRoundedButton("Back");
        backBtn.setBounds(10, 10, 80, 30);
        backBtn.addActionListener(e -> {
            new OwnerDashboard();
            dispose();
        });
        panel.add(backBtn);

        loadSales();
        setVisible(true);
    }

    private void loadSales() {
        File file = new File("offline_sales.txt");
        if (!file.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                salesListModel.addElement(line);
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error reading sales: " + e.getMessage());
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
        SwingUtilities.invokeLater(OfflineSalesReportPage::new);
    }
}