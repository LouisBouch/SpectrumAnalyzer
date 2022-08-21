package tools;

import java.awt.Dimension;
import java.awt.Toolkit;

public interface ScreenSizeTool {
	public static final Dimension SCREENSIZE = Toolkit.getDefaultToolkit().getScreenSize();
	public static final int HEIGHT = SCREENSIZE.height;
	public static final int WIDTH = SCREENSIZE.width;
}
