public class ChunkDiff extends Chunk {

    /**
     * Constructs a new Chunk with the type of QOI_OP_DIFF.
     * @param rgb
     * @param rgbLast
     */
    public ChunkDiff(int rgb, int rgbLast) {
        super(1);
        data = new int[getSize()];
        
        int r = (rgb >> 16) & 0xff;
        int g = (rgb >> 8) & 0xff;
        int b = rgb & 0xff;

        int rLast = (rgbLast >> 16) & 0xff;
        int gLast = (rgbLast >> 8) & 0xff;
        int bLast = rgbLast & 0xff;

        int dr = r - rLast + 2;
        int dg = g - gLast + 2;
        int db = b - bLast + 2;

        data[0] = 0x40 | ((dr & 0x03) << 4) | ((dg & 0x03) << 2) | (db & 0x03);
    }

}
