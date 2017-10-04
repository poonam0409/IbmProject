package com.ibm.is.sappack.cw.app.services.bdr;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class BphImportFileContainer {

	private byte[] fileContent;
	private BphImportFileType fileType;

	public BphImportFileContainer() {
		this.fileContent = new byte[0];
	}

	public BphImportFileContainer(InputStream inputStream, BphImportFileType fileType) throws IOException {
		setFileContent(inputStream);
		setFileType(fileType);
	}

	public byte[] getFileContent() {
		return this.fileContent;
	}

	public void setFileContent(InputStream inputStream) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		try {
			int nRead;
			byte[] data = new byte[16384];

			while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
				baos.write(data, 0, nRead);
			}

			baos.flush();
			this.fileContent = baos.toByteArray();
		} finally {
			baos.close();
		}
	}

	public BphImportFileType getFileType() {
		return fileType;
	}

	public void setFileType(BphImportFileType fileType) {
		this.fileType = fileType;
	}
}
