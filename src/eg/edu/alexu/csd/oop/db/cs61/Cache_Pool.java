package eg.edu.alexu.csd.oop.db.cs61;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

public class Cache_Pool {

	private static Cache_Pool cache;
	private ArrayList<Table> pool;
	private ObjectBuilder builder;
	private static final int MAXPOOL = 5;
	private static final int MAXFREQ = 2;
	private String database = "";
	private Read read;

	private Cache_Pool() {
		this.pool = new ArrayList<>();
		this.builder = new ObjectBuilder();
		this.read = new Read();
	}

	public static Cache_Pool getInstance() {
		if (cache == null) {
			cache = new Cache_Pool();
		}
		return cache;
	}

	public Table getTable(String tableName) throws SQLException {

		for (int i = 0; i < pool.size(); i++) {

			if (pool.get(i).getTableName().equals(tableName)) {
				Table table = pool.remove(i);
				return table;
			}

		}

		try {
			builder.setDatabase(this.database);
			builder.makeTable(tableName);
		} catch (SAXException | IOException | ParserConfigurationException | SQLException e) {
			throw new SQLException();
		}
		return builder.getTable();
	}

	public void returnTable(Table table) {

		table.incFreq();
		pool.add(table);

		if (pool.size() > MAXPOOL) {
			int index = 0;
			int min = Integer.MAX_VALUE;

			for (int i = 0; i < MAXFREQ; i++) {
				int temp = pool.get(i).getFreq();
				if (min > temp) {
					min = temp;
					index = i;
				}
			}

			Table t = pool.remove(index);
			try {
				read.writeData(t.getData(), t.getTableName(), this.database);
				System.out.println("table: " + t.getTableName() + "is written !!");
			} catch (ParserConfigurationException e) {
				e.printStackTrace();
			}

		}

	}
	
	public void removeTable (String tableName) {
		
		for (int i = 0; i < this.pool.size(); i++) {
			if (this.pool.get(i).getTableName().equals(tableName)) {
				this.pool.remove(i);
				break;
			}
		}
		
	}
	
	public void setDatabase(String database) {
		for (int i = 0; i < this.pool.size(); i++) {
			Table t = this.pool.get(i);
			try {
				read.writeData(t.getData(), t.getTableName(), this.database);
				System.out.println("table: " + t.getTableName() + "is written !!");
			} catch (ParserConfigurationException e) {
				
			}
		}
		this.pool.clear();
		this.database = database;
	}
	
	public void saveFiles() {
		for (int i = 0; i < this.pool.size(); i++) {
			Table t = this.pool.get(i);
			try {
				read.writeData(t.getData(), t.getTableName(), this.database);
				System.out.println("table: " + t.getTableName() + "is written !!");
			} catch (ParserConfigurationException e) {
				
			}
		}
	}
	
	public String getDatabase() {
		return this.database;
	}

}
