import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.*;
import java.net.*;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class Model {

	//Config file
    private Properties config;
    private int incorrectLeads = 0;
    public Model() {
    	config = new Properties();
    }
    
    public void loadConfig(String fileName) {
        try (InputStream input = new FileInputStream(fileName)) {
            config.load(input);
        } catch (IOException ex) {
            System.err.println("Error loading config file: " + ex.getMessage());
        }
    }

    public void saveConfig(String fileName, String key, String value) {
        try (OutputStream output = new FileOutputStream(fileName)) {
            config.setProperty(key, value);
            config.store(output, null);
        } catch (IOException ex) {
            System.err.println("Error saving config file: " + ex.getMessage());
        }
    }

    public String getConfigValue(String key) {
        return config.getProperty(key, "");
    }
	public static void main(String[] args) {
		new View();
	}

	public void XmlFetch() throws Exception {

		// The URL of the XML file to download given by @Tomas Gustavsson
		String url = "http://bizlab.kau.se:8280/leads/v1/currentweek";

		// The authentication token given by @Tomas Gustavsson
		String token = "299c5fb8e6b25f3c26c2813943cba265";

		// The file path and name to save the XML file
		String filePath = "src/xmlfile.xml";

		try {
			// Create the URL object
			URL xmlUrl = new URL(url);

			// Create the HTTP connection
			HttpURLConnection con = (HttpURLConnection) xmlUrl.openConnection();

			// Set the request method (adviced by @Tomas Gustavsson)
			con.setRequestMethod("GET");

			// Add the authentication token to the request header
			con.setRequestProperty("Authorization", "Bearer " + token);

			// Get the response code
			int responseCode = con.getResponseCode();

			// Check if the response code is 200 OK Which is code you get with an valid connection
			if (responseCode == HttpURLConnection.HTTP_OK) {

				// Create the file output stream
				FileOutputStream outputStream = new FileOutputStream(filePath);

				// Get the input stream from the HTTP connection
				InputStream inputStream = con.getInputStream();

				// Set the buffer size for reading the input stream
				byte[] buffer = new byte[4096];

				// Initialize the read count
				int bytesRead = -1;

				// Loop through the input stream and write the data to the file
				while ((bytesRead = inputStream.read(buffer)) != -1) {
					outputStream.write(buffer, 0, bytesRead);
				}

				// Close the output stream and input stream
				outputStream.close();
				inputStream.close();

				// Print the success message so you know that its succefull.
				System.out.println("XML file downloaded successfully!");

			} else {
				// If the response code is not 200 OK, print the error message, here we will add function to send email also
				System.out.println("Error: " + responseCode);
			}
			//Default try catch to see what made the error.
		} catch (MalformedURLException e) {
			System.out.println("Error: Invalid URL");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("Error: IOException");
			e.printStackTrace();
		}
	}

	public void XMLParser() throws ParserConfigurationException {

		// The path of the CSV file to write
		String csvFilePath   = "src/InvalidCsvfile.csv";
		
		//https://www.tutorialspoint.com/java_xml/java_dom_parse_document.htm
		try {
			File inputFile = new File("src/xmlfile.xml");
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(inputFile);
			doc.getDocumentElement().normalize();
			System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
			NodeList leadList = doc.getElementsByTagName("lead");
			System.out.println("----------------------------");

			ArrayList alName = new ArrayList<String>();
			ArrayList alCity = new ArrayList<String>();
			ArrayList alAddress = new ArrayList<String>();
			ArrayList alZip = new ArrayList<String>();
			ArrayList alContact = new ArrayList<String>();
			ArrayList alTel = new ArrayList<String>();
			ArrayList alSize = new ArrayList<String>();
			ArrayList alProvider = new ArrayList<String>();
			ArrayList alEmail = new ArrayList<String>();
			ArrayList root = new ArrayList <ArrayList>();
			
			String name = "";
			String city = "";
			String address = "";
			String zip = "";
			String contact = ""; 
			String number = ""; 
			String size = ""; 
			String provider = "";
			String email = ""; 
			
			int leadSize = doc.getDocumentElement().getChildNodes().getLength();
			//name, address etc får sin egna array som de placeras i efter validering
			//ej godkänd validering == null på den platsen i array:en
			String[] nameArray= new String [leadSize];
			String[] addressArray= new String [leadSize];
			String[] zipArray= new String [leadSize];
			String[] cityArray= new String [leadSize];
			String[] contactArray= new String [leadSize];
			String[] telArray= new String [leadSize];
			String[] sizeArray= new String [leadSize];
			String[] providerArray= new String [leadSize];
			String[] emailArray= new String [leadSize];
			
			int correctLead = 0;
			
			System.out.println("Validating starting");
			for (int temp = 0; temp < leadList.getLength(); temp++) {
				Node nNode = leadList.item(temp);
				//System.out.println("\nCurrent Element :" + nNode.getNodeName());
				
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) nNode;

					// Name
					name = eElement
							.getElementsByTagName("name")
							.item(0)
							.getTextContent();

					String regex1 = "^[\\p{L} .'-]+$"; // Unicode, tillåter endast bokstäver, mellanrum och ÅÄÖ

					if(patternMatches(name,regex1)) {
						nameArray[temp] = name;
					}

					// Address
					address = eElement.getElementsByTagName("address")
							.item(0)
							.getTextContent();
					//name, address etc får sin egna array som de placeras i efter validering
					//ej godkänd validering == null på den platsen i array:en
					addressArray[temp] = address;
					
					//Zip
					zip = eElement
							.getElementsByTagName("zip")
							.item(0)
							.getTextContent();
					zipArray[temp] = zip; 

					//CITY
					city = eElement
							.getElementsByTagName("city")
							.item(0)
							.getTextContent();

					if(patternMatches(city,regex1)) {
						cityArray[temp] = city;
					}

					//CONTACT
					contact = eElement
							.getElementsByTagName("contact")
							.item(0)
							.getTextContent();

					if(patternMatches(contact,regex1)) {
						contactArray[temp] = contact;
					}

					//Telephone number   
					number = eElement
							.getElementsByTagName("tele")
							.item(0)
							.getTextContent();
					
					String regex5 = "[\\d\\s-]+";
					if(patternMatches(number,regex5)) {
						telArray[temp] = number;
					}

					// Size 
					size = eElement
							.getElementsByTagName("size")
							.item(0)
							.getTextContent();
					String regex2 = "^[0-9]*$"; // regex only numbers  
					String regex4 = "^[\\d./-]+$"; // regex numbers and . - 

					if(patternMatches(size,regex4)) {
						sizeArray[temp] = size;
					}

					//Current provider
					provider = 
							eElement
							.getElementsByTagName("current_provider")
							.item(0)
							.getTextContent();
					providerArray[temp] = provider; 

					//Email
					email= eElement
							.getElementsByTagName("email")
							.item(0)
							.getTextContent();

					String regexPattern3 = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@" 
							+ "[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";

					if(patternMatches(email,regexPattern3)) {
						emailArray[temp] = email;
					}
				}
			}
				
			
			
				System.out.println("--------------------------------------------------");
				
				File csvFile = new File(csvFilePath);
				FileWriter writer = new FileWriter(csvFile,true);
				
				//https://www.java67.com/2015/07/how-to-append-text-to-existing-file-in-java-example.html#:~:text=You%20can%20append%20text%20into,the%20file%20in%20append%20mode.
				// Lägger till och inte börjar om från början i existerande fil. 
				BufferedWriter bw = null;
		        PrintWriter pw = null;
				
		        bw = new BufferedWriter(writer);
	            pw = new PrintWriter(bw);
				
				// Timestamp
	            LocalDateTime dateTime = LocalDateTime.now() ;
	            
				// I denna loopen som är baserad i läng på hur många leads det finns så jämför den på varje plats i alla arrayer
				// om det finns null någonstans, finns inte det så lägs den omgången in i respektive ArrayList
				//om inte så läggs den inte in i AL, men då alla namn, addresser för sig etc. 
	            
				for(int i = 0; i<leadSize; i++) {
					if(nameArray[i]  != null && addressArray[i] != null && zipArray[i] != null && cityArray[i] != null 
							&& contactArray[i] != null && telArray[i] != null && sizeArray[i] != null
							&& providerArray[i] != null && emailArray[i] != null) {
						
						 alName.add(nameArray[i]);
						 alAddress.add(addressArray[i]);
						 alZip.add(zipArray[i]);
						 alCity.add(cityArray[i]);
						 alContact.add(contactArray[i]);
						 alTel.add(telArray[i]);
						 alSize.add(sizeArray[i]);
						 alProvider.add(providerArray[i]);
						 alEmail.add(emailArray[i]);	 
						 correctLead++;
					} else {
						incorrectleads();
						String inName = nameArray[i];
						String inAddress = addressArray[i];
						String inZip = zipArray[i];
						String inCity = cityArray[i];
						String inContact = contactArray[i];
						String inTel = telArray[i];
						String inSize = sizeArray[i];
						String inProvider = providerArray[i];
						String inEmail = emailArray[i]; 
						if(inName == null) {
							pw.println("Företagsnamn är inkorrekt");
						}
						if(inAddress == null) {
							pw.println("Adressen är inkorrekt");
						}
						if(inZip == null) {
							pw.println("Zip koden är inkorrekt");
						}
						if(inCity == null) {
							pw.println("Staden är inkorrekt");
						}
						if(inContact == null) {
							pw.println("Kontaktpersonen är inkorrekt");
						}
						if(inTel == null) {
							pw.println("Telefonnummret är inkorrekt");
						}
						if(inSize == null) {
							pw.println("storleken är inkorrekt");
						}
						if(inProvider == null) {
							pw.println("provider är inkorrekt");
						}
						if(inEmail == null) {
							pw.println("Email är inkorrekt");
						}
						pw.flush();
						
						pw.print(inName + "," );
						pw.print(inAddress + ",");
						pw.print(inZip + ",");
						pw.print(inCity + ",");
						pw.print(inContact + ",");
						pw.print(inTel + ",");
						pw.print(inSize + ",");
						pw.print(inProvider + ",");
						pw.print(inEmail + "\n");
						pw.flush();
						
						
					}
				}
				
				if(getIncorrectleads() > 0) {
					pw.flush();
					pw.print(DateTimeFormatter.ofPattern("yyyy/MM/dd; HH:mm:ss").format(dateTime)+ "\n");
					pw.print("Invalid leads / leadlist : " + getIncorrectleads() + "/" + leadSize + "\n");
					pw.flush();
				}
				
				pw.print("\n");
				pw.flush();
				//Close writer
				writer.close();
				
				System.out.println("Validating done");
				
				//Efter att det har jämförts och lagts in i var sin AL så läggs de in i var sin behållare,
				//men nu då inte namn för sig etc utan i varje omgång. Som det var från början fast nu utan de som är inkorrekta
				//Sedan läggs varje lead efter omgång in i en AL som då är rooten
				List<String> leads[];
				leads = new LinkedList[alName.size()];
				
				String csvFilePath2   = "src/TmpCSV.csv";
				File csvFile2 = new File(csvFilePath2);
				FileWriter writer2 = new FileWriter(csvFile2);
				BufferedWriter bw2 = null;
		        PrintWriter pw2 = null;
				
		        bw2 = new BufferedWriter(writer2);
	            pw2 = new PrintWriter(bw2);
				
	            System.out.println("Writing correct leads to XML file");
	            for (int i = 0; i < alName.size(); i++) {
		            writer2.append(alName.get(i).toString() + ",");
	                writer2.append(alAddress.get(i).toString()+ ","); 
	                writer2.append(alZip.get(i).toString()+ ","); 
	                writer2.append(alCity.get(i).toString()+ ","); 
	                writer2.append(alContact.get(i).toString()+ ","); 
	                writer2.append(alTel.get(i).toString()+ ","); 
	                writer2.append(alSize.get(i).toString()+ ","); 
	                writer2.append(alProvider.get(i).toString()+ ","); 
	                writer2.append(alEmail.get(i).toString()+ "\n"); 
		            }
		            writer2.close();
				
				for (int i = 0; i < alName.size(); i++) {
					
					leads[i]= new LinkedList();
					leads[i].add(alName.get(i).toString());
					leads[i].add(alAddress.get(i).toString()); 
					leads[i].add(alZip.get(i).toString()); 
					leads[i].add(alCity.get(i).toString()); 
					leads[i].add(alContact.get(i).toString()); 
					leads[i].add(alTel.get(i).toString()); 
					leads[i].add(alSize.get(i).toString()); 
					leads[i].add(alProvider.get(i).toString()); 
					leads[i].add(alEmail.get(i).toString()); 
					
					root.add(leads[i]);
					//System.out.println(leads[i]);
				}
				
				
		}catch(Exception e) {
			System.out.println("Fault = " + e); 
		}
		
		try {
			TmpCsvToXml();
		} catch (ParserConfigurationException | IOException | TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void TmpCsvToXml() throws ParserConfigurationException, FileNotFoundException, IOException, TransformerException {
		String xmlFilePath   = "src/xmlfile.xml";

		// The path of the CSV file to read
		String csvFilePath   = "src/TmpCSV.csv";

		// Create a file object for the input CSV file
		File csvFile = new File(csvFilePath);

		// Create a DocumentBuilderFactory object to create a DocumentBuilder
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

		// Create a new Document object
		Document doc = dBuilder.newDocument();

		// Create the root element of the XML document
		Element leadsElement = doc.createElement("leads");
		leadsElement.setAttribute("xmlns:m", "http://webscraper.se.leads-format.1.0");
		doc.appendChild(leadsElement);

		// Read the CSV file line by line and create a new XML element for each row
		try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
			String line;
			while ((line = br.readLine()) != null) {
				String[] values = line.split(",");
				Element leadElement = doc.createElement("lead");
				leadElement.setAttribute("xmlns", "http://ws.apache.org/ns/synapse");
				//leadElement.setAttribute("id", values[0]);
				leadsElement.appendChild(leadElement);
				Element nameElement = doc.createElement("name");
				nameElement.setTextContent(values[0]);
				leadElement.appendChild(nameElement);
				Element addressElement = doc.createElement("address");
				addressElement.setTextContent(values[1]);
				leadElement.appendChild(addressElement);
				Element zipElement = doc.createElement("zip");
				zipElement.setTextContent(values[2]);
				leadElement.appendChild(zipElement);
				Element cityElement = doc.createElement("city");
				cityElement.setTextContent(values[3]);
				leadElement.appendChild(cityElement);
				Element contactElement = doc.createElement("contact");
				contactElement.setTextContent(values[4]);
				leadElement.appendChild(contactElement);
				Element teleElement = doc.createElement("tele");
				teleElement.setTextContent(values[5]);
				leadElement.appendChild(teleElement);
				Element sizeElement = doc.createElement("size");
				sizeElement.setTextContent(values[6]);
				leadElement.appendChild(sizeElement);
				Element currentProviderElement = doc.createElement("current_provider");
				currentProviderElement.setTextContent(values[7]);
				leadElement.appendChild(currentProviderElement);
				//Checks if theres an email and skips if theres not
				if (values.length >= 9) {
					Element emailElement = doc.createElement("email");
					emailElement.setTextContent(values[8]);
					leadElement.appendChild(emailElement);
				}
			}
		}

		// Write the XML document to a file
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		DOMSource source = new DOMSource(doc);
		StreamResult result = new StreamResult(new File(xmlFilePath));
		transformer.transform(source, result);
		System.out.println("Conversion successful!");
	}
	
	//Validator
	public static boolean patternMatches(String emailAddress, String regexPattern) {
		return Pattern.compile(regexPattern)
				.matcher(emailAddress)
				.matches();
		//https://www.baeldung.com/java-email-validation-regex
	}

	public void XmlToCSV() throws IOException {

		// The path of the XML file to read
		String xmlFilePath   = "src/xmlfile.xml";

		// The path of the CSV file to write
		String csvFilePath   = "src/csvfile.csv";

		try {
			// Create file objects for input and output files
			File xmlFile = new File(xmlFilePath);
			File csvFile = new File(csvFilePath);

			// Create a DocumentBuilder object to parse the XML file
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

			// Parse the XML file into a Document object
			Document doc = dBuilder.parse(xmlFile);

			// Normalize the Document object to remove any empty text nodes
			doc.getDocumentElement().normalize();

			// Get a list of all the "lead" nodes in the XML file
			NodeList leadList = doc.getElementsByTagName("lead");

			// Create a FileWriter object to write to the CSV file
			FileWriter writer = new FileWriter(csvFile);

			/*
			// Write the headers to the CSV file
			writer.append("Name,Address,Zip,City,Contact,Tele,Size,Current Provider,Email\n");
			 */

			// Loop through the "lead" nodes and write the data to the CSV file
			for (int i = 0; i < leadList.getLength(); i++) {
				Node leadNode = leadList.item(i);

				if (leadNode.getNodeType() == Node.ELEMENT_NODE) {
					Element leadElement = (Element) leadNode;

					// Get the values of the "name", "address", "zip", "city",
					// "contact", "tele", "size", "current_provider", and "email"
					// elements and write them to the CSV file
					String name = getValue("name", leadElement);
					String address = getValue("address", leadElement);
					String zip = getValue("zip", leadElement);
					String city = getValue("city", leadElement);
					String contact = getValue("contact", leadElement);
					String tele = getValue("tele", leadElement);
					String size = getValue("size", leadElement);
					String currentProvider = getValue("current_provider", leadElement);
					String email = getValue("email", leadElement);

					writer.append(name + ",");
					writer.append(address + ",");
					writer.append(zip + ",");
					writer.append(city + ",");
					writer.append(contact + ",");
					writer.append(tele + ",");
					writer.append(size + ",");
					writer.append(currentProvider + ",");
					writer.append(email + "\n");
				}
			}

			// Close the FileWriter object
			writer.close();

			System.out.println("Conversion successful!");

		} catch (ParserConfigurationException | IOException e) {
			e.printStackTrace();
		} catch (org.xml.sax.SAXException e) {
			e.printStackTrace();
		}
	}

	public static void CsvToXml() throws IOException, ParserConfigurationException, TransformerException {
		// The path of the XML file to write
		String xmlFilePath   = "src/oldxmlfile.xml";

		// The path of the CSV file to read
		String csvFilePath   = "src/csvfile.csv";

		// Create a file object for the input CSV file
		File csvFile = new File(csvFilePath);

		// Create a DocumentBuilderFactory object to create a DocumentBuilder
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

		// Create a new Document object
		Document doc = dBuilder.newDocument();

		// Create the root element of the XML document
		Element leadsElement = doc.createElement("leads");
		leadsElement.setAttribute("xmlns:m", "http://webscraper.se.leads-format.1.0");
		doc.appendChild(leadsElement);

		// Read the CSV file line by line and create a new XML element for each row
		try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
			String line;
			while ((line = br.readLine()) != null) {
				String[] values = line.split(",");
				Element leadElement = doc.createElement("lead");
				leadElement.setAttribute("xmlns", "http://ws.apache.org/ns/synapse");
				//leadElement.setAttribute("id", values[0]);
				leadsElement.appendChild(leadElement);
				Element nameElement = doc.createElement("name");
				nameElement.setTextContent(values[0]);
				leadElement.appendChild(nameElement);
				Element addressElement = doc.createElement("address");
				addressElement.setTextContent(values[1]);
				leadElement.appendChild(addressElement);
				Element zipElement = doc.createElement("zip");
				zipElement.setTextContent(values[2]);
				leadElement.appendChild(zipElement);
				Element cityElement = doc.createElement("city");
				cityElement.setTextContent(values[3]);
				leadElement.appendChild(cityElement);
				Element contactElement = doc.createElement("contact");
				contactElement.setTextContent(values[4]);
				leadElement.appendChild(contactElement);
				Element teleElement = doc.createElement("tele");
				teleElement.setTextContent(values[5]);
				leadElement.appendChild(teleElement);
				Element sizeElement = doc.createElement("size");
				sizeElement.setTextContent(values[6]);
				leadElement.appendChild(sizeElement);
				Element currentProviderElement = doc.createElement("current_provider");
				currentProviderElement.setTextContent(values[7]);
				leadElement.appendChild(currentProviderElement);
				//Checks if theres an email and skips if theres not
				if (values.length >= 9) {
					Element emailElement = doc.createElement("email");
					emailElement.setTextContent(values[8]);
					leadElement.appendChild(emailElement);
				}
			}
		}

		// Write the XML document to a file
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		DOMSource source = new DOMSource(doc);
		StreamResult result = new StreamResult(new File(xmlFilePath));
		transformer.transform(source, result);
		System.out.println("Conversion successful!");
	}


	public static void compareXml() throws Exception  {
		// Parse the first XML file
		File xmlFileOld = new File("src/oldxmlfile.xml");
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc1 = dBuilder.parse(xmlFileOld);
		doc1.getDocumentElement().normalize();

		// Parse the second XML file
		File xmlFileNew = new File("src/xmlfile.xml");
		Document doc2 = dBuilder.parse(xmlFileNew);
		doc2.getDocumentElement().normalize();

		// Get the leads from the first XML file
		NodeList leadNodes1 = doc1.getElementsByTagName("lead");

		// Get the leads from the second XML file
		NodeList leadNodes2 = doc2.getElementsByTagName("lead");

		// Compare the leads based on their names
		for (int i = 0; i < leadNodes1.getLength(); i++) {
			Node lead1 = leadNodes1.item(i);
			//item(0) = name. item(1) = address etc.
			String name1 = lead1.getChildNodes().item(0).getTextContent();

			for (int j = 0; j < leadNodes2.getLength(); j++) {
				Node lead2 = leadNodes2.item(j);
				String name2 = lead2.getChildNodes().item(0).getTextContent();

				if (name1.equals(name2)) {
					System.out.println("Lead " + name1 + " is present in both XML files.");
					break;
				}
			}
		}
	}

	private static String getValue(String tag, Element element) {
		NodeList nodeList = element.getElementsByTagName(tag).item(0).getChildNodes();
		Node node = nodeList.item(0);
		if (node == null) {
			return "";
		}
		return node.getNodeValue();
	}
	
	
	//https://www.tutorialspoint.com/javamail_api/javamail_api_quick_guide.htm
	public void EmailSender() {
		//Sender Email Cred
		final String senderEmail = "EmiFanGirl@hotmail.com";
		final String senderPassword = "Spindel!2"; //SPRID EJ DENNA INLOGGNING H�LSAR @EMELIE
		
		//Recievers Email Cred
		final String recipientEmail1 = "eh9348@gmail.com";
		final String recipientEmail2 = "kevinspammkonto@gmail.com";
		
		//Smtp Connection
		Properties properties = new Properties();
		properties.put("mail.smtp.host", "smtp.office365.com");
	    properties.put("mail.smtp.port", "587");
	    properties.put("mail.smtp.auth", "true");
	    properties.put("mail.smtp.starttls.enable", "true");
	    
	    //Make Smtp Conenction
	    Session session = Session.getInstance(properties, new Authenticator() {
	    	protected PasswordAuthentication getPasswordAuthentication() {
	    		return new PasswordAuthentication(senderEmail, senderPassword);
	    	}
	    });
	    
	    try {
	    	//Make First message
	    	Message mail1 = new MimeMessage(session);
	    	mail1.setFrom(new InternetAddress(senderEmail));
	    	mail1.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail1));
	    	mail1.setSubject("Test Email");
	    	mail1.setText("This is a 1st test email sent from the best programmer ever @SpwaN");
	    	
	    	//Make Second message
	    	Message mail2 = new MimeMessage(session);
	    	mail2.setFrom(new InternetAddress(senderEmail));
	    	mail2.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail2));
	    	mail2.setSubject("Test Email");
	    	mail2.setText("This is a 2nd test email sent from the best programmer ever @SpwaN");
	    	
	    	// Create transport object
	        Transport transport = session.getTransport("smtp");

	        // Connect to SMTP server
	        transport.connect("smtp.office365.com", senderEmail, senderPassword);
	    	
	    	//Send First message
	    	transport.send(mail1, mail1.getAllRecipients());
	    	System.out.println("Mail 1 Sent Successfully!");
	    	
	    	//Send Second message
	    	transport.send(mail2, mail2.getAllRecipients());
	    	System.out.println("Mail 2 Sent Successfully!");
	    	
	    	transport.close();
	    	
	    } catch (Exception e) {
	    	e.printStackTrace();
	    }
	}
    
    private void incorrectleads() {
    	incorrectLeads++;
    }
    public int getIncorrectleads() {
    	return incorrectLeads;
    }
}