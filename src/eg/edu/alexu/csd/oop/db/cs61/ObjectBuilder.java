package eg.edu.alexu.csd.oop.db.cs61;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

public class ObjectBuilder {
	// Object builder of the read file
	
	// contain all data needed as public like th table
	private Table table;
	private ArrayList<LinkedHashMap<String, String>> data;
	private LinkedHashMap<String, String> schema;
	private Read read = new Read();
	private String database = "";
	
	private void buildScheme(String tableName) throws SQLException {
		// make a map<String, String>
		schema = new LinkedHashMap<>();
		schema = read.ReadSchema(tableName, database);
		table.setSchema(schema);
	}
	
	
	private void buildData(String tableName) throws SAXException, IOException, ParserConfigurationException {
		data = new ArrayList<>();
		data  = read.readTable(tableName, database);
		// make a array list of map<String, String>
		table.setData(data);
	}
	
	
	public void makeTable(String tableName) throws SAXException, IOException, ParserConfigurationException, SQLException {
		table = new Table();
		table.setTableName(tableName);
		try {
			buildScheme(tableName);
			buildData(tableName);
		} catch(Exception e) {
			throw new SQLException();
		}
	}
	
	public Table getTable() {
		
		return table;
		
	}
	
	public void setDatabase(String database) {
		this.database = database;
	}
	
}