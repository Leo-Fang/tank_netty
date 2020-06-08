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
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import tank.net.Client;
import tank.net.TankDirChangedMsg;
import tank.net.TankStartMovingMsg;
import tank.net.TankStopMsg;

public class TankFrame extends Frame {

	public static final TankFrame INSTANCE = new TankFrame();
	
	Random r = new Random();
	
	public static final int GAME_WIDTH = 800;
	public static final int GAME_HEIGHT = 600;
	
	Tank myTank = new Tank(r.nextInt(GAME_WIDTH), r.nextInt(GAME_HEIGHT), Dir.DOWN, Group.GOOD, this);
	List<Bullet> bullets = new ArrayList<>();
	Map<UUID, Tank> tanks = new HashMap<>();
	List<Explode> explodes = new ArrayList<>();
	
	public void addTank(Tank t) {
		tanks.put(t.getId(), t);
	}
	
	public void addBullet(Bullet b) {
		bullets.add(b);
	}
	
	public Tank findTankByUUID(UUID id) {
		return tanks.get(id);
	}
	
	public Bullet findBulletByUUID(UUID id) {
		for (int i = 0; i < bullets.size(); i++) {
			if(bullets.get(i).getId().equals(id))
				return bullets.get(i);
		}
		return null;
	}
	
	public TankFrame() {
		setSize(GAME_WIDTH, GAME_HEIGHT);
		setTitle("Tank War");
		setResizable(false);
//		setVisible(true);
		
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
		g.drawString("bullets:"+bullets.size(), 10, 60);
		g.drawString("tanks:"+tanks.size(), 10, 80);
		g.drawString("explodes:"+explodes.size(), 10, 100);
		g.setColor(c);
		
		myTank.paint(g);
		//画子弹
		for (int i = 0; i < bullets.size(); i++) {
			bullets.get(i).paint(g);
		}

		tanks.values().stream().forEach((e)->e.paint(g));
		
		//画爆炸
		for (int i = 0; i < explodes.size(); i++) {
			explodes.get(i).paint(g);
		}
		//碰撞检测
		Collection<Tank> values = tanks.values();
		for (int i = 0; i < bullets.size(); i++) {
			for (Tank t : values) {
				bullets.get(i).collideWith(t);
			}
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
			
//			new Thread(()->new Audio("audio/tank_move.wav").play()).start();
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
			Dir dir = myTank.getDir();
			
			if(!bL && !bR && !bU && !bD) {
				myTank.setMoving(false);
				Client.INSTANCE.send(new TankStopMsg(getMainTank()));
			} else {
				if(bL)
					myTank.setDir(Dir.LEFT);
				if(bR)
					myTank.setDir(Dir.RIGHT);
				if(bU)
					myTank.setDir(Dir.UP);
				if(bD)
					myTank.setDir(Dir.DOWN);
				
				if(!myTank.isMoving())
					Client.INSTANCE.send(new TankStartMovingMsg(getMainTank()));
				myTank.setMoving(true);
				
				if(dir != myTank.getDir())
					Client.INSTANCE.send(new TankDirChangedMsg(myTank));
			}		
		}

	}

	public Tank getMainTank() {		
		return this.myTank;
	}
	
}
