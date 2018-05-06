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
<<<<<<< HEAD
<<<<<<< HEAD
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

    public Recognizer(String path, FaceRecognizer faceRec) {
        this.path = path;
        this.faceRec = faceRec;
        righteyeDetector = new CascadeClassifier(cascadePath_right);
        lefteyeDetector = new CascadeClassifier(cascadePath_left);
        
        /*File trainedData = new File("./src/main/resourсes/files/opencv/recData");
        if(trainedData.exists()){
            faceRec.read(trainedData.getAbsolutePath());
            isTrained = true;
        }*/
    }

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
                Imgproc.resize(m, m, new Size(100, 100));
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

=======
=======
>>>>>>> dcc377ea1d0adc6afec258daad8837c435476e29
import org.opencv.core.Size;
import org.opencv.face.FaceRecognizer;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

public class Recognizer{
    public Recognizer(String path, FaceRecognizer faceRec){
        this.path = path;
        this.faceRec = faceRec;
    }
    
    private final String path;
    private final Map<Integer, String> regPersons = new HashMap<>();
    private final FaceRecognizer faceRec;
    private final List<Mat> images = new ArrayList<>();
    private JTextArea log;
    private int[] labels;
    private int numOfImgs;
   
      
    public void configure(JTextArea log){
        this.log = log;
        createListOfPhotos();
        readData();  
    }
    
    private void createListOfPhotos(){
        File root = new File(path);
        File listOfPhotos = new File(path + "/paths.txt");
        FileWriter out;
        
        if(listOfPhotos.exists())
            listOfPhotos.delete();
        try {   
            listOfPhotos.createNewFile();
            out = new  FileWriter(listOfPhotos);
        
         
            StringBuilder output = new StringBuilder();
            int labelIndex = 0;
            numOfImgs = 0;
            for(File dir : root.listFiles((File pathname) -> { return pathname.isDirectory();})){
                for(File photo : dir.listFiles()){
                    output.append(photo.getAbsolutePath())
                          .append( ";")
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
        } catch (IOException ex) {}
    }
    
    private void readData(){
        
        try {
            BufferedReader in = new BufferedReader(new FileReader(new File(path + "/paths.txt")));
            
            labels = new int[numOfImgs];
            images.clear();
            regPersons.clear();
            String line;
            String separator = ";";
            for(int i=0;i<numOfImgs;++i){
                    line = in.readLine();
                    Mat m = Imgcodecs.imread( line.split(separator)[0], Imgcodecs.IMREAD_GRAYSCALE);
                    Imgproc.resize(m, m, new Size(200, 200));
                    images.add(m);
                    
                    labels[i] = Integer.parseInt(line.split(separator)[1]) ;
                    
                    if(!regPersons.containsKey(labels[i]))
                        regPersons.put(labels[i], line.split(separator)[2]);
                } 
            
            Mat m = new MatOfInt(labels);
            faceRec.train(images, m);
            System.out.println("Train complete!");
            
            int i = 0;
            for(String person : regPersons.values())
                log.append(i++ + ") " + person + "\n");
            
<<<<<<< HEAD
>>>>>>> dcc377ea1d0adc6afec258daad8837c435476e29
=======
>>>>>>> dcc377ea1d0adc6afec258daad8837c435476e29
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Recognizer.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("File not found!");
        } catch (IOException ex) {
            Logger.getLogger(Recognizer.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Input error!");
        }
<<<<<<< HEAD
<<<<<<< HEAD

    }

    private Mat processFrame(Mat frame) {

        //----------------------------
      
        Imgproc.resize(frame, frame, new Size(frame.height()*6, frame.width()*6));
        Mat newFace = frame.submat(0, frame.height()*2/3, 0, frame.width()/2);
        Mat newFace2 = frame.submat(0, frame.height()*2/3, frame.width()/2, frame.width());
        //Imgproc.equalizeHist(frame, frame);
        detectEyes(null, newFace);
        detectEyes(null, newFace2);
        //-----TODO: Обработка
        HighGui.imshow("Left", newFace);
        HighGui.imshow("Right", newFace2);
        HighGui.waitKey(100);
        Imgproc.resize(frame, frame, new Size(100, 100));
        //-----------------------------
        return frame;
    }

    public String recognize(Mat frame) {

        int[] label = new int[10];
        double[] conf = new double[10];
        faceRec.predict(processFrame(frame), label, conf);

        System.out.println("label - " + label + " : " + conf[0]);

        if (regPersons.containsKey(label[0])) {
            return regPersons.get(label[0]) + " - " + conf[0];
        } else {
            return "Annonimus!";
        }
    }

    private void detectEyes(CascadeClassifier detector, Mat face) {
    
        MatOfRect eyes = new MatOfRect();
        righteyeDetector.detectMultiScale(face, eyes, 1.03, 5,
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
=======
=======
>>>>>>> dcc377ea1d0adc6afec258daad8837c435476e29
        
        
    }
    
    
    public String recognize(Mat frame){
        
        int[] label = new int[10];
        double[] conf = new double[10];
        Imgproc.cvtColor(frame, frame, Imgproc.COLOR_BGR2GRAY);
        Imgproc.resize(frame, frame, new Size(200,200));
        
        faceRec.predict( frame, label, conf);
        
        if(regPersons.containsKey(label[0]))
            return regPersons.get(label[0]);
        else
            return "Annonimus!";
    }
    
<<<<<<< HEAD
}
>>>>>>> dcc377ea1d0adc6afec258daad8837c435476e29
=======
}
>>>>>>> dcc377ea1d0adc6afec258daad8837c435476e29
