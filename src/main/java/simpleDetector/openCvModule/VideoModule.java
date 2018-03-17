
package simpleDetector.openCvModule;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import simpleDetector.UIModule.Window;

public interface VideoModule {
    public void makePhoto(String userName, int counter);
    public void setOuputVideoWindow(JPanel p);
    
    public void startVideo(boolean recOn);
    public void stopVideo();
   
    public void train();
    
    public void configure(Window coreGUI, JTextArea log, JTextArea log2);
    

}
