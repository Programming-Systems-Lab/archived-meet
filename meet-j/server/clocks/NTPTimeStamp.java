/*
 * NTPTimeStamp.java
 *
 * Created on December 6, 2002, 1:41 AM
 */
//package psl.meet.server.clocks;

import java.util.BitSet;
import java.util.Date;
import java.math.BigInteger;
import java.lang.Math;

/**
 *
 * @author mkuba
 */
public class NTPTimeStamp {


    // seconds since Jan 1 1900 00:00:00 UT
    private long secs;

    // fraction of a second
    private float fracsecs;

    // system offset, seconds between 1970 and 1900
    private final BigInteger OFFSET = new BigInteger("2208970800");

    /**
     * create a new NTPTimeStamp
     */
    public NTPTimeStamp() {
	Date now = new Date();
	secs = (long)(now.getTime() / 1000);
	secs += OFFSET.longValue();

	// parse into fracsecs
	float foo = (float)(now.getTime() % 1000);
	fracsecs = foo / 1000;
    }


    /**
     * create a new NTPTimeStamp, where
     * system clock is set to a non-UT timezone
     */
    public NTPTimeStamp(int tz) {
	Date now = new Date();
	secs = (long)(now.getTime() / 1000);
	secs += OFFSET.longValue();
	secs -= tz*3600;

	// parse into fracsecs
	float foo = (float)(now.getTime() % 1000);
	fracsecs = foo / 1000;

    }


    /**
     * create an NTPTimeStamp for a given timestamp and timezone
     * time == milliseconds since 1970
     */
    public NTPTimeStamp(long time, int tz) {
	secs = (long)(time / 1000) + OFFSET.longValue() - (tz*3600);
	float foo = (float)(time % 1000);
	fracsecs = foo / 1000;
    }

    /**
     * create an NTPTimeStamp for a given Date object and timezone
     */
    public NTPTimeStamp(Date now, int tz) {
	long time = now.getTime();
	secs = (long)(time / 1000) + OFFSET.longValue() - (tz*3600);

	float foo = (float)(time % 1000);
	fracsecs = foo / 1000;
    }


    /**
     * create an NTPTimeStamp given 8 bytes in the NTP Timestamp format
     */
    public NTPTimeStamp(byte[] b) {

	// parse the bytes to get a long

	byte[] c = new byte[5];
	c[0] = 0;
	c[1] = b[0];
	c[2] = b[1];
	c[3] = b[2];
	c[4] = b[3];

	BigInteger bigint = new BigInteger(c);
        secs = bigint.longValue();

	byte[] d = new byte[4];

	for (int i=0; i<4; i++) {
	    d[i] = b[i+4];
	}

	// parse last four bytes
	fracsecs = bytesToFloat(d);
    }

    /**
     * return number of seconds since 1900
     */
    public long getSecs() {
	return secs;
    }


    /**
     * return fraction of second
     */
    public float getFracSecs() {
	return fracsecs;
    }


    /**
     * return milliseconds since 1900
     */
    public long getLong() {
	long foo = (long)(1000 * fracsecs);

	//System.out.println("secs = " + secs);
	//System.out.println("fracsecs = " + fracsecs);

	return (1000*secs + foo);
    }

    /**
     * take the seconds, return
     * 8 bytes in NTP Timestamp format
     * for use in Tx and Rx of NTP info
     */
    public byte[] getBytes() {

	BigInteger bar = BigInteger.valueOf(secs);
	byte[] b1 = bar.toByteArray();

	
	if (b1.length > 4) {
	    for (int i=0; i<4; i++) {
		b1[i] = b1[i+1];
	    }
	}	

        BitSet bs = floatToFixedPoint(fracsecs);
        byte[] b2 = toByteArray(bs);

	byte[] output = new byte[8];

	for (int i=0; i< 4; i++) {
	    output[i] = b1[i];
	    output[i+4] = b2[i];
	}

	return output;

    }


    /**
     * Returns a bitset containing the values in bytes.
     * The byte-ordering of bytes must be big-endian which means
     * the most significant bit is in element 0.
     */
    public static BitSet fromByteArray(byte[] bytes) {
	BitSet bits = new BitSet(32);

	for (int i=0; i<4; i++) {
	    for (int j=0; j<8; j++) {
		if ((bytes[4-i-1] & (1 << j)) > 0) {
		    bits.set(8*i + j);
		}
	    }
	}

	return bits;
    }
    
    
    /**
     * BitSet -> ByteArray
     */    
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


    /**
     * DEBUG method
     */
    private static void printByteArray(byte[] b) {
        for (int i=0; i<b.length; i++) {
            System.out.print(b[i] + " ");
            if (i % 8 == 7) System.out.print("\n");
        }
    }


    /**
     * bytes to a floating point
     */
    private static float bytesToFloat(byte[] b) {
	
	float output = 0;

	BitSet bs = fromByteArray(b);

	for (int i=0; i<32; i++) {
	    if (bs.get(i)) {
		output += Math.pow(2,i-32);
	    }
	}

	return output;

    }

    /**
     * floating point to fixed point
     */
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

    /**
     * test program
     */
    public static void main(String args[]) throws Exception {

	Date nowDate = new Date();
	System.out.println("Date:");
	System.out.println(nowDate.getTime());

	System.out.println("Test default constructor");
	NTPTimeStamp now = new NTPTimeStamp();
	printByteArray(now.getBytes());
	System.out.println(now.getLong());

	System.out.println("Test byte[] constructor");

	byte[] b = new byte[8];
	b[0] = -125;
	b[1] = -86;
	b[2] = 56;
	b[3] = 48;
	b[4] = -128;
	b[5] = 0;
	b[6] = 0;
	b[7] = 0;
	NTPTimeStamp byteTest = new NTPTimeStamp(b);
	printByteArray(byteTest.getBytes());
	System.out.println(byteTest.getLong());

	System.out.println("Test long constructor");
	
	BigInteger BigOffset = new BigInteger("2208970800000");
	NTPTimeStamp longTest = new NTPTimeStamp(BigOffset.longValue(),-5);
	printByteArray(longTest.getBytes());
	System.out.println(longTest.getLong());

    }
    

}
