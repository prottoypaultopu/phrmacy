import java.awt.*;
import java.io.*;
import javax.swing.*;


public class OwnerViewOrdersPage extends JFrame {
    private DefaultListModel<String> orderListModel;
    private JList<String> orderList;

    public OwnerViewOrdersPage() {
        setTitle("Customer Orders (Owner View)");
        setSize(600, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        BackgroundPanel panel = new BackgroundPanel("photo/home.jpg");
        panel.setLayout(null);

        JLabel titleLabel = new JLabel("Customer Orders");
        titleLabel.setBounds(200, 20, 200, 30);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        titleLabel.setForeground(Color.WHITE);
        panel.add(titleLabel);

        orderListModel = new DefaultListModel<>();
        orderList = new JList<>(orderListModel);
        JScrollPane scrollPane = new JScrollPane(orderList);
        scrollPane.setBounds(50, 70, 500, 180);
        panel.add(scrollPane);

        JButton fulfillBtn = createRoundedButton("Mark as Delivered");
        fulfillBtn.setBounds(180, 270, 200, 35);
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

        add(panel);
        setVisible(true);
    }

    private void loadOrders() {
        File file = new File("customer_orders.txt");
        if (!file.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            orderListModel.clear();
            while ((line = br.readLine()) != null) {
                orderListModel.addElement(line);
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

        // Move to delivered_orders.txt
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("delivered_orders.txt", true))) {
            bw.write(selected);
            bw.newLine();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error saving to delivered orders.");
            return;
        }

        // Remove from list and file
        orderListModel.removeElement(selected);
        saveRemainingOrders();
        JOptionPane.showMessageDialog(this, "Order marked as delivered.");
    }

    private void saveRemainingOrders() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("customer_orders.txt"))) {
            for (int i = 0; i < orderListModel.size(); i++) {
                bw.write(orderListModel.get(i));
                bw.newLine();
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

    // For testing standalone
    public static void main(String[] args) {
        SwingUtilities.invokeLater(OwnerViewOrdersPage::new);
    }
}