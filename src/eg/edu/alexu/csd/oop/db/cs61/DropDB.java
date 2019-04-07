package eg.edu.alexu.csd.oop.db.cs61;

import java.sql.SQLException;

import eg.edu.alexu.csd.oop.db.Command;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DropDB  implements  Command<Boolean>{
	private String dataBase;
	private String path;
	@Override
	public Boolean execute(String query) throws SQLException {
		// TODO Auto-generated method stub
		String regex = "(DROP)([\\s]*) {1}(DATABASE)([\\s]*) {1}([\\w\\d_0-9]*)([\\s]*?)";
		Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher match = pattern.matcher(query);
        if (match.find()) {
        	if (query.replaceAll(" ", "").length() != match.group(0).replaceAll(" ", "").length()) {
				throw new SQLException();
			}
        	String DatabaseName = match.group(5).trim().toLowerCase();
        	DatabaseName = DatabaseName.toLowerCase();
        	System.out.println(path + DatabaseName + "   hyms7 dh");
        	File index = new File(path +DatabaseName);
        	if (!index.exists()) {
        		return false;
        	}
        	String[]entries = index.list();
        	for(String s: entries){
        	    File currentFile = new File(index.getPath(),s);
        	    currentFile.delete();
        	}
        	Cache_Pool.getInstance().setDatabase(path + DatabaseName);
        	index.delete();
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


}

