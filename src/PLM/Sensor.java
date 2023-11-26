package PLM;

class Sensor {
	DBManager db = new DBManager();
	private float waterIn, waterOut;
	int head_sensor, end_sensor;
	Sensor(int head_id, int end_id) {
		head_sensor = head_id;
		end_sensor = end_id;
	}
	float getWaterIn() {
		waterIn = db.getWater(head_sensor);
		return waterIn;
	}
	float getWaterOut() {
		waterOut = db.getWater(end_sensor);
		return waterOut;
	}
}