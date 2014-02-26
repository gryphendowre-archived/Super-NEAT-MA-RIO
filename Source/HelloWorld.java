import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JFrame;

import com.mojang.mario.MarioComponent;



public class HelloWorld {

    public static void main(String[] args) {
        System.out.println("Hello, World");
        jumpStartMario();
    }
    
    /**
     * This method mimics the FrameLauncher.java class in starting the Mario Program.
     */
    public static void jumpStartMario()
    {
    	MarioComponent mario = new MarioComponent(640, 480);
        JFrame frame = new JFrame("Mario Test");
        frame.setContentPane(mario);
        frame.pack();
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setLocation((screenSize.width-frame.getWidth())/2, (screenSize.height-frame.getHeight())/2);
        
        frame.setVisible(true);
        
        mario.start();
    }
    

}