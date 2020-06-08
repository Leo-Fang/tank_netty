package tank.net;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import tank.TankFrame;

public class Client {

	public static final Client INSTANCE = new Client();
	private Channel channel = null;
	private Client() {}
	
	public void connect() {
		//线程池
		EventLoopGroup group = new NioEventLoopGroup(1);
		Bootstrap b = new Bootstrap();
		
		try {
			ChannelFuture f = b.group(group).channel(NioSocketChannel.class)
								.handler(new ClientChannelInitializer()).connect("localhost", 8888);
			
			f.addListener(new ChannelFutureListener() {
				@Override
				public void operationComplete(ChannelFuture future) throws Exception {
					if(!future.isSuccess()) {
						System.out.println("Not connected!");
					} else {
						System.out.println("Connected!");
						channel = future.channel();
					}	
				}
			});
			
			f.sync();
			//wait until close
			f.channel().closeFuture().sync();
			System.out.println("已经退出");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			group.shutdownGracefully();
		}
		
	}

	public void closeConnect() {
//		this.send("_bye_");
	}

	public void send(Msg msg) {
		System.out.println("SEND:" + msg);
		channel.writeAndFlush(msg);
	}

}

class ClientChannelInitializer extends ChannelInitializer<SocketChannel> {

	@Override
	protected void initChannel(SocketChannel ch) throws Exception {
		ch.pipeline()
			.addLast(new MsgEncoder())
			.addLast(new MsgDecoder())
			.addLast(new ClientHandler());
	}
	
}

class ClientHandler extends SimpleChannelInboundHandler<Msg> {

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {				
		ctx.writeAndFlush(new TankJoinMsg(TankFrame.INSTANCE.getMainTank()));
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, Msg msg) throws Exception {
		System.out.println(msg);
		msg.handle();
	}
	
}



