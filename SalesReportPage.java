import java.awt.*;
import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
import javax.swing.*;

public class SalesReportPage extends JFrame {
    private JTextArea reportArea;
    private JButton exportBtn;
    private double totalSales = 0;

    public SalesReportPage() {
        setTitle("Sales Report");
        setSize(600, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel(null);
        panel.setBackground(Color.WHITE);

        JLabel title = new JLabel("Sales Summary Report");
        title.setBounds(180, 20, 300, 30);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        panel.add(title);

        reportArea = new JTextArea();
        reportArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        reportArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(reportArea);
        scrollPane.setBounds(50, 70, 500, 300);
        panel.add(scrollPane);

        exportBtn = new JButton("Export Report");
        exportBtn.setBounds(220, 390, 150, 35);
        exportBtn.addActionListener(e -> exportReport());
        panel.add(exportBtn);

        JButton backBtn = new JButton("Back");
        backBtn.setBounds(10, 10, 80, 30);
        backBtn.addActionListener(e -> {
            new OwnerDashboard();
            dispose();
        });
        panel.add(backBtn);

        loadReport();

        add(panel);
        setVisible(true);
    }

    private void loadReport() {
        File file = new File("delivered_orders.txt");
        if (!file.exists()) {
            reportArea.setText("No delivered orders found.");
            return;
        }

        int orderCount = 0;
        totalSales = 0;
        List<String> lines = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                lines.add(line);
                double total = extractTotalPrice(line);
                totalSales += total;
                orderCount++;
            }
        } catch (IOException e) {
            reportArea.setText("Error reading delivered orders: " + e.getMessage());
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("----------------------\n");
        sb.append("Total Orders: ").append(orderCount).append("\n");
        sb.append("Total Sales: $").append(String.format("%.2f", totalSales)).append("\n");
        sb.append("----------------------\n");
        sb.append("\nORDER LIST:\n");

        for (String line : lines) {
            sb.append(line).append("\n");
        }

        reportArea.setText(sb.toString());
    }

    private double extractTotalPrice(String line) {
        try {
            if (line.contains("Total:")) {
                String[] parts = line.split("Total:");
                String amount = parts[1].replaceAll("[^0-9.]", "").trim();
                return Double.parseDouble(amount);
            } else if (line.toLowerCase().contains("total")) {
                for (String word : line.split("[|]") ) {
                    if (word.toLowerCase().contains("total")) {
                        String amt = word.replaceAll("[^0-9.]", "").trim();
                        return Double.parseDouble(amt);
                    }
                }
            }
        } catch (Exception ignored) {}
        return 0;
    }

    private void exportReport() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setSelectedFile(new File("sales_report.txt"));
        int option = fileChooser.showSaveDialog(this);
        if (option == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                writer.write(reportArea.getText());
                JOptionPane.showMessageDialog(this, "Report exported to " + file.getAbsolutePath());
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Failed to export: " + e.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(SalesReportPage::new);
    }
}
