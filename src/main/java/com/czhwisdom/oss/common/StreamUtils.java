package com.czhwisdom.oss.common;

import java.io.*;

public class StreamUtils {

	public static final int BUFFER_SIZE = 1024;

	/**
	 * tansfer the stream's data to byte[]
	 * 
	 * @param inputStream
	 * @return
	 */
	public static byte[] read(InputStream inputStream) {
		byte[] result;

		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

		byte[] buffer = new byte[BUFFER_SIZE];

		int count = 0;
		try {
			while ((count = inputStream.read(buffer)) != -1) {
				outputStream.write(buffer, 0, count);
			}

			outputStream.flush();

			result = outputStream.toByteArray();

			outputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
			result = new byte[0];
		}

		return result;
	}

	/**
	 * convert Object to byte[]
	 * 
	 * @param object
	 * @return
	 * @throws IOException
	 */
	public static byte[] read(Object object) throws IOException {
		ByteArrayOutputStream byteOutput = new ByteArrayOutputStream();
		ObjectOutputStream objectOutput = new ObjectOutputStream(byteOutput);

		objectOutput.writeObject(object);
		objectOutput.flush();
		objectOutput.close();

		try {
			byteOutput.flush();
			return byteOutput.toByteArray();
		} finally {
			byteOutput.close();
		}
	}

	/**
	 * convert the byte[] to Object
	 * 
	 * @param data
	 * @return
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static Object write(byte[] data) throws IOException,
			ClassNotFoundException {
		ObjectInputStream inputStream = new ObjectInputStream(
				new ByteArrayInputStream(data));
		try {
			return inputStream.readObject();
		} catch (ClassNotFoundException e) {
			throw e;
		} finally {
			inputStream.close();
		}
	}

}
