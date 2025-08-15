package minecraft.clone;

import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import java.nio.*;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

public class App {

	// The window handle
	private long window;
	private PointerBuffer monitors;
	int currentMonitor = 0;

	public String getGreeting()
	{
		return "HELLO";
	}
	public void run() {
		System.out.println("Hello LWJGL " + Version.getVersion() + "!");

		init();
		loop();

		// Free the window callbacks and destroy the window
		glfwFreeCallbacks(window);
		glfwDestroyWindow(window);

		// Terminate GLFW and free the error callback
		glfwTerminate();
		glfwSetErrorCallback(null).free();
	}

	private void init() {
		// Setup an error callback. The default implementation
		// will print the error message in System.err.
		GLFWErrorCallback.createPrint(System.err).set();

		// Initialize GLFW. Most GLFW functions will not work before doing this.
		if ( !glfwInit() )
			throw new IllegalStateException("Unable to initialize GLFW");

		// Configure GLFW
		glfwDefaultWindowHints(); // optional, the current window hints are already the default
		glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
		glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE); // the window will be resizable

		// get monitors
		monitors = glfwGetMonitors();
		System.out.println(monitors.capacity());
		GLFWVidMode mode = glfwGetVideoMode(glfwGetPrimaryMonitor());

		// Create the window, fullscreen initially
		window = glfwCreateWindow(mode.width(), mode.height(), "Hello World!", glfwGetPrimaryMonitor(), NULL); // can make default monitor configurable
		if ( window == NULL )
			throw new RuntimeException("Failed to create the GLFW window");

		/* CALLBACKS */	

		// Setup a key callback. It will be called every time a key is pressed, repeated or released.
		glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
			// movement
			if ( key == GLFW_KEY_W && (action == GLFW_PRESS || action == GLFW_REPEAT) ){
				System.out.println("W");
			}
			if ( key == GLFW_KEY_A && (action == GLFW_PRESS || action == GLFW_REPEAT) ){
				System.out.println("A");
			}
			if ( key == GLFW_KEY_S && (action == GLFW_PRESS || action == GLFW_REPEAT) ){
				System.out.println("S");
			}
			if ( key == GLFW_KEY_D && (action == GLFW_PRESS || action == GLFW_REPEAT) ){
				System.out.println("D");
			}


			// meta?
			if ( key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE )
				glfwSetWindowShouldClose(window, true); // We will detect this in the rendering loop
			if ( key == GLFW_KEY_F4 && action == GLFW_PRESS){ // can shift this to a menu.
				long monitor = monitors.get((++currentMonitor)%monitors.capacity());
				GLFWVidMode vidMode = glfwGetVideoMode(monitor);
				glfwSetWindowMonitor(window, monitor, vidMode.width() / 2, vidMode.height() / 2, vidMode.width(), vidMode.height(), vidMode.refreshRate());
				
			}
		});

		glfwSetMouseButtonCallback(window, (window, button, action , mods) -> {
			if ( button == GLFW_MOUSE_BUTTON_LEFT && action == GLFW_RELEASE){
				System.out.println("LEFT CLICK!");
			}
			else if ( button == GLFW_MOUSE_BUTTON_RIGHT && action == GLFW_RELEASE)
			{
				System.out.println("RIGHT CLICK!");
			}
		});

		glfwSetScrollCallback(window, (window, xoffset, yoffset) -> {
			System.out.println(Double.toString(xoffset) +" "+ Double.toString(yoffset));
		});

		// monitor connect/disconnect callback
		glfwSetMonitorCallback((monitor, event) -> {
			if ( event == GLFW_CONNECTED )
				System.out.println("Secondary monitor connected!");
			else if ( event == GLFW_DISCONNECTED ){
				System.out.println("Secondary monitor disconnected!");
			}
		});

		// glfwSetCursorEnterCallback(GLFW_NO_WINDOW_CONTEXT, null); //TODO: Implement this
		glfwSetCursorPosCallback(window, (window, xpos, ypos) -> {
			System.out.println("Pointer coordinates: "+Double.toString(xpos) +","+ Double.toString(ypos));
		});
		
		// Get the thread stack and push a new frame
		try ( MemoryStack stack = stackPush() ) {
			IntBuffer pWidth = stack.mallocInt(1); // int*
			IntBuffer pHeight = stack.mallocInt(1); // int*

			// Get the window size passed to glfwCreateWindow
			glfwGetWindowSize(window, pWidth, pHeight);

			// Get the resolution of the primary monitor
			GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

			// Center the window
			glfwSetWindowPos(
				window,
				(vidmode.width() - pWidth.get(0)) / 2,
				(vidmode.height() - pHeight.get(0)) / 2
			);
			glfwSetCursorPos(window, (pWidth.get(0)) / 2, (pHeight.get(0)) / 2); // center the cursor

		} // the stack frame is popped automatically

		// Make the OpenGL context current
		glfwMakeContextCurrent(window);
		// Enable v-sync --> TODO: can add options for this
		glfwSwapInterval(1);

		// Make the window visible
		glfwShowWindow(window);
	}

	private void loop() {
		// This line is critical for LWJGL's interoperation with GLFW's
		// OpenGL context, or any context that is managed externally.
		// LWJGL detects the context that is current in the current thread,
		// creates the GLCapabilities instance and makes the OpenGL
		// bindings available for use.
		GL.createCapabilities();

		// Set the clear color
		glClearColor(1.0f, 0.0f, 0.0f, 0.0f);

		// Run the rendering loop until the user has attempted to close
		// the window or has pressed the ESCAPE key.
		while ( !glfwWindowShouldClose(window) ) {
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer

			glfwSwapBuffers(window); // swap the color buffers

			// Poll for window events. The key callback above will only be
			// invoked during this call.
			glfwPollEvents();
		}
	}

	public static void main(String[] args) {
		new App().run();
	}

}

