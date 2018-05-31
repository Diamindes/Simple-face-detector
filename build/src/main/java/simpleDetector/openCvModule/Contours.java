package simpleDetector.openCvModule;

import java.util.HashSet;
import java.util.Set;
import org.opencv.core.Point;
import org.opencv.core.Rect;

public class Contours {

    private  int acc ;
    private int lives ;
    private int number = 1;

    public Contours(int acc, int lives) {
        this.acc = acc;
        this.lives = lives;
    }

    Set<Contour> objects = new HashSet<>();

    public Contour handleContour(Rect area) {
        Point newCenter = findCenter(area);

        for (Contour obj : objects) {
            if (findDistance(obj.getCenter(), newCenter) < acc) {
                obj.setLives(lives);
                obj.setCenter(newCenter);
                obj.setArea(area);
                obj.isHidden = false;
                return obj;
            }
        }

        Contour newObject = new Contour(newCenter, "Object " + (number++), lives, area);
        newObject.isHidden = false;
        objects.add(newObject);
        return newObject;
    }

    private Point findCenter(Rect area) {
        return new Point(area.x + area.width/2, area.y + area.height/2);
    }

    private long findDistance(Point center, Point newCenter) {
        double dist = Math.sqrt(Math.pow(center.x - newCenter.x, 2)+
                Math.pow(center.y - newCenter.y, 2));
        return Math.round(dist);
    }
    
    public void makeDMG(){
        
        Set<Contour> copyObjects = new HashSet<>(objects);
        
        copyObjects.forEach((obj) -> {
            if(obj.isHidden){
                obj.setLives(obj.getLives()-1);
                if(obj.getLives() == 0){  
                    System.out.println(obj.getObjName() + " dead!" );
                    objects.remove(obj); 
                }        
            } else{
                obj.isHidden = true;
            }
        });
    }
}
