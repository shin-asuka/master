package com.vipkid.trpm.filter;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.zip.GZIPOutputStream;

import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

public class GZipServletResponseWrapper extends HttpServletResponseWrapper {

	private GZipServletOutputStream gzipServletOutputStream = null;

	private PrintWriter printWriter = null;

	public GZipServletResponseWrapper(HttpServletResponse response) {
		super(response);
	}

	public void close() throws IOException {
		if (null != printWriter) {
			printWriter.close();
		} else if (null != gzipServletOutputStream) {
			gzipServletOutputStream.close();
		}
	}

	@Override
	public void flushBuffer() throws IOException {
		super.flushBuffer();

		if (null != printWriter) {
			printWriter.flush();
		} else if (null != gzipServletOutputStream) {
			gzipServletOutputStream.flush();
		}
	}

	@Override
	public ServletOutputStream getOutputStream() throws IOException {
		if (null != printWriter) {
			throw new IllegalStateException("getWriter() has already been called on this response.");
		}

		if (null == gzipServletOutputStream) {
			gzipServletOutputStream = new GZipServletOutputStream(getResponse().getOutputStream());
		}

		return gzipServletOutputStream;
	}

	@Override
	public PrintWriter getWriter() throws IOException {
		if (null != gzipServletOutputStream) {
			throw new IllegalStateException(
					"getOutputStream() has already been called on this response.");
		}

		if (null == printWriter) {
			gzipServletOutputStream = new GZipServletOutputStream(getResponse().getOutputStream());

			printWriter = new PrintWriter(new OutputStreamWriter(gzipServletOutputStream,
					getResponse().getCharacterEncoding()));
		}

		return printWriter;
	}

	class GZipServletOutputStream extends ServletOutputStream {

		private GZIPOutputStream gzipOutputStream = null;

		public GZipServletOutputStream(OutputStream outputStream) throws IOException {
			super();
			gzipOutputStream = new GZIPOutputStream(outputStream);
		}

		@Override
		public void close() throws IOException {
			gzipOutputStream.close();
		}

		@Override
		public void flush() throws IOException {
			gzipOutputStream.flush();
		}

		@Override
		public void write(byte b[]) throws IOException {
			gzipOutputStream.write(b);
		}

		@Override
		public void write(byte b[], int off, int len) throws IOException {
			gzipOutputStream.write(b, off, len);
		}

		@Override
		public void write(int b) throws IOException {
			gzipOutputStream.write(b);
		}

		@Override
		public boolean isReady() {
			return false;
		}

		@Override
		public void setWriteListener(WriteListener writeListener) {
		}

	}

}
