import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;

public class OrderTrackingPage extends JFrame {
    private JTextField contactField;
    private JTextArea orderDisplayArea;
    private JScrollPane scrollPane;

    public OrderTrackingPage() {
        setTitle("Track My Orders");
        setSize(700, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        BackgroundPanel panel = new BackgroundPanel("photo/home.jpg");
        panel.setLayout(null);
        setContentPane(panel);

        JLabel titleLabel = new JLabel("Track Your Orders");
        titleLabel.setBounds(250, 20, 300, 40);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        panel.add(titleLabel);

        JLabel contactLabel = new JLabel("Enter Contact Number:");
        contactLabel.setBounds(50, 80, 160, 25);
        contactLabel.setFont(new Font("Arial", Font.BOLD, 14));
        contactLabel.setForeground(Color.WHITE);
        panel.add(contactLabel);

        contactField = new JTextField();
        contactField.setBounds(220, 80, 200, 30);
        contactField.setFont(new Font("Arial", Font.PLAIN, 14));
        panel.add(contactField);

        JButton searchBtn = createRoundedButton("Track Orders");
        searchBtn.setBounds(440, 80, 120, 30);
        searchBtn.addActionListener(e -> trackOrders());
        panel.add(searchBtn);

        orderDisplayArea = new JTextArea();
        orderDisplayArea.setEditable(false);
        orderDisplayArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        orderDisplayArea.setBackground(new Color(255, 255, 255, 230));
        orderDisplayArea.setForeground(Color.BLACK);
        orderDisplayArea.setText("Enter your contact number and click 'Track Orders' to see your order history.");

        scrollPane = new JScrollPane(orderDisplayArea);
        scrollPane.setBounds(50, 130, 600, 350);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        panel.add(scrollPane);

        JButton clearBtn = createRoundedButton("Clear");
        clearBtn.setBounds(200, 500, 100, 35);
        clearBtn.addActionListener(e -> {
            contactField.setText("");
            orderDisplayArea.setText("Enter your contact number and click 'Track Orders' to see your order history.");
        });
        panel.add(clearBtn);

        JButton backBtn = createRoundedButton("Back");
        backBtn.setBounds(320, 500, 100, 35);
        backBtn.addActionListener(e -> {
            new CustomerDashboard();
            dispose();
        });
        panel.add(backBtn);

        setVisible(true);
    }

    private void trackOrders() {
        String contactNumber = contactField.getText().trim();

        if (contactNumber.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter your contact number!");
            return;
        }

        ArrayList<String> customerOrders = new ArrayList<>();
        ArrayList<String> deliveredOrders = new ArrayList<>();

        searchOrdersInFile("customer_orders.txt", contactNumber, customerOrders, "PENDING");

        searchOrdersInFile("delivered_orders.txt", contactNumber, deliveredOrders, "DELIVERED");

        displayOrderResults(contactNumber, customerOrders, deliveredOrders);
    }

    private void searchOrdersInFile(String fileName, String contactNumber, ArrayList<String> orders, String status) {
        File file = new File(fileName);
        if (!file.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            StringBuilder currentOrder = new StringBuilder();
            String line;
            boolean orderStarted = false;
            boolean contactMatches = false;

            while ((line = br.readLine()) != null) {
                if (line.trim().equals("--- ORDER ---")) {
                    // Save previous order if it matches
                    if (orderStarted && contactMatches && currentOrder.length() > 0) {
                        String orderWithStatus = currentOrder.toString().replace("Status: PENDING", "Status: " + status);
                        orders.add(orderWithStatus);
                    }

                    currentOrder = new StringBuilder();
                    orderStarted = true;
                    contactMatches = false;
                    currentOrder.append(line).append("\n");
                } else if (orderStarted) {
                    currentOrder.append(line).append("\n");

                    if (line.startsWith("Contact: ") && line.substring(9).trim().equals(contactNumber)) {
                        contactMatches = true;
                    }
                }
            }

            if (orderStarted && contactMatches && currentOrder.length() > 0) {
                String orderWithStatus = currentOrder.toString().replace("Status: PENDING", "Status: " + status);
                orders.add(orderWithStatus);
            }

        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error reading " + fileName + ": " + e.getMessage());
        }
    }

    private void displayOrderResults(String contactNumber, ArrayList<String> pendingOrders, ArrayList<String> deliveredOrders) {
        StringBuilder result = new StringBuilder();
        result.append("ORDER TRACKING RESULTS FOR: ").append(contactNumber).append("\n");
        result.append("=".repeat(60)).append("\n\n");

        if (pendingOrders.isEmpty() && deliveredOrders.isEmpty()) {
            result.append("No orders found for contact number: ").append(contactNumber).append("\n");
            result.append("Please check your contact number or contact the store if you believe this is an error.");
        } else {
            if (!pendingOrders.isEmpty()) {
                result.append("PENDING ORDERS (").append(pendingOrders.size()).append("):\n");
                result.append("-".repeat(40)).append("\n");
                for (int i = 0; i < pendingOrders.size(); i++) {
                    result.append("PENDING ORDER #").append(i + 1).append(":\n");
                    result.append(pendingOrders.get(i)).append("\n");
                }
            }

            if (!deliveredOrders.isEmpty()) {
                result.append("\nDELIVERED ORDERS (").append(deliveredOrders.size()).append("):\n");
                result.append("-".repeat(40)).append("\n");
                for (int i = 0; i < deliveredOrders.size(); i++) {
                    result.append("DELIVERED ORDER #").append(i + 1).append(":\n");
                    result.append(deliveredOrders.get(i)).append("\n");
                }
            }

            result.append("\n").append("=".repeat(60)).append("\n");
            result.append("SUMMARY:\n");
            result.append("Total Pending Orders: ").append(pendingOrders.size()).append("\n");
            result.append("Total Delivered Orders: ").append(deliveredOrders.size()).append("\n");
            result.append("Total Orders: ").append(pendingOrders.size() + deliveredOrders.size()).append("\n");
        }

        orderDisplayArea.setText(result.toString());
        orderDisplayArea.setCaretPosition(0); // Scroll to top
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
        button.setFont(new Font("Arial", Font.BOLD, 14));
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
        SwingUtilities.invokeLater(OrderTrackingPage::new);
    }
}