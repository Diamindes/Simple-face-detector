package simpleDetector.openCvModule;

import java.time.LocalDateTime;
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
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.core.Rect2d;
import org.opencv.face.EigenFaceRecognizer;
import org.opencv.face.FisherFaceRecognizer;
import org.opencv.face.LBPHFaceRecognizer;
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
    private Contours ctrlSystem; 
    private boolean isMakingPhoto = false;
    private String outputPhotoPath;
    private Settings settings;

    public Detector(Statistics logSystem, Settings settings) {

        this.logSystem = logSystem;
        this.settings = settings;
        ctrlSystem = new Contours(settings.DetectorContourDistance, settings.DetectorContourLivesCount);
        //TODO
        frontDetector = new CascadeClassifier(front);

        String path2 = "./src/main/resourсes/files/opencv/cascades/lbpcascade_frontalface_improved.xml";
        finder = new CascadeClassifier(path2);

        //TODO: Настройка распознавания
        //recognizer = new Recognizer("./src/main/resourсes/faces",
         //       EigenFaceRecognizer.create(80, 20000));
        
        // recognizer = new Recognizer("./src/main/resourсes/faces",
        //         FisherFaceRecognizer.create(80, 20000));
        
          recognizer = new Recognizer("./src/main/resourсes/faces",
                  LBPHFaceRecognizer.create(), settings);
          
        // isTrained = recognizer.status();
    }

    public void train() {
        isTrained = false;
        recognizer.configure(logSystem.getLog("regLog")); //TODO
        isTrained = true;
    }

    public void findObject(Mat frame, List<MatOfPoint> contours) {

        String status = "Empty";
        int counter = 0;

        if (!isMakingPhoto) {
            for (MatOfPoint contour : contours) {
                if ((contour.width() * contour.height()) < settings.DetectorMinContourSize) {
                    continue;
                }

                Rect contourArea = Imgproc.boundingRect(contour);
                contourArea.height /= 2;
                Contour obj = ctrlSystem.handleContour(contourArea);

                findFace(frontDetector, frame, obj);

                counter += 1;
                status = "Under attack!!";
            }

            ctrlSystem.makeDMG();
            Imgproc.putText(frame, "Room status: " + status, new Point(10, 20), 2, 0.5, new Scalar(255, 0, 0), 2);
            Imgproc.putText(frame, "Counter : " + counter, new Point(10, 50), 2, 0.5, new Scalar(255, 0, 0), 2);
            Imgproc.putText(frame, "Face counter : " + logSystem.counter, new Point(10, 80), 2, 0.5, new Scalar(255, 0, 0), 2);

        } else {
            findFace(frontDetector, frame, null);
        }
    }

    private void findFace(CascadeClassifier detector, Mat frame, Contour contour) {

        if (!isMakingPhoto) {

            Mat grayFrame = new Mat();
            Rect object = contour.getArea();

            Imgproc.cvtColor(frame, grayFrame, Imgproc.COLOR_BGR2GRAY);
            Mat scaledFace = grayFrame.submat(object);
            Imgproc.resize(scaledFace, scaledFace, new Size(scaledFace.width() * 2, scaledFace.height() * 2));

            MatOfRect targets = new MatOfRect();
            detector.detectMultiScale(scaledFace, targets, settings.DetectorFaceScale, 
                    settings.DetectorFaceAcc,
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

                if (isTrained) {
                    Rect recFace = face.clone();
                    recFace.x = object.x + face.x / 2;
                    recFace.y = object.y + face.y / 2;
                    recFace.width /= 2;
                    recFace.height /= 2;

                    objectName = recognizer.recognize(grayFrame.submat(recFace));
                    contour.setObjName(objectName);
                    JTextArea log = logSystem.getLog("recLog");
                    log.append("Timestamp: " + mark[0] + " " + mark[1] + "    Face: " + objectName + "\n");
                }

                Imgproc.rectangle(frame, new Point(object.x + face.x / 2, object.y + face.y / 2),
                        new Point(object.x + face.x / 2 + face.width / 2, object.y + face.y / 2 + face.height / 2),
                        settings.RecognizerFaceLineColor, 1);
                logSystem.counter += 1;
            }
            Imgproc.putText(frame, objectName, new Point(object.x, object.y + object.height + 25),
                    2, 1, new Scalar(255, 255, 0), 2);

        } else {
            Mat scaledFace = frame.clone();

            Imgproc.cvtColor(scaledFace, scaledFace, Imgproc.COLOR_BGR2GRAY);
            Imgproc.resize(scaledFace, scaledFace, new Size(scaledFace.width() * 2, scaledFace.height() * 2));

            MatOfRect targets = new MatOfRect();
            detector.detectMultiScale(scaledFace, targets, settings.DetectorFaceScale, 
                    settings.DetectorFaceAcc,
                    Objdetect.CASCADE_DO_CANNY_PRUNING,
                    new Size(30, 30),
                    new Size(frame.size().width, frame.size().height));

            for (Rect face : targets.toArray()) {
                face.x =  face.x - 50;
                face.y =  face.y - 60;
                face.height =  face.height + 90;
                face.width =  face.width + 50;
                makePhoto(scaledFace.submat(face));
                break;
            }
        }

    }

    public void setIsMakingPhoto(boolean bool, String path) {
        isMakingPhoto = bool;
        outputPhotoPath = path;
    }

    private void makePhoto(Mat face) {

        Imgproc.resize(face, face, new Size(92,112));
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
}
