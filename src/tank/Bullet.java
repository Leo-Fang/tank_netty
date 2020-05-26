package tank;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

public class Bullet {

	private static final int SPEED = 10;
	private  int x, y;
	private Dir dir;
	public static final int WIDTH = ResourceMgr.bulletD.getWidth();
	public static final int HEIGHT = ResourceMgr.bulletD.getHeight();
	
	private boolean living = true;//¶¨Òå×Óµ¯ÊÙÃü
	TankFrame tf = null;
	
	public Bullet(int x, int y, Dir dir, TankFrame tf) {
		this.x = x;
		this.y = y;
		this.dir = dir;
		this.tf = tf;
	}
	
	public void paint(Graphics g) {
		if(!living)
			tf.bullets.remove(this);
		
		switch(dir){
		case LEFT:
			g.drawImage(ResourceMgr.bulletL, x, y, null);
			break;
		case RIGHT:
			g.drawImage(ResourceMgr.bulletR, x, y, null);
			break;
		case UP:
			g.drawImage(ResourceMgr.bulletU, x, y, null);
			break;
		case DOWN:
			g.drawImage(ResourceMgr.bulletD, x, y, null);
			break;
		}
		
		move();
	}

	private void move() {
		switch(dir) {
		case LEFT:
			x -= SPEED;
			break;
		case RIGHT:
			x += SPEED;
			break;
		case UP:
			y -= SPEED;
			break;
		case DOWN:
			y += SPEED;
			break;
		default:
			break;
		}
		if(x<0 || y<0 || x>TankFrame.GAME_WIDTH || y>TankFrame.GAME_HEIGHT)
			living = false;
	}

	public void collideWith(Tank tank) {
		Rectangle rectB = new Rectangle(this.x, this.y, this.WIDTH, this.HEIGHT);
		Rectangle rectT = new Rectangle(tank.getX(), tank.getY(), Tank.WIDTH, Tank.HEIGHT);
		if(rectB.intersects(rectT)){
			tank.die();
			this.die();
		}
	}

	private void die() {
		this.living = false;
	}
	
}
