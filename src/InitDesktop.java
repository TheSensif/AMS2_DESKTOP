import database.Database;
import models.ModeloTransferencia;
import models.SnapshotValue;
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
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import java.awt.event.InputEvent;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.handshake.ServerHandshake;
import org.json.*;
import windows.SnapshotTable;


public class InitDesktop extends JFrame {
	private ModeloTransferencia modelo = SnapshotValue.modeloTransferencia;
	private File archivo;
	private static InitDesktop frame;
	private WebSocketClient cc;
	private JPanel contentPane;
	private JScrollPane scrollPane;

	private JMenuBar menuBar;
	private JMenu mnArchivo,mnVisualizacion;
	private JMenuItem mntmCargarConfiguracion, snapshot, loadSnapshot;

	private JPanel sensorPanel;
	private JPanel dropdownPanel;
	private JPanel sliderPanel;
	private JPanel switchPanel;
	private Boolean activated;
	private JSONObject json=new JSONObject("{'switch':[],'slider':[],'dropdown':[],'sensor':[]}"); // we create the json object
	public String value;
	//JSON manage method
	//String jsonString = "{'pageInfo': {'pageName': 'abc','pagePic':'http://example.com/content.jpg'}}"; //assign your JSON String here
	// JSONObject obj = new JSONObject(jsonString);
	// String pageName = obj.getJSONObject("pageInfo").getString("pageName");
	// System.out.println(pageName);

	// if we use arrays we gonna usa json.append(key, value)

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
					Database.startDatabase();
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
		setBounds(100, 100, 450, 450);
		
		menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		mnArchivo = new JMenu("File");
		menuBar.add(mnArchivo);
		
		mntmCargarConfiguracion = new JMenuItem("Charge configuration");
		//Charging the icon
		// BufferedImage bi = ImageIO.read(new File(System.getProperty("user.dir") + "/src/images/load.png"));
		// Image icon = bi.getScaledInstance(16,16,Image.SCALE_SMOOTH);
		// mntmCargarConfiguracion.setIcon(new ImageIcon(icon));
		mntmCargarConfiguracion.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				openFile();
			}
		});
		mntmCargarConfiguracion.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK));
		mnArchivo.add(mntmCargarConfiguracion);

		snapshot = new JMenuItem("SNAPSHOT ");

		snapshot.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK));
		mnArchivo.add(snapshot);
		snapshot.setEnabled(false);

		loadSnapshot = new JMenuItem("LOAD SNAPSHOT");
		loadSnapshot.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, InputEvent.CTRL_DOWN_MASK));
		loadSnapshot.setEnabled(false);
		mnArchivo.add(loadSnapshot);

		mnVisualizacion = new JMenu("Visualization");
		menuBar.add(mnVisualizacion);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new GridLayout(2, 2));
		setContentPane(contentPane);
		switchPanel = new JPanel();
		switchPanel.setBorder(BorderFactory.createLineBorder(Color.black));
		contentPane.add(switchPanel);


		sliderPanel = new JPanel();
		sliderPanel.setBorder(BorderFactory.createLineBorder(Color.black));
		contentPane.add(sliderPanel);

		dropdownPanel = new JPanel();
		dropdownPanel.setBorder(BorderFactory.createLineBorder(Color.black));
		contentPane.add(dropdownPanel);

		sensorPanel = new JPanel();
		sensorPanel.setBorder(BorderFactory.createLineBorder(Color.black));
		contentPane.add(sensorPanel);
		
		
	}
	
	public static byte[] jsonToBytes (JSONObject obj) {
        byte[] result = null;
        try {
            // Transforma l'objecte a bytes[]
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(obj.toString());
            oos.flush();
            result = bos.toByteArray();
        } catch (IOException e) { e.printStackTrace(); }
        return result;
    }

	public String bytesToObject (ByteBuffer arr) {
        String result = "error";
        try {
            // Transforma el ByteButter en byte[]
            byte[] bytesArray = new byte[arr.remaining()];
            arr.get(bytesArray, 0, bytesArray.length);

            // Transforma l'array de bytes en objecte
            ByteArrayInputStream in = new ByteArrayInputStream(bytesArray);
            ObjectInputStream is = new ObjectInputStream(in);
            return (String) is.readObject();

        } catch (ClassNotFoundException e) { e.printStackTrace();
        } catch (UnsupportedEncodingException e) { e.printStackTrace();
        } catch (IOException e) { e.printStackTrace(); }
        return result;
    }

	private void openFile() { // Open ChoserFile for .xml
		HashMap<String,DataModule> dm = new HashMap<>();
		//Generating a filter for the file
		JFileChooser jf = new JFileChooser(System.getProperty("user.dir"));
		FileNameExtensionFilter filtro = new FileNameExtensionFilter("XML Files","xml");
		jf.setFileFilter(filtro);
		jf.showOpenDialog(this);
		archivo = jf.getSelectedFile();
		
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
								DataModule id = new DataModule(e.getTagName(),e.getAttribute("id"),e.getAttribute("default"),e.getAttribute("label"),values);
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
						String toJson="{";

						switch (dm.get(componentes.get(i)).getEtiqueta()) {
							case "switch":
								toJson+="'id':"+dm.get(componentes.get(i)).getId()+",";

								JLabel labelSwitch = new JLabel(dm.get(componentes.get(i)).getName());
								JToggleButton tglbtn = new JToggleButton("");
								//We will get the inital state specified on the xml
								if ((dm.get(componentes.get(i)).getDefaul().equals("on"))) {
									activated = true;
									tglbtn.setText("Active");
									toJson+="'default':true,";
								} else {
									activated = false;
									tglbtn.setText("Not active");
									toJson+="'default':false,";
								}
								toJson+="'name':"+dm.get(componentes.get(i)).getName()+",";
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
										// TODO HERE PUT METHOD TO SEND INFO TO SERVER
									}
								});;

								toJson+="},";
								json.append("switch", new JSONObject(toJson) );
								switchPanel.add(labelSwitch);
								switchPanel.add(tglbtn);
								contentPane.add(switchPanel);
								
								break;
							case "slider":
								JLabel labelSlider = new JLabel(dm.get(componentes.get(i)).getName());
								JSlider jSlider = new JSlider(dm.get(componentes.get(i)).getMin(),dm.get(componentes.get(i)).getMax(),Double.valueOf((dm.get(componentes.get(i)).getDefaul())).intValue());
								jSlider.setPaintTicks(true);
								jSlider.setMajorTickSpacing(1);
								jSlider.setMinorTickSpacing(1);
								jSlider.setPaintLabels(true);

								toJson+="'id':"+dm.get(componentes.get(i)).getId()+",";

								toJson+="'default':"+Double.valueOf((dm.get(componentes.get(i)).getDefaul()))+",";
								toJson+="'min':"+dm.get(componentes.get((i))).getMin()+",";
								toJson+="'max':"+dm.get(componentes.get((i))).getMax()+",";
								toJson+="'step':"+dm.get(componentes.get((i))).getStep()+",";

								//Firstly we will get it as a string, then we will have to convert it into a double because it has a decimal, finally we will convert it into int
								jSlider.setValue(Double.valueOf((dm.get(componentes.get(i)).getDefaul())).intValue());

								toJson+="},";
								json.append("slider", new JSONObject(toJson) );

								sliderPanel.add(labelSlider);
								sliderPanel.add(jSlider);
								contentPane.add(sliderPanel);

								break;
							case "dropdown":
								JLabel label = new JLabel(dm.get(componentes.get(i)).getLabel() + ":");
								JComboBox comboBox = new JComboBox();

								toJson+="'id':"+dm.get(componentes.get(i)).getId()+",";
								toJson+="'default':"+dm.get(componentes.get(i)).getDefaul()+",";
								toJson+="'values':[";
								// TODO ¿??¿?¿?¿?¿?¿? need key and value
								//Creating an arraylist with the keys of the hashmap
								ArrayList<String> dropdownKeys = new ArrayList<>();
								for (Object key : dm.get(componentes.get(i)).getValue().keySet()) {
								    String lKey = (String) key;
								    dropdownKeys.add(lKey);
								}
								
								for (Object key : dm.get(componentes.get(i)).getValue().keySet() ) {
								    String lKey = (String) key;
									String value = dm.get(componentes.get(i)).getValue().get(key);
								    toJson+="{"+lKey+":"+value+"},";
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
								toJson+="]},";
								json.append("dropdown", new JSONObject(toJson) );
								dropdownPanel.add(label);
								dropdownPanel.add(comboBox);
								contentPane.add(dropdownPanel);
								break;
							case "sensor":
								JTextArea textArea = new JTextArea();

								toJson+="'id':"+dm.get(componentes.get(i)).getId()+",";

								//The sensor will be a textArea with the specified units
								textArea.setText(dm.get(componentes.get(i)).getUnits());
								toJson+="'units':"+dm.get(componentes.get(i)).getUnits()+",";
								toJson+="'thresholdlow':"+dm.get(componentes.get(i)).getThresholdlow()+",";
								toJson+="'thresholdhigh':"+dm.get(componentes.get(i)).getThresholdhigh()+",";

								//Enabled at false to not let the user modify it
								textArea.setEnabled(false); // TODO WHAT IS THIS? WHY DISABLED?
								
								toJson+="},";
								json.append("sensor", new JSONObject(toJson) );

								sensorPanel.add(textArea);
								contentPane.add(sensorPanel);

								break;
						}
					}
					snapshot.setEnabled(true);
					loadSnapshot.setEnabled(true);

					snapshot.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							Connection conn = UtilsSQLite.connect(System.getProperty("user.dir") + "/src/database/database.db");
							String name;
							name = JOptionPane.showInputDialog("Name save snapshot: ");
							UtilsSQLite.sqlSnapshots(conn,"INSERT INTO snaptshots (json,date,name) VALUES ('"+ json + "',strftime('%Y-%m-%d %H:%M'),\""+name+"\");");
							UtilsSQLite.disconnect(conn);
						}
					});

					loadSnapshot.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							SnapshotTable snapshotTable = new SnapshotTable();
							snapshotTable.setVisible(true);


						}
					});

					System.out.println("--------------------------------------------------");
					System.out.println(json);
					doc.getDocumentElement().normalize();

					try {
						cc = new WebSocketClient(new URI("ws://127.0.0.1:8888"), (Draft) new Draft_6455()) {
							@Override
							public void onMessage(String message) {
								
							}

							@Override
							public void onMessage(ByteBuffer message) {
								String tempString= bytesToObject(message);
								JSONObject tempJson=new JSONObject(tempString);
								// TODO update value
								System.out.println("RECIVING NEW VALUE:");
								System.out.println(tempJson);
							}
		  
							@Override
							public void onOpen(ServerHandshake handshake) {
							  System.out.println("CONNECTED");
								cc.send(jsonToBytes(json));
								
							}
		  
							@Override
							public void onClose(int code, String reason, boolean remote) {
								
							}
		  
							@Override
							public void onError(Exception ex) {
								
							}
						};
		  
						
						cc.connect();
					  } catch (URISyntaxException ex) {
						System.out.println("Error Connection:"+ex);
					  }


				} catch (ParserConfigurationException e) {
					throw new RuntimeException(e);
				} catch (IOException e) {
					throw new RuntimeException(e);
				} catch (SAXException e) {
					throw new RuntimeException(e);
				}
			}else {
				JOptionPane.showMessageDialog(null," It is not an xml","Error",JOptionPane.ERROR_MESSAGE);
			}

		}
	}
}

