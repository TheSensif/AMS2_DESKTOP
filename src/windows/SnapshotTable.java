package windows;

import database.UtilsSQLite;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SnapshotTable extends JFrame {
    String filePath = System.getProperty("user.dir") + "/src/database/database.db";
    private JPanel contentPane;
    public SnapshotTable() {
        init();
        mostrar();
    }

    private void mostrar() {
        DefaultTableModel model = new DefaultTableModel();
        Connection conn = UtilsSQLite.connect(filePath);
        ResultSet rs = UtilsSQLite.getTable(conn,"SELECT name,date FROM snaptshots;");
        model.addColumn("Name");
        model.addColumn("Date");
        try {
            while (rs.next()) {
                model.addRow(new Object[]{rs.getString("name"),rs.getString("date")});
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        JTable table = new JTable();
        table.setModel(model);
        contentPane.add(table);
    }

    public void init() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 450, 300);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

        setContentPane(contentPane);
    }
}
