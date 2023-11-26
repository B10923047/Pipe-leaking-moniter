package PLM;

class FormatValidation {
	Boolean pipeIntegrity(String name, String phone_num, String mail, String address, String head_sensor, String end_sensor, String location) {
		if(name.equals("") | phone_num.equals("") | address.equals("") | head_sensor.equals("") | end_sensor.equals("") | location.equals("")) {
			return true;
		} else {
			return false;
		}
	}
	Boolean sensor_id(String head_sensor, String end_sensor) {
		if(head_sensor.equals(end_sensor)) {
			return true;
		} else {
			return false;
		}
	}
	Boolean phone_num(String phone_num) {
		if(!phone_num.matches("^([0-9]{4}-?[0-9]{3}-?[0-9]{3})$")) {
			return true;
		} else {
			return false;
		}
	}
	Boolean APMIntegrity(String head_sensor, String end_sensor, String date, String time) {
		if(head_sensor.equals("") | end_sensor.equals("") | date.equals("") | time.equals("")) {
			return true;
		} else {
			return false;
		}
	}
	Boolean date(String date) {
		if(!date.matches("\\d{4}/\\d{2}/\\d{2}")){
			return true;
		} else {
			return false;
		}
	}
	Boolean time(String time) {
		if(!time.matches("^([0-2]{1}?[0-9]{1}:?[0-5]{1}?[0-9]{1})$")){
			return true;
		} else {
			return false;
		}
	}
}
