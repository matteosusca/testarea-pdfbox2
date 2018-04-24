package mkl.testarea.pdfbox2.content;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.util.Matrix;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.common.io.ByteStreams;

/**
 * @author mkl
 */
public class AddImage {
    final static File RESULT_FOLDER = new File("target/test-outputs", "content");

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        RESULT_FOLDER.mkdirs();
    }

    /**
     * <a href="https://stackoverflow.com/questions/49958604/draw-image-at-mid-position-using-pdfbox-java">
     * Draw image at mid position using pdfbox Java
     * </a>
     * <p>
     * This is the OP's original code. It mirrors the image.
     * This can be fixed as shown in {@link #testImageAppendNoMirror()}.
     * </p>
     */
    @Test
    public void testImageAppendLikeShanky() throws IOException {
        try (   InputStream resource = getClass().getResourceAsStream("/mkl/testarea/pdfbox2/sign/test.pdf");
                InputStream imageResource = getClass().getResourceAsStream("Willi-1.jpg")   )
        {
            PDDocument doc = PDDocument.load(resource);
            PDImageXObject pdImage = PDImageXObject.createFromByteArray(doc, ByteStreams.toByteArray(imageResource), "Willi");

            int w = pdImage.getWidth();
            int h = pdImage.getHeight();

            PDPage page = doc.getPage(0);
            PDPageContentStream contentStream = new PDPageContentStream(doc, page, PDPageContentStream.AppendMode.APPEND, true);

            float x_pos = page.getCropBox().getWidth();
            float y_pos = page.getCropBox().getHeight();

            float x_adjusted = ( x_pos - w ) / 2;
            float y_adjusted = ( y_pos - h ) / 2;

            Matrix mt = new Matrix(1f, 0f, 0f, -1f, page.getCropBox().getLowerLeftX(), page.getCropBox().getUpperRightY());
            contentStream.transform(mt);
            contentStream.drawImage(pdImage, x_adjusted, y_adjusted, w, h);
            contentStream.close();

            doc.save(new File(RESULT_FOLDER, "test-with-image-shanky.pdf"));
            doc.close();

        }
    }

    /**
     * <a href="https://stackoverflow.com/questions/49958604/draw-image-at-mid-position-using-pdfbox-java">
     * Draw image at mid position using pdfbox Java
     * </a>
     * <p>
     * This is a fixed version of the the OP's original code, cf.
     * {@link #testImageAppendLikeShanky()}. It does not mirrors the image.
     * </p>
     */
    @Test
    public void testImageAppendNoMirror() throws IOException {
        try (   InputStream resource = getClass().getResourceAsStream("/mkl/testarea/pdfbox2/sign/test.pdf");
                InputStream imageResource = getClass().getResourceAsStream("Willi-1.jpg")   )
        {
            PDDocument doc = PDDocument.load(resource);
            PDImageXObject pdImage = PDImageXObject.createFromByteArray(doc, ByteStreams.toByteArray(imageResource), "Willi");

            int w = pdImage.getWidth();
            int h = pdImage.getHeight();

            PDPage page = doc.getPage(0);
            PDPageContentStream contentStream = new PDPageContentStream(doc, page, PDPageContentStream.AppendMode.APPEND, true);

            float x_pos = page.getCropBox().getWidth() + page.getCropBox().getLowerLeftX();
            float y_pos = page.getCropBox().getHeight() + page.getCropBox().getLowerLeftY();

            float x_adjusted = ( x_pos - w ) / 2;
            float y_adjusted = ( y_pos - h ) / 2;

            contentStream.drawImage(pdImage, x_adjusted, y_adjusted, w, h);
            contentStream.close();

            doc.save(new File(RESULT_FOLDER, "test-with-image-no-mirror.pdf"));
            doc.close();

        }
    }
}
