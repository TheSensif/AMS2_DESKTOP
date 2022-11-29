import database.Database;
import database.UtilsSQLite;
import models.ModeloTransferencia;
import models.SnapshotValue;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import modules.DataModule;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.GroupLayout.Alignment;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import java.awt.event.InputEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
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
import java.util.Map;
import java.util.Objects;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.handshake.ServerHandshake;
import org.json.*;
import windows.SnapshotTable;

public class InitDesktop extends JFrame {
	private ModeloTransferencia modelo = SnapshotValue.modeloTransferencia;
	private static InitDesktop frame;
	private WebSocketClient cc;
	private JPanel contentPane;
	private JScrollPane scrollPane;
	private boolean restarts=false;

	private JMenuBar menuBar;
	private JMenu mnArchivo,mnVisualizacion,mnSnapshot;
	private JMenuItem mntmCargarConfiguracion, snapshotsave, loadSnapshot, applySnapshot;

	private JPanel sensorPanel;
	private JPanel dropdownPanel;
	private JPanel sliderPanel;
	private JPanel switchPanel;
	private JPanel blockPanel;
	private Boolean activated;
	//private JSONObject json=new JSONObject("{'switch':[],'slider':[],'dropdown':[],'sensor':[]}"); // we create the json object
	private JSONObject json; // we create the json object
	private JSONObject jsonObj = new JSONObject();
	private String blockName;

	private HashMap <String,JSlider> hashSliders=new HashMap<>();
	private HashMap <String,JComboBox> hashDropdown=new HashMap<>();
	private HashMap <String,JToggleButton> hashSwitch=new HashMap<>();
	//private HashMap <String,JTextArea> hashSensor;

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

		mnVisualizacion = new JMenu("Visualization");
		menuBar.add(mnVisualizacion);

		mnSnapshot = new JMenu("SnapShot");
		menuBar.add(mnSnapshot);

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

		snapshotsave = new JMenuItem("SnapShot");

		snapshotsave.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK));
		mnSnapshot.add(snapshotsave);
		snapshotsave.setEnabled(false);

		loadSnapshot = new JMenuItem("Load SnapShot");
		loadSnapshot.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, InputEvent.CTRL_DOWN_MASK));
		loadSnapshot.setEnabled(false);

		mnSnapshot.add(loadSnapshot);

		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new GridLayout(2, 2));
		setContentPane(contentPane);

		
		
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
		HashMap<String,HashMap> generalData = new HashMap<>();
		//HashMap<String, DataModule> dm = new HashMap<>();
		//Generating a filter for the file
		JFileChooser jf = new JFileChooser(System.getProperty("user.dir"));
		FileNameExtensionFilter filtro = new FileNameExtensionFilter("XML Files", "xml");
		jf.setFileFilter(filtro);
		jf.showOpenDialog(this);
		File archivo = jf.getSelectedFile();

		if (archivo != null) {
			String name = archivo.getName();
			String[] part = name.split("\\.");
			//If the file is filtered we will open it
			if (Objects.equals(part[1], "xml")) {

				try {
					DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
					DocumentBuilder db = null;
					db = dbf.newDocumentBuilder();
					Document doc = db.parse(archivo);

					// TODO make for what contains all bellow

					// ERROR CONTROL FOR BLOCKS;
					//	ArrayList<String> BlocksPKS=new ArrayList<String>();
					// For in blocks{
					//Element e = (Element) node;
					// NamedNodeMap attrb = e.getAttributes();
					// if(attrb.getNamedItem("name")==null || attrb.getNamedItem("default")==null){

					// 	JOptionPane.showMessageDialog(null," It is not an valid xml","Error",JOptionPane.ERROR_MESSAGE);
					// 	return;
					// };
					//if(BlocksPKS.contains(e.getAttribute("id"))){
					// 	JOptionPane.showMessageDialog(null," It is not an valid xml IDs repeated","Error",JOptionPane.ERROR_MESSAGE);
					// 	return;
					// }
					// BlocksPKS.add(e.getAttribute("id"));
					//}

					ArrayList<String> PKS = new ArrayList<String>();
					NodeList control = doc.getElementsByTagName("controls");
					if (control.getLength() != 0) {
						for (int i = 0; i < control.getLength(); i++) {
							HashMap<String, DataModule> dm = new HashMap<>();
							int quantityElements = 0;
							ArrayList<String> elementIds = new ArrayList<>();
							Node nodeControl = control.item(i);
							if (nodeControl.getNodeType() == Node.ELEMENT_NODE) {
								Element e = (Element) nodeControl;
								//We will store the data of the diferents components
								NodeList switc = e.getElementsByTagName("switch");
								NodeList slider = e.getElementsByTagName("slider");
								NodeList dropdown = e.getElementsByTagName("dropdown");
								NodeList sensor = e.getElementsByTagName("sensor");

								generalData.put(e.getAttribute("name"), dm);
								//json = new JSONObject("{'switch':[],'slider':[],'dropdown':[],'sensor':[]}");
								json = new JSONObject();
								//blockJson = new JSONObject("{'"+e.getAttribute("name")+"':"+json+"}");

								blockName = e.getAttribute("name");
								//private JSONObject json=new JSONObject("{'switch':[],'slider':[],'dropdown':[],'sensor':[]}"); // we create the json object

								blockPanel = new JPanel();


								blockPanel.setBorder(BorderFactory.createLineBorder(Color.magenta, 3));

								contentPane.add(blockPanel);


								for (int j = 0; j < switc.getLength(); j++) {
									Node node = switc.item(j);
									// this.contentPane.removeAll();
									if (node.getNodeType() == Node.ELEMENT_NODE) {
										Element el = (Element) node;
										NamedNodeMap attrb = el.getAttributes();
										if (attrb.getNamedItem("id") == null || attrb.getNamedItem("default") == null) {

											JOptionPane.showMessageDialog(null, " It is not an valid xml", "Error", JOptionPane.ERROR_MESSAGE);
											return;
										}
										;
										if (attrb.getNamedItem("default").getNodeValue().equals("on") == false && attrb.getNamedItem("default").getNodeValue().equals("off") == false) {
											JOptionPane.showMessageDialog(null, " It is not an valid xml", "Error", JOptionPane.ERROR_MESSAGE);
											return;
										}

										if (PKS.contains(e.getAttribute("id"))) {
											JOptionPane.showMessageDialog(null, " It is not an valid xml IDs repeated", "Error", JOptionPane.ERROR_MESSAGE);
											return;
										}
										PKS.add(e.getAttribute("id"));

										DataModule id = new DataModule(el.getTagName(), el.getAttribute("id"), el.getAttribute("default"), el.getTextContent());

										dm.put(el.getAttribute("id"), id);
										elementIds.add(el.getAttribute("id"));
									}
								}

								for (int j = 0; j < slider.getLength(); j++) {
									Node node = slider.item(j);

									if (node.getNodeType() == Node.ELEMENT_NODE) {
										Element el = (Element) node;
										NamedNodeMap attrb = el.getAttributes();
										if (attrb.getNamedItem("id") == null || attrb.getNamedItem("default") == null || attrb.getNamedItem("min") == null || attrb.getNamedItem("max") == null || attrb.getNamedItem("step") == null) {

											JOptionPane.showMessageDialog(null, " It is not an valid xml", "Error", JOptionPane.ERROR_MESSAGE);
											return;
										}
										;
										if (Integer.valueOf(attrb.getNamedItem("min").getNodeValue()) > Integer.valueOf(attrb.getNamedItem("max").getNodeValue())) {
											JOptionPane.showMessageDialog(null, " It is not an valid xml", "Error", JOptionPane.ERROR_MESSAGE);
											return;
										}
										if (Double.valueOf(attrb.getNamedItem("default").getNodeValue()) > Double.valueOf(attrb.getNamedItem("max").getNodeValue()) || Double.valueOf(attrb.getNamedItem("default").getNodeValue()) < Double.valueOf(attrb.getNamedItem("min").getNodeValue())) {
											JOptionPane.showMessageDialog(null, " It is not an valid xml", "Error", JOptionPane.ERROR_MESSAGE);
											return;
										}
										if (PKS.contains(el.getAttribute("id"))) {
											JOptionPane.showMessageDialog(null, " It is not an valid xml IDs repeated", "Error", JOptionPane.ERROR_MESSAGE);
											return;
										}
										PKS.add(el.getAttribute("id"));
										DataModule id = new DataModule(el.getTagName(), el.getAttribute("id"), el.getAttribute("default"), Integer.parseInt(el.getAttribute("min")), Integer.parseInt(el.getAttribute("max")), el.getAttribute("step"), el.getTextContent());
										dm.put(el.getAttribute("id"), id);
										elementIds.add(el.getAttribute("id"));
									}
								}

								for (int j = 0; j < dropdown.getLength(); j++) {
									Node node = dropdown.item(j);

									if (node.getNodeType() == Node.ELEMENT_NODE) {
										Element el = (Element) node;

										NamedNodeMap attrb = el.getAttributes();
										if (attrb.getNamedItem("id") == null || attrb.getNamedItem("default") == null) {

											JOptionPane.showMessageDialog(null, " It is not an valid xml", "Error", JOptionPane.ERROR_MESSAGE);
											return;
										}
										;

										HashMap<String, String> values = new HashMap();
										NodeList option = doc.getElementsByTagName("option");

										ArrayList<String> DropPKS = new ArrayList<String>();

										if (el.getElementsByTagName("option").getLength() == 0) {

											JOptionPane.showMessageDialog(null, " It is not an valid xml; DropDown options EMPTY!", "Error", JOptionPane.ERROR_MESSAGE);
											return;
										}
										;

										for (int k = 0; k < el.getElementsByTagName("option").getLength(); k++) {
											Node nodeOption = option.item(k);
											if (nodeOption.getNodeType() == Node.ELEMENT_NODE) {
												Element eo = (Element) nodeOption;


												if (eo.getAttribute("value") == null || DropPKS.contains(eo.getAttribute("value"))) {

													JOptionPane.showMessageDialog(null, " It is not an valid xml; DropDown values Error", "Error", JOptionPane.ERROR_MESSAGE);
													return;
												}
												;
												DropPKS.add(eo.getAttribute("value"));
												values.put(eo.getAttribute("value"), eo.getTextContent());
											}
										}
										if (PKS.contains(el.getAttribute("id"))) {
											JOptionPane.showMessageDialog(null, " It is not an valid xml IDs repeated", "Error", JOptionPane.ERROR_MESSAGE);
											return;
										}
										PKS.add(el.getAttribute("id"));
										DataModule id = new DataModule(el.getTagName(), el.getAttribute("id"), el.getAttribute("default"), el.getAttribute("label"), values);
										dm.put(el.getAttribute("id"), id);
										elementIds.add(el.getAttribute("id"));
									}
								}

								for (int j = 0; j < sensor.getLength(); j++) {
									Node node = sensor.item(j);

									if (node.getNodeType() == Node.ELEMENT_NODE) {
										Element el = (Element) node;
										NamedNodeMap attrb = el.getAttributes();
										if (attrb.getNamedItem("id") == null || attrb.getNamedItem("units") == null || attrb.getNamedItem("thresholdhigh") == null || attrb.getNamedItem("thresholdlow") == null) {

											JOptionPane.showMessageDialog(null, " It is not an valid xml", "Error", JOptionPane.ERROR_MESSAGE);
											return;
										}
										;
										if (Double.valueOf(attrb.getNamedItem("thresholdhigh").getNodeValue()) < Double.valueOf(attrb.getNamedItem("thresholdlow").getNodeValue())) {
											JOptionPane.showMessageDialog(null, " It is not an valid xml threshold error", "Error", JOptionPane.ERROR_MESSAGE);
											return;
										}
										if (PKS.contains(el.getAttribute("id"))) {
											JOptionPane.showMessageDialog(null, " It is not an valid xml IDs repeated", "Error", JOptionPane.ERROR_MESSAGE);
											return;
										}

										PKS.add(el.getAttribute("id"));
										DataModule id = new DataModule(el.getTagName(), el.getAttribute("id"), el.getAttribute("units"), Integer.parseInt(el.getAttribute("thresholdlow")), Integer.parseInt(el.getAttribute("thresholdhigh")), el.getTextContent());
										dm.put(el.getAttribute("id"), id);
										elementIds.add(el.getAttribute("id"));
									}
								}
							}

							switchPanel = new JPanel();

							sliderPanel = new JPanel();

							dropdownPanel = new JPanel();

							sensorPanel = new JPanel();

							ArrayList<String> componentes = new ArrayList<>();

							//We will create an array with the length of the quantity of the components
							//The array will be the quanity but with formated to string, to match the information that the XML provides
							for (int j = 0; j < dm.size(); j++) {
								componentes.add(String.valueOf(j));
							}


							//Looking for the existens of the elements in the block
							Boolean switchExists = false;
							Boolean sliderExists = false;
							Boolean dropdownExists = false;
							Boolean sensorExists = false;


							for (int j = 0; j < dm.size(); j++) {
								//We will look to get the component id with its tagName
								//Depending on the tagname it will create a diferent component
								String toJson = "{";
								System.out.println(dm.get(componentes.get(j)));
								switch (dm.get(componentes.get(j)).getEtiqueta()) {
									case "switch":
										toJson += "'id':" + dm.get(componentes.get(j)).getId() + ",";
										String ide = dm.get(componentes.get(j)).getId();

										JLabel labelSwitch = new JLabel(dm.get(componentes.get(j)).getName());

										JToggleButton tglbtn = new JToggleButton("");
										//We will get the inital state specified on the xml
										if ((dm.get(componentes.get(j)).getDefaul().equals("on"))) {
											activated = true;
											tglbtn.setText("Active");
											toJson += "'default':true,";
										} else {
											activated = false;
											tglbtn.setText("Not active");
											toJson += "'default':false,";
										}
										toJson += "'name':" + dm.get(componentes.get(j)).getName() + ",";
										tglbtn.addActionListener(new ActionListener() {

											//Changing its state every time its pressed
											public void actionPerformed(ActionEvent e) {
												if (activated == true) {
													tglbtn.setText("Not active");
													tglbtn.disable();
													activated = false;
												} else {
													tglbtn.setText("Active");
													tglbtn.enable();
													activated = true;
												}
												// TODO HERE PUT METHOD TO SEND INFO TO SERVER TODO!!!! DO THE SAME WITH OTHER COMPONENTS
												JSONObject jsonTemp = new JSONObject("{" + "component:switch,id:" + ide + "," + "value:" + activated + "}");
												cc.send(jsonToBytes(jsonTemp));
											}
										});
										;


										toJson += "},";
										json.append("switch", new JSONObject(toJson));

										if (switchExists == false) {
											scrollPane = new JScrollPane();
											quantityElements++;
											switchExists = true;
											//JSONObject jsonSwitch = new JSONObject("{'switch':[]");
											//todo CUANDO SEPAMOS QUE SE AÑADE UN COMPONENTE TAMBIEN LO MODIFICAMOS AL JSON BASE
										}


										switchPanel.setBorder(BorderFactory.createLineBorder(Color.black));
										switchPanel.add(labelSwitch);
										switchPanel.add(tglbtn);

										hashSwitch.put(ide, tglbtn);

										scrollPane.setViewportView(switchPanel);
										blockPanel.add(scrollPane);

										break;
									case "slider":
										JLabel labelSlider = new JLabel(dm.get(componentes.get(j)).getName());
										JSlider jSlider = new JSlider(dm.get(componentes.get(j)).getMin(), dm.get(componentes.get(j)).getMax(), Double.valueOf((dm.get(componentes.get(j)).getDefaul())).intValue());
										String ide2 = dm.get(componentes.get(j)).getId();
										jSlider.setPaintTicks(true);
										jSlider.setMajorTickSpacing(1);
										jSlider.setMinorTickSpacing(1);
										jSlider.setPaintLabels(true);

										toJson += "'id':" + dm.get(componentes.get(j)).getId() + ",";

										toJson += "'default':" + Double.valueOf((dm.get(componentes.get(j)).getDefaul())) + ",";
										toJson += "'min':" + dm.get(componentes.get((j))).getMin() + ",";
										toJson += "'max':" + dm.get(componentes.get((j))).getMax() + ",";
										toJson += "'step':" + dm.get(componentes.get((j))).getStep() + ",";

										//Firstly we will get it as a string, then we will have to convert it into a double because it has a decimal, finally we will convert it into int
										jSlider.setValue(Double.valueOf((dm.get(componentes.get(j)).getDefaul())).intValue());

										jSlider.addChangeListener(new ChangeListener() {

											@Override
											public void stateChanged(ChangeEvent e) {
												// TODO Auto-generated method stub
												int valueTemp = jSlider.getValue();
												JSONObject jsonTemp = new JSONObject("{" + "component:slider,id:" + ide2 + "," + "value:" + valueTemp + "}");
												cc.send(jsonToBytes(jsonTemp));
											}

										});

										toJson += "},";
										json.append("slider", new JSONObject(toJson));

										if (sliderExists == false) {
											scrollPane = new JScrollPane();
											quantityElements++;
											sliderExists = true;
											//JSONObject jsonSlider = new JSONObject("{'slider':[]");
										}

										hashSliders.put(ide2, jSlider);
										sliderPanel.setBorder(BorderFactory.createLineBorder(Color.black));
										sliderPanel.add(labelSlider);
										sliderPanel.add(jSlider);
										scrollPane.setViewportView(sliderPanel);
										blockPanel.add(scrollPane);

										break;
									case "dropdown":
										JLabel label = new JLabel(dm.get(componentes.get(j)).getLabel() + ":");
										JComboBox comboBox = new JComboBox();

										toJson += "'id':" + dm.get(componentes.get(j)).getId() + ",";
										toJson += "'default':" + dm.get(componentes.get(j)).getDefaul() + ",";
										String ide3 = dm.get(componentes.get(j)).getId();
										toJson += "'values':[";
										// TODO ¿??¿?¿?¿?¿?¿? need key and value
										//Creating an arraylist with the keys of the hashmap
										ArrayList<String> dropdownKeys = new ArrayList<>();
										for (Object key : dm.get(componentes.get(j)).getValue().keySet()) {
											String lKey = (String) key;
											dropdownKeys.add(lKey);
										}

										for (Object key : dm.get(componentes.get(j)).getValue().keySet()) {
											String lKey = (String) key;
											String value = dm.get(componentes.get(j)).getValue().get(key);
											toJson += "{" + lKey + ":" + value + "},";
										}
										//We will iterate a for over with the Id quantity
										for (int k = 0; k < dropdownKeys.size(); k++) {

											//Checking if the iteration is the same of the deafult value
											if (String.valueOf(dropdownKeys.get(k)).equals(String.valueOf(dm.get(componentes.get(j)).getDefaul()))) {
												comboBox.addItem((dm.get(componentes.get(j)).getValue().get(dropdownKeys.get(k))));
												//If it is, we will set it as selected Item
												comboBox.setSelectedItem((dm.get(componentes.get(j)).getValue().get(dropdownKeys.get(k))));
											} else {
												//If not instead we just add it at the Dropdown
												comboBox.addItem((dm.get(componentes.get(j)).getValue().get(dropdownKeys.get(k))));
											}

										}

										toJson += "]},";
										json.append("dropdown", new JSONObject(toJson));
										dropdownPanel.add(label);

										if (dropdownExists == false) {
											scrollPane = new JScrollPane();
											quantityElements++;
											dropdownExists = true;
											//JSONObject jsonDropdown = new JSONObject("{'dropdown':[]");
										}

										comboBox.addItemListener(new ItemListener() {

											@Override
											public void itemStateChanged(ItemEvent e) {
												System.out.println("CHANGE");
												// TODO Auto-generated method stub
												int valueTemp = comboBox.getSelectedIndex();
												JSONObject jsonTemp = new JSONObject("{" + "component:dropdown,id:" + ide3 + "," + "value:" + valueTemp + "}");
												cc.send(jsonToBytes(jsonTemp));
											}
										});
										hashDropdown.put(ide3, comboBox);
										dropdownPanel.setBorder(BorderFactory.createLineBorder(Color.black));
										dropdownPanel.add(label);
										dropdownPanel.add(comboBox);
										scrollPane.setViewportView(dropdownPanel);
										blockPanel.add(scrollPane);
										break;
									case "sensor":
										JTextArea textArea = new JTextArea();

										toJson += "'id':" + dm.get(componentes.get(j)).getId() + ",";

										//The sensor will be a textArea with the specified units
										textArea.setText(dm.get(componentes.get(j)).getUnits());
										toJson += "'units':" + dm.get(componentes.get(j)).getUnits() + ",";
										toJson += "'thresholdlow':" + dm.get(componentes.get(j)).getThresholdlow() + ",";
										toJson += "'thresholdhigh':" + dm.get(componentes.get(j)).getThresholdhigh() + ",";

										//Enabled at false to not let the user modify it
										textArea.setEnabled(false); // TODO WHAT IS THIS? WHY DISABLED?

										toJson += "},";
										json.append("sensor", new JSONObject(toJson));

										if (sensorExists == false) {
											scrollPane = new JScrollPane();
											quantityElements++;
											sensorExists = true;
											//JSONObject jsonSensor = new JSONObject("{'sensor':[]");
										}
										// textArea.ListenerInp
										//hashSensor.put(ide4, textArea)
										sensorPanel.setBorder(BorderFactory.createLineBorder(Color.black));
										sensorPanel.add(textArea);
										scrollPane.setViewportView(sensorPanel);
										blockPanel.add(scrollPane);

										break;
								}
								if ((Math.round(quantityElements / 2) == 0)) {
									blockPanel.setLayout(new GridLayout(1, 1));
								} else {
									blockPanel.setLayout(new GridLayout((Math.round(quantityElements / 2)), (Math.round(quantityElements / 2))));
								}
							}
							jsonObj.append(blockName, json);
						}
					}
					System.out.println(generalData);

					//DELETE ALL CONTENTPANEL HERE
					sliderPanel.removeAll();
					dropdownPanel.removeAll();
					switchPanel.removeAll();
					sensorPanel.removeAll();

					//restart server
					if (restarts == true) {
						cc.send("restart");
					}


					//Update the panel view
					contentPane.revalidate();
					contentPane.repaint();

					snapshotsave.setEnabled(true);
					loadSnapshot.setEnabled(true);

					snapshotsave.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							Connection conn = UtilsSQLite.connect(System.getProperty("user.dir") + "/src/database/database.db");
							String name;
							name = JOptionPane.showInputDialog("Name save snapshot: ");
							UtilsSQLite.sqlSnapshots(conn,"INSERT INTO snaptshots (json,date,name) VALUES ('"+ jsonObj + "',strftime('%Y-%m-%d %H:%M'),\""+name+"\");");
							UtilsSQLite.disconnect(conn);
						}
					});

					loadSnapshot.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							loadSnapshot();
						}
					});

					System.out.println("--------------------------------------------------");
					System.out.println(jsonObj);
					doc.getDocumentElement().normalize();

					try {
						if (restarts == false) {
							cc = new WebSocketClient(new URI("ws://127.0.0.1:8888"), (Draft) new Draft_6455()) {
								@Override
								public void onMessage(String message) {

								}

								@Override
								public void onMessage(ByteBuffer message) {
									String tempString = bytesToObject(message);
									JSONObject tempJson = new JSONObject(tempString);
									// TODO update value
									System.out.println("RECIVING NEW VALUE:");
									System.out.println(tempJson);
									if (tempJson.getString("component").equalsIgnoreCase("switch")) {
										JToggleButton t = hashSwitch.get(String.valueOf(tempJson.getInt("id")));
										if (tempJson.getBoolean("value")) {
											t.setText("Active");
											activated = true;
										} else {
											t.setText("Not active");
											activated = false;
										}

									} else if (tempJson.getString("component").equalsIgnoreCase("slider")) {
										JSlider s = hashSliders.get(String.valueOf(tempJson.getInt("id")));
										s.setValue(tempJson.getInt("value"));
									} else if (tempJson.getString("component").equalsIgnoreCase("dropdown")) {
										JComboBox c = hashDropdown.get(String.valueOf(tempJson.getInt("id")));
										c.setSelectedIndex(tempJson.getInt("value"));
									}

								}

								@Override
								public void onOpen(ServerHandshake handshake) {
									System.out.println("CONNECTED");
									cc.send(jsonToBytes(jsonObj));

								}

								@Override
								public void onClose(int code, String reason, boolean remote) {

								}

								@Override
								public void onError(Exception ex) {

								}
							};


							cc.connect();
						} else {
							cc.send(jsonToBytes(jsonObj));
						}

						restarts = true;
					} catch (URISyntaxException ex) {
						System.out.println("Error Connection:" + ex);
					}


				} catch (ParserConfigurationException e) {
					throw new RuntimeException(e);
				} catch (IOException e) {
					throw new RuntimeException(e);
				} catch (SAXException e) {
					throw new RuntimeException(e);
				}
			} else {
				JOptionPane.showMessageDialog(null, " It is not an xml", "Error", JOptionPane.ERROR_MESSAGE);
			}

		}

	}
		private void saveSnapshot() {

		}

		private void loadSnapshot() {
			SnapshotTable snapshotTable = new SnapshotTable();
			snapshotTable.setVisible(true);
		}
	}



