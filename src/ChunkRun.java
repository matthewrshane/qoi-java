public class ChunkRun extends Chunk {

    /**
     * Constructs a new Chunk with the type of QOI_OP_RUN.
     * @param length
     */
    public ChunkRun(int length) {
        super(1);
        data = new int[getSize()];
        
        int run = length + (-1);

        data[0] = 0xc0 | (run & 0x3f);
    }

}
