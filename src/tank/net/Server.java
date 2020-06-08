package tank.net;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.GlobalEventExecutor;

public class Server {

	public static final ChannelGroup clients = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);	
	
	public void serverStart() {
		EventLoopGroup bossGroup = new NioEventLoopGroup(1);
		EventLoopGroup workerGroup = new NioEventLoopGroup(2);
		try {
			ServerBootstrap b = new ServerBootstrap();
			ChannelFuture f = b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
								.childHandler(new ChannelInitializer<SocketChannel>() {
									@Override
									protected void initChannel(SocketChannel ch) throws Exception {
										ChannelPipeline pl = ch.pipeline();
										pl.addLast(new MsgEncoder());
										pl.addLast(new MsgDecoder());
										pl.addLast(new ServerChildHandler());
									}
								}).bind(8888).sync();
			
			ServerFrame.INSTANCE.updateServerMsg("Server started!");
			
			f.channel().closeFuture().sync();
		} catch (Exception e) {			
			e.printStackTrace();
		} finally {
			workerGroup.shutdownGracefully();
			bossGroup.shutdownGracefully();
		}
	}

}

class ServerChildHandler extends ChannelInboundHandlerAdapter {

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		Server.clients.add(ctx.channel());
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		/*ByteBuf buf = null;
		try {
			buf = (ByteBuf) msg;
			byte[] bytes = new byte[buf.readableBytes()];
			buf.getBytes(buf.readerIndex(), bytes);
			String s = new String(bytes);
			
			ServerFrame.INSTANCE.updateClientMsg(s);
			if(s.equals("_bye_")){
				ServerFrame.INSTANCE.updateServerMsg("客户端要求退出！");
				Server.clients.remove(ctx.channel());
				ctx.close();
			} else {
				Server.clients.writeAndFlush(msg);
			}
		} finally {
			
		}*/
		ServerFrame.INSTANCE.updateClientMsg(msg.toString());
		Server.clients.writeAndFlush(msg);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		cause.printStackTrace();
		//删除出现异常的客户端channel，并关闭连接
		Server.clients.remove(ctx.channel());
		ctx.close();
	}
	
}


