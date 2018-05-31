
package simpleDetector.openCvModule;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import simpleDetector.UIModule.Window;

public interface VideoModule {
    public void addUser(String userName, JLabel output);
    public void setOuputWindow(JPanel p);
    
    public void startVideo();
    public void stopVideo();
   
    public void train();
    
    public void configure(Window coreGUI, Settings settings, JTextArea... logs);
    
    public void setBG(boolean turn);
   
}
