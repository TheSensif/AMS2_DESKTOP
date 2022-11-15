import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import modules.DataModule;

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
import java.util.Map;
import java.util.Objects;

public class InitDesktop extends JFrame {
	private static InitDesktop frame;
	private JPanel contentPane;
	private JScrollPane scrollPane;
	private Boolean activated;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					//Database.startDatabase();
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
		
		JMenu mnArchivo = new JMenu("File");
		menuBar.add(mnArchivo);
		
		JMenuItem mntmCargarConfiguracion = new JMenuItem("Charge configuration");
		//Charging the icon
		/*BufferedImage bi = ImageIO.read(new File(System.getProperty("user.dir") + "/src/images/load.png"));
		Image icon = bi.getScaledInstance(16,16,Image.SCALE_SMOOTH);
		mntmCargarConfiguracion.setIcon(new ImageIcon(icon));*/
		mntmCargarConfiguracion.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				openFile();
			}
		});
		mntmCargarConfiguracion.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK));
		mnArchivo.add(mntmCargarConfiguracion);
		
		
		JMenu mnVisualizacion = new JMenu("Visualization");
		menuBar.add(mnVisualizacion);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new GridLayout(2, 2));
		setContentPane(contentPane);

		
		
	}
	
	private void openFile() { // Open ChoserFile for .xml
		HashMap<String,DataModule> dm = new HashMap<>();
		//Generating a filter for the file
		JFileChooser jf = new JFileChooser(System.getProperty("user.dir"));
		FileNameExtensionFilter filtro = new FileNameExtensionFilter("XML Files","xml");
		jf.setFileFilter(filtro);
		jf.showOpenDialog(this);
		File archivo = jf.getSelectedFile();
		
		if (archivo != null) {
			String name = archivo.getName();
			String [] part = name.split("\\.");
			//If the file is filtered we will open it
			if (Objects.equals(part[1],"xml")){
				try {
					DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
					DocumentBuilder db = null;
					db = dbf.newDocumentBuilder();
					Document doc = db.parse(archivo);

					//We will store this element on a Datamodule if it exists
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

					//We will store this element on a Datamodule if it exists
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
					
					//We will store this element on a Datamodule if it exists
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

						//We will store this element on a Datamodule if it exists
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
					
					ArrayList<String> componentes = new ArrayList<>();
				

					//We will create an array with the length of the quantity of the components
					//The array will be the quanity but with formated to string, to match the information that the XML provides
					for (int i = 0; i < dm.size(); i++) {
						componentes.add(String.valueOf(i));
					}
					
					//Update the panel view
					contentPane.revalidate();
					contentPane.repaint();

					
					for (int i = 0; i < dm.size(); i++) {		
						//We will look to get the component id with its tagName
						//Depending on the tagname it will create a diferent component
						switch (dm.get(componentes.get(i)).getEtiqueta()) {
							case "switch":
								scrollPane = new JScrollPane();
								
								JToggleButton tglbtn = new JToggleButton("");
								//We will get the inital state specified on the xml
								if ((dm.get(componentes.get(i)).getDefaul().equals("on"))) {
									activated = true;
									tglbtn.setText("Active");
								} else {
									activated = false;
									tglbtn.setText("Not active");

								}
								
								tglbtn.addActionListener(new ActionListener() {
									
									//Changing its state every time its pressed
									public void actionPerformed(ActionEvent e) {
										if (activated == true) {
											tglbtn.setText("Not active");
											tglbtn.disable();
											activated = false;
										}
										else {
											tglbtn.setText("Active");
											tglbtn.enable();
											activated = true;
										}
									}
								});;
								scrollPane.setViewportView(tglbtn);
								contentPane.add(scrollPane);
								
								break;
							case "slider":
								JSlider jSlider = new JSlider();
								scrollPane = new JScrollPane();

								//Firstly we will get it as a string, then we will have to convert it into a double because it has a decimal, finally we will convert it into int
								jSlider.setValue(Double.valueOf((dm.get(componentes.get(i)).getDefaul())).intValue());
								scrollPane.setViewportView(jSlider);
								contentPane.add(scrollPane);

								break;
							case "dropdown":
								JComboBox comboBox = new JComboBox();
								scrollPane = new JScrollPane();

								//Creating an arraylist with the keys of the hashmap
								ArrayList<String> dropdownKeys = new ArrayList<>();
								for (Object key : dm.get(componentes.get(i)).getValue().keySet()) {
								    String lKey = (String) key;
								    dropdownKeys.add(lKey);
								}
								
								//We will iterate a for over with the Id quantity
								for (int j = 0; j < dropdownKeys.size(); j++) {
									
									//Checking if the iteration is the same of the deafult value
									if (String.valueOf(dropdownKeys.get(j)).equals(String.valueOf(dm.get(componentes.get(i)).getDefaul()))) {
										comboBox.addItem((dm.get(componentes.get(i)).getValue().get(dropdownKeys.get(j))));
										//If it is, we will set it as selected Item
										comboBox.setSelectedItem((dm.get(componentes.get(i)).getValue().get(dropdownKeys.get(j))));
									} else {
										//If not instead we just add it at the Dropdown
										comboBox.addItem((dm.get(componentes.get(i)).getValue().get(dropdownKeys.get(j))));
									}
									
								}
								
								scrollPane.setViewportView(comboBox);
								contentPane.add(scrollPane);
								break;
							case "sensor":
								JTextArea textArea = new JTextArea();
								scrollPane = new JScrollPane();
								//The sensor will be a textArea with the specified units
								textArea.setText(dm.get(componentes.get(i)).getUnits());
								//Enabled at false to not let the user modify it
								textArea.setEnabled(false);
								
								scrollPane.setViewportView(textArea);
								contentPane.add(scrollPane);

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
				
				System.out.println("It is xml");
			}else {
				System.out.println("It is not an xml");
				JOptionPane.showMessageDialog(null," It is not an xml");
			}

		}
	}
}
