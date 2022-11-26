package windows;

import java.awt.EventQueue;
import java.awt.GridLayout;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import database.UtilsSQLite;
import models.ModeloTransferencia;
import models.SnapshotValue;

import java.awt.event.MouseAdapter;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;

public class SnapshotTable extends JFrame {
	private String filePath = System.getProperty("user.dir") + "/src/database/database.db";
	private JPanel contentPane;
	private JTable table;
	private String value;

	
	public SnapshotTable() {

		DefaultTableModel model = new DefaultTableModel() {
			@Override
			public boolean isCellEditable(int filas, int columnas) {
				return false;
				
			}
		};
		
		model.addColumn("Name");
        model.addColumn("Date");
        
        Connection conn = UtilsSQLite.connect(filePath);
        ResultSet rs = UtilsSQLite.getTable(conn,"SELECT name,date FROM snaptshots;");
		
        try {
            while (rs.next()) {
                model.addRow(new Object[]{rs.getString("name"),rs.getString("date")});
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        
		table = new JTable(model);
		table.addMouseListener(new MouseAdapter() {


			@Override
			public void mouseClicked(MouseEvent e) {
				int seleccionar = table.rowAtPoint(e.getPoint());
				value = String.valueOf(table.getValueAt(seleccionar, 0));
			}
		});
		
		JScrollPane scroll = new JScrollPane(table);
		JButton btnSend = new JButton();
		btnSend.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ModeloTransferencia modelo = SnapshotValue.modeloTransferencia;
				modelo.setValue(value);
				setVisible(false);
			}
		});
		btnSend.setText("SEND");
		//setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();

		setContentPane(contentPane);
		GroupLayout gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane.setHorizontalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_contentPane.createSequentialGroup()
							.addGap(148)
							.addComponent(btnSend, GroupLayout.PREFERRED_SIZE, 113, GroupLayout.PREFERRED_SIZE))
						.addComponent(scroll, GroupLayout.PREFERRED_SIZE, 434, GroupLayout.PREFERRED_SIZE))
					.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
		);
		gl_contentPane.setVerticalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(Alignment.TRAILING, gl_contentPane.createSequentialGroup()
					.addComponent(scroll, GroupLayout.DEFAULT_SIZE, 190, Short.MAX_VALUE)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(btnSend, GroupLayout.PREFERRED_SIZE, 38, GroupLayout.PREFERRED_SIZE)
					.addGap(22))
		);
		contentPane.setLayout(gl_contentPane);
	}

}
