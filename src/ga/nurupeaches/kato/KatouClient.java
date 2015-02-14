package ga.nurupeaches.kato;

import ga.nurupeaches.kato.utils.ArrayUtils;

import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.logging.Logger;

/**
 * Root class for the Kato client.
 */
public class KatouClient {

	/**
	 * Global logger.
	 */
	public static final Logger LOGGER = Logger.getLogger("Katou");

	public static void main(String[] arguments){
		if(!ArrayUtils.contains(arguments, "-gui")){
			System.out.println("KatouCLI - Hello, world!");
		} else {
			System.out.println("KatouGUI - Hello, world!");
			System.out.println("GUI is unstable!");
		}
	}

	/**
	 * Constrcutor for Katou. Currently nothing.
	 */
	public KatouClient(){

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