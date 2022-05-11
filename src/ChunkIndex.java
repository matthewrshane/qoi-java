public class ChunkIndex extends Chunk {

    /**
     * Constructs a new Chunk with the type of of QOI_OP_INDEX.
     * @param index
     */
    public ChunkIndex(int index) {
        super(1);
        data = new int[getSize()];
        data[0] = 0x00 | index;
    }

}
