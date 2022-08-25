package tools;

public class Vector2D {
	/**The x value of the vector*/
	private double x;
	/**The y value of the vector*/
	private double y;
	
	/**
	 * Creates a vector using anther vector as reference
	 * @param vector The reference vector
	 */
	public Vector2D(Vector2D vector) {
		this.x = vector.getX();
		this.y = vector.getY();
	}
	/**
	 * Creates a vector using and x and y value
	 * @param x The x value
	 * @param y The y value
	 */
	public Vector2D(double x, double y) {
		this.x = x;
		this.y = y;
	}
	/**
	 * Create vector with default field values (0)
	 */
	public Vector2D() {
		x = 0;
		y = 0;
	}
	
	/**
	 * Adds a value to both fields
	 * @param value The value to add
	 * @return The vector with the new values
	 */
	public Vector2D add(double value) {
		x += value;
		y += value;
		return this;
	}
	/**
	 * Adds an x and y value to the vector
	 * @param x The x value to add
	 * @param y The y value to add
	 * @return The vector with the new values
	 */
	public Vector2D add(double x, double y) {
		this.x += x;
		this.y += y;
		return this;
	}
	/**
	 * Adds an x/y value to the vector
	 * @param vector The vector to add
	 * @return The vector with the new values
	 */
	public Vector2D add(Vector2D vector) {
		y += vector.getX();
		x += vector.getY();
		return this;
	}
	
	/**
	 * Adds a value to x
	 * @param value The value to add
	 * @return The new x value
	 */
	public double addX(double value) {
		x += value;
		return x;
	}
	/**
	 * Adds a value to y
	 * @param value The value to add
	 * @return The new y value
	 */
	public double addY(double value) {
		y += value;
		return y;
	}
	
	/**
	 * Multiplies the vector by and x and y value
	 * @param x The x value to add
	 * @param y The y value to add
	 * @return The vector with the new values
	 */
	public Vector2D multiply(double x, double y) {
		this.x *= x;
		this.y *= y;
		return this;
	}
	/**
	 * Multiplies the values of the vector with a multiplier
	 * @param multiplier The value to multiply with
	 * @return The vector with the new values
	 */
	public Vector2D multiply(double multiplier) {
		x *= multiplier;
		y *= multiplier;
		return this;
	}
	/**
	 * Multiplies the x/y value of the current vector with the x/y value of the second one.
	 * @param vector The vector to multiply with
	 * @return The vector with the new values
	 */
	public Vector2D multiply(Vector2D vector) {
		x *= vector.getX();
		y *= vector.getY();
		return this;
	}
	
	/**
	 * Multiplies the x value with a multiplier
	 * @param xMultiplier The value to multiply x with
	 * @return The new x value
	 */
	public double multiplyX(double xMultiplier) {
		x *= xMultiplier;
		return x;
	}
	/**
	 * Multiplies the y value with a multiplier
	 * @param yMultiplier The value to multiply x with
	 * @return The new x value
	 */
	public double multiplyY(double yMultiplier) {
		y *= yMultiplier;
		return y;
	}
	
	/**
	 * Sets the x value
	 * @param x The x value to set
	 */
	public void setX(double x) {
		this.x = x;
	}
	/**
	 * Sets the y value
	 * @param y The y value to set
	 */
	public void setY(double y) {
		this.y = y;
	}
	/**
	 * Copy values of another vector
	 * @param vector The vector to copy values from
	 */
	public void copyVector(Vector2D vector) {
		x = vector.getX();
		y = vector.getY();
	}
	/**
	 * Sets the x and y values of the vector
	 * @param x The x value
	 * @param y The y value
	 */
	public void setValues(double x, double y) {
		this.x = x;
		this.y = y;
	}
	/**
	 * Gets the x value
	 * @return Return the x value
	 */
	public double getX() {
		return x;
	}
	/**
	 * Gets the y value
	 * @return Return the y value
	 */
	public double getY() {
		return y;
	}
	/**
	 * Gets the rounded value of x
	 * @return Returns the rounded value of x
	 */
	public int getRoundedX() {
		return (int) Math.round(x);
	}
	/**
	 * Gets the rounded value of y
	 * @return Returns the rounded value of y
	 */
	public int getRoundedY() {
		return (int) Math.round(y);
	}
	/**
	 * Gets the floored value of x using (int) cast
	 * @return Returns the floored value of x
	 */
	public int getFlooredX() {
		return (int) x;
	}
	/**
	 * Gets the rounded value of y using (int) cast
	 * @return Returns the rounded value of y
	 */
	public int getFlooredY() {
		return (int) y;
	}
	/**
	 * Gets the ceiling value of x
	 * @return Returns the ceiling value of x
	 */
	public int getCeilingX() {
		return (int) Math.ceil(x);
	}
	/**
	 * Gets the ceiling value of y
	 * @return Returns the ceiling value of y
	 */
	public int getCeilingY() {
		return (int) Math.ceil(y);
	}
	
}
