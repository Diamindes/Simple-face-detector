package simpleDetector.openCvModule;

import java.time.LocalDateTime;
<<<<<<< HEAD
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JTextArea;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfInt;
import org.opencv.core.MatOfPoint;
=======
import java.util.HashMap;
import java.util.Map;
import javax.swing.JTextArea;
import org.opencv.core.Mat;
>>>>>>> dcc377ea1d0adc6afec258daad8837c435476e29
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
<<<<<<< HEAD
import org.opencv.core.Size;
import org.opencv.core.Rect2d;
import org.opencv.face.EigenFaceRecognizer;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.objdetect.Objdetect;
import org.opencv.videoio.VideoCapture;

public class Detector {

    //----------------------- TODO
    private final String front
            = "./src/main/resourсes/files/opencv/front.xml";
    private final String eyes
            = "./src/main/resourсes/files/opencv/eyes.xml";

    private final CascadeClassifier frontDetector;
    CascadeClassifier finder;
    //-----------------------

    private Recognizer recognizer;
    private boolean isTrained = false;
    private Statistics logSystem;
    private Contours ctrlSystem = new Contours();
    private boolean isMakingPhoto = false;
    private String outputPhotoPath;

    public Detector(Statistics logSystem) {

        this.logSystem = logSystem;
        //TODO
        frontDetector = new CascadeClassifier(front);

        String path2 = "./src/main/resourсes/files/opencv/cascades/lbpcascade_frontalface_improved.xml";
        finder = new CascadeClassifier(path2);

        //TODO: Настройка распознавания
        recognizer = new Recognizer("./src/main/resourсes/faces",
                EigenFaceRecognizer.create(80, 20000));
       // isTrained = recognizer.status();
    }

    public void train() {
        isTrained = false;
        recognizer.configure(logSystem.getLog("regLog")); //TODO
        isTrained = true;
    }

    public void findObject(Mat frame, List<MatOfPoint> contours, int minSize) {

        String status = "Empty";
        int counter = 0;

        for (MatOfPoint contour : contours) {
            if ((contour.width() * contour.height()) < minSize) {
                continue;
            }

            Rect contourArea = Imgproc.boundingRect(contour);
            contourArea.height /= 2;
            Contour obj = ctrlSystem.handleContour(contourArea);
 
            findFace(frontDetector, frame, obj, 1.05, 50);

            counter += 1;
            status = "Under attack!!";
        }
        
        ctrlSystem.makeDMG();
        Imgproc.putText(frame, "Room status: " + status, new Point(10, 20), 2, 0.5, new Scalar(255, 0, 0), 2);
        Imgproc.putText(frame, "Counter : " + counter, new Point(10, 50), 2, 0.5, new Scalar(255, 0, 0), 2);
        Imgproc.putText(frame, "Face counter : " + logSystem.counter, new Point(10, 80), 2, 0.5, new Scalar(255, 0, 0), 2);
    }

    private void findFace(CascadeClassifier detector, Mat frame, Contour contour,
            double scale, int acc) {

        Mat grayFrame = new Mat();
        Rect object = contour.getArea();
        
        Imgproc.cvtColor(frame, grayFrame, Imgproc.COLOR_BGR2GRAY);
        Mat scaledFace = grayFrame.submat(object);
        Imgproc.resize(scaledFace, scaledFace, new Size(scaledFace.width() * 2, scaledFace.height() * 2));

        MatOfRect targets = new MatOfRect();
        detector.detectMultiScale(scaledFace, targets, scale, acc,
                Objdetect.CASCADE_DO_CANNY_PRUNING,
                new Size(30, 30),
                new Size(frame.size().width, frame.size().height));

        Imgproc.rectangle(frame, new Point(object.x, object.y),
                 new Point(object.x + object.width, object.y + object.height),
                new Scalar(255, 255, 0), 3);

        String[] mark = LocalDateTime.now().toString().split("T");
        logSystem.getLog("objLog").append("Timestamp: " + mark[0] + " " + mark[1] + " - Movement detected\n");

        String objectName = contour.getObjName();
        for (Rect face : targets.toArray()) {
            
            
            
            if (isMakingPhoto) {
                makePhoto(frame.submat(face));
                objectName = "";
                isMakingPhoto = false;
            }

            if (isTrained) {
                Rect recFace = face.clone();
                recFace.x = object.x + face.x / 2;
                recFace.y = object.y + face.y / 2;
                recFace.width /=2;
                recFace.height /=2; 
                        
                
                objectName = recognizer.recognize(grayFrame.submat(recFace));
                contour.setObjName(objectName);
                JTextArea log = logSystem.getLog("recLog");
                log.append("Timestamp: " + mark[0] + " " + mark[1] + "    Face: " + objectName + "\n");
            }

            Imgproc.rectangle(frame, new Point(object.x + face.x / 2, object.y + face.y / 2),
                     new Point(object.x + face.x / 2 + face.width / 2, object.y + face.y / 2 + face.height / 2),
                    new Scalar(0, 0, 255), 1);
            logSystem.counter += 1; 
        }
        Imgproc.putText(frame, objectName, new Point(object.x, object.y + object.height + 25),
                    2, 1, new Scalar(255, 255, 0), 2);
    }

    public void setIsMakingPhoto(boolean bool, String path) {
        isMakingPhoto = bool;
        outputPhotoPath = path;
    }

    private void makePhoto(Mat face) {

        // Imgproc.cvtColor(face, face, Imgproc.COLOR_BGR2GRAY);
        //face = face.submat((int)(face.height()*0.1), (int)(face.height()*0.9),
        //        (int)(face.width()*0.1), (int)(face.width()*0.9));
        Imgcodecs.imwrite(outputPhotoPath + ".jpg", face);
        Size size = face.size();

        for (int x = 0; x < size.width / 2; x++) {
            for (int y = 0; y < size.height; y++) {
                double point1[] = face.get(y, x);
                double point2[] = face.get(y, (int) size.width - x - 1);

                face.put(y, x, point2);
                face.put(y, (int) size.width - x - 1, point1);
            }
        }
        Imgcodecs.imwrite(outputPhotoPath + "_r" + ".jpg", face);

    }
=======
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

public class Detector{
    private final String front = 
            "./src/main/resourсes/files/opencv/front.xml";
   
    private final CascadeClassifier frontDetector;
    private final Map<String, Integer> statistic = new HashMap<>(); 
    private boolean isMakingPhoto = false;
    private String outputPhotoPath;
    
    public Detector(){
       frontDetector = new CascadeClassifier(front); 
    }
    
     public Mat[] findFace(Mat frame, Recognizer rec, JTextArea log1, JTextArea log2){
       return doDetect(frontDetector, frame, new Scalar(0,255,0), rec, log1, log2); 
     }
     
     private int i=0;
     private int index = 0;
     private Mat[] doDetect(CascadeClassifier detector, Mat frame
             , Scalar color, Recognizer rec, JTextArea log1, JTextArea log2){
         MatOfRect faceFinds = new MatOfRect();
         detector.detectMultiScale(frame, faceFinds);
         Mat[] faces = new Mat[faceFinds.toArray().length];
         int faceIndex = 0;

         for(Rect face: faceFinds.toArray()){
             
             faces[faceIndex++] = frame.clone().submat(face);
             Imgproc.rectangle(frame, new Point(face.x,face.y)
                     , new Point(face.x+face.width, face.y+face.height), 
                      color);
             
             if(isMakingPhoto){
                 makePhoto(faces[0]);
                 isMakingPhoto = false;
             }
             
             if(rec!=null){
                 
                String predict = rec.recognize(faces[faceIndex-1] );  
                //---
                int num = 20;
                //---
                if(index < num){
                    if( statistic.containsKey(predict) )
                        statistic.put(predict, statistic.get(predict) + 1 );
                    else
                        statistic.put(predict, 1 );

                    index += 1;
                }else{
                    int max = 0;
                    String userName ="";
                    for(Map.Entry<String, Integer> pair : statistic.entrySet())
                        if(max < pair.getValue()){
                            max = pair.getValue();
                            userName = pair.getKey();
                        }
                    
                    log1.setText("");
                    log1.append(" Face:   " + userName + " Chance:  " +(max*100/num)+"%\n");
                    String[] mark =LocalDateTime.now().toString().split("T");
                    log2.append( mark[0] +" "+ mark[1]+ " | "+userName+"\n");
                    index = 0;
                    statistic.clear();
                }
  
             }
         }
         return faces;
     }
     
     public void setIsMakingPhoto(boolean bool, String path){
         isMakingPhoto = bool;
         outputPhotoPath = path;
     }
     
     private void makePhoto(Mat face){
                
                Imgcodecs.imwrite(outputPhotoPath, face); 
     }
>>>>>>> dcc377ea1d0adc6afec258daad8837c435476e29
}
