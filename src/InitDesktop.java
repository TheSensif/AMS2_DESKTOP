import database.Database;
import modules.DataModule;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.GroupLayout.Alignment;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import java.awt.event.InputEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class InitDesktop extends JFrame {
	private static InitDesktop frame;
	private JPanel contentPane;
	private JScrollPane scrollPane;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Database.startDatabase();
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
	public InitDesktop() throws IOException {
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		JMenu mnArchivo = new JMenu("Archivo");
		menuBar.add(mnArchivo);
		
		JMenuItem mntmCargarConfiguracion = new JMenuItem("Cargar Configuracion");
		// Cargar icono
		BufferedImage bi = ImageIO.read(new File(System.getProperty("user.dir") + "/src/images/load.png"));
		Image icon = bi.getScaledInstance(16,16,Image.SCALE_SMOOTH);
		mntmCargarConfiguracion.setIcon(new ImageIcon(icon));
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

		scrollPane = new JScrollPane();
		GroupLayout gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane.setHorizontalGroup(
				gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_contentPane.createSequentialGroup()
								.addContainerGap()
								.addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 410, GroupLayout.PREFERRED_SIZE)
								.addContainerGap(24, Short.MAX_VALUE))
		);
		gl_contentPane.setVerticalGroup(
				gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_contentPane.createSequentialGroup()
								.addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 231, GroupLayout.PREFERRED_SIZE)
								.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
		);
		contentPane.setLayout(gl_contentPane);
		
		
		
	}
	
	private void openFile() { // Open ChoserFile for .xml
		HashMap<String,DataModule> dm = new HashMap<>();
		JFileChooser jf = new JFileChooser(System.getProperty("user.dir"));
		FileNameExtensionFilter filtro = new FileNameExtensionFilter("Archivos XML","xml");
		jf.setFileFilter(filtro);
		jf.showOpenDialog(this);
		File archivo = jf.getSelectedFile();
		
		if (archivo != null) {
			String name = archivo.getName();
			String [] part = name.split("\\.");
			if (Objects.equals(part[1],"xml")){
				try {
					DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
					DocumentBuilder db = null;
					db = dbf.newDocumentBuilder();
					Document doc = db.parse(archivo);

					NodeList switc = doc.getElementsByTagName("switch");
					if ( switc.getLength() != 0) {
						for (int i = 0; i < switc.getLength(); i++) {
							Node node = switc.item(i);

							if (node.getNodeType() == Node.ELEMENT_NODE) {
								Element e = (Element) node;
								DataModule id = new DataModule(e.getTagName(),e.getAttribute("id"),e.getAttribute("default"),e.getTextContent());

								dm.put(e.getAttribute("id"),id);
							}
						}

						System.out.println(dm);
					}

					NodeList slider = doc.getElementsByTagName("slider");

					if (slider.getLength() != 0) {
						for (int i = 0; i < slider.getLength(); i++) {
							Node node = slider.item(i);

							if (node.getNodeType() == Node.ELEMENT_NODE) {
								Element e = (Element) node;
								DataModule id = new DataModule(e.getTagName(),e.getAttribute("id"),e.getAttribute("default"),Integer.parseInt(e.getAttribute("min")),Integer.parseInt(e.getAttribute("max")),e.getAttribute("step"),e.getTextContent());
								dm.put(e.getAttribute("id"),id);
							}
						}
						System.out.println(dm);

					}
					NodeList dropdown = doc.getElementsByTagName("dropdown");

					if (dropdown.getLength() != 0) {
						for (int i = 0; i < dropdown.getLength(); i++) {
							Node node = dropdown.item(i);

							if (node.getNodeType() == Node.ELEMENT_NODE) {
								Element e = (Element) node;
								HashMap<String,String> values = new HashMap();
								NodeList option = doc.getElementsByTagName("option");
								for (int j = 0; j < e.getElementsByTagName("option").getLength(); j++) {
									Node nodeOption = option.item(j);
									if (nodeOption.getNodeType() == Node.ELEMENT_NODE) {
										Element eo = (Element) nodeOption;
										values.put(eo.getAttribute("value"), eo.getTextContent());
									}
								}
								DataModule id = new DataModule(e.getTagName(),e.getAttribute("id"),e.getAttribute("default"),values);
								dm.put(e.getAttribute("id"),id);
							}
							System.out.println(dm);
						}

						NodeList sensor = doc.getElementsByTagName("sensor");

						if (sensor.getLength() != 0) {
							for (int i = 0; i < sensor.getLength(); i++) {
								Node node = sensor.item(i);

								if (node.getNodeType() == Node.ELEMENT_NODE) {
									Element e = (Element) node;
									DataModule id = new DataModule(e.getTagName(),e.getAttribute("id"),e.getAttribute("units"),Integer.parseInt(e.getAttribute("thresholdlow")),Integer.parseInt(e.getAttribute("thresholdhigh")),e.getTextContent());
									dm.put(e.getAttribute("id"),id);
								}
							}
							System.out.println(dm);

						}

					}
					System.out.println(dm.size());
					for (int i = 0; i < dm.size(); i++) {
						switch (dm.get(i).getEtiqueta()) {
							case "switch":
								break;
							case "slider":
								break;
							case "dropdown":
								break;
							case "sensor":
								break;
						}
					}

					doc.getDocumentElement().normalize();
				} catch (ParserConfigurationException e) {
					throw new RuntimeException(e);
				} catch (IOException e) {
					throw new RuntimeException(e);
				} catch (SAXException e) {
					throw new RuntimeException(e);
				}

				System.out.println("Es un xml");
			}else {
				System.out.println("No es un xml");
			}

		}
	}
}
