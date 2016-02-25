package laser_distance_scanner;

import org.lwjgl.Sys;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;

import data_processing.Clustering;
import data_processing.Processing;
import data_processing.Settings;
import data_processing.SimpleCluster;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.*;

import java.awt.Point;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

public class Graphics {

	// private GLFWErrorCallback errorCallback;
	private GLFWKeyCallback keyCallback;

	@SuppressWarnings("unused")
	private GLFWScrollCallback scrollCallBack;

	@SuppressWarnings("unused")
	private GLFWMouseButtonCallback mouseButtonCallBack;

	@SuppressWarnings("unused")
	private GLFWCursorPosCallback cursorPosCallBack;

	private double xold = 0;

	private double yold = 0;

	private boolean leftButtonPressed = false;

	private float xMove = 1;

	private float yMove = 1;

	private static int SCANNER_SIZE = 45;

	// The window handle
	private long window;

	private Distance_scanner scn;

	private boolean drawPoints = true;

	private int toChange = 0;

	private float zoom = 1;

	public void run() {
		System.out.println("Hello LWJGL " + Sys.getVersion() + "!");

		scn = Distance_scanner.getDistanceScanner(); // Getting/Creating
														// Distance_scanner

		scn.start();

		try {
			init();
			loop();

			// Release window and window callbacks
			glfwDestroyWindow(window);
			keyCallback.release();
		} finally {
			// Terminate GLFW and release the GLFWErrorCallback
			glfwTerminate();
			// errorCallback.release();
		}
	}

	private void init() {
		// Setup an error callback. The default implementation
		// will print the error message in System.err.
		// glfwSetErrorCallback(errorCallback =
		// GLFWErrorCallback.createPrint(System.err));

		// Initialize GLFW. Most GLFW functions will not work before doing this.
		if (glfwInit() != GL11.GL_TRUE)
			throw new IllegalStateException("Unable to initialize GLFW");

		// Configure our window
		glfwDefaultWindowHints(); // optional, the current window hints are
									// already the default
		glfwWindowHint(GLFW_VISIBLE, GL_FALSE); // the window will stay hidden
												// after creation
		glfwWindowHint(GLFW_RESIZABLE, GL_TRUE); // the window will be resizable

		int WIDTH = 700;
		int HEIGHT = 700;

		// Create the window
		window = glfwCreateWindow(WIDTH, HEIGHT, "Laser Distance Scanner",
				NULL, NULL);
		if (window == NULL)
			throw new RuntimeException("Failed to create the GLFW window");

		// Setup a key callback. It will be called every time a key is pressed,
		// repeated or released.

		glfwSetScrollCallback(window,
				scrollCallBack = new GLFWScrollCallback() {

					@Override
					public void invoke(long window, double xoffset,
							double yoffset) {
						zoom += yoffset / 5;
					}
				});

		glfwSetMouseButtonCallback(window,
				mouseButtonCallBack = new GLFWMouseButtonCallback() {

					@Override
					public void invoke(long window, int button, int action,
							int mods) {
						if (button == GLFW_MOUSE_BUTTON_LEFT
								&& action == GLFW_PRESS) {
							leftButtonPressed = true;
						}

						if (button == GLFW_MOUSE_BUTTON_LEFT
								&& action == GLFW_RELEASE) {
							leftButtonPressed = false;
						}
					}

				});

		glfwSetCursorPosCallback(window,
				cursorPosCallBack = new GLFWCursorPosCallback() {
					@Override
					public void invoke(long window, double xpos, double ypos) {
						if (leftButtonPressed) {
							xMove += (float) (xpos - xold) * 10;
							yMove += (float) (yold - ypos) * 10;
						}

						xold = xpos;
						yold = ypos;

					}
				});

		glfwSetKeyCallback(window, keyCallback = new GLFWKeyCallback() {
			@Override
			public void invoke(long window, int key, int scancode, int action,
					int mods) {
				if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE)
					glfwSetWindowShouldClose(window, GL_TRUE); // We will detect
																// this in our
																// rendering
																// loop
				if (key == GLFW_KEY_SPACE && action == GLFW_RELEASE) {
					if (Distance_scanner.playRecord) {
						Distance_scanner.playRecord = false;
						System.out.println("Record STOP");
					} else {
						Distance_scanner.playRecord = true;
						System.out.println("Record PLAY");
					}
				}

				if (key == GLFW_KEY_LEFT && action == GLFW_RELEASE) {
					if (!Distance_scanner.playRecord) {
						Distance_scanner.lastFrame = true;
					}
				}

				if (key == GLFW_KEY_RIGHT && action == GLFW_RELEASE) {
					if (!Distance_scanner.playRecord) {
						Distance_scanner.nextFrame = true;
					}
				}

				if (key == GLFW_KEY_1 && action == GLFW_RELEASE) {
					if (drawPoints) {
						drawPoints = false;
						System.out.println("drawPoints OF");
					} else {
						drawPoints = true;
						System.out.println("drawPoints ON");
					}
				}

				if (key == GLFW_KEY_G && action == GLFW_RELEASE) {
					if (Settings.isGraymap_state()) {
						Settings.setGraymap_state(false);
						System.out.println("Gl�tten OF");
					} else {
						Settings.setGraymap_state(true);
						System.out.println("Gl�tten ON");
					}
				}

				if (key == GLFW_KEY_X && action == GLFW_RELEASE) {
					switch (toChange) {
					case 0:
						Settings.setClustering_threshold(Settings
								.getClustering_threshold() + 0.1);
						System.out.println("threshold: "
								+ Settings.getClustering_threshold());
						break;
					case 1:
						break;
					case 2:
						Distance_scanner.slomo += 1;
						System.out.println("slomo: " + Distance_scanner.slomo);
						break;
					case 3:
						Settings.setClustering_search_range(Settings
								.getClustering_search_range() + 1);
						System.out.println("searchRange: "
								+ Settings.getClustering_search_range());
						break;
					}
				}

				if (key == GLFW_KEY_C && action == GLFW_RELEASE) {
					switch (toChange) {
					case 0:
						Settings.setClustering_threshold(Settings
								.getClustering_threshold() - 0.1);
						System.out.println("threshold: "
								+ Settings.getClustering_threshold());
						break;
					case 1:
						break;
					case 2:
						if (Distance_scanner.slomo > 1) {
							Distance_scanner.slomo -= 1;
							System.out.println("slomo: "
									+ Distance_scanner.slomo);
						}
						break;
					case 3:
						Settings.setClustering_search_range(Settings
								.getClustering_search_range() - 1);
						System.out.println("searchRange: "
								+ Settings.getClustering_search_range());
						break;
					}
				}

				if (key == GLFW_KEY_S && action == GLFW_RELEASE) {
					toChange++;
					if (toChange > 5) {
						toChange = 0;
					}
					switch (toChange) {
					case 0:
						System.out.println("threshold");
						break;
					case 1:
						break;
					case 2:
						System.out.println("slomo");
						break;
					case 3:
						System.out.println("searchRange");
						break;
					case 4:
						System.out.println("Range");
						break;
					case 5:
						System.out.println("Size");
						break;

					}
				}

			}
		});

		// Get the resolution of the primary monitor
		/*
		 * GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor()); //
		 * Center our window glfwSetWindowPos( window, (vidmode.getWidth() -
		 * WIDTH) / 2, (vidmode.getHeight() - HEIGHT) / 2 );
		 */
		glfwSetWindowPos(window, (900 - WIDTH) / 2, (900 - HEIGHT) / 2);
		// Make the OpenGL context current
		glfwMakeContextCurrent(window);
		// Enable v-sync
		glfwSwapInterval(1);

		// Make the window visible
		glfwShowWindow(window);
	}

	private synchronized void drawSensorPixel() {
		if (drawPoints) {
			if (Distance_scanner.getDistanceScanner().isUsingSimFile()) {
				glColor4f(0.0f, 0.5f, 0.5f, 1.0f);
			} else {
				glColor4f(1.0f, 0.0f, 0.0f, 1.0f);
			}
			glPointSize(4);
			
			

			CopyOnWriteArrayList<Point> poi = new CopyOnWriteArrayList<Point>();
			poi.addAll(SynchronListHandler.getPointVector());

			CopyOnWriteArrayList<SimpleCluster> simC = new CopyOnWriteArrayList<SimpleCluster>();
			simC.addAll(SynchronListHandler.getSimpleCluster());

			glBegin(GL_POINTS);
			
			for (Point p: poi){
				
				glColor4f(0.0f,1.0f,0.0f,0.5f);
				glVertex2f((float)p.x,(float)p.y);
				
			}
			
			glEnd();
			
			for (SimpleCluster sC : simC) {
				try{
				Point mid = new Point(0,0);
				setColor(1);
				int z=0;
				for (int i = sC.getFirstElement(); i <= sC.getLastElement() && i < poi.size(); i++) {
					//glVertex2f(((float) poi.get(i).x), ((float) poi.get(i).y));
					mid.x+=poi.get(i).x;
					mid.y+=poi.get(i).y;
					z++;
				}
				if(z==0)
					continue;
				mid.x/=z;
				mid.y/=z;
				//glBegin(GL_POINTS);
				//glVertex2f(((float) mid.x), ((float) mid.y));
				//glEnd();
				drawCross(poi.get(sC.getFirstElement()).x,poi.get(sC.getFirstElement()).y);
				drawCross(poi.get(sC.getLastElement()).x,poi.get(sC.getLastElement()).y);
				
				//drawCross(mid.x, mid.y);
				glColor4f(1.0f,0.0f,0.0f,0.5f);
				glBegin(GL_POLYGON);
				//glVertex2f( poi.get(sC.getFirstElement()).x*(float)1.1, poi.get(sC.getFirstElement()).y*(float)1.1);
				glVertex2f(poi.get(sC.getFirstElement()).x,poi.get(sC.getFirstElement()).y);
				glVertex2f(mid.x,mid.y);
				glVertex2f(poi.get(sC.getLastElement()).x,poi.get(sC.getLastElement()).y);
				//glVertex2f(poi.get(sC.getLastElement()).x*(float)1.1,poi.get(sC.getLastElement()).y*(float)1.1);
				glVertex2f(mid.x*(float)1.5,mid.y*(float)1.5);
				glEnd();
				}catch (Exception e){
					System.out.println(e);
				}
				
			}
		}
	}
	
	private void drawCross(float x, float y){
		glBegin(GL_QUADS);
		glColor4f(1.0f,0.0f,0.0f,1.0f);
		glVertex2f(x-30,y-30);
		glVertex2f(x+30,y-30);
		glVertex2f(x+30,y+30);
		glVertex2f(x-30,y+30);
		glEnd();
	}

	private void setColor(int i) {
		switch (i % 9) {
		case 0:
			glColor3f(0.5f, 0.5f, 1.0f);
			break;
		case 1:
			glColor3f(1.0f, 0.0f, 0.0f);
			break;
		case 2:
			glColor3f(0.0f, 1.0f, 0.0f);
			break;
		case 3:
			glColor3f(0.0f, 0.0f, 1.0f);
			break;
		case 4:
			glColor3f(1.0f, 1.0f, 0.0f);
			break;
		case 5:
			glColor3f(1.0f, 0.0f, 1.0f);
			break;
		case 6:
			glColor3f(0.0f, 1.0f, 1.0f);
			break;
		case 7:
			glColor3f(0.5f, 0.5f, 0.5f);
			break;
		case 8:
			glColor3f(1.0f, 0.5f, 0.5f);
			break;
		}
	}

	private void loop() {
		// This line is critical for LWJGL's interoperation with GLFW's
		// OpenGL context, or any context that is managed externally.
		// LWJGL detects the context that is current in the current thread,
		// creates the GLCapabilities instance and makes the OpenGL
		// bindings available for use.
		GL.createCapabilities();

		glEnable(GL_BLEND); // enable blending (opacity)
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

		// Set the clear color
		glClearColor(1.0f, 1.0f, 1.0f, 0.0f);

		glLineWidth(3);

		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		glOrtho(-3500, 3500, -3500, 3500, -1, 1); // To change the resolution of
		// coord.system; change -6 & +6 to range
		// (default: -1 & +1)
		glMatrixMode(GL_MODELVIEW);

		// Run the rendering loop until the user has attempted to close
		// the window or has pressed the ESCAPE key.
		while (glfwWindowShouldClose(window) == GL_FALSE) {
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the
																// framebuffer

			glPointSize(2);

			glPushMatrix();

			glTranslatef(xMove, yMove, 1);

			glScalef(zoom, zoom, 1);

			drawSensorPixel();

			glPointSize(1);

			for (float g = 0; g < 10000; g += 500) {
				glColor4f(1 - (g / 10000), (g / 10000), 0.0f, 0.3f);
				glBegin(GL_POINTS);
				for (float i = 0; i < 360; i += 1) {

					float x = (SCANNER_SIZE + g) * (float) Math.cos(i);
					float y = (SCANNER_SIZE + g) * (float) Math.sin(i);

					glVertex2f(x, y);

				}
				glEnd();
			}

			glPopMatrix();

			glfwSwapBuffers(window); // swap the color buffers

			// Poll for window events. The key callback above will only be
			// invoked during this call.
			glfwPollEvents();
		}

		scn.interrupt();
	}

}
