import java.awt.*;
import java.io.*;
import java.util.*;
import javax.swing.*;


public class SearchOrderPage extends JFrame {
    private JTextField searchField;
    private DefaultListModel<String> searchResultsModel;
    private JList<String> searchResults;

    public SearchOrderPage() {
        setTitle("Search & Order Medicine");
        setSize(600, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        BackgroundPanel panel = new BackgroundPanel("photo/3.jpg");
        panel.setLayout(null);

        JLabel titleLabel = new JLabel("Search Medicine");
        titleLabel.setBounds(220, 20, 200, 40);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.blue);
        panel.add(titleLabel);

        JLabel searchLabel = new JLabel("Search by Name or Company:");
        searchLabel.setBounds(50, 80, 200, 30);
        searchLabel.setForeground(Color.black);
        panel.add(searchLabel);

        searchField = new JTextField();
        searchField.setBounds(250, 80, 250, 30);
        panel.add(searchField);

        JButton searchBtn = createRoundedButton("Search");
        searchBtn.setBounds(200, 130, 200, 40);
        panel.add(searchBtn);

        searchResultsModel = new DefaultListModel<>();
        searchResults = new JList<>(searchResultsModel);
        JScrollPane scrollPane = new JScrollPane(searchResults);
        scrollPane.setBounds(50, 190, 500, 120);
        panel.add(scrollPane);

        JButton orderBtn = createRoundedButton("Order Selected Medicine");
        orderBtn.setBounds(180, 320, 230, 40);
        panel.add(orderBtn);

        JButton backBtn = createRoundedButton("back");
        backBtn.setBounds(10, 10, 80, 30);
        panel.add(backBtn);

        searchBtn.addActionListener(e -> performSearch());
        orderBtn.addActionListener(e -> orderSelectedMedicine());
        backBtn.addActionListener(e -> {
            new CustomerDashboard();
            dispose();
        });

        setContentPane(panel);
        setVisible(true);
    }

    private void performSearch() {
        String query = searchField.getText().trim().toLowerCase();
        searchResultsModel.clear();
        if (query.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter search text.");
            return;
        }

        File file = new File("products.txt");
        if (!file.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            boolean found = false;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length < 5) continue;
                String name = parts[0].toLowerCase();
                String company = parts[4].toLowerCase();

                if (name.contains(query) || company.contains(query)) {
                    searchResultsModel.addElement(line);
                    found = true;
                }
            }
            if (!found) {
                JOptionPane.showMessageDialog(this, "No products found.");
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error reading products: " + e.getMessage());
        }
    }

    private void orderSelectedMedicine() {
        String selected = searchResults.getSelectedValue();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Please select a medicine to order.");
            return;
        }

        String[] parts = selected.split("\\|");
        if (parts.length < 5) return;
        String name = parts[0];
        int availableQty;
        try {
            availableQty = Integer.parseInt(parts[1]);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid quantity in data.");
            return;
        }

        String qtyStr = JOptionPane.showInputDialog(this, "Enter quantity to order (Available: " + availableQty + "):");
        if (qtyStr == null) return;

        int orderQty;
        try {
            orderQty = Integer.parseInt(qtyStr);
            if (orderQty <= 0 || orderQty > availableQty) {
                JOptionPane.showMessageDialog(this, "Invalid quantity.");
                return;
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid quantity.");
            return;
        }

        // Save to customer_orders.txt (simple append)
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("customer_orders.txt", true))) {
            bw.write(name + "|" + orderQty);
            bw.newLine();
            JOptionPane.showMessageDialog(this, "Order placed for " + orderQty + " of " + name);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error saving order: " + e.getMessage());
        }
    }

    private JButton createRoundedButton(String text) {
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
        button.setBackground(new Color(0, 102, 204));
        button.setFont(new Font("Arial", Font.BOLD, 16));
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(SearchOrderPage::new);
    }
}