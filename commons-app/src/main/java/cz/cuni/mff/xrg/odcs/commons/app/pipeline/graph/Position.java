/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cuni.mff.xrg.odcs.commons.app.pipeline.graph;

import cz.cuni.mff.xrg.odcs.commons.app.dao.DataObject;
import java.awt.Point;
import javax.persistence.*;

/**
 * Represent coordinates of object in system (on canvas).
 *
 * @author Jiri Tomes
 */
@Entity
@Table(name = "ppl_position")
public class Position implements DataObject {

	/**
	 * Primary key of graph stored in db
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_ppl_position")
	@SequenceGenerator(name = "seq_ppl_position", allocationSize = 1)
	private Long id;

	/**
	 * X coordinate in pixels
	 */
	@Column(name = "pos_x")
	private int x;

	/**
	 * Y coordinate in pixels
	 */
	@Column(name = "pos_y")
	private int y;

	/**
	 * No-arg constructor for JPA
	 */
	public Position() {
	}

	/**
	 * Create new instance for position based on parameters.
	 *
	 * @param x int value of X axis
	 * @param y int value of Y axis.
	 */
	public Position(int x, int y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * Copy constructor.
	 *
	 * @param position the value of position.
	 */
	public Position(Position position) {
		x = position.x;
		y = position.y;
	}

	/**
	 * Change actual position.
	 *
	 * @param newX new value of X axis.
	 * @param newY new value of Y axis.
	 */
	public void changePosition(int newX, int newY) {
		x = newX;
		y = newY;
	}

	/**
	 * Returns the actual position as point.
	 *
	 * @return The instance of {@link Point} as actual position.
	 */
	public Point getPositionAsPoint() {
		Point positionPoint = new Point(x, y);
		return positionPoint;
	}

	/**
	 * Returns the value of X axis of the position.
	 *
	 * @return The value of X axis of the position.
	 */
	public int getX() {
		return x;
	}

	/**
	 * Set the new value of X axis for this position.
	 *
	 * @param newX The new value of X axis of the position.
	 */
	public void setX(int newX) {
		x = newX;
	}

	/**
	 * Returns the value of Y axis of the position.
	 *
	 * @return The value of Y axis of the position.
	 */
	public int getY() {
		return y;
	}

	/**
	 * Set the new value of Y axis for this position.
	 *
	 * @param newY The new value of Y axis of the position.
	 */
	public void setY(int newY) {
		y = newY;
	}

	/**
	 * Returns the set ID of this position as {@link Long} value.
	 *
	 * @return the set ID of this position as {@link Long} value.
	 */
	@Override
	public Long getId() {
		return id;
	}
}
