import java.nio.ByteBuffer;

public class Packet {
    private int sequenceNumber;
    private int windowSize;
    private byte[] data;
    private boolean isAck;
    private boolean isSyn;
    private boolean isFin;

    public Packet(int sequenceNumber, int windowSize, byte[] data, boolean isAck, boolean isSyn, boolean isFin) {
        this.sequenceNumber = sequenceNumber;
        this.windowSize = windowSize;
        this.data = data;
        this.isAck = isAck;
        this.isSyn = isSyn;
        this.isFin = isFin;
    }

    public int getSequenceNumber() {
        return sequenceNumber;
    }

    public int getWindowSize() {
        return windowSize;
    }

    public byte[] getData() {
        return data;
    }

    public boolean isAck() {
        return isAck;
    }

    public boolean isSyn() {
        return isSyn;
    }

    public boolean isFin() {
        return isFin;
    }

    public byte[] toBytes() {
        ByteBuffer buffer = ByteBuffer.allocate(12 + data.length);
        buffer.putInt(sequenceNumber);
        buffer.putInt(windowSize);
        byte flags = 0;
        flags |= isAck ? 1 << 2 : 0;
        flags |= isSyn ? 1 << 1 : 0;
        flags |= isFin ? 1 : 0;
        buffer.put(flags);
        buffer.put(data);
        return buffer.array();
    }

    public static Packet fromBytes(byte[] bytes) {
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        int sequenceNumber = buffer.getInt();
        int windowSize = buffer.getInt();
        byte flags = buffer.get();
        boolean isAck = (flags & (1 << 2)) != 0;
        boolean isSyn = (flags & (1 << 1)) != 0;
        boolean isFin = (flags & 1) != 0;
        byte[] data = new byte[bytes.length - 12];
        buffer.get(data);
        return new Packet(sequenceNumber, windowSize, data, isAck, isSyn, isFin);
    }

    public void setSquenceNumber(int sequenceNumber) {
    }
}