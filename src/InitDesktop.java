import java.awt.EventQueue;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;

import javax.swing.*;
import javax.swing.GroupLayout.Alignment;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.awt.event.InputEvent;

public class InitDesktop extends JFrame {
	private static InitDesktop frame;
	private JPanel contentPane;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
					frame = new InitDesktop();
					frame.setTitle("IETI Industry");
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public InitDesktop() {
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		JMenu mnArchivo = new JMenu("Archivo");
		menuBar.add(mnArchivo);
		
		JMenuItem mntmCargarConfiguracion = new JMenuItem("Cargar Configuracion");
		mntmCargarConfiguracion.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				openFile();
			}
		});
		mntmCargarConfiguracion.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK));
		mnArchivo.add(mntmCargarConfiguracion);
		
		
		JMenu mnVisualizacion = new JMenu("Visualizacion");
		menuBar.add(mnVisualizacion);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		GroupLayout gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane.setHorizontalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGap(0, 426, Short.MAX_VALUE)
		);
		gl_contentPane.setVerticalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGap(0, 253, Short.MAX_VALUE)
		);
		contentPane.setLayout(gl_contentPane);
		
		
		
	}
	
	private void openFile() { // Open ChoserFile for .xml
		JFileChooser jf = new JFileChooser(System.getProperty("user.dir"));
		FileNameExtensionFilter filtro = new FileNameExtensionFilter("Archivos XML","xml");
		jf.setFileFilter(filtro);
		jf.showOpenDialog(this);
		File archivo = jf.getSelectedFile();
		
		if (archivo != null) {
			System.out.println(archivo.getAbsolutePath());
		}
	}
}
