package PLM;

import java.io.FileInputStream;
import java.io.OutputStream;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DBManager {
    FormatValidation fv = new FormatValidation();
    Connection con = null;
    Statement st = null;
    PreparedStatement ps = null;
    ResultSet rs = null;
    DBManager() {
    	try {
    		String url = "jdbc:mysql://localhost:3306/db";
    	    String user = "root";
    	    String password = "123456";
    		con = DriverManager.getConnection(url,user,password);
    	    st = con.createStatement();
    	} catch (Exception e) {
			e.printStackTrace();
		}
    }
    public ResultSet select(String sql) {
    	try {
    		rs = st.executeQuery(sql);
    	} catch(SQLException e) {
			e.printStackTrace();
		}
    	return rs;
    }
    void addPicture() {
    	try {
    		rs = select("select * from `picture` where `path` = \"" + "D:/pipe.png" + "\"");
    		if(!rs.next()) {
    			ps = con.prepareStatement("insert into picture(path, picture) values(?, ?)");
        		ps.setObject(1, "D:/pipe.png");
        		FileInputStream fis = new FileInputStream("D:/pipe.png");
        		byte[] b = new byte[fis.available()];
        		fis.read(b);
        		Blob pic = con.createBlob();
        		OutputStream os = pic.setBinaryStream(1);
        		os.write(b);
        		os.close();
        		ps.setBlob(2, fis);
        		ps.execute();
        		ps.close();
        		fis.close();
    		}
    	} catch(Exception e) {
    		System.out.println("System can't add picture");
		}
    }
    Boolean addUser(String name, String phone_num, String mail, String address) {
		try {
			rs = st.executeQuery("select * from `user` where `phone_num` = \"" + phone_num + "\"");
			if(!rs.next()) {
				ps = con.prepareStatement("insert into user(name, phone_num, mail, address) values(?, ?, ?, ?)");
				ps.setObject(1, name);
				ps.setObject(2, phone_num);
				ps.setObject(3, mail);
				ps.setObject(4, address);
				ps.execute();
				ps.close();
				return false;
			} else if(name.equals(rs.getString("name"))){
				return false;
			} else {
				return true;
			}
		} catch(Exception e) {
			return false;
		}
	}
    void addSensor(String head_sensor, String end_sensor) {
    	try {
    		ps = con.prepareStatement("insert into sensor(pin) values(?)");
    		rs = select("select * from `sensor` where `pin` = '" + head_sensor + "'");
    		if(!rs.next()) {
    			ps.setObject(1, Integer.parseInt(head_sensor));
    			ps.execute();
    			ps.close();
    		}
    		ps = con.prepareStatement("insert into sensor(pin) values(?)");
    		rs = select("select * from `sensor` where `pin` = '" + end_sensor + "'");
    		if(!rs.next()) {
    			ps.setObject(1, Integer.parseInt(end_sensor));
    			ps.execute();
    			ps.close();
    		}
    	} catch(Exception e) {
    		System.out.print("Can'n add sensor");
    	}
    }
    String addPipe(String name, String phone_num, String mail, String address, String head_sensor, String end_sensor, String location) {
		try {
			if(fv.pipeIntegrity(name, phone_num, mail, address, location, head_sensor, end_sensor)) {
				return "Not filled out completely"; 
			}
			if(fv.sensor_id(head_sensor, end_sensor)) {
				return "Duplicate sensor number";
			}
			if(fv.phone_num(phone_num)) {
				return "Cell phone number format is no match";
			}
			if(addUser(name, phone_num, mail, address)) {
				return "This cell phone number has been registered";
			}
			addSensor(head_sensor, end_sensor);
			rs = select("select * from `user` where `name` = \"" + name + "\" and `phone_num` = \"" + phone_num + "\"");
			if(rs.next()) {
				int user_id = rs.getInt("user_id");
				rs = select("select * from `sensor` where `pin` = '" + head_sensor + "'");
				if(rs.next()) {
					int head_pin = rs.getInt("sensor_id");
					rs = select("select * from `sensor` where `pin` = '" + end_sensor + "'");
					if(rs.next()) {
						int end_pin = rs.getInt("sensor_id");
						rs = select("select * from `pipe` where `head_sensor` = '" + head_sensor + "' and `end_sensor` = '" + end_sensor + "'");
						if(!rs.next()) {
							ps = con.prepareStatement("insert into pipe(user_id, head_sensor, end_sensor, location) values(?, ?, ?, ?)");
							ps.setObject(1, user_id);
							ps.setObject(2, head_pin);
							ps.setObject(3, end_pin);
							ps.setObject(4, location);
							ps.execute();
							ps.close();
							return "Device added successfully";
						} else {
							return "This device is already registered";
						}
					} else {
						return "No water outlet found";
					}
				} else {
					return "No water inlet found";
				}
			} else {
				return "Username entered incorrectly";
			}
		} catch(Exception e) {
			return "Failed to add device";
		}
	}
	String addAPM(String head_sensor, String end_sensor, String date, String time) {
		try {
			if(fv.APMIntegrity(head_sensor, end_sensor, date, time)) {
				return "資料未填寫完整";
			}
			if(fv.date(date)) {
				return "未按照日期格式填寫";
			}
			if(fv.time(time)) {
				return "未按照時間格式填寫";
			}
			int sensorIn_id = 0;
			rs = select("select * from `sensor` where `pin` = '" + head_sensor + "'");
			if(rs.next()) {
				sensorIn_id = rs.getInt("sensor_id");
			}
			int sensorOut_id = 0;
			rs = select("select * from `sensor` where `pin` = '" + end_sensor + "'");
			if(rs.next()) {
				sensorOut_id = rs.getInt("sensor_id");
			}
			rs = st.executeQuery("select * from `pipe` where `head_sensor` = '" + sensorIn_id + "' and `end_sensor` = '" + sensorOut_id + "'");
			if(rs.next()) {
				int pipe_id = rs.getInt("pipe_id");
				rs = st.executeQuery("select * from `appointment` where `pipe_id` = " + pipe_id);
				if(!rs.next()) {
					rs = select("select * from `appointment` where `date` = \"" + date + "\" and `time` = \"" + time + "\"");
					if(!rs.next()) {
						ps = con.prepareStatement("insert into appointment(pipe_id, date, time) values(?, ?, ?)");
						ps.setObject(1, pipe_id);
						ps.setObject(2, date);
						ps.setObject(3, time);
						ps.execute();
						ps.close();
						return "Successfully booked!";
					} else {
						return "This time slot has been reserved";
					}
				} else {
					return "This device has an appointment for maintenance";
				}
			} else {
				return "Device id entered incorrectly";
			}
		} catch(Exception e) {
			return "Appointment not successful";
		}
	}
	boolean identityVerification(String name, String phone_num) {
		try {
			rs = st.executeQuery("select * from `user` where `name` = \"" + name + "\" and `phone_num` = \"" + phone_num + "\"");
			return rs.next();
		} catch(Exception e) {
			return false;
		}
	}
	String[][] getAPMRecord() {
		String[][] APMRecord = null;
		try {
			rs = st.executeQuery("select * from `appointment`");
			int i = 0;
			while(rs.next()) {
				i++;
			}
			APMRecord = new String[i][8];
			rs = st.executeQuery("select * from `appointment` order by date, time");
			int x = 0;
			while(rs.next()) {
				APMRecord[x][0] = Integer.toString(rs.getInt("pipe_id"));
				APMRecord[x][6] = rs.getString("date");
				APMRecord[x][7] = rs.getString("time");
				x++;
			}
			for(int y = 0; y < i; y++) {
				rs = select("select * from `pipe` where `pipe_id` = '" + APMRecord[y][0] + "'");
				rs.next();
				APMRecord[y][0] = Integer.toString(rs.getInt("user_id"));
				APMRecord[y][3] = Integer.toString(rs.getInt("head_sensor"));
				APMRecord[y][4] = Integer.toString(rs.getInt("end_sensor"));
				APMRecord[y][5] = rs.getString("location");
				
				rs = select("select * from `sensor` where `sensor_id` = '" + APMRecord[y][3] + "'");
				rs.next();
				APMRecord[y][3] =  Integer.toString(rs.getInt("pin"));
				
				rs = select("select * from `sensor` where `sensor_id` = '" + APMRecord[y][4] + "'");
				rs.next();
				APMRecord[y][4] =  Integer.toString(rs.getInt("pin"));
				
				rs = select("select * from `user` where `user_id` = '" + APMRecord[y][0] + "'");
				rs.next();
				APMRecord[y][0] = rs.getString("name");
				APMRecord[y][1] = rs.getString("phone_num");
				APMRecord[y][2] = rs.getString("address");
			}
		} catch(Exception e) {
			System.out.println(":<");
		}
		return APMRecord;
	}
	float getWater(int sensor_id) {
		try {
			rs = st.executeQuery("select * from `sensor` where `pin` = '" + sensor_id + "'");
			if(rs.next()) {
				return rs.getFloat("water");
			} else {
				return 0;
			}
		} catch(Exception e) {
			return 0;
		}
	}
	String[][] getUserData(String phone_num) {
		String[][] userData = null;
		try {
			int user_id = 0;
			int i = 0;
			rs = select("select * from `user` where `phone_num` = \"" + phone_num + "\"");
			if(rs.next()) {
				user_id = rs.getInt("user_id");
			}
			rs = select("select * from `pipe` where `user_id` = '" + user_id + "'");
			while(rs.next()) {
				i++;
			}
			int x = 0;
			userData = new String[i][6];
			rs = select("select * from `pipe` where `user_id` = '" + user_id + "'");
			while(rs.next()) {
				userData[x][0] = rs.getString("location");
				userData[x][1] = Integer.toString(rs.getInt("head_sensor"));
				userData[x][2] = Integer.toString(rs.getInt("end_sensor"));
				x++;
			}
			for(int y = 0; y < i; y++) {
				rs = select("select * from `sensor` where `sensor_id` = '" + userData[y][1] + "'");
				rs.next();
				userData[y][1] = Integer.toString(rs.getInt("pin"));
				
				rs = select("select * from `sensor` where `sensor_id` = '" + userData[y][2] + "'");
				rs.next();
				userData[y][2] = Integer.toString(rs.getInt("pin"));
			}
			return userData;
		} catch(Exception e) {
			return userData;
		}
	}
	String getMail(String phone_num) {
		try {
			rs = select("select * from `user` where `phone_num` = \"" + phone_num + "\"");
			if(rs.next()) {
				return rs.getString("mail");
			} else {
				return "";
			}
		} catch(Exception e) {
			return "";
		}
	}
}
