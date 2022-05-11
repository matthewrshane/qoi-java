public class ChunkLUMA extends Chunk {

    /**
     * Constructs a new Chunk with the type of QOI_OP_LUMA.
     * @param rgb
     * @param rgbLast
     */
    public ChunkLUMA(int rgb, int rgbLast) {
        super(2);
        data = new int[getSize()];
        
        int r = (rgb >> 16) & 0xff;
        int g = (rgb >> 8) & 0xff;
        int b = rgb & 0xff;

        int rLast = (rgbLast >> 16) & 0xff;
        int gLast = (rgbLast >> 8) & 0xff;
        int bLast = rgbLast & 0xff;

        int dr = r - rLast;
        int dg = g - gLast;
        int db = b - bLast;

        int diffGreen = dg + 32;
        int dr_dg = dr - dg + 8;
        int db_dg = db - dg + 8;

        data[0] = 0x80 | (diffGreen & 0x3f);
        data[1] = ((dr_dg & 0x0f) << 4) | (db_dg & 0x0f);
    }

}
