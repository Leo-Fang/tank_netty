package tank;

public class Main {

	public static void main(String[] args) throws InterruptedException {
		
		TankFrame tf = new TankFrame();
		
		//初始化地方坦克
		for (int i = 0; i < 5; i++) {
			tf.tanks.add(new Tank(50+i*150, 200, Dir.DOWN, tf));
		}
		
		//每隔50ms刷新一次Frame
		while(true) {
			Thread.sleep(50);
			tf.repaint();
		}
		
	}
	
}
