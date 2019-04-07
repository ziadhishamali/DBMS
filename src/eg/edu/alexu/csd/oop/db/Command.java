package eg.edu.alexu.csd.oop.db;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;

public interface Command <T> {
	
	/**
	 * it executes the given command. 
	 * @return ... the the results of the execution.
	 */
	public T execute(String query) throws SQLException;
	public void setDataBase(String dataBase);
	public void setPath(String Path);
	
}
