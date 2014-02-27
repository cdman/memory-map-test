import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;

public class Main {
	private static final int _4M = 4 * 1024 * 1024;
	private final static byte[] BUFF = new byte[_4M];

	/**
	 * Method to ensure that buffer reads aren't optimized out
	 */
	private static void checkBuff(byte[] buff) {
		int sum = 0;
		for (int i = 0; i < buff.length; ++i) {
			sum += buff[i];
		}
		if ((sum & 1) > 2) {
			throw new RuntimeException();
		}
	}

	private static void read(ByteBuffer source) {
		for (int i = 0; i < 8; ++i) {
			System.err.println(i);
			for (int j = 0; j < 1024; ++j) {
				source.clear();
				source.get(BUFF);
				checkBuff(BUFF);
			}
		}
	}

	private static void fillBuff(byte[] buff, int start) {
		for (int i = 0; i < buff.length; ++i) {
			buff[i] = (byte)(i + start);
		}
	}

	private static void write(ByteBuffer destination) {
		for (int i = 0; i < 8; ++i) {
			System.err.println(i);
			fillBuff(BUFF, i);
			for (int j = 0; j < 1024; ++j) {
				destination.clear();
				destination.put(BUFF);
			}
		}
	}

	public static void main(String[] args) throws Exception {
		RandomAccessFile f = new RandomAccessFile(args[0], "rw");
		FileChannel fc = f.getChannel();

		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		while (true) {
			System.out.println("File: " + args[0]);
			System.out.println("1. Read0 2. Read1 3. Write0 4. Write1");

			String l = in.readLine();
			if (l.equals("1")) {
				ByteBuffer read0 = fc.map(FileChannel.MapMode.READ_ONLY, 0, _4M);
				read0.order(ByteOrder.nativeOrder());
				read(read0);
			} else if (l.equals("2")) {
				ByteBuffer read1 = fc.map(FileChannel.MapMode.READ_ONLY, _4M, _4M);
				read1.order(ByteOrder.nativeOrder());
				read(read1);
			} else if (l.equals("3")) {
				ByteBuffer write0 = fc.map(FileChannel.MapMode.READ_WRITE, 0, _4M);
				write0.order(ByteOrder.nativeOrder());
				write(write0);
			} else if (l.equals("4")) {
				ByteBuffer write1 = fc.map(FileChannel.MapMode.READ_WRITE, _4M, _4M);
				write1.order(ByteOrder.nativeOrder());
				write(write1);
			} else {
				break;
			}
		}

		fc.force(true);
		f.getFD().sync();
		fc.close();
		f.close();
	}
}
