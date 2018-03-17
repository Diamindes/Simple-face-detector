package simpleDetector.openCvModule;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.util.concurrent.TimeUnit;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.face.EigenFaceRecognizer;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;
import simpleDetector.UIModule.Window;


public class OpenCv implements VideoModule{
    private boolean cumOn = false;
    private JPanel outputWindow;
    private Window coreGUI;
    private JTextArea log1, log2;
    private VideoCapture cam;
    private Detector detector;
    private Recognizer recognizer;
    private Mat[] faces;
    
    
    @Override
    public void configure(Window coreGUI, JTextArea log1, JTextArea log2){
        this.coreGUI = coreGUI;
        this.log1 = log1;
        this.log2 = log2;
        cam = new VideoCapture(0);
        detector = new Detector();
        EigenFaceRecognizer eigen = EigenFaceRecognizer.create();
        eigen.setThreshold(10000);
        recognizer = new Recognizer("./src/main/resourсes/faces", eigen);
    }
    
    public void setImage(Mat frame){
        int channels = frame.channels();
        Imgproc.resize(frame, frame, new Size(outputWindow.getHeight(),outputWindow.getWidth()));
        
        
        int bufSize = channels*frame.cols()*frame.rows();
        byte[] buffer = new byte[bufSize];
        frame.get(0, 0, buffer);
        BufferedImage image = new BufferedImage(frame.cols(), frame.rows(),
                                                    BufferedImage.TYPE_3BYTE_BGR);
        final byte[] pixels = ( (DataBufferByte)image.getRaster().getDataBuffer() ).getData();
        System.arraycopy(buffer, 0, pixels, 0, buffer.length);

        outputWindow.getGraphics().drawImage(image, 0, 0, outputWindow);
        
    }
    
    @Override
    public void startVideo(boolean recOn) {
        cumOn=true;
        
        Mat frame = new Mat();
        if(cam.isOpened()){
            while(cumOn){
                cam.read(frame);
               
                if(recOn)
                    faces = detector.findFace(frame, recognizer, log1, log2);
                else
                    faces = detector.findFace(frame, null, log1, log2);
                
                setImage(frame);
                coreGUI.pack(); 
            }
        }
        
    }
    
    @Override
    public void stopVideo() {
        cumOn = false;   
    }


    @Override
    public void makePhoto(String userName, int counter) {
        
        String paths = "./src/main/resourсes/faces/";
        File dir = new File(paths + userName);
        if(!dir.exists())
                dir.mkdir();
        
        try {TimeUnit.SECONDS.sleep(1);} catch (InterruptedException ex) {}
        for(int i=0;i<counter;i++){
            try {TimeUnit.MILLISECONDS.sleep(500);} catch (InterruptedException ex) {}
            detector.setIsMakingPhoto(true, paths +userName+"/face"+i+".jpg");
        }
        detector.setIsMakingPhoto(false, null);      
    }
    
    @Override
    public void setOuputVideoWindow(JPanel out) {
       outputWindow = out;
    }

    @Override
    public void train() {
        recognizer.configure(coreGUI.getRecognizerLog());
    }
}
