package ir.asparsa.hobbytaste.server.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Created by hadi on 1/17/2017 AD.
 */
@Component
public class ImageUtil {

    private final static Logger logger = LoggerFactory.getLogger(ImageUtil.class);

    @Value("${image.maxLongHeight}")
    private int maxLongHeight;

    public BufferedImage scale(
            MultipartFile source
    ) {
        try {
            BufferedImage imgIn = ImageIO.read(source.getInputStream());
            if (imgIn.getHeight() <= maxLongHeight) {
                return null;
            }
            return scale(imgIn, imgIn.getHeight() / (double) imgIn.getHeight());
        } catch (IOException e) {
            logger.error("Cannot scale to thumbnail: ", e);
        }
        return null;
    }

    public BufferedImage scale(
            BufferedImage source,
            double ratio
    ) {
        int w = (int) (source.getWidth() * ratio);
        int h = (int) (source.getHeight() * ratio);
        BufferedImage bi = getCompatibleImage(w, h);
        Graphics2D g2d = bi.createGraphics();
        double xScale = (double) w / source.getWidth();
        double yScale = (double) h / source.getHeight();
        AffineTransform at = AffineTransform.getScaleInstance(xScale, yScale);
        g2d.drawRenderedImage(source, at);
        g2d.dispose();
        return bi;
    }

    private BufferedImage getCompatibleImage(
            int w,
            int h
    ) {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gd = ge.getDefaultScreenDevice();
        GraphicsConfiguration gc = gd.getDefaultConfiguration();
        BufferedImage image = gc.createCompatibleImage(w, h);
        return image;
    }

    public boolean saveImage(
            BufferedImage source,
            File file
    ) {
        try {
            ImageIO.write(source, "jpeg", file);
            return true;
        } catch (IOException e) {
            logger.error("Cannot save the image: ", e);
        }
        return false;
    }
}
