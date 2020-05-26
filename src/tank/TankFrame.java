package tank;

import java.awt.Color;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

public class TankFrame extends Frame {

	public static final int GAME_WIDTH = Integer.parseInt((String)PropertyMgr.get("gameWidth"));
	public static final int GAME_HEIGHT = Integer.parseInt((String)PropertyMgr.get("gameHeight"));
	
	Tank myTank = new Tank(200, 400, Dir.DOWN, Group.GOOD, this);
	List<Bullet> bullets = new ArrayList<>();
	List<Tank> tanks = new ArrayList<>();
	List<Explode> explodes = new ArrayList<>();
	
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
		Color c = g.getColor();
		g.setColor(Color.WHITE);
		g.drawString("子弹的数量"+bullets.size(), 10, 60);
		g.drawString("敌人的数量"+tanks.size(), 10, 80);
		g.drawString("爆炸的数量"+explodes.size(), 10, 100);
		g.setColor(c);
		
		myTank.paint(g);
		//画子弹
		for (int i = 0; i < bullets.size(); i++) {
			bullets.get(i).paint(g);
		}
		//画坦克
		for (int i = 0; i < tanks.size(); i++) {
			tanks.get(i).paint(g);
		}
		//碰撞检测
		for (int i = 0; i < bullets.size(); i++) {
			for (int j = 0; j < tanks.size(); j++) {
				bullets.get(i).collideWith(tanks.get(j));
			}
		}
		//画爆炸
		for (int i = 0; i < explodes.size(); i++) {
			explodes.get(i).paint(g);
		}
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
				
			case KeyEvent.VK_CONTROL:
				myTank.fire();
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
