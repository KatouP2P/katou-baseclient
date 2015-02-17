package ga.nurupeaches.kato;

import ga.nurupeaches.kato.network.manager.NetworkManager;
import ga.nurupeaches.kato.network.manager.tcp.TCPNetworkManager;
import ga.nurupeaches.kato.network.packets.Packet;
import ga.nurupeaches.kato.network.packets.PacketVersion;
import ga.nurupeaches.kato.utils.ArrayUtils;

import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.logging.Logger;

/**
 * Root class for the Kato client.
 */
public class KatouClient {

	/**
	 * Global logger.
	 */
	public static final Logger LOGGER = Logger.getLogger("Katou");

	// PURELY DECLARED HERE FOR DEBUGGING PURPOSES
	public final NetworkManager NETWORK_MANAGER;

	public static void main(String[] arguments) throws Throwable {
		if(ArrayUtils.contains(arguments, "-gui")){
			System.out.println("KatouGUI - Hello, world!");
			System.out.println("GUI is unstable!");

			KatouClient client = new KatouClient();
			client.initGUI();
		} else {
			System.out.println("KatouCLI - Hello, world!");
		}
	}

	/**
	 * Constrcutor for Katou. Currently nothing.
	 */
	public KatouClient() throws Throwable{
		// The port number was decided merely by adding the letters in "katou" as their index in the alphabet
		// followed by slapping on two 0's to get it over the first 1024 ports restriction.
		NETWORK_MANAGER = new TCPNetworkManager(6800);
	}

	/**
	 * Initialize the GUI.
	 * TODO: Implement the GUI later; focus on the actual P2P part.
	 */
	private void initGUI(){
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

		Frame frame = new Frame("Katou - Plain P2P!");
		frame.addWindowListener(new BaseFrameListener());
		frame.setSize((int)screenSize.getWidth()/3, (int)screenSize.getHeight()/3);
		frame.setVisible(true);


		Button button = new Button("click to send data");
		button.addActionListener((event) ->{

			try{
				SocketChannel channel = SocketChannel.open();
				channel.connect(new InetSocketAddress(InetAddress.getLocalHost(), 6800));
				Packet packet = new PacketVersion("r u hi edition");
				ByteBuffer buffer = ByteBuffer.allocate(packet.size() + 2193);
				buffer.put(packet.getID());
				packet.write(buffer);
				channel.write(buffer);
				channel.read(ByteBuffer.allocate(999));
			} catch (IOException e){
				e.printStackTrace();
			}

		});
		frame.add(button);
		while(true){
			NETWORK_MANAGER.tick();
		}
	}

	/**
	 * The base frame listener. Only handles the frame closing and basic painting.
	 */
	private class BaseFrameListener implements WindowListener {

		@Override
		public void windowClosing(WindowEvent event){
			System.exit(0); // 0 is successful.
		}

		@Override
		public void windowActivated(WindowEvent e){}

		@Override
		public void windowClosed(WindowEvent e){}

		@Override
		public void windowDeactivated(WindowEvent e){}

		@Override
		public void windowDeiconified(WindowEvent e){}

		@Override
		public void windowIconified(WindowEvent e){}

		@Override
		public void windowOpened(WindowEvent e){}

	}

}