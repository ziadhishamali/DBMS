package eg.edu.alexu.csd.oop.db.cs61;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import org.w3c.dom.Element;

//this class will use in the pool or cache and built by the builder
public class Table {
	// contains schema and ArrayList of HashMap
	
	private String tableName = "";
	private int freq = 0;
	private ArrayList<LinkedHashMap<String, String>> data;
	private LinkedHashMap<String, String> schema;
	
	public Table() {
		data = new ArrayList<>();
		schema = new LinkedHashMap<>();
	}
	
	public void setSchema(LinkedHashMap<String, String> schema) {
		this.schema = schema;
	}
	public void setData(ArrayList<LinkedHashMap<String, String>> data) {
		ArrayList<LinkedHashMap<String, String>> data2 = new ArrayList<>();
		for(int i = 0; i < data.size(); i++) {
			LinkedHashMap<String, String> temp = new LinkedHashMap<>();
			for (String key : schema.keySet()) {
				temp.put(key, data.get(i).get(key));
			}
			data2.add(temp);
		}
		this.data = data2;
	}
	public LinkedHashMap<String, String> getSchema() {//will not be void
		return schema;
	}
	public ArrayList<LinkedHashMap<String, String>> getData() {//will not be void
		ArrayList<LinkedHashMap<String, String>> data2 = new ArrayList<>();
		for(int i = 0; i < data.size(); i++) {
			LinkedHashMap<String, String> temp = new LinkedHashMap<>();
			for (String key : schema.keySet()) {
				temp.put(key, data.get(i).get(key));
			}
			data2.add(temp);
		}
		this.data = data2;
		return data;
	}
	
	public void setTableName(String tableName) {
		
		this.tableName = tableName;
	}
	
	public String getTableName() {
		return this.tableName;
	}
	
	public void incFreq() {
		freq++;
	}
	
	public int getFreq() {
		return freq;
	}
		
}
