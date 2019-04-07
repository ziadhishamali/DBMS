package eg.edu.alexu.csd.oop.db.cs61;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Calendar;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.JTextField;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import java.awt.Font;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;

public class MainUI extends JFrame {

	private JPanel contentPane;
	private JTextField textField;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainUI frame = new MainUI();
					frame.setTitle("DBMS command line");
					frame.addWindowListener(new java.awt.event.WindowAdapter() {
					    @Override
					    public void windowClosing(java.awt.event.WindowEvent windowEvent) {
					    	Cache_Pool.getInstance().saveFiles();
					    }
					});
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public MainUI() {
		
		Proxy.getInstance().setDataBase(new IDatabase());
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 1200, 900);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		textField = new JTextField();
		textField.setToolTipText("ENTER THE COMMAND HERE");
		textField.setFont(new Font("Tahoma", Font.BOLD, 17));
		textField.setBounds(28, 785, 1124, 47);
		textField.setBorder(BorderFactory.createCompoundBorder(
				textField.getBorder(), 
		        BorderFactory.createEmptyBorder(10, 10, 10, 10)));
		contentPane.add(textField);
		textField.setColumns(10);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(28, 16, 1124, 758);
		contentPane.add(scrollPane);
		
		JTextArea textArea = new JTextArea();
		textArea.setEditable(false);
		textArea.setFont(new Font("Arial", Font.BOLD, 17));
		textArea.setBorder(BorderFactory.createCompoundBorder(
				textArea.getBorder(), 
		        BorderFactory.createEmptyBorder(10, 10, 10, 10)));
		scrollPane.setViewportView(textArea);
		

		textField.addKeyListener(new KeyListener() {
			
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					
					ArrayList<String> cols = new ArrayList<>();

					if (textField.getText().equalsIgnoreCase("clear")) {
						textArea.setText("");
					} else if (textField.getText().equals("")) {
						textArea.append(">>> ");
						textArea.append("Syntax Error !! please Enter a valid SQL query");
						textArea.append("\n\n");
					} else {
						ArrayList<String> text = Proxy.getInstance().verifyQuery(textField.getText().toString());
						cols = Proxy.getInstance().getColNames();
						if (!text.isEmpty()) {
							textArea.append(">>> ");
						} else {
							textArea.append(">>> Nothing to be displayed !!");
							textArea.append("\n");
						}
						String s = "";
						if (cols != null && cols.size() > 0) {
							for (int i = 0; i < cols.size() ; i++) {
								if (i != cols.size() - 1) {
									s += cols.get(i) + " ..... ";
								}else {
									s += cols.get(i);
								}
								    
							}
							textArea.append(s);
							textArea.append("\n\n");
						}
						for (int i = 0; i < text.size(); i++) {
						    textArea.append("       ");
							textArea.append(text.get(i));
							textArea.append("\n");
						}
						textArea.append("\n");
						
						if (cols != null) {
							
							if (textField.getText().toString().toLowerCase().contains("select") && cols.size() > 0) {
								JPanel panel = new JPanel();
								String arr [] = new String [cols.size()];
								for (int j = 0; j < cols.size() ; j++) {
									arr[j] = cols.get(j);
								}
								JTable table = new JTable(Proxy.getInstance().getselect(), arr);
								table.getTableHeader().setFont(new Font("SansSerif", Font.ITALIC, 20));
								table.setFont(new Font("Serif", Font.BOLD, 20));
								DefaultTableCellRenderer dtcr = new DefaultTableCellRenderer();  
							    dtcr.setHorizontalAlignment(SwingConstants.CENTER);
							    for (int j = 0 ; j < table.getColumnCount() ;j++) {
							    	table.getColumnModel().getColumn(j).setCellRenderer(dtcr);
							    }
								table.setRowHeight(30);
								panel.add(new JScrollPane(table));
								JOptionPane.showMessageDialog(null,panel,"Table",JOptionPane.INFORMATION_MESSAGE);
								cols.clear();
							}
							
						}
						
					}
					
				} else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
					textField.setText("");
				} else if (e.isControlDown() && (e.getKeyCode() == KeyEvent.VK_S)) {
					System.out.println("Iam saving !!!");
					Cache_Pool.getInstance().saveFiles();
				}
			}

			@Override
			public void keyReleased(KeyEvent e) {
				
			}

			@Override
			public void keyTyped(KeyEvent e) {
				
			}
		});
	}
}
