package PLM;

import java.awt.*;
import java.util.Timer;
import java.util.TimerTask;
import java.awt.event.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.swing.*;

public class ClientGUI {
	DateTimeFormatter date = DateTimeFormatter.ofPattern("yyyy/MM/dd");
	DateTimeFormatter time = DateTimeFormatter.ofPattern("HH:mm:ss");
	CalSystem cs = null;
	DBManager db = new DBManager();
	SendMail sm = new SendMail();
	JTextField name;
	JTextField phone_num;
	JFrame login;
	
	ClientGUI() {
		JLabel label1 = new JLabel("Name:");
		label1.setBounds(100, 25, 100, 23);
		
		name = new JTextField();
		name.setBounds(190, 25, 150, 23);
		
		JLabel label2 = new JLabel("Phone number:");
		label2.setBounds(100, 75, 100, 23);
		
		phone_num = new JTextField();
		phone_num.setBounds(190, 75, 150, 23);
		
		JButton addBtn = new JButton("Login");
		addBtn.setBounds(150, 150, 100, 23);
		addBtn.addActionListener(new IdentityVerification());
		
		JPanel panel = new JPanel(null);
		panel.add(label1);
		panel.add(name);
		panel.add(label2);
		panel.add(phone_num);
		panel.add(addBtn);
		
		login = new JFrame("Client");
		login.setSize(450, 270);
		login.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		login.setContentPane(panel);;
		login.setVisible(true);
		login.setResizable(false);
		db.addPicture();
	}
	private class IdentityVerification implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if(db.identityVerification(name.getText(), phone_num.getText())) {
				JLabel date_time = new JLabel(date.format(LocalDateTime.now()) + " " + time.format(LocalDateTime.now()), JLabel.CENTER);
				date_time.setBounds(10, 20, 200, 23);
				
				JLabel user = new JLabel("User" + name.getText());
				user.setBounds(225, 20, 200, 23);
				
				String[][] userData = db.getUserData(phone_num.getText());
				String[] monitorTitle = {"Location","Head sensor", "End sendor", "Lost Water(L)", "Lost Money(TWD)", "State"};

				for(int i = 0; i < userData.length; i++) {
					cs = new CalSystem(Integer.parseInt(userData[i][1]), Integer.parseInt(userData[i][2]));
					userData[i][3] = String.valueOf(cs.getWaterLost());
					userData[i][4] = String.valueOf(cs.getMoneyLost());
					userData[i][5] = cs.getState();
				}
				
				JTable monitor = new JTable(userData, monitorTitle) {
//					private static final long serialVersionUID = 1L;
//					public boolean isCellEditable(int row, int column){
//						return false;
//					}
				};
				
				JScrollPane monitorSP = new JScrollPane(monitor);
				monitorSP.setBounds(20, 50, 900, 350);
				
				JButton APMRecord = new JButton("Appointment");
				APMRecord.setBounds(375, 425, 240, 50);
				APMRecord.addActionListener(new AddAPMHandler());
				
				JPanel panel = new JPanel(null);
		        panel.add(date_time);
		        panel.add(monitorSP);
		        panel.add(user);
		        panel.add(APMRecord);
		        
				JFrame main = new JFrame("Monitor");
				main.setSize(960, 540);
				main.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				main.setContentPane(panel);
				main.setVisible(true);
				main.setResizable(false);
				
				Timer timer = new Timer();
				TimerTask task = new TimerTask() {
					public void run() {
						date_time.setText(date.format(LocalDateTime.now()) + " " + time.format(LocalDateTime.now()));
						for(int i = 0; i < userData.length; i++) {
							cs = new CalSystem(Integer.parseInt(userData[i][1]), Integer.parseInt(userData[i][2]));
							cs.calculation();
							monitor.setValueAt(null, i, 3);
							monitor.setValueAt(null, i, 4);
							monitor.setValueAt(null, i, 5);
							monitor.setValueAt(String.valueOf(Math.round(cs.getWaterLost() * 100.0) / 100.0), i, 3);
							monitor.setValueAt(String.valueOf(Math.round(cs.getMoneyLost() * 100.0) / 100.0), i, 4);
							monitor.setValueAt(cs.getState(), i, 5);
							user.setText("user:" + name.getText() + "   Number of registered devices:" + monitor.getRowCount());
							Boolean b = true;
							Boolean m = true;
							Boolean s = true;
							if(cs.getMoneyLost() > 100 & b) {
								sm.send(db.getMail(phone_num.getText()), cs.getMoneyLost());
								sm.setState("large");
								b = false;
							} else if(cs.getMoneyLost() > 10 & m) {
								sm.send(db.getMail(phone_num.getText()), cs.getMoneyLost());
								sm.setState("Medium");
								m = false;
							} else if(cs.getMoneyLost() > 1 & s) {
								sm.send(db.getMail(phone_num.getText()), cs.getMoneyLost());
								sm.setState("small");
								b = false;
							} else if(cs.getWaterLost() == 0){
								s = true;
								b = true;
								m = true;
							}
 							
						}
					}
				};
				timer.schedule(task, 0, 1000); 
				
				login.setVisible(false);
			} else {
				JOptionPane.showMessageDialog(login,"User not found", "", JOptionPane.WARNING_MESSAGE);
			}
		}
	}
	private class AddAPMHandler implements ActionListener {
		JTextField sensor_in;
		JTextField sensor_out;
		JTextField date;
		JTextField time;
		JFrame apm;
		public void actionPerformed(ActionEvent e) {
			
			JLabel label1 = new JLabel("Head sensor:");
			label1.setBounds(80, 15, 200, 23); 
			
			sensor_in = new JTextField();
			sensor_in.setBounds(240, 15, 200, 23);  
			
			JLabel label2 = new JLabel("End sensor:");
			label2.setBounds(80, 45, 200, 23); 
			
			sensor_out = new JTextField();
			sensor_out.setBounds(240, 45, 200, 23);  
			
			JLabel label3 = new JLabel("Date(****/***/***):");
			label3.setBounds(80, 75, 200, 23); 
			
			date = new JTextField();
			date.setBounds(240, 75, 200, 23); 
			JLabel label4 = new JLabel("Time(**:**):");
			label4.setBounds(80, 105, 200, 23); 
			
			time = new JTextField();
			time.setBounds(240, 105, 200, 23);
			
			JButton addAPM = new JButton("Submit");
			addAPM.setBounds(160, 135, 100, 23); 
			addAPM.setPreferredSize(new Dimension(40, 40));
			addAPM.addActionListener(new AddAPM());
			
			JPanel panel = new JPanel(null);
			panel.add(label1);
			panel.add(sensor_in);
			panel.add(label2);
			panel.add(sensor_out);
			panel.add(label3);
			panel.add(date);
			panel.add(label4);
			panel.add(time);
			panel.add(addAPM);
			
			apm = new JFrame("Appointment");
			apm.setSize(480, 270);
			apm.setContentPane(panel);
			apm.setVisible(true);
			apm.setResizable(false);
		}
		private class AddAPM implements ActionListener {
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(apm, db.addAPM(sensor_in.getText(), sensor_out.getText(), date.getText(), time.getText()), "", JOptionPane.WARNING_MESSAGE);
			}
		}
	}
	public static void main(String[] args) {
		new ClientGUI();
	}
}
