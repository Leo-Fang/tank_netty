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
		
		//��Ӽ��̼����¼�
		this.addKeyListener(new MyKeyListener());
		
		//��Ӵ��ڼ����¼������Frame���Ͻǵġ��������Թرմ���
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
	}

	//�����Ļ��˸����
	Image offScreenImage = null;//���ڴ��ж���һ��ͼƬ

	@Override
	public void update(Graphics g) {//�����g����Ļ�Ļ���
		if(offScreenImage == null) {
			offScreenImage = this.createImage(GAME_WIDTH, GAME_HEIGHT);
		}
		Graphics gOffScreen = offScreenImage.getGraphics();
		Color c = gOffScreen.getColor();
		gOffScreen.setColor(Color.BLACK);
		gOffScreen.fillRect(0, 0, GAME_WIDTH, GAME_HEIGHT);
		gOffScreen.setColor(c);
		paint(gOffScreen);
		g.drawImage(offScreenImage, 0, 0, null);//���ڴ��е�ͼƬһ���Ի�����Ļ��
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
		//���ӵ�
		for (int i = 0; i < bullets.size(); i++) {
			bullets.get(i).paint(g);
		}

		tanks.values().stream().forEach((e)->e.paint(g));
		
		//����ը
		for (int i = 0; i < explodes.size(); i++) {
			explodes.get(i).paint(g);
		}
		//��ײ���
		Collection<Tank> values = tanks.values();
		for (int i = 0; i < bullets.size(); i++) {
			for (Tank t : values) {
				bullets.get(i).collideWith(t);
			}
		}
	}
	
	class MyKeyListener extends KeyAdapter {
		
		//����booleanֵ���ж��ƶ�����
		boolean bL = false;
		boolean bR = false;
		boolean bU = false;
		boolean bD = false;
		
		//��дkeyPressed��keyReleased����
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
	
		//���ݰ��������ƶ�����
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
