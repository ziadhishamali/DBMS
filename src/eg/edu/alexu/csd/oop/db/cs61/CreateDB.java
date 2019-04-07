package eg.edu.alexu.csd.oop.db.cs61;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import eg.edu.alexu.csd.oop.db.Command;

public class CreateDB implements Command<Boolean>{
	private String dataBase;
	private String path;
	@Override
	public Boolean execute(String query) throws SQLException {
		// TODO Auto-generated method stub
		String regex = "(CREATE)([\\s]*) {1}(DATABASE)([\\s]*) {1}([\\w\\d_0-9]*)([\\s]*?)";
		Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher match = pattern.matcher(query);
        if (match.find()) {
        	if (query.replaceAll(" ", "").length() != match.group(0).replaceAll(" ", "").length()) {
				throw new SQLException();
			}
             String DatabaseName = match.group(5).trim().toLowerCase();
             System.out.println(path + dataBase);
             File dir = new File( path + dataBase);
     		 dir.mkdirs();
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
