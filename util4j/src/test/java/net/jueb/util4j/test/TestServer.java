package net.jueb.util4j.test;

import java.net.InetSocketAddress;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import net.jueb.util4j.net.JConnection;
import net.jueb.util4j.net.JConnectionFactory;
import net.jueb.util4j.net.nettyImpl.NettyConnectionFactory;
import net.jueb.util4j.net.nettyImpl.handler.listenerHandler.adapter.IdleListenerHandlerAdapter;
import net.jueb.util4j.net.nettyImpl.listener.HeartAbleConnectionListener;
import net.jueb.util4j.net.nettyImpl.server.NettyServer;
import net.jueb.util4j.net.nettyImpl.server.NettyServerConfig;
import net.jueb.util4j.queue.queueExecutor.queueGroup.QueueGroupExecutor;
import net.jueb.util4j.queue.queueExecutor.queueGroup.impl.DefaultQueueGroupExecutor;

public class TestServer {
	public class GameMsg{
		int code;
		public int getCode() {
			return code;
		}
		public void setCode(int code) {
			this.code = code;
		}
	}
	public static void main(String[] args) {
		final QueueGroupExecutor qe=new DefaultQueueGroupExecutor();
		final short mainQueue=1;
		final ChannelHandler logicHandler=new IdleListenerHandlerAdapter<GameMsg>(new HeartAbleConnectionListener<GameMsg>(){
			@Override
			public void connectionOpened(JConnection connection) {//连接打开
			}
			@Override
			public void connectionClosed(JConnection connection) {//连接关闭
			}
			@Override
			public JConnectionFactory getConnectionFactory() {
				return new NettyConnectionFactory();
			}
			@Override
			protected void sendHeartReq(JConnection connection) {//发送心跳
			}
			@Override
			protected void sendHeartRsp(JConnection connection) {//回复心跳
			}
			@Override
			protected boolean isHeartReq(GameMsg msg) {//是否是心跳消息
				return false;
			}
			@Override
			protected void doMessageArrived(JConnection conn, GameMsg msg) {//处理消息 
				int msgcode=msg.getCode();
//				qe.execute(mainQueue,ScripManger.getInstance.buildScript(msgcode,conn,msg));//热更新脚本管理器取出消息对应处理逻辑并放入队列执行
			}
		});
		ChannelHandler initHandler=new ChannelInitializer<NioSocketChannel>() {
			protected void initChannel(NioSocketChannel ch) throws Exception {
//				ch.pipeline().addLast(new Decoder());
//				ch.pipeline().addLast(new Encoder());
				ch.pipeline().addLast(logicHandler);
			}
		};
		new NettyServer(new NettyServerConfig(2,3), new InetSocketAddress("0.0.0.0", 20),initHandler).start();
	}
}
