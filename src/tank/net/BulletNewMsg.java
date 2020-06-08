package tank.net;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.UUID;

import tank.Bullet;
import tank.Dir;
import tank.Group;
import tank.TankFrame;

public class BulletNewMsg extends Msg {

	public int x, y;
	public Dir dir;
	public Group group;
	public UUID id;
	public UUID playerId;
	
	public BulletNewMsg() {
	}
	
	public BulletNewMsg(Bullet bullet) {
		this.x = bullet.getX();
		this.y = bullet.getY();
		this.dir = bullet.getDir();
		this.group = bullet.getGroup();
		this.id = bullet.getId();
		this.playerId = bullet.getPlayerId();
	}
	
	@Override
	public byte[] toBytes() {
		ByteArrayOutputStream baos = null;
		DataOutputStream dos = null;
		byte[] bytes = null;
		try {
			baos = new ByteArrayOutputStream();
			dos = new DataOutputStream(baos);
			
			dos.writeLong(this.playerId.getMostSignificantBits());
			dos.writeLong(this.playerId.getLeastSignificantBits());
			dos.writeLong(id.getMostSignificantBits());
			dos.writeLong(id.getLeastSignificantBits());
			dos.writeInt(x);
			dos.writeInt(y);
			dos.writeInt(dir.ordinal());
			dos.writeInt(group.ordinal());
			dos.flush();
			bytes = baos.toByteArray();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if(baos != null)
					baos.close();
			} catch (IOException e) {					
				e.printStackTrace();
			}
			try {
				if(dos != null)
					dos.close();
			} catch (IOException e) {		
				e.printStackTrace();
			}			
		}
		return bytes;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		
		builder.append(this.getClass().getName()).append("[")
				.append("playerId=" + playerId + " | ")
				.append("uuid=" + id + " | ")
				.append("x=" + x + " | ")
				.append("y=" + y + " | ")
				.append("dir=" + dir + " | ")
				.append("group=" + group).append("]");
		
		return builder.toString();
	}

	@Override
	public void handle() {
		if(this.id.equals(TankFrame.INSTANCE.getMainTank().getId()))
			return;
		Bullet bullet = new Bullet(this.playerId, x, y, dir, group, TankFrame.INSTANCE);
		bullet.setId(this.id);
		TankFrame.INSTANCE.addBullet(bullet);
	}

	@Override
	public void parse(byte[] bytes) {
		DataInputStream dis = new DataInputStream(new ByteArrayInputStream(bytes));
		try {
			this.playerId = new UUID(dis.readLong(), dis.readLong());
			this.id = new UUID(dis.readLong(), dis.readLong());
			this.x = dis.readInt();
			this.y = dis.readInt();
			this.dir = Dir.values()[dis.readInt()];
			this.group = Group.values()[dis.readInt()];
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				dis.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public MsgType getMsgType() {
		return MsgType.BulletNew;	
	}	
	
}
