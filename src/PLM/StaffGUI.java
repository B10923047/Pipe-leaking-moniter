package PLM;

import java.awt.*;
import java.util.Timer;
import java.util.TimerTask;
import java.awt.event.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.swing.*;

public class StaffGUI {
	DateTimeFormatter date = DateTimeFormatter.ofPattern("yyyy/MM/dd");
	DateTimeFormatter time = DateTimeFormatter.ofPattern("HH:mm:ss");
	DBManager db = new DBManager();
	StaffGUI() {
        JLabel date_time = new JLabel(date.format(LocalDateTime.now()) + " " + time.format(LocalDateTime.now()), JLabel.CENTER);
        date_time.setBounds(150, 25, 200, 30); 
		
		JButton APMRecord = new JButton("Appointment record");
		APMRecord.setBounds(150, 75, 200, 30); 
		APMRecord.addActionListener(new APMRecord());
		
		JButton addDevece = new JButton("Add device");
		addDevece.setBounds(150, 125, 200, 30); 
		addDevece.addActionListener(new AddDeviceHandler());
		
		JPanel panel = new JPanel(null); 
		panel.add(date_time);
        panel.add(APMRecord);
        panel.add(addDevece);
        
        
		JFrame main = new JFrame("Staff");
		main.setSize(480, 270);
		main.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		main.setContentPane(panel);;
		main.setVisible(true);
		main.setResizable(false);
		
		Timer timer = new Timer();
		TimerTask task = new TimerTask() {
			public void run() {
				date_time.setText(date.format(LocalDateTime.now()) + " " + time.format(LocalDateTime.now()));
			}
		};
		timer.schedule(task, 0, 1000); 
	}
	private class APMRecord implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			String[][] APMRecord = db.getAPMRecord();
			String[] recordTitle = {"User","Phone number", "Home address", "Head sensor pin", "End sensor pin", "Location", "Date", "Time"};

			JTable record = new JTable(APMRecord, recordTitle) {
				private static final long serialVersionUID = 1L;
				public boolean isCellEditable(int row, int column){
					return false;
				}
			};
			
			JScrollPane jscrollpane = new JScrollPane(record);
			
			JFrame recordWindow = new JFrame("Appointment record");
			recordWindow.setSize(1600, 900);
			recordWindow.getContentPane().setLayout(new GridLayout(1, 1));
			recordWindow.add(jscrollpane, BorderLayout.CENTER);
			recordWindow.setVisible(true);
			recordWindow.setResizable(false);
		}
	}
	private class AddDeviceHandler implements ActionListener {
		JTextField name;
		JTextField phone_num;
		JTextField mail;
		JTextField address;
		JTextField location;
		JTextField head_sensor;
		JTextField end_sensor;
		JFrame addDevice;
		public void actionPerformed(ActionEvent e) {
			JLabel label1 = new JLabel("Name:");
			label1.setBounds(25, 15, 100, 23);  
			
			name = new JTextField();
			name.setBounds(125, 15, 100, 23);  
			
			JLabel label2 = new JLabel("Phone number:");
			label2.setBounds(25, 45, 100, 23);  
			
			phone_num = new JTextField();
			phone_num.setBounds(125, 45, 100, 23);
			
			JLabel label3 = new JLabel("Email:");
			label3.setBounds(25, 75, 100, 23);  
			
			mail = new JTextField();
			mail.setBounds(125, 75, 275, 23); 
			
			JLabel label4 = new JLabel("Home address:");
			label4.setBounds(25, 105, 100, 23);  
			
			address = new JTextField();
			address.setBounds(125, 105, 275, 23);  
			
			JLabel label5 = new JLabel("Head sensor pin:");
			label5.setBounds(25, 135, 100, 23);  
			
			head_sensor = new JTextField();
			head_sensor.setBounds(125, 135, 50, 23);  
			
			JLabel label6 = new JLabel("End sensor pin:");
			label6.setBounds(25, 165, 100, 23);  
			
			end_sensor = new JTextField();
			end_sensor.setBounds(125, 165, 50, 23);
			
			JLabel label7 = new JLabel("Location:");
			label7.setBounds(25, 195, 100, 23);  
			
			location = new JTextField();
			location.setBounds(125, 195, 100, 23); 
			
			JButton addBtn = new JButton("Submit");
			addBtn.setBounds(350, 200, 100, 23); 
			addBtn.addActionListener(new AddDevice());
			
			JLabel label8 = new JLabel("(****-***-***)");
			label8.setBounds(250, 45, 100, 23);  
		    
			JPanel panel = new JPanel(null); 
			panel.add(label1);
			panel.add(name);
			panel.add(label2);
			panel.add(phone_num);
			panel.add(label3);
			panel.add(mail);
			panel.add(label4);
			panel.add(address);
			panel.add(label5);
			panel.add(head_sensor);
			panel.add(label6);
			panel.add(end_sensor);
			panel.add(label7);
			panel.add(location);
			panel.add(label8);
			panel.add(addBtn);
			
			addDevice = new JFrame("Add device");
			addDevice.setSize(480, 270);
			addDevice.setContentPane(panel);
			addDevice.setVisible(true);
			addDevice.setResizable(false);
		}
		private class AddDevice implements ActionListener {
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(addDevice, db.addPipe(name.getText(), phone_num.getText(), mail.getText(), address.getText(), head_sensor.getText(), end_sensor.getText(), location.getText()), "", JOptionPane.INFORMATION_MESSAGE);
				head_sensor.setText("");
				end_sensor.setText("");
				location.setText("");
			}
		}
	}
	public static void main(String[] args) {
		new StaffGUI();
	}
}
