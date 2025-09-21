import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import javax.swing.*;

public class CustomerCancelOrderPage extends JFrame {
    private DefaultListModel<String> orderListModel;
    private JList<String> orderList;

    public CustomerCancelOrderPage() {
        setTitle("Cancel Pending Order");
        setSize(600, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel(null);
        panel.setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel("Pending Orders");
        titleLabel.setBounds(200, 20, 200, 30);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        panel.add(titleLabel);

        orderListModel = new DefaultListModel<>();
        orderList = new JList<>(orderListModel);
        JScrollPane scrollPane = new JScrollPane(orderList);
        scrollPane.setBounds(50, 70, 500, 200);
        panel.add(scrollPane);

        JButton cancelBtn = new JButton("Cancel Selected Order");
        cancelBtn.setBounds(180, 290, 200, 35);
        cancelBtn.addActionListener(e -> cancelSelectedOrder());
        panel.add(cancelBtn);

        JButton backBtn = new JButton("Back");
        backBtn.setBounds(10, 10, 80, 30);
        backBtn.addActionListener(e -> {
            new CustomerDashboard();
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

    private void cancelSelectedOrder() {
        String selected = orderList.getSelectedValue();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Please select an order to cancel.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to cancel this order?", "Confirm Cancel", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        // Move to cancelled_orders.txt
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("cancelled_orders.txt", true))) {
            bw.write(selected);
            bw.newLine();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error moving to cancelled orders: " + e.getMessage());
            return;
        }

        // Remove from customer_orders.txt
        orderListModel.removeElement(selected);
        saveRemainingOrders();
        JOptionPane.showMessageDialog(this, "Order cancelled.");
    }

    private void saveRemainingOrders() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("customer_orders.txt"))) {
            for (int i = 0; i < orderListModel.size(); i++) {
                bw.write(orderListModel.get(i));
                bw.newLine();
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error updating orders: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(CustomerCancelOrderPage::new);
    }
}
