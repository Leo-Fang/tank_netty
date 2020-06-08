package tank.net;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.UUID;

import tank.Dir;
import tank.Tank;
import tank.TankFrame;

public class TankStopMsg extends Msg {

	public int x, y;
	public UUID id;
	
	public TankStopMsg(int x, int y, Dir dir, UUID id) {
		super();
		this.x = x;
		this.y = y;
		this.id = id;
	}
	
	public TankStopMsg() {
	}
	
	public TankStopMsg(Tank t) {
		this.x = t.getX();
		this.y = t.getY();
		this.id = t.getId();
	}
	
	@Override
	public byte[] toBytes() {
		ByteArrayOutputStream baos = null;
		DataOutputStream dos = null;
		byte[] bytes = null;
		try {
			baos = new ByteArrayOutputStream();
			dos = new DataOutputStream(baos);
			
			dos.writeLong(id.getMostSignificantBits());
			dos.writeLong(id.getLeastSignificantBits());
			dos.writeInt(x);
			dos.writeInt(y);
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
				.append("uuid=" + id + " | ")
				.append("x=" + x + " | ")
				.append("y=" + y + " | ").append("]");
		
		return builder.toString();
	}

	@Override
	public void handle() {
		//if this msg is send by myself, do nothing
		if(this.id.equals(TankFrame.INSTANCE.getMainTank().getId()))
			return;
		
		Tank t = TankFrame.INSTANCE.findTankByUUID(this.id);
		if(t != null) {
			t.setMoving(false);
			t.setX(this.x);
			t.setY(this.y);
		}
	}

	@Override
	public void parse(byte[] bytes) {
		DataInputStream dis = new DataInputStream(new ByteArrayInputStream(bytes));
		try {
			this.id = new UUID(dis.readLong(), dis.readLong());
			this.x = dis.readInt();
			this.y = dis.readInt();
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
		return MsgType.TankStop;	
	}	
	
}
