package test;

import static org.junit.Assert.assertNotNull;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.junit.Test;

public class ImageTest {

	@Test
	public void test() {

		try {
			BufferedImage image = ImageIO.read(new File("E:/project/tank/src/images/tankL.gif"));
			assertNotNull(image);
			
			BufferedImage image2 = ImageIO.read(ImageTest.class.getClassLoader().getResourceAsStream("images/tankL.gif"));
			//上面的ImageTest.class也可以换成this.getClass()
			assertNotNull(image2);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

}
