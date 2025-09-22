import java.awt.*;
import java.io.*;
import java.time.LocalDate;
import javax.swing.*;
import javax.swing.table.*;

public class ViewAllMedicinesPage extends JFrame {
    private JTable table;
    private DefaultTableModel tableModel;

    public ViewAllMedicinesPage() {
        setTitle("View All Medicines");
        setSize(700, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        BackgroundPanel panel = new BackgroundPanel("photo/home.jpg");
        panel.setLayout(new BorderLayout());
        setContentPane(panel);

        String[] columnNames = {"Name", "Quantity", "Price", "Expiry Date", "Company"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);
        table.setFillsViewportHeight(true);

        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);

        JButton backBtn = createRoundedButton("Back");
        backBtn.addActionListener(e -> {
            new OwnerDashboard();
            dispose();
        });

        JPanel bottomPanel = new JPanel();
        bottomPanel.setOpaque(false);
        bottomPanel.add(backBtn);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        loadProducts();

        setVisible(true);
    }

    private void loadProducts() {
        tableModel.setRowCount(0);
        try (BufferedReader reader = new BufferedReader(new FileReader("products.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length >= 5) {
                    int quantity = Integer.parseInt(parts[1].trim());
                    String expiryStr = parts[3].trim();
                    LocalDate expiryDate;
                    boolean isExpiringSoon = false;

                    try {
                        expiryDate = LocalDate.parse(expiryStr);
                        LocalDate today = LocalDate.now();
                        if (!expiryDate.isBefore(today) && expiryDate.minusDays(30).isBefore(today)) {
                            isExpiringSoon = true;
                        }
                    } catch (Exception ex) {
                        expiryDate = null;
                    }

                    Object[] row = parts;

                    tableModel.addRow(row);

                    table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
                        @Override
                        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                                       boolean hasFocus, int row, int column) {
                            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                            int qty = Integer.parseInt(table.getValueAt(row, 1).toString());
                            String expiry = table.getValueAt(row, 3).toString();
                            LocalDate now = LocalDate.now();
                            boolean expiring = false;
                            try {
                                LocalDate exp = LocalDate.parse(expiry);
                                expiring = !exp.isBefore(now) && exp.minusDays(30).isBefore(now);
                            } catch (Exception ignored) {}

                            if (qty <= 5) {
                                c.setForeground(Color.RED);
                            } else if (expiring) {
                                c.setForeground(Color.ORANGE);
                            } else {
                                c.setForeground(Color.BLACK);
                            }
                            return c;
                        }
                    });
                }
            }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error loading products: " + ex.getMessage());
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

    public static class BackgroundPanel extends JPanel {
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
        SwingUtilities.invokeLater(ViewAllMedicinesPage::new);
    }
}
