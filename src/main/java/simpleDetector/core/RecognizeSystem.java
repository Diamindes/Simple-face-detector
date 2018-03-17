package simpleDetector.core;

import java.awt.EventQueue;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import simpleDetector.openCvModule.Detector;
import simpleDetector.openCvModule.Recognizer;

import org.opencv.imgproc.*;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfInt;
import org.opencv.core.Size;
import org.opencv.face.LBPHFaceRecognizer;
import org.opencv.face.EigenFaceRecognizer;
import org.opencv.face.FaceRecognizer;
import org.opencv.face.FisherFaceRecognizer;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.videoio.VideoCapture;
import simpleDetector.UIModule.Window;
import simpleDetector.openCvModule.OpenCv;
import simpleDetector.openCvModule.VideoModule;


public class RecognizeSystem {

   
   static{  System.loadLibrary(Core.NATIVE_LIBRARY_NAME); }
   
    public static void main(String[] args) {
        
        EventQueue.invokeLater(new Runnable()
        {
            public void run()
            {
                VideoModule openCV = new OpenCv();
                Window window = new Window(openCV);
                window.setSize(1200, 700);
                window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                window.setVisible(true);    
            }
        }); 
        
      
           
           
           /*String const_path = "/home/diamind/Programming/Projects/Java/Local/OpenCV";
           Recognizer rec = new Recognizer(const_path + "/srcOpenCV/faces.txt",
           EigenFaceRecognizer.create() );
           
           System.out.println( rec.recognize(const_path + "/srcOpenCV/test.jpg") );
           
           Detector detector = new Detector();
           VideoCapture cam = new VideoCapture(1);
           
           Mat frame = new Mat();
           if(cam.isOpened()){
           while(true){
           cam.read(frame);
           detector.findFace(frame,rec);
           HighGui.imshow("Camera", frame);
           HighGui.waitKey(2);
           
           }
           }else
           System.out.println("Nope!");
           cam.release();
      */
    }
    
}
