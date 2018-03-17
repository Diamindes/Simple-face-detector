package simpleDetector.openCvModule;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JTextArea;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

public class Detector{
    private final String front = 
            "/home/diamind/Programming/Projects/Java/Local/OpenCV/srcOpenCV/front.xml";;
   
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
}
