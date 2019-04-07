package eg.edu.alexu.csd.oop.db.cs61;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class Read {
	/*public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException {
		writeData(readTable("table1", "C:\\Users\\ziadh\\Desktop"), "table2", "C:\\Users\\ziadh\\Desktop");
	}*/
	
	public ArrayList<LinkedHashMap<String, String>> readTable(String tableName, String database) throws SAXException, IOException, ParserConfigurationException {
		
		DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = builderFactory.newDocumentBuilder();
		
		ArrayList<LinkedHashMap<String, String>> dataMap = new ArrayList<>();
		
		//Document document = builder.parse(tableName + ".xml");
		File file = new File(database + System.getProperty("file.separator") + tableName + ".xml");
		if (file.length() == 0) {
			return dataMap;
		}
		Document document = builder.parse(database + System.getProperty("file.separator") + tableName + ".xml");
		NodeList list = document.getElementsByTagName("Row");
		for (int i = 0; i < list.getLength(); i++) {
			LinkedHashMap<String, String> map = new LinkedHashMap<>();
			Node p = list.item(i);
			if (p.getNodeType() == Node.ELEMENT_NODE) {
				Element dev = (Element) p;
				//System.out.println("id "+ dev.getAttribute("id"));
				NodeList list2 = dev.getChildNodes();
				for (int j = 0; j < list2.getLength(); j++) {
					Node node = list2.item(j);
					if (node.getNodeType() == Node.ELEMENT_NODE) {
						Element element = (Element) node;
						//System.out.println(element.getTagName() + "   " + element.getTextContent());
						map.put(element.getTagName(), element.getTextContent());
					}
				}
				dataMap.add(map);
			}
		}
		
		System.out.println(dataMap);
		
		return dataMap;
	}
	
	public void writeData(ArrayList<LinkedHashMap<String, String>> info, String tableName, String database) throws ParserConfigurationException {
		
		DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = builderFactory.newDocumentBuilder();
		
		Document document = builder.newDocument();
		
		Element element = document.createElement("Table");
		document.appendChild(element);
		
		for (int i = 0; i < info.size(); i++) {
			Element element3 = document.createElement("Row");
			element.appendChild(element3);
			for (String key : info.get(i).keySet()) {
				Element element2 = document.createElement(key);
				
				element2.appendChild(document.createTextNode(info.get(i).get(key)));
				element3.appendChild(element2);
			}
		}

		TransformerFactory factory = TransformerFactory.newInstance();
		Transformer transformer = null;
		try {
			transformer = factory.newTransformer();
		} catch (TransformerConfigurationException e) {
			
		}
		DOMSource xmlSource = new DOMSource(document);
		StreamResult result = new StreamResult(new File(database + System.getProperty("file.separator") + tableName + ".xml"));
		try {
			transformer.transform(xmlSource, result);
		} catch (TransformerException e) {
			
		}

	}
	
	public static LinkedHashMap<String, String> ReadSchema(String tableName, String database) throws SQLException {
		// We need to provide file path as the parameter:
		// double backquote is to avoid compiler interpret words
		// like \test as \t (ie. as a escape sequence)
		File file = new File(database + System.getProperty("file.separator") + tableName + ".xsd");
		//File file = new File(database + System.getProperty("file.separator") + tableName);
		LinkedHashMap<String, String> schema = new LinkedHashMap<>();
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(file));
		} catch (FileNotFoundException e) {
			throw new SQLException();
		}

		String st = "";
		try {
			for (int i = 0; i < 5; i++) {
				st = br.readLine();
			}
			String x = st;
			while (!x.equals("</xs:sequence>")) {
				st = br.readLine();
				StringTokenizer token = new StringTokenizer(st, " ");
		        StringBuffer sb = new StringBuffer();
		        while(token.hasMoreElements()){
		            sb.append(token.nextElement()).append(" ");
		        }
		        x = sb.toString().trim();
				String regex = "(\\<)(xs)([:])(element)([\\s]*)(name)([\\s]*?)([\\=])([\"])([\\w\\d_0-9]*)([\"])([\\s]*)(type)([\\s]*?)([\\=])([\"])(xs)([:])([\\w\\d_0-9]*)([\"])([\\/])(\\>)";
				Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
				Matcher match = pattern.matcher(x);
				if (match.find()) {
					String coloumnName = match.group(10);  //COLOUMN NAME
					String coloumnType = match.group(19);  //COLOUMN TYPE
					//System.out.println(coloumnName);
					//System.out.println(coloumnType);
					schema.put(coloumnName,coloumnType);
				}
			}
			br.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(schema);
		return schema;
	}
}