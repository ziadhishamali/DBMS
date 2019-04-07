package eg.edu.alexu.csd.oop.db.cs61;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import eg.edu.alexu.csd.oop.db.Command;

//create table of the instance database
public class CreateTable implements Command<Boolean> {

	private LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
	private String Fdataname = "";
	private String FSchemaname = "";
	private final static String NS_PREFIX = "xs:";
	private String Tname = "";
	private String dataBase;
	private String path;

	@Override
	public Boolean execute(String query) throws SQLException {
		// TODO Auto-generated method stub
		String regex = "(CREATE)([\\s]*) {1}(TABLE)([\\s]*) {1}([\\w\\d_0-9]*)([\\s]*)([\\(])([\\w\\d_\\s\\w\\,]+)([\\)])";
		Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
		Matcher match = pattern.matcher(query);
		if (match.find()) {
			if (query.replaceAll(" ", "").length() != match.group(0).replaceAll(" ", "").length()) {
				throw new SQLException();
			} else {

				String temp1 = "";
				boolean flag1 = false;
				boolean flag2 = false;
				int v = 0;
				Pattern pattern1 = Pattern.compile("[a-zA-Z0-9_]*");
				for (int i = 0; i < query.length(); i++) {
					if (flag1) {
						flag1 = false;
						break;
					} else if (query.charAt(i) != 'l' && query.charAt(i) != 'L') {
						continue;
					} else {
						v = i + 2;
						while (query.charAt(v) != '(') {
							temp1 = query.charAt(v) + temp1;
							v++;
							flag1 = true;
						}
					}
				}
				if (temp1.matches("[\\s]*")) {
					throw new SQLException();

				}
				
				String d = "";
				for (int h = 0; h < temp1.length(); h++) {
					d = temp1.charAt(h) + d;
				}
				/*for (int i = query.length() - 1; i >= 0; i--) {
					v = i;
					if (!flag2) {
						while (query.charAt(v) != '(') {
							v--;
							continue;
						}
					}
					if (!flag2) {
						i = v - 1;
						flag2 = true;

					}
					if (flag1) {
						flag1 = false;
						break;
					}
					if (query.charAt(i) == ' ') {
						continue;
					} else {
						for (int u = i; u >= 0; u--) {
							if (query.charAt(u) == ' ') {
								flag1 = true;
								break;
							}
							temp1 = query.charAt(u) + temp1;
						}
					}
				}*/
				String tempName = d.trim().toLowerCase();
				Matcher matcher = pattern1.matcher(tempName);
				if (!matcher.matches()) {
					// System.out.println("string '" + tempName + "' contains special character");
					throw new SQLException();

				} else {
					String tableName = tempName.trim().toLowerCase();
					Tname = tableName;
					Fdataname = path + dataBase + "\\" + tableName + ".xml";
					System.out.println(Fdataname + "    KKKKKKKKKK");
					// FSchemaname = Proxy.getInstance().getDataBase() +
					// System.getProperty("file.separator") + tableName + ".xsd";

					boolean flag5 = false;
					System.out.println("DDDDDDDDDDDD:" + match.group(8));
					String[] partions = match.group(8).split(",");
					for (int i = 0; i < partions.length; i++) {
						String[] group = partions[i].split("\\s+");
						/*
						 * if (group.length <= 2) { throw new SQLException(); } else {
						 */
						ArrayList<String> temp = new ArrayList<String>();
						for (int j = 0; j < group.length; j++) {
							if (!group[j].equals("")) {
								temp.add(group[j]);
							} else {
								flag5 = true;
							}
						}
						if (flag5 && group.length <= 2) {
							throw new SQLException();
						} else if (!flag5 && group.length <= 1) {
							throw new SQLException();
						} else {
							if (!(temp.get(1).trim().equalsIgnoreCase("INT")
									|| temp.get(1).trim().equalsIgnoreCase("VARCHAR"))) {
								throw new SQLException();
							}
							map.put(temp.get(0), temp.get(1));
							flag5 = false;
						}
						// }
					}

					File data = new File(Fdataname);

					if (data.exists()) {
						return false;
					}
					try {
						data.createNewFile();
						// schema.createNewFile();
					} catch (IOException e) {
						return false;
					}

					/*
					 * for (Map.Entry<String, String> entry : map.entrySet()) {
					 * System.out.println(entry.getKey() + " " + entry.getValue()); }
					 */

					try {
						WriteSchema(map, tableName);
					} catch (ParserConfigurationException e) {
						return false;
					}
				}
			}
		}
		return true;
	}

	@Override
	public void setDataBase(String dataBase) {
		// TODO Auto-generated method stub
		this.dataBase = dataBase;
	}

	@Override
	public void setPath(String path) {
		// TODO Auto-generated method stub
		this.path = path;
	}

	private static class NameTypeElementMaker {
		private String nsPrefix;
		private Document doc;

		public NameTypeElementMaker(String nsPrefix, Document doc) {
			this.nsPrefix = nsPrefix;
			this.doc = doc;
		}

		public Element createElement(String elementName, String nameAttrVal, String typeAttrVal) {
			Element element = doc.createElementNS(XMLConstants.W3C_XML_SCHEMA_NS_URI, nsPrefix + elementName);
			if (nameAttrVal != null)
				element.setAttribute("name", nameAttrVal);
			if (typeAttrVal != null)
				element.setAttribute("type", typeAttrVal);
			return element;
		}

		public Element createElement(String elementName, String nameAttrVal) {
			return createElement(elementName, nameAttrVal, null);
		}

		public Element createElement(String elementName) {
			return createElement(elementName, null, null);
		}
	}

	void WriteSchema(LinkedHashMap<String, String> info, String tableName) throws ParserConfigurationException {

		try {
			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
			Document doc = docBuilder.newDocument();

			Element schemaRoot = doc.createElementNS(XMLConstants.W3C_XML_SCHEMA_NS_URI, NS_PREFIX + "schema");
			doc.appendChild(schemaRoot);

			NameTypeElementMaker elMaker = new NameTypeElementMaker(NS_PREFIX, doc);

			Element idTypeName = elMaker.createElement("element");
			idTypeName.setAttribute("name", NS_PREFIX + Tname);
			schemaRoot.appendChild(idTypeName);

			Element idType = elMaker.createElement("complexType");
			idTypeName.appendChild(idType);

			Element idTypeRestr = elMaker.createElement("sequence");
			idType.appendChild(idTypeRestr);

			for (String key : map.keySet()) {
				Element idTypeRestrPattern = elMaker.createElement("element");
				idTypeRestrPattern.setAttribute("name", key);
				idTypeRestrPattern.setAttribute("type", NS_PREFIX + map.get(key));
				idTypeRestr.appendChild(idTypeRestrPattern);
			}

			TransformerFactory tFactory = TransformerFactory.newInstance();
			Transformer transformer = tFactory.newTransformer();
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			DOMSource domSource = new DOMSource(doc);
			transformer.transform(domSource, new StreamResult(Cache_Pool.getInstance().getDatabase()
					+ System.getProperty("file.separator") + tableName + ".xsd"));

		} catch (FactoryConfigurationError | ParserConfigurationException | TransformerException e) {
			// handle exception
			e.printStackTrace();
		}

	}

}
