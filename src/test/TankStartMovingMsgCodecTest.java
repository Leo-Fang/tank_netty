package test;

import static org.junit.Assert.assertEquals;

import java.util.UUID;

import org.junit.Test;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;
import tank.Dir;
import tank.net.MsgDecoder;
import tank.net.MsgEncoder;
import tank.net.MsgType;
import tank.net.TankStartMovingMsg;

public class TankStartMovingMsgCodecTest {

	@Test
	public void testEncoder() {
		EmbeddedChannel ch = new EmbeddedChannel();
		
		UUID id = UUID.randomUUID();
		TankStartMovingMsg msg = new TankStartMovingMsg(id, 5, 10, Dir.LEFT);
		
		ch.pipeline().addLast(new MsgEncoder());
		ch.writeOutbound(msg);
		
		ByteBuf buf = (ByteBuf)ch.readOutbound();
		MsgType msgType = MsgType.values()[buf.readInt()];
		assertEquals(MsgType.TankStartMoving, msgType);
		
		int length = buf.readInt();
		assertEquals(28, length);
		
		UUID uuid = new UUID(buf.readLong(), buf.readLong());
		int x = buf.readInt();
		int y = buf.readInt();
		int dirOrdinal = buf.readInt();
		Dir dir = Dir.values()[dirOrdinal];
		
		assertEquals(5, x);
		assertEquals(10, y);
		assertEquals(Dir.LEFT, dir);
		assertEquals(id, uuid);		
	}
	
	@Test
	public void testDecoder() {
		EmbeddedChannel ch = new EmbeddedChannel();
		
		UUID id = UUID.randomUUID();
		TankStartMovingMsg msg = new TankStartMovingMsg(id, 5, 10, Dir.LEFT);
		
		ch.pipeline().addLast(new MsgDecoder());
		
		ByteBuf buf = Unpooled.buffer();
		buf.writeInt(MsgType.TankStartMoving.ordinal());
		byte[] bytes = msg.toBytes();
		buf.writeInt(bytes.length);
		buf.writeBytes(bytes);
		
		ch.writeInbound(buf.duplicate());
		TankStartMovingMsg msgR = (TankStartMovingMsg)ch.readInbound();
		
		assertEquals(5, msgR.getX());
		assertEquals(10, msgR.getY());
		assertEquals(Dir.LEFT, msgR.getDir());
		assertEquals(id, msgR.getId());
	}
	
}




