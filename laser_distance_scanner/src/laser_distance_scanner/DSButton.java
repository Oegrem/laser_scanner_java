package laser_distance_scanner;

import static org.lwjgl.opengl.GL11.*;

import java.awt.Point;
import java.util.ArrayList;

import javafx.scene.paint.Color;

public class DSButton {

	private Point minXY;
	private Point maxXY;
	private int ID;
	private float r, g, b, a;

	public DSButton(Point _minXY, Point _maxXY, int _ID, float _r, float _g,
			float _b, float _a) {
		minXY = _minXY;
		maxXY = _maxXY;
		ID = _ID;
		r = _r;
		g = _g;
		b = _b;
		a = _a;
	}

	public int getID() {
		return ID;
	}

	public void drawButton() {

		glColor4f(r, g, b, a);
		glBegin(GL_QUADS);
		glVertex2f(minXY.x, minXY.y);
		glVertex2f(minXY.x, maxXY.y);
		glVertex2f(maxXY.x, maxXY.y);
		glVertex2f(maxXY.x, minXY.y);
		glEnd();
	}

	public boolean isClicked(double x, double y) {
		if (x<minXY.x || y<minXY.y || x>maxXY.x || y>maxXY.y) {
			return false;
		} else {
			return true;
		}
	}

	public static int isButtonClicked(double x, double y,
			ArrayList<DSButton> buttons) {

		for(DSButton dsb : buttons){
			if(dsb.isClicked(x, y)){
				return dsb.getID();
			}
		}
		
		return -1;
	}

}
