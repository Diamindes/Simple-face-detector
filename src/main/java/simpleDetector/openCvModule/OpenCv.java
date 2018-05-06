package simpleDetector.openCvModule;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
<<<<<<< HEAD
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.face.EigenFaceRecognizer;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
=======
import java.util.concurrent.TimeUnit;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.face.EigenFaceRecognizer;
>>>>>>> dcc377ea1d0adc6afec258daad8837c435476e29
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;
import simpleDetector.UIModule.Window;

<<<<<<< HEAD
public class OpenCv implements VideoModule {

    private JPanel outputWindow;
    private Window coreGUI;

    private VideoCapture cam;
    private boolean cumOn = false;
    private Detector detector;

    private Statistics logSystem;

    @Override
    public void configure(Window coreGUI, JTextArea... log) {
        this.coreGUI = coreGUI;
        //--------------------
        logSystem = new Statistics();
        logSystem.addLog("objLog", log[0]);
        logSystem.addLog("recLog", log[1]);
        logSystem.addLog("regLog", log[2]);

        //--------------------
        cam = new VideoCapture("/home/diamind/NetBeansProjects/Simple-face-detector/src/main/resourсes/files/opencv/videos/video6");
        detector = new Detector(logSystem);
    }

    private void setImage(Mat frame) {
        int channels = frame.channels();
        Imgproc.resize(frame, frame, new Size(outputWindow.getHeight(), outputWindow.getWidth()));

        int bufSize = channels * frame.cols() * frame.rows();
        byte[] buffer = new byte[bufSize];
        frame.get(0, 0, buffer);
        BufferedImage image = new BufferedImage(frame.cols(), frame.rows(),
                BufferedImage.TYPE_3BYTE_BGR);
        final byte[] pixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        System.arraycopy(buffer, 0, pixels, 0, buffer.length);

        outputWindow.getGraphics().drawImage(image, 0, 0, outputWindow);

    }

    @Override
    public void startVideo() {
        cumOn = true;

        Mat frame = new Mat();
        Mat grayFrame = new Mat();
        Mat frameDiff = new Mat();
        Mat trashFrame = new Mat();
        Mat background = null;
        List<MatOfPoint> contours = new ArrayList<>();
        int minSize = 400;

        if (cam.isOpened()) {
            for (int i = 0; i < 10; ++i) {
                cam.read(frame);
            }

            while (cumOn) {
                cam.read(frame);

                //  Imgproc.resize(frame, frame, new Size(500, frame.height()));
                Imgproc.cvtColor(frame, grayFrame, Imgproc.COLOR_BGR2GRAY);
                Imgproc.GaussianBlur(grayFrame, grayFrame, new Size(21, 21), 0);

                if (background == null) {
                    background = grayFrame.clone();
                    continue;
                }

                Mat kernal = Imgproc.getStructuringElement(Imgproc.CV_SHAPE_ELLIPSE, new Size(7, 7));
                Mat hierarchy = new Mat();

                Core.absdiff(grayFrame, background, frameDiff);
                Imgproc.threshold(frameDiff, trashFrame, 25, 255, Imgproc.THRESH_BINARY);
                Imgproc.dilate(trashFrame, trashFrame, kernal, new Point(-1, -1), 3);
                Imgproc.findContours(trashFrame.clone(), contours, hierarchy,
                        Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

                detector.findObject(frame, contours, minSize);

                setImage(frame);

                //HighGui.imshow("Thresh", trashFrame);
                //HighGui.imshow("Frame Delta", frameDiff);
                //HighGui.imshow("Background", background);
                // HighGui.waitKey(1);
                coreGUI.pack();
                contours.clear();
            }
        }

    }

    @Override
    public void stopVideo() {
        cumOn = false;
    }

    @Override
    public void addUser(String userName, JLabel output) { //TODO

        String paths = "./src/main/resourсes/faces/";
        File dir = new File(paths + userName);
        if (!dir.exists()) {
            dir.mkdir();
        }

        int counter = 30;
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException ex) {
        }
        for (int i = 0; i < counter; i++) {
            try {
                TimeUnit.MILLISECONDS.sleep(500);
            } catch (InterruptedException ex) {
            }
            detector.setIsMakingPhoto(true, paths + userName + "/face" + i);
            output.setText((i + 1) + "/" + counter + " photos done");
        }
        detector.setIsMakingPhoto(false, null);
    }

    @Override
    public void setOuputWindow(JPanel out) {
        outputWindow = out;
=======

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
>>>>>>> dcc377ea1d0adc6afec258daad8837c435476e29
    }

    @Override
    public void train() {
<<<<<<< HEAD
        detector.train();
=======
        recognizer.configure(coreGUI.getRecognizerLog());
>>>>>>> dcc377ea1d0adc6afec258daad8837c435476e29
    }
}
