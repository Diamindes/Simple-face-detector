
package simpleDetector.openCvModule;

import org.opencv.core.Scalar;

public class Settings {
    public String SystemCumID;
    public int SystemHoldRate;
    
    public int DetectorMinContourSize; 
    public int DetectorContourLivesCount;
    public int DetectorContourDistance;
    public  double DetectorFaceScale;
    public int DetectorFaceAcc;
   
    
    public int RecognizerFaceWidth;
    public int RecognizerFaceHeight;
    public Scalar RecognizerFaceLineColor;
    
    public Settings(String SystemCumID, int SystemHoldRate, int DetectorMinContourSize, int DetectorContourDistance, double DetectorFaceScale, 
            int DetectorFaceAcc,int DetectorContourLivesCount, int RecognizerFaceWidth, int RecognizerFaceHeight, 
            Scalar RecognizerFaceLineColor) {
        this.SystemCumID = SystemCumID;
        this.SystemHoldRate = SystemHoldRate;
        
        this.DetectorMinContourSize = DetectorMinContourSize;
        this.DetectorFaceScale = DetectorFaceScale;
        this.DetectorFaceAcc = DetectorFaceAcc;
        this.DetectorContourLivesCount = DetectorContourLivesCount;
        
        this.RecognizerFaceWidth = RecognizerFaceWidth;
        this.RecognizerFaceHeight = RecognizerFaceHeight;
        this.RecognizerFaceLineColor = RecognizerFaceLineColor;   
        this.DetectorContourDistance =  DetectorContourDistance;
    }
    
}
