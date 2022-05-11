public class QOIHeader {
    
    private byte[] data;

    public QOIHeader(int width, int height) {
        data = new byte[14];
        for(int i = 0; i < 4; i++) {
            data[i] = (byte) "qoif".charAt(i);
        }

        for(int i = 4; i < 8; i++) {
            data[i] = (byte) ((width >> ((7 - i) * 8)) & 0xff); 
        }

        for(int i = 8; i < 12; i++) {
            data[i] = (byte) ((height >> ((11 - i) * 8)) & 0xff); 
        }

        data[12] = (byte) 4;
        data[13] = (byte) 0;
    }

    public byte[] getData() {
        return data;
    }

}
