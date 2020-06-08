package tank;

import tank.net.Client;

public class Main {

	public static void main(String[] args) throws InterruptedException {
		
		TankFrame tf = TankFrame.INSTANCE;
		tf.setVisible(true);
		
//		int initTankCount = Integer.parseInt((String) PropertyMgr.get("initTankCount"));
//		
//		//��ʼ���ط�̹��
//		for (int i = 0; i < initTankCount; i++) {
//			tf.tanks.add(new Tank(10+i*80, 200, Dir.DOWN, Group.BAD, tf));
//		}
//		new Thread(()->new Audio("audio/war1.wav").loop()).start();
		
		/*//ÿ��50msˢ��һ��Frame
		while(true) {
			Thread.sleep(50);
			tf.repaint();
		}*/
		new Thread(()-> {
			while(true) {
				try {
					Thread.sleep(25);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				tf.repaint();
			}
		}).start();
		
		Client.INSTANCE.connect();
	}
	
}
