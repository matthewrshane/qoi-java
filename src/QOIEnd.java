public class QOIEnd {
    
    private byte[] data;

    public QOIEnd() {
        data = new byte[8];
        for(int i = 0; i < 7; i++) {
            data[i] = (byte) 0x00;
        }

        data[7] = (byte) 0x01;
    }

    public byte[] getData() {
        return data;
    }

}
