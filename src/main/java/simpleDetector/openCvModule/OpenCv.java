package simpleDetector.openCvModule;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
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
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;
import simpleDetector.UIModule.Window;

public class OpenCv implements VideoModule {

    private JPanel outputWindow;
    private Window coreGUI;

    private VideoCapture cam;
    private boolean cumOn = false;
    private Detector detector;
    private Settings settings;

    private Statistics logSystem;

    @Override
    public void configure(Window coreGUI,  Settings settings, JTextArea... log) {
        this.coreGUI = coreGUI;
        //--------------------
        logSystem = new Statistics();
        logSystem.addLog("objLog", log[0]);
        logSystem.addLog("recLog", log[1]);
        logSystem.addLog("regLog", log[2]);

        //--------------------
        this.settings = settings;
        //"http://78.232.164.211:8084/mjpg/video.mjpg");
        //http://208.72.70.172/mjpg/1/video.mjpg?timestamp=1526243215700
        // http://88.190.98.55/mjpg/video.mjpg
        //http://81.149.56.38:8081/mjpg/video.mjpg
        cam = new VideoCapture(0);//"/home/diamind/NetBeansProjects/Simple-face-detector/src/main/resourсes/files/opencv/videos/video6");
        detector = new Detector(logSystem, settings);
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
       
        if (cam.isOpened()) {
            for (int i = 0; i < settings.SystemHoldRate; ++i) {
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

                detector.findObject(frame, contours);

                setImage(frame);

                HighGui.imshow("Thresh", trashFrame);
                //HighGui.imshow("Frame Delta", frameDiff);
                //HighGui.imshow("Background", background);
                 HighGui.waitKey(1);
                coreGUI.pack();
                contours.clear();
            }
        }else{
            System.out.println("fail");
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
    }

    @Override
    public void train() {
        detector.train();
    }
}
