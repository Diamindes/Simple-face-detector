
package simpleDetector.openCvModule;

import javax.swing.JFrame;
<<<<<<< HEAD
import javax.swing.JLabel;
=======
>>>>>>> dcc377ea1d0adc6afec258daad8837c435476e29
import javax.swing.JPanel;
import javax.swing.JTextArea;
import simpleDetector.UIModule.Window;

public interface VideoModule {
<<<<<<< HEAD
    public void addUser(String userName, JLabel output);
    public void setOuputWindow(JPanel p);
    
    public void startVideo();
=======
    public void makePhoto(String userName, int counter);
    public void setOuputVideoWindow(JPanel p);
    
    public void startVideo(boolean recOn);
>>>>>>> dcc377ea1d0adc6afec258daad8837c435476e29
    public void stopVideo();
   
    public void train();
    
<<<<<<< HEAD
    public void configure(Window coreGUI, JTextArea... logs);
=======
    public void configure(Window coreGUI, JTextArea log, JTextArea log2);
>>>>>>> dcc377ea1d0adc6afec258daad8837c435476e29
    

}
