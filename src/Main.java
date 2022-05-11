import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;

public class Main {
    public static void main(String[] args) {
        new Main();
    }

    public Main() {
        File imageFile = null;

        JFileChooser fileChooser = new JFileChooser();

        if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            imageFile = fileChooser.getSelectedFile();
        } else {
            System.out.println("No file chosen. Aborting.");
            System.exit(-1);
        }

        BufferedImage image = null;

        try {
            image = ImageIO.read(imageFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

        QOIHeader header = new QOIHeader(image.getWidth(), image.getHeight());

        int[] index = new int[64];

        int imageLength = image.getWidth() * image.getHeight();

        int[] pixels = new int[imageLength];
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                pixels[(y * image.getWidth()) + x] = image.getRGB(x, y);
            }
        }

        ArrayList<Chunk> chunks = new ArrayList<Chunk>();

        int lastPxPos = imageLength - 1;

        int run = 0;
        int px = 0;
        int pxPrev = 0;
        int pxIndex = 0;

        int r, g, b;
        int rPrev, gPrev, bPrev;

        int dataBytes = 0;

        for (int i = 0; i < pixels.length; i++) {
            px = pixels[i];

            r = (px >> 16) & 0xff;
            g = (px >> 8) & 0xff;
            b = px & 0xff;

            rPrev = (pxPrev >> 16) & 0xff;
            gPrev = (pxPrev >> 8) & 0xff;
            bPrev = pxPrev & 0xff;

            // Check if the current pixel is equal to the previous pixel. This constitues a
            // run.
            if (px == pxPrev) {
                run++;
                if (run == 62 || i == lastPxPos) {
                    chunks.add(new ChunkRun(run));
                    dataBytes += 1;
                    run = 0;
                }
            } else {
                pxIndex = getIndexHash(r, g, b);

                // Check if there was a run before this pixel that has now ended.
                if (run > 0) {
                    chunks.add(new ChunkRun(run));
                    dataBytes += 1;
                    run = 0;
                }

                // Check if the stored value in the index is equal to our current pixel. If so,
                // we send an index chunk.
                if (index[pxIndex] == px) {
                    chunks.add(new ChunkIndex(pxIndex));
                    dataBytes += 1;
                } else {
                    // There was no index, so we'll set the index to the current pixel.
                    index[pxIndex] = px;

                    // Get the differences in r, g, and b.
                    int dr = r - rPrev;
                    int dg = g - gPrev;
                    int db = b - bPrev;

                    int dr_dg = dr - dg;
                    int db_dg = db - dg;

                    // Check to see if we should send a diff, luma, or rgb chunk.
                    if (dr > -3 && dr < 2 &&
                            dg > -3 && dg < 2 &&
                            db > -3 && db < 2) {
                        // We want to send a diff chunk here.
                        chunks.add(new ChunkDiff(px, pxPrev));
                        dataBytes += 1;
                    } else if (dr_dg > -9 && dr_dg < 8 &&
                            dg > -33 && dg < 32 &&
                            db_dg > -9 && db_dg < 8) {
                        // We want to send a LUMA chunk here.
                        chunks.add(new ChunkLUMA(px, pxPrev));
                        dataBytes += 2;
                    } else {
                        // Otherwise, we want to send an RGB chunk here.
                        chunks.add(new ChunkRGB(px));
                        dataBytes += 4;
                    }
                }
            }
            pxPrev = px;
        }

        QOIEnd end = new QOIEnd();

        byte[] bytes = new byte[dataBytes + header.getData().length + end.getData().length];
        int dataIndex = 0;

        for (int i = 0; i < header.getData().length; i++) {
            bytes[dataIndex + i] = header.getData()[i];
        }
        dataIndex += header.getData().length;

        for (Chunk chunk : chunks) {
            for (int d = 0; d < chunk.getData().length; d++) {
                bytes[dataIndex + d] = (byte) chunk.getData()[d];
            }
            dataIndex += chunk.getSize();
        }

        for (int i = 0; i < end.getData().length; i++) {
            bytes[dataIndex + i] = end.getData()[i];
        }
        dataIndex += end.getData().length;

        if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
            imageFile = fileChooser.getSelectedFile();
        } else {
            System.out.println("No file chosen. Aborting.");
            System.exit(-1);
        }

        try {
            Path path = Paths.get(imageFile.toURI());
            Files.write(path, bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private int getIndexHash(int r, int g, int b) {
        return (r * 3 + g * 5 + b * 7 + 255 * 11) % 64;
    }
}
