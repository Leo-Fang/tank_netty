package tank;

import java.awt.Color;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class TankFrame extends Frame {

	private static final int GAME_WIDTH = 800;
	private static final int GAME_HEIGHT = 600;
	Tank myTank = new Tank(200, 200, Dir.DOWN);
	Bullet b = new Bullet(300, 300, Dir.DOWN);
	
	public TankFrame() {
		setSize(GAME_WIDTH, GAME_HEIGHT);
		setTitle("Tank War");
		setResizable(false);
		setVisible(true);
		
		//添加键盘监听事件
		this.addKeyListener(new MyKeyListener());
		
		//添加窗口监听事件，点击Frame右上角的“×”可以关闭窗口
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
	}

	//解决屏幕闪烁问题
	Image offScreenImage = null;//在内存中定义一张图片

	@Override
	public void update(Graphics g) {//这里的g是屏幕的画柄
		if(offScreenImage == null) {
			offScreenImage = this.createImage(GAME_WIDTH, GAME_HEIGHT);
		}
		Graphics gOffScreen = offScreenImage.getGraphics();
		Color c = gOffScreen.getColor();
		gOffScreen.setColor(Color.BLACK);
		gOffScreen.fillRect(0, 0, GAME_WIDTH, GAME_HEIGHT);
		gOffScreen.setColor(c);
		paint(gOffScreen);
		g.drawImage(offScreenImage, 0, 0, null);//把内存中的图片一次性画到屏幕上
	}
	
	@Override
	public void paint(Graphics g) {
		myTank.paint(g);
		b.paint(g);
	}
	
	class MyKeyListener extends KeyAdapter {
		
		//设置boolean值来判断移动方向
		boolean bL = false;
		boolean bR = false;
		boolean bU = false;
		boolean bD = false;
		
		//重写keyPressed和keyReleased方法
		@Override
		public void keyPressed(KeyEvent e) {	
			int key = e.getKeyCode();
			switch(key) {
			case KeyEvent.VK_LEFT:
				bL = true;
				break;
			case KeyEvent.VK_RIGHT:
				bR = true;
				break;
			case KeyEvent.VK_UP:
				bU = true;
				break;
			case KeyEvent.VK_DOWN:
				bD = true;
				break;
			default:
				break;
			}			
			setMainTankDir();
		}


		@Override
		public void keyReleased(KeyEvent e) {
			int key = e.getKeyCode();
			switch(key) {
			case KeyEvent.VK_LEFT:
				bL = false;
				break;
			case KeyEvent.VK_RIGHT:
				bR = false;
				break;
			case KeyEvent.VK_UP:
				bU = false;
				break;
			case KeyEvent.VK_DOWN:
				bD = false;
				break;
			default:
				break;
			}
			setMainTankDir();
		}
	
		//根据按键设置移动方向
		public void setMainTankDir() {
			if(!bL && !bR && !bU && !bD)
				myTank.setMoving(false);
			else
				myTank.setMoving(true);
			
			if(bL)
				myTank.setDir(Dir.LEFT);
			if(bR)
				myTank.setDir(Dir.RIGHT);
			if(bU)
				myTank.setDir(Dir.UP);
			if(bD)
				myTank.setDir(Dir.DOWN);
			
		}
	}
	
}
