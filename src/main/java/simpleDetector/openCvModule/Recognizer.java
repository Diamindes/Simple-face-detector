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
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.objdetect.Objdetect;

public class Recognizer {

    public Recognizer(String path, FaceRecognizer faceRec, Settings settings) {
        this.path = path;
        this.settings = settings;
        this.faceRec = faceRec;
        righteyeDetector = new CascadeClassifier(cascadePath_right);
        lefteyeDetector = new CascadeClassifier(cascadePath_left);
        
        /*File trainedData = new File("./src/main/resourсes/files/opencv/recData");
        if(trainedData.exists()){
            faceRec.read(trainedData.getAbsolutePath());
            isTrained = true;
        }*/
    }
    
    private Settings settings;
    private final String path;
    private final FaceRecognizer faceRec;
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

        /*File trainedData = new File("./src/main/resourсes/files/opencv/recData");
        if(trainedData.exists())
            trainedData.delete();
       
        faceRec.save(trainedData.getAbsolutePath());*/
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

    private Mat processFrame(Mat frame) {

        //----------------------------
        Mat frame2 = frame.clone();
        Imgcodecs.imwrite("./faces.jpg", frame);
          try {
            Runtime.getRuntime().exec("convert -resize 600% -quality 90 ./faces.jpg ./faces2.jpg");
        } catch (IOException ex) {
            Logger.getLogger(Recognizer.class.getName()).log(Level.SEVERE, null, ex);
        }
        frame = Imgcodecs.imread("./faces2.jpg");
        
        Mat newFace = frame.submat(0, frame.height()*2/3, 0, frame.width()/2);
        Mat newFace2 = frame.submat(0, frame.height()*2/3, frame.width()/2, frame.width());   
        
        
        //Imgproc.equalizeHist(frame, frame);
        detectEyes(null, newFace);
        detectEyes(null, newFace2);
        //-----TODO: Обработка
        HighGui.imshow("Left(regular)", newFace);
        HighGui.imshow("Right(concole)", newFace2);
        HighGui.waitKey(100);
        Imgproc.resize(frame2, frame2, new Size(settings.RecognizerFaceWidth,
                settings.RecognizerFaceHeight));
        //-----------------------------
        return frame2;
    }

    public String recognize(Mat frame) {

        int[] label = new int[10];
        double[] conf = new double[10];
        
        faceRec.predict(processFrame(frame), label, conf);

        System.out.println("label - "+ frame.size() +"  "+ label + " : " + conf[0]);
        
        if(conf[0] < 1000)
            label[0] = -1;
        
        if (regPersons.containsKey(label[0])) {
            return regPersons.get(label[0]) + " - " + conf[0];
        } else {
            return "Annonimus!";
        }
    }

    private void detectEyes(CascadeClassifier detector, Mat face) {
    
        MatOfRect eyes = new MatOfRect();
        righteyeDetector.detectMultiScale(face, eyes, 1.03, 3,
                Objdetect.CASCADE_DO_CANNY_PRUNING,
                new Size(5, 5),
                new Size(face.size().width, face.size().height));
        
   
        for (Rect eye : eyes.toArray()) {
            Imgproc.rectangle(face, new Point(eye.x, eye.y),
                     new Point(eye.x + eye.width, eye.y + eye.height),
                    new Scalar(255,0,0),1);
        }
        
        

    }

    /*
    public boolean status() {
        return isTrained;
    }*/
}
