package eg.edu.alexu.csd.oop.db.cs61;

import java.io.File;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import eg.edu.alexu.csd.oop.db.Command;

public class DropTable implements Command<Boolean>{
	private String dataBase;
	private String path;
	@Override
	public Boolean execute(String query) throws SQLException {
		// TODO Auto-generated method stub
		try {
			String regex = "(DROP)([\\s]*) {1}(TABLE)([\\s]*) {1}([\\w\\d_0-9]*)([\\s]*?)";
			Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
	        Matcher match = pattern.matcher(query);
	        if (match.find()) {
	        	if (query.replaceAll(" ", "").length() != match.group(0).replaceAll(" ", "").length()) {
					throw new SQLException();
				}
	        	System.out.println("hii");
	        	 String tableName = match.group(5).trim().toLowerCase();
	        	 File indexData = new File(Cache_Pool.getInstance().getDatabase() + 
	        			 System.getProperty("file.separator") + tableName + ".xml");
	        	 File indexSchema = new File(Cache_Pool.getInstance().getDatabase() + 
	        			 System.getProperty("file.separator") + tableName + ".xsd");
	        	 
	        	 System.out.println(indexSchema.getAbsolutePath().toString());
	        	 indexData.delete();
	        	 indexSchema.delete();
	        	 Cache_Pool.getInstance().removeTable(tableName);
	        }
			return true;
		} catch (Exception e) {
			return false;
		}
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
