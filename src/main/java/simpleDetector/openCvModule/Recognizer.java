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
            
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Recognizer.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("File not found!");
        } catch (IOException ex) {
            Logger.getLogger(Recognizer.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Input error!");
        }
        
        
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
    
}