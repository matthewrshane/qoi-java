public class Chunk {

    protected int[] data;
    private int size;

    protected Chunk(int size) {
        this.size = size;
    }

    public int[] getData() {
        return data;
    }

    public int getSize() {
        return size;
    }

}
