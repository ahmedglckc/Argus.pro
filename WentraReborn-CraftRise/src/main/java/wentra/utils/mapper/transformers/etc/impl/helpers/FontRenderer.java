package wentra.utils.mapper.transformers.etc.impl.helpers;

import org.lwjgl.opengl.GL11;
import wentra.utils.mapper.transformers.etc.Render2DHelper;
import wentra.utils.mapper.transformers.etc.impl.utils.Reflector;
import wentra.utils.mapper.Mapper;

import java.lang.reflect.Method;


public class FontRenderer {

	public static int renderTextScale(String text, int x, int y, int color, boolean scaleEnabled, int scale) {
		if (scaleEnabled) {
			GL11.glPushMatrix();

			GL11.glTranslated(x + 5, y + 5, 0);
			GL11.glScaled(scale, scale, 1.0);
			GL11.glTranslated(-(x + 5), -(y + 5), 0);

			int result = 0;
			FontRenderer.drawText(text, x, y, color);

			GL11.glPopMatrix();

			return result;
		} else {
			int result = 0;
			FontRenderer.drawText(text, x, y, color);
			return result;
		}
	}

	public static int getStringWidth(Object fontRenderer, String text) {
		if (text == null || text.isEmpty()) {
			return 0;
		}

		int totalWidth = 0;
		boolean isBold = false;

		for (int i = 0; i < text.length(); i++) {
			char c = text.charAt(i);

			if (c == '§' && i + 1 < text.length()) {
				i++;
				char nextChar = text.charAt(i);
				if (nextChar == 'l' || nextChar == 'L') {
					isBold = true;
				} else if (nextChar == 'r' || nextChar == 'R') {
					isBold = false;
				}
				continue;
			}

			totalWidth += 1;
		}

		return totalWidth;
	}

	public static void drawText(String text, float x, float y, int color) {
		try {
			Method drawString = Reflector.getMethodUsingParameters(
					Mapper.FontRenderer,
					String.class, float.class, float.class, int.class
			);
			if (drawString != null) {
				drawString.invoke(Render2DHelper.fontRenderer, text, x, y, color);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void drawTextWithoutShadow(Object fontRenderer, String text, float x, float y, int color) {
		try {
			Method drawString = Reflector.getMethodUsingParameters(
					Mapper.FontRenderer,
					String.class, float.class, float.class, int.class, boolean.class
			);
			if (drawString != null) {
				drawString.invoke(fontRenderer, text, x, y, color, false);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static float getFontHeight() {
		return 9;
	}
}