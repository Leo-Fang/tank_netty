package tank;

public class Main {

	public static void main(String[] args) throws InterruptedException {
		
		TankFrame tf = new TankFrame();
		
		//每隔50ms刷新一次Frame
		while(true) {
			Thread.sleep(50);
			tf.repaint();
		}
		
	}
	
}
