/*
 * NTPTimeStamp.java
 *
 * Created on December 6, 2002, 1:41 AM
 */
package psl.meet.server.clocks;

import java.util.BitSet;
import java.util.Date;

/**
 *
 * @author mkuba
 */
public class NTPTimeStamp {


    // milliseconds since Jan 1 1900 00:00:00 UT
    private long milli;


    public NTPTimeStamp() {
	Date now = new Date();
	milli = now.getTime();;
    }


    public NTPTimeStamp(long time) {
	milli = time;
    }


    public NTPTimeStamp(byte[] b) {

	// parse the bytes to get a long
    }


    // take the milliseconds, return
    // 8 bytes in NTP Timestamp format
    // for use in Tx and Rx of NTP info

    public byte[] getBytes() {

	long now = milli;

        // convert msec to sec
        int time1 = (int) now / 1000;

        // get remainder for 2nd part of timestamp
        long time2 = now % 1000;
        float foo = (float) time2 /  1000;

        BitSet bs = floatToFixedPoint(foo);
        byte[] b2 = toByteArray(bs);

        byte[] b = new byte[8];

        // in == long since Epoch
        // must convert to NTP TimeStamp format
        // first 32 bits = seconds since 1900 00:00:00 UT
        // second 32 bits = fixed point fraction of a second

        b[0] = (byte)(time1 & 0xff);
        b[1] = (byte)((time1 >> 8) & 0xff);
        b[2] = (byte)((time1 >> 16) & 0xff);
        b[3] = (byte)(time1 >>> 24);

        b[4] = b2[0];
        b[5] = b2[1];
        b[6] = b2[2];
        b[7] = b2[3];

	return b;
    }


    // method to take byte array and
    // calculate proper long

    public static long NTPTimeStampToLong(byte[] b) {

        int in = (b[0] & 0xff) | ((b[1] << 8) & 0xff00) | ((b[2] << 24) >>> 8) | (b[3] << 24);
        return 0;
    }



    // Returns a bitset containing the values in bytes.
    // The byte-ordering of bytes must be big-endian which means the most significant bit is in element 0.
    public static BitSet fromByteArray(byte[] bytes) {
	BitSet bits = new BitSet();
	for (int i=0; i<bytes.length*8; i++) {
	    if ((bytes[bytes.length-i/8-1]&(1<<(i%8))) > 0) {
		bits.set(i);
	    }
	}
	return bits;
    }
    

    private static byte[] toByteArray(BitSet bits) {
        byte[] bytes = new byte[4];

        for (int i=0; i<4; i++) {
            for (int j=0; j<8; j++) {
                if (bits.get(8*i + j)) {
                    bytes[4-(i)-1] |= 1 << j;
                }
            }
        }

        return bytes;
    }


    private static void printByteArray(byte[] b) {
        for (int i=0; i<b.length; i++) {
            System.out.print(b[i] + " ");
            if (i % 8 == 7) System.out.print("\n");
        }
    }


    private static BitSet floatToFixedPoint(float foo) {

        BitSet output = new BitSet(32);

        foo = foo % 1;
        int idx = 0;

        while (idx < 32) {
            if (foo >= 0.5) {
                output.set(31-idx);
            }
            idx++;
            foo = foo * 10;
            foo = foo % 1;
        }

        return output;

    }


    public static void main(String args[]) throws Exception {

	NTPTimeStamp now = new NTPTimeStamp();

	printByteArray(now.getBytes());

	Date epochcalc = new Date(0,0,1);

	System.out.println(epochcalc);
	System.out.println(epochcalc.getTime());

    }
    

}
