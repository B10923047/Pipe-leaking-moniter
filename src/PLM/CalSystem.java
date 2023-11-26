package PLM;

class CalSystem {
	private float waterLost = 0, moneyLost = 0;
	private String state = "No leaking :)";
	Sensor sensor;

	CalSystem(int head_id, int end_id) {
		sensor = new Sensor(head_id, end_id); 
	}
	void calculation() {
		calWaterLoss();
		calMoneyLoss();
		stateChange();
	}
	private void calWaterLoss() {
		waterLost += sensor.getWaterIn() - sensor.getWaterOut();
		if(waterLost < 0.1) {
			waterLost = 0;
		}
	}
	private void calMoneyLoss() {
		moneyLost = (float) (waterLost / 1000 * 10.10625);
	}
	private void stateChange() {
		if(waterLost >= 10) {
			state = "Dangerious(leaking) :(";
		} else if(waterLost >= 1) {
			state = "Waring(leaking) :|";
		} else if (waterLost >= 0.1) {
			state = "Leaking :/";
		} else if(sensor.getWaterIn() == 0){
			state = "No leaking :)";
		}
	}
	float getWaterLost() {
		return waterLost;
	}
	float getMoneyLost() {
		return moneyLost;
	}
	String getState() {
		return state;
	}
}
