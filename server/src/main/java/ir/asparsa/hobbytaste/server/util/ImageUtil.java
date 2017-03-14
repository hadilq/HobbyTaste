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
                logger.info("No need to scale: image height is " + imgIn.getHeight() + " and max is " + maxLongHeight);
                return null;
            }
            return scale(imgIn, (double) maxLongHeight / imgIn.getHeight());
        } catch (IOException e) {
            logger.error("Cannot scale to thumbnail: ", e);
        }
        return null;
    }

    public BufferedImage scale(
            BufferedImage imgIn,
            double ratio
    ) {
        int w = (int) (imgIn.getWidth() * ratio);
        int h = (int) (imgIn.getHeight() * ratio);

        BufferedImage thumbnailOut = new BufferedImage(w, h, imgIn.getType());
        Graphics2D g = thumbnailOut.createGraphics();
        AffineTransform transform = AffineTransform.getScaleInstance(ratio, ratio);
        g.drawImage(imgIn, transform, (img, infoFlags, x, y, width, height) -> true);

        return thumbnailOut;
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
