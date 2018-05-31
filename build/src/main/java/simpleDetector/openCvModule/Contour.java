/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simpleDetector.openCvModule;

import java.util.Objects;
import org.opencv.core.Point;
import org.opencv.core.Rect;

public class Contour {

    private Point center;
    private Rect area = null;
    private String objName;
    private int lives;
    public boolean isHidden = true;
    
    public Contour(Point center, String objName, int lives, Rect area) {
        this.center = center;
        this.objName = objName;
        this.lives = lives;
        this.area = area;
    }

    public Point getCenter() {
        return center;
    }

    public String getObjName() {
        return objName;
    }

    public int getLives() {
        return lives;
    }

    public void setCenter(Point center) {
        this.center = center;
    }

    public void setObjName(String objName) {
        this.objName = objName;
    }

    public void setLives(int lives) {
        this.lives = lives;
    }

    public Rect getArea() {
        return area;
    }

    public void setArea(Rect area) {
        this.area = area;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 67 * hash + Objects.hashCode(this.center);
        hash = 67 * hash + Objects.hashCode(this.area);
        hash = 67 * hash + Objects.hashCode(this.objName);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Contour other = (Contour) obj;
        if (!Objects.equals(this.center, other.center)) {
            return false;
        }
        return true;
    }

    
}
