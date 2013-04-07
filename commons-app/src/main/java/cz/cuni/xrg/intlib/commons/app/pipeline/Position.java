/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cuni.xrg.intlib.commons.app.pipeline;

import java.awt.Point;

/**
 * Represent coordinates of object in system (on canvas).
 *
 * @author Jiri Tomes
 */
public class Position {

    private int x;
    private int y;

    public Position(int x, int y) {
        this.x = x;
        this.y = y;

    }

    public void changePosition(int newX, int newY) {
        x = newX;
        y = newY;
    }

    public Point getPositionAsPoint() {
        Point positionPoint = new Point(x, y);
        return positionPoint;
    }

    public int getX() {
        return x;
    }

    public void setX(int newX) {
        x = newX;
    }

    public int getY() {
        return y;
    }

    public void setY(int newY) {
        y = newY;
    }
}
