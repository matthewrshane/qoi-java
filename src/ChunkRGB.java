public class ChunkRGB extends Chunk {

    /**
     * Constructs a new Chunk with the type of QOI_OP_RGB.
     * @param rgb
     */
    public ChunkRGB(int rgb) {
        super(4);
        data = new int[getSize()];
        data[0] = 0xfe;
        data[1] = (rgb >> 16) & 0xff;
        data[2] = (rgb >> 8) & 0xff;
        data[3] = rgb & 0xff;
    }

}
