package eg.edu.alexu.csd.oop.db.cs61;

import java.io.File;
import java.sql.SQLException;

import javax.lang.model.element.QualifiedNameable;

import eg.edu.alexu.csd.oop.db.Command;
import eg.edu.alexu.csd.oop.db.Database;

public class IDatabase implements Database{
	private String dataBase = "";
	private Validation verify = new Validation();
	private Command objectCommand;
	private String path = "";
	
	@Override
	public String createDatabase(String databaseName, boolean dropIfExists) {
		databaseName = databaseName.toLowerCase();
		File f = new File(databaseName);
		int index = databaseName.length()-1;
		for(int i = databaseName.length()-1; i >= 0; i--) {
			if(databaseName.charAt(i) == '\\') {
				index = i;
				break;
			}
		}
		dataBase = databaseName.substring(index+1, databaseName.length());
		path = databaseName.substring(0, index + 1);
		if(dropIfExists && f.exists()) {
			System.out.println("hyms7777");
			objectCommand = new DropDB();
			objectCommand.setPath(path);
			objectCommand.setDataBase(dataBase);
			try {
				objectCommand.execute("DROP DATABASE "+ dataBase);
			} catch (Exception e) {
				
			}
		}
		objectCommand = new CreateDB();
		System.out.println(dataBase + "   Name");
		System.out.println(path  +  "   Path");
		Cache_Pool.getInstance().setDatabase(databaseName);
		objectCommand.setPath(path);
		objectCommand.setDataBase(dataBase);
		try {
			objectCommand.execute("CREATE DATABASE " + dataBase);
		} catch (Exception e) {
			
		}
		return databaseName;
	}

	@Override
	public boolean executeStructureQuery(String query) throws SQLException {
		try {
			query = query.toLowerCase();
			objectCommand = verify.createVerify(query);
			System.out.println(query + "    QQQQQ");
			if(query.contains("create") && query.contains("database")) {
				System.out.println(query + "    NOOO");
				path = "";
				String s = "";
				boolean flag = false;
				for (int i = query.length() - 1; i >= 0; i--) {
					if (flag) {
						flag = false;
						break;
					}
					if (query.charAt(i) == ' ') {
						continue;
					} else {
						for (int j = i; j >= 0; j--) {
							if (query.charAt(j) == ' ') {
								flag = true;
								break;
							}
							s = query.charAt(j) + s;
						}
					}
				}
				dataBase = s;
				Cache_Pool.getInstance().setDatabase(s);
			}else if(dataBase == "") {
				throw new SQLException();
			}
			objectCommand.setDataBase(dataBase);
			objectCommand.setPath(path);
			// not yet handled that return true always untill now
			return (boolean) objectCommand.execute(query);
		} catch (Exception e) {
			throw new SQLException();
		}
	}

	@Override
	public Object[][] executeQuery(String query) throws SQLException {
		//query = query.toLowerCase();
		if(dataBase == "") {
			throw new SQLException();
		}
		
		try {
			
			objectCommand = verify.selectVerify(query);
			objectCommand.setDataBase(dataBase);
			objectCommand.setPath(path);
			return (Object[][]) objectCommand.execute(query);
			
		} catch (Exception e) {
			throw new SQLException();
		}
	}

	@Override
	public int executeUpdateQuery(String query) throws SQLException {
		//query = query.toLowerCase();
		if(dataBase == "") {
			throw new SQLException();
		}
		try {
			
			objectCommand = verify.updateVerify(query);
			objectCommand.setDataBase(dataBase);
			objectCommand.setPath(path);
			return (int) objectCommand.execute(query);
			
		} catch (Exception e) {
			throw new SQLException();
		}
	}

}
