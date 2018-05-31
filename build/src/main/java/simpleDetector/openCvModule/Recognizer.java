package simpleDetector.openCvModule;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JTextArea;
import org.opencv.core.Mat;
import org.opencv.core.MatOfInt;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.face.FaceRecognizer;
import org.opencv.face.LBPHFaceRecognizer;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.objdetect.Objdetect;

public class Recognizer {

    public Recognizer(String path, LBPHFaceRecognizer faceRec, Settings settings) {
        this.path = path;
        this.settings = settings;
        this.faceRec = faceRec;
        righteyeDetector = new CascadeClassifier(cascadePath_right);
        lefteyeDetector = new CascadeClassifier(cascadePath_left);
        
        faceRec.setThreshold(200);
    }

    private Settings settings;
    private final String path;
    private final LBPHFaceRecognizer faceRec;
    private JTextArea log;
    private boolean isTrained = false;

    private CascadeClassifier righteyeDetector;
    private CascadeClassifier lefteyeDetector;
    private String cascadePath_right = "./src/main/resourсes/files/opencv/cascades/haarcascade_righteye_2splits.xml";
    private String cascadePath_left = "./src/main/resourсes/files/opencv/cascades/haarcascade_lefteye_2splits.xml";

    private final Map<Integer, String> regPersons = new HashMap<>();
    private int numOfImgs;

    public void configure(JTextArea log) {
        this.log = log;

        createListOfPhotos();
        readData();
             
    }

    private void createListOfPhotos() {
        File root = new File(path);
        File listOfPhotos = new File(path + "/paths.txt");
        FileWriter out;

        if (listOfPhotos.exists()) {
            listOfPhotos.delete();
        }
        try {
            listOfPhotos.createNewFile();
            out = new FileWriter(listOfPhotos);

            StringBuilder output = new StringBuilder();
            int labelIndex = 0;
            numOfImgs = 0;
            for (File dir : root.listFiles((File pathname) -> {
                return pathname.isDirectory();
            })) {
                for (File photo : dir.listFiles()) {
                    output.append(photo.getAbsolutePath())
                            .append(";")
                            .append(labelIndex)
                            .append(";")
                            .append(dir.getName())
                            .append("\n");
                    numOfImgs += 1;
                }
                labelIndex += 1;
            }
            out.write(output.toString());
            out.flush();
        } catch (IOException ex) {
        }
    }

    private void readData() {

        try {
            
            BufferedReader in = new BufferedReader(new FileReader(new File(path + "/paths.txt")));

            int[] labels = new int[numOfImgs];
            List<Mat> images = new ArrayList<>();

            regPersons.clear();
            String line;
            String separator = ";";

            for (int i = 0; i < numOfImgs; ++i) {
                line = in.readLine();
                Mat m = Imgcodecs.imread(line.split(separator)[0], Imgcodecs.IMREAD_GRAYSCALE);

                Imgproc.resize(m, m, new Size(settings.RecognizerFaceWidth,
                        settings.RecognizerFaceHeight));
                images.add(m);

                labels[i] = Integer.parseInt(line.split(separator)[1]);

                if (!regPersons.containsKey(labels[i])) {
                    regPersons.put(labels[i], line.split(separator)[2]);
                }
            }

            Mat m = new MatOfInt(labels);
            faceRec.train(images, m);
            System.out.println("Train complete!");

            int i = 0;
            if (log != null) {
                log.setText("");
                for (String person : regPersons.values()) {
                    log.append(i++ + ") " + person + "\n");
                }
            }

        } catch (FileNotFoundException ex) {
            Logger.getLogger(Recognizer.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("File not found!");
        } catch (IOException ex) {
            Logger.getLogger(Recognizer.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Input error!");
        }

    }

    public Mat processFrame(Mat frame) {

        //----------------------------
       
        Imgcodecs.imwrite("./bufImg.jpg", frame);
        try {
            Runtime.getRuntime().exec("python points.py bufImg.jpg bufImg2.jpg");

        } catch (IOException ex) {
             System.out.println("Python fail!");
        }
        Mat frame2 = Imgcodecs.imread("./bufImg2.jpg");
        //-----------------------------
        if((frame2 == null)||(frame2.empty()))
            frame2=frame;
        
        Imgproc.cvtColor(frame2, frame2, Imgproc.COLOR_RGB2GRAY);
        Imgproc.resize(frame2, frame2, new Size(92,112)); 
        return frame2;
    }

    public String recognize(Mat frame) {

        int[] label = new int[10];
        double[] conf = new double[10];
         
        faceRec.predict(processFrame(frame), label, conf);


        if (regPersons.containsKey(label[0])) {
            return regPersons.get(label[0]);
        } else {
            return "Annonimus!";
        }
    }
}
