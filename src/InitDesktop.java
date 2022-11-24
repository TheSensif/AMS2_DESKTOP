import database.Database;
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
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
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
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.handshake.ServerHandshake;
import org.json.*;

public class InitDesktop extends JFrame {
	private static InitDesktop frame;
	private WebSocketClient cc;
	private JPanel contentPane;

	private JScrollPane scrollPane;
	private JPanel sensorPanel;
	private JPanel dropdownPanel;
	private JPanel sliderPanel;
	private JPanel switchPanel;
	private JPanel blockPanel;
	private Boolean activated;
	private JSONObject json=new JSONObject("{'switch':[],'slider':[],'dropdown':[],'sensor':[]}"); // we create the json object

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
		
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		JMenu mnArchivo = new JMenu("File");
		menuBar.add(mnArchivo);
		
		JMenuItem mntmCargarConfiguracion = new JMenuItem("Charge configuration");
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
		
		
		JMenu mnVisualizacion = new JMenu("Visualization");
		menuBar.add(mnVisualizacion);
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


					NodeList control = doc.getElementsByTagName("controls");
					if (control.getLength() != 0) {
						for (int i = 0; i < control.getLength(); i++) {
							HashMap<String,DataModule> dm = new HashMap<>();
							int quantityElements = 0;
							Node nodeControl = control.item(i);
							if (nodeControl.getNodeType() == Node.ELEMENT_NODE) {
								Element e = (Element) nodeControl;
								//We will store the data of the diferents components
								NodeList switc = e.getElementsByTagName("switch");
								NodeList slider = e.getElementsByTagName("slider");
								NodeList dropdown = e.getElementsByTagName("dropdown");
								NodeList sensor = e.getElementsByTagName("sensor");

								generalData.put(e.getAttribute("name"),dm);

								blockPanel = new JPanel();
								blockPanel.setBorder(BorderFactory.createLineBorder(Color.magenta));

								contentPane.add(blockPanel);

								switchPanel = new JPanel();

								sliderPanel = new JPanel();

								dropdownPanel = new JPanel();

								sensorPanel = new JPanel();



								//SWITCH
								for (int j= 0; j < switc.getLength(); j++) {
									Node node = switc.item(j);

									if (node.getNodeType() == Node.ELEMENT_NODE) {
										Element el = (Element) node;
										DataModule id = new DataModule(el.getTagName(),el.getAttribute("id"),el.getAttribute("default"),el.getTextContent());

										dm.put(el.getAttribute("id"),id);
									}
								}

								//SLIDER
								for (int j= 0; j < slider.getLength(); j++) {
									Node node = slider.item(j);

									if (node.getNodeType() == Node.ELEMENT_NODE) {
										Element el = (Element) node;
										DataModule id = new DataModule(el.getTagName(),el.getAttribute("id"),el.getAttribute("default"),Integer.parseInt(el.getAttribute("min")),Integer.parseInt(el.getAttribute("max")),el.getAttribute("step"),el.getTextContent());

										dm.put(el.getAttribute("id"),id);
									}
								}

								//DROPDOWN
								for (int j= 0; j < dropdown.getLength(); j++) {
									Node node = dropdown.item(j);

									if (node.getNodeType() == Node.ELEMENT_NODE) {
										Element el = (Element) node;
										HashMap<String, String> values = new HashMap();
										//REVISAR ESTE NODELIST
										NodeList option = e.getElementsByTagName("option");
										for (int k = 0; k < e.getElementsByTagName("option").getLength(); k++) {
											Node nodeOption = option.item(k);
											if (nodeOption.getNodeType() == Node.ELEMENT_NODE) {
												Element eo = (Element) nodeOption;
												values.put(eo.getAttribute("value"), eo.getTextContent());
											}
										}
										System.out.println(values);
										DataModule id = new DataModule(el.getTagName(), el.getAttribute("id"), el.getAttribute("default"), el.getAttribute("label"), values);
										dm.put(el.getAttribute("id"),id);

									}
									}
								//SENSOR
								for (int j= 0; j < sensor.getLength(); j++) {
									Node node = sensor.item(j);

									if (node.getNodeType() == Node.ELEMENT_NODE) {
										Element el = (Element) node;
										DataModule id = new DataModule(el.getTagName(),el.getAttribute("id"),el.getAttribute("units"),Integer.parseInt(el.getAttribute("thresholdlow")),Integer.parseInt(el.getAttribute("thresholdhigh")),el.getTextContent());

										dm.put(el.getAttribute("id"),id);
									}
								}
							}

							ArrayList<String> componentes = new ArrayList<>();


							//We will create an array with the length of the quantity of the components
							//The array will be the quanity but with formated to string, to match the information that the XML provides
							for (int j = 0; j < dm.size(); j++) {
								componentes.add(String.valueOf(j));
							}

							//Update the panel view
							contentPane.revalidate();
							contentPane.repaint();
							System.out.println(dm);

							//Looking for the existens of the elements in the block
							Boolean switchExists = false;
							Boolean sliderExists = false;
							Boolean dropdownExists = false;
							Boolean sensorExists = false;

							for (int j = 0; j < dm.size(); j++) {
								//We will look to get the component id with its tagName
								//Depending on the tagname it will create a diferent component
								String toJson="{";

								switch (dm.get(componentes.get(j)).getEtiqueta()) {
									case "switch":
										toJson+="'id':"+dm.get(componentes.get(j)).getId()+",";

										JLabel labelSwitch = new JLabel(dm.get(componentes.get(j)).getName());
										JToggleButton tglbtn = new JToggleButton("");
										//We will get the inital state specified on the xml
										if ((dm.get(componentes.get(j)).getDefaul().equals("on"))) {
											activated = true;
											tglbtn.setText("Active");
											toJson+="'default':true,";
										} else {
											activated = false;
											tglbtn.setText("Not active");
											toJson+="'default':false,";
										}
										toJson+="'name':"+dm.get(componentes.get(j)).getName()+",";
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

										if (switchExists == false) {
											scrollPane = new JScrollPane();
											quantityElements++;
											switchExists = true;
										}



										switchPanel.setBorder(BorderFactory.createLineBorder(Color.black));
										switchPanel.add(labelSwitch);
										switchPanel.add(tglbtn);

										//We will create a new Scroll Panel and add the latest info to it
										scrollPane.setViewportView(switchPanel);
										blockPanel.add(scrollPane);

										break;
									case "slider":
										JLabel labelSlider = new JLabel(dm.get(componentes.get(j)).getName());
										JSlider jSlider = new JSlider(dm.get(componentes.get(j)).getMin(),dm.get(componentes.get(j)).getMax(),Double.valueOf((dm.get(componentes.get(j)).getDefaul())).intValue());
										jSlider.setPaintTicks(true);
										jSlider.setMajorTickSpacing(1);
										jSlider.setMinorTickSpacing(1);
										jSlider.setPaintLabels(true);

										toJson+="'id':"+dm.get(componentes.get(j)).getId()+",";

										toJson+="'default':"+Double.valueOf((dm.get(componentes.get(j)).getDefaul()))+",";
										toJson+="'min':"+dm.get(componentes.get((j))).getMin()+",";
										toJson+="'max':"+dm.get(componentes.get((j))).getMax()+",";
										toJson+="'step':"+dm.get(componentes.get((j))).getStep()+",";

										//Firstly we will get it as a string, then we will have to convert it into a double because it has a decimal, finally we will convert it into int
										jSlider.setValue(Double.valueOf((dm.get(componentes.get(j)).getDefaul())).intValue());

										toJson+="},";
										json.append("slider", new JSONObject(toJson) );

										if (sliderExists == false) {
											scrollPane = new JScrollPane();
											quantityElements++;
											sliderExists = true;
										}
										sliderPanel.setBorder(BorderFactory.createLineBorder(Color.black));
										sliderPanel.add(labelSlider);
										sliderPanel.add(jSlider);
										//We will create a new Scroll Panel and add the latest info to it
										//todo ARREGLAR ESTO
										scrollPane.setViewportView(sliderPanel);
										blockPanel.add(scrollPane);
										break;
									case "dropdown":
										JLabel label = new JLabel(dm.get(componentes.get(j)).getLabel() + ":");
										JComboBox comboBox = new JComboBox();

										toJson+="'id':"+dm.get(componentes.get(j)).getId()+",";
										toJson+="'default':"+dm.get(componentes.get(j)).getDefaul()+",";
										toJson+="'values':[";
										// TODO ¿??¿?¿?¿?¿?¿? need key and value
										//Creating an arraylist with the keys of the hashmap
										ArrayList<String> dropdownKeys = new ArrayList<>();
										for (Object key : dm.get(componentes.get(j)).getValue().keySet()) {
											String lKey = (String) key;
											dropdownKeys.add(lKey);
										}

										for (Object key : dm.get(componentes.get(j)).getValue().keySet() ) {
											String lKey = (String) key;
											String value = dm.get(componentes.get(j)).getValue().get(key);
											toJson+="{"+lKey+":"+value+"},";
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
										toJson+="]},";
										json.append("dropdown", new JSONObject(toJson) );

										if (dropdownExists == false) {
											scrollPane = new JScrollPane();
											quantityElements++;
											dropdownExists = true;
										}
										dropdownPanel.setBorder(BorderFactory.createLineBorder(Color.black));
										dropdownPanel.add(label);
										dropdownPanel.add(comboBox);
										//We will create a new Scroll Panel and add the latest info to it
										scrollPane.setViewportView(dropdownPanel);
										blockPanel.add(scrollPane);

										break;
									case "sensor":
										JTextArea textArea = new JTextArea();

										toJson+="'id':"+dm.get(componentes.get(j)).getId()+",";

										//The sensor will be a textArea with the specified units
										textArea.setText(dm.get(componentes.get(j)).getUnits());
										toJson+="'units':"+dm.get(componentes.get(j)).getUnits()+",";
										toJson+="'thresholdlow':"+dm.get(componentes.get(j)).getThresholdlow()+",";
										toJson+="'thresholdhigh':"+dm.get(componentes.get(j)).getThresholdhigh()+",";

										//Enabled at false to not let the user modify it
										textArea.setEnabled(false); // TODO WHAT IS THIS? WHY DISABLED?

										toJson+="},";
										json.append("sensor", new JSONObject(toJson) );

										if (sensorExists == false) {
											scrollPane = new JScrollPane();
											quantityElements++;
											sensorExists = true;
										}
										sensorPanel.setBorder(BorderFactory.createLineBorder(Color.black));
										sensorPanel.add(textArea);
										//We will create a new Scroll Panel and add the latest info to it
										scrollPane.setViewportView(sensorPanel);
										blockPanel.add(scrollPane);
										break;
								}
								System.out.println("Quantity"+quantityElements);
								if ((Math.round(quantityElements/2) == 0)) {
									blockPanel.setLayout(new GridLayout(1, 1));
								} else {
									blockPanel.setLayout(new GridLayout((Math.round(quantityElements/2)), (Math.round(quantityElements/2))));
								}
							}
						}
					}
					System.out.println(generalData);
					

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

