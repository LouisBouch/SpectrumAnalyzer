package tools;

import java.awt.Dimension;
import java.awt.Toolkit;

public interface ScreenSizeTool {
	public static final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	public static final int height = screenSize.height;
	public static final int width = screenSize.width;
}
