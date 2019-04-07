package eg.edu.alexu.csd.oop.db.cs61;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parsing {
	
	public void createDatabase (String query) {
		
		String regex = "(CREATE)([\\s]*)(DATABASE)([\\s]*)([[0-9]?a-zA-Z[\\_]?]*)([\\s]*?)";
		Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher match = pattern.matcher(query);
        
        String DatabaseName = match.group(5);
		
	}
	
	public void dropDatabase (String query) {
		
		String regex = "(DROP)([\\s]*)(DATABASE)([\\s]*)([[0-9]?a-zA-Z[\\_]?]*)([\\s]*?)";
		Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher match = pattern.matcher(query);
        
        String DatabaseName = match.group(5);
		
	}
	
	public void dropTable (String query) {
		
		String regex = "(DROP)([\\s]*)(TABLE)([\\s]*)([[0-9]?a-zA-Z[\\_]?]*)([\\s]*?)";
		Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher match = pattern.matcher(query);
        
        String tableName = match.group(5);
		
	}
	
	public void selectTable (String query) {
		
		String regex = "(USE)([\\s]*)([\\w\\d_0-9]*)([\\s]*?)";
		Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher match = pattern.matcher(query);
        
        String tableName = match.group(3);
				
	}
	
	
	public void createTable (String query) {
		
		String regex = "(CREATE)([\\s]*)(TABLE)([\\s]*)([\\w\\d_0-9]*)([\\s]*)([\\(])([\\w\\d_\\s\\w\\,]+)([\\)])";
		Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher match = pattern.matcher(query);
        
        String tableName = match.group(5);
        
        ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> map = new HashMap();
		String[] partions = match.group(8).split(",");
    	for (int i = 0; i < partions.length; i++) {
    		String[] group = partions[i].split("\\s+");
    		ArrayList<String> temp = new ArrayList<String>();
    		for (int j = 0; j < group.length; j++) {
    			if (!group[j].equals("")) {
    				temp.add(group[j]);
    			}
    		}
    		map.put(temp.get(0), temp.get(1));
    		list.add(map); //ONE ROW
    	}
    	
	}
	
	
	public void insertInTable (String query) {
		
		String regex = "(INSERT)([\\s]*)(INTO)([\\s]*)([\\w\\d_0-9]*)([\\s]*?)([\\(]){0,1}([\\w\\d_0-9,?[\\s]?]*){0,1}([\\)]){0,1}([\\s]*){0,1}(VALUES)([\\s]*)([\\(])([[']?\\w\\d_0-9[']?,?[\\s]?]*)([\\)])";
		Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher match = pattern.matcher(query);
        
        String tableName = match.group(5);
        
        ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> map = new HashMap();
        String[] partions1 = match.group(8).split(",");
		String[] partions2 = match.group(14).split(",");
		ArrayList<String> temp1 = new ArrayList<String>();
		ArrayList<String> temp2 = new ArrayList<String>();
		
		

    	for (int i = 0; i < partions1.length; i++) {
    		temp1.add(partions1[i].trim());
    		temp2.add(partions2[i].trim());
    	}
    	
    	for (int i = 0; i < partions1.length; i++) {
    		map.put(temp1.get(i), temp2.get(i));
    		list.add(map);
    	}
    	
    	/* list here is the added ROW */
        
	}
	
	public void selectFromTable1 (String query) {
		
		String regex = "(SELECT)([\\s]*)([*] {1})([\\s]*)(FROM)([\\s]*)([\\w\\d_0-9]*)([\\s]*?)(WHERE) {0,1}([\\s]*){0,1}([a-zA-Z_0-9\\s\\W\\s0-9]*){0,1}";
		Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher match = pattern.matcher(query);
        
        String tableName = match.group(7);
        String condition = match.group(11);
		
	}
	
	public void selectFromTable2 (String query) {
		
		String regex = "(SELECT)([\\s]*)([\\w\\d_0-9,?[\\s]?]*)([\\s]*)(FROM)([\\s]*?)([\\w\\d_0-9]*)([\\s]*?)";
		Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher match = pattern.matcher(query);
        
        String tableName = match.group(7);
        
        ArrayList<String> list = new ArrayList<String>();
        String[] partions = match.group(4).split(",");
        for (int i = 0; i < partions.length; i++) {
    		list.add(partions[i].trim());
    	}


		
	}
	
	
	public void deleteFromTable (String query) {
		
		String regex = "(DELETE)([\\s]?)(FROM)([\\s]?)([\\w\\d_0-9]*)([\\s]?)((WHERE)([\\s]?)([a-zA-Z_0-9\\s\\W\\s0-9]*)){0,1}";

		Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher match = pattern.matcher(query);
        
        String tableName = match.group(5);	
        String condition = match.group(7);
        
	}
	
	
	public HashMap<String, String> updateTable (String query) {
		
		String regex = "(UPDATE)([\\s]*)([\\w\\d_0-9]*)([\\s]*)(SET)([\\s]*)([a-zA-Z_0-9[\\s]*?=[\\']?0-9[\\']?[\\s]*?[,]?]*)([\\s]*)((WHERE)([\\s]?)([a-zA-Z_0-9\\s\\W\\s0-9]*)){0,1}";
		
		Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher match = pattern.matcher(query);
        match.find();
        
        String tableName = match.group(3);
        String condition = match.group(9);
        
        HashMap<String, String> map = new HashMap();
        String[] partions = match.group(7).split(",");
    	for (int i = 0; i < partions.length; i++) {
    		String[] group = partions[i].split("=");
    		ArrayList<String> temp = new ArrayList<String>();
    		for (int j = 0; j < group.length; j++) {
    			temp.add(group[j].trim());
    		}
    		map.put(temp.get(0), temp.get(1));
    	}
    	
    	return map;

	}
	
	
	
	

}
