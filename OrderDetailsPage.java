import java.awt.*;
import java.io.*;
import java.util.*;
import javax.swing.*;

public class OrderDetailsPage extends JFrame {
    private JComboBox<String> nameBox;
    private JTextField phoneField, transactionField;
    private JComboBox<String> paymentMethodBox;
    private JLabel transactionLabel;

    private String productName;
    private int quantity;
    private Set<String> customerNamesSet;

    public OrderDetailsPage(String productName, int quantity) {
        this.productName = productName;
        this.quantity = quantity;
        this.customerNamesSet = new HashSet<>();

        setTitle("Order Details");
        setSize(500, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.setBackground(Color.WHITE);

        JLabel title = new JLabel("Enter Order Details");
        title.setBounds(150, 10, 250, 30);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        panel.add(title);

        JLabel nameLabel = new JLabel("Customer Name:");
        nameLabel.setBounds(50, 60, 150, 25);
        panel.add(nameLabel);

        nameBox = new JComboBox<>();
        loadCustomerNames(); // Load names from file
        nameBox.setEditable(true);
        nameBox.setBounds(200, 60, 200, 25);
        panel.add(nameBox);

        JLabel phoneLabel = new JLabel("Phone Number:");
        phoneLabel.setBounds(50, 100, 150, 25);
        panel.add(phoneLabel);

        phoneField = new JTextField();
        phoneField.setBounds(200, 100, 200, 25);
        panel.add(phoneField);

        JLabel productLabel = new JLabel("Product:");
        productLabel.setBounds(50, 140, 150, 25);
        panel.add(productLabel);

        JLabel productValue = new JLabel(productName);
        productValue.setBounds(200, 140, 200, 25);
        panel.add(productValue);

        JLabel quantityLabel = new JLabel("Quantity:");
        quantityLabel.setBounds(50, 180, 150, 25);
        panel.add(quantityLabel);

        JLabel quantityValue = new JLabel(String.valueOf(quantity));
        quantityValue.setBounds(200, 180, 200, 25);
        panel.add(quantityValue);

        JLabel paymentLabel = new JLabel("Payment Method:");
        paymentLabel.setBounds(50, 220, 150, 25);
        panel.add(paymentLabel);

        paymentMethodBox = new JComboBox<>(new String[]{"Cash on Delivery", "Online"});
        paymentMethodBox.setBounds(200, 220, 200, 25);
        panel.add(paymentMethodBox);

        transactionLabel = new JLabel("Transaction ID:");
        transactionLabel.setBounds(50, 260, 150, 25);
        transactionLabel.setVisible(false);
        panel.add(transactionLabel);

        transactionField = new JTextField();
        transactionField.setBounds(200, 260, 200, 25);
        transactionField.setVisible(false);
        panel.add(transactionField);

        paymentMethodBox.addActionListener(e -> {
            boolean isOnline = paymentMethodBox.getSelectedItem().equals("Online");
            transactionLabel.setVisible(isOnline);
            transactionField.setVisible(isOnline);
        });

        JButton confirmBtn = new JButton("Confirm Order");
        confirmBtn.setBounds(150, 310, 180, 30);
        confirmBtn.addActionListener(e -> confirmOrder());
        panel.add(confirmBtn);

        add(panel);
        setVisible(true);
    }

    private void confirmOrder() {
        String name = ((JTextField) nameBox.getEditor().getEditorComponent()).getText().trim();
        String phone = phoneField.getText().trim();
        String payment = (String) paymentMethodBox.getSelectedItem();
        String txnId = transactionField.getText().trim();

        if (name.isEmpty() || phone.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all required fields.");
            return;
        }

        if (!phone.matches("\\d{10}")) {
            JOptionPane.showMessageDialog(this, "Please enter a valid 10-digit phone number.");
            return;
        }

        if (payment.equals("Online") && txnId.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter transaction ID for online payment.");
            return;
        }

        // Sanitize
        name = name.replace("|", "/");
        phone = phone.replace("|", "/");
        txnId = txnId.replace("|", "/");

        try {
            String orderPath = System.getProperty("user.home") + File.separator + "owner_orders.txt";
            BufferedWriter bw = new BufferedWriter(new FileWriter(orderPath, true));
            bw.write(name + "|" + phone + "|" + productName + "|" + quantity + "|" + payment + "|" + (payment.equals("Online") ? txnId : "N/A"));
            bw.newLine();
            bw.close();

            saveCustomerName(name); // Append new name if not already saved

            JOptionPane.showMessageDialog(this, "Order submitted successfully!");
            dispose();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error saving order: " + e.getMessage());
        }
    }

    private void loadCustomerNames() {
        String path = System.getProperty("user.home") + File.separator + "customer_names.txt";
        File file = new File(path);

        nameBox.addItem(""); // empty option

        if (file.exists()) {
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = br.readLine()) != null) {
                    String name = line.trim();
                    if (!name.isEmpty() && customerNamesSet.add(name)) {
                        nameBox.addItem(name);
                    }
                }
            } catch (IOException e) {
                System.out.println("Error reading customer names: " + e.getMessage());
            }
        }
    }

    private void saveCustomerName(String name) {
        if (customerNamesSet.contains(name)) return; // already exists

        customerNamesSet.add(name);
        nameBox.addItem(name);

        String path = System.getProperty("user.home") + File.separator + "customer_names.txt";
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(path, true))) {
            bw.write(name);
            bw.newLine();
        } catch (IOException e) {
            System.out.println("Error saving customer name: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new OrderDetailsPage("Paracetamol", 5));
    }
}
