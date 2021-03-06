/* ==================================================================
 *  Eniware Open Source:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.web.security;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import org.apache.commons.codec.digest.DigestUtils;

/**
 * {@link HttpServletRequestWrapper} to aid in computing hash values for the
 * request content.
 * 
 * @version 1.0
 * @since 1.11
 */
public class SecurityHttpServletRequestWrapper extends HttpServletRequestWrapper {

	private final int maximumLength;
	private boolean requestBodyCached;
	private byte[] cachedRequestBody; // TODO: support writing to temp file if body > maximumLength!

	private byte[] cachedMD5 = null;
	private byte[] cachedSHA1 = null;
	private byte[] cachedSHA256 = null;

	/**
	 * Construct from a request.
	 * 
	 * @param request
	 *        the request to wrap
	 */
	public SecurityHttpServletRequestWrapper(HttpServletRequest request, int maxLength) {
		super(request);
		this.maximumLength = maxLength;
	}

	private void cacheRequestBody() throws IOException {
		if ( requestBodyCached ) {
			return;
		}
		requestBodyCached = true;
		InputStream in = super.getInputStream();
		ByteArrayOutputStream out = new ByteArrayOutputStream(4096);
		try {
			int byteCount = 0;
			byte[] buffer = new byte[4096];
			int bytesRead = -1;
			while ( (bytesRead = in.read(buffer)) != -1 ) {
				out.write(buffer, 0, bytesRead);
				byteCount += bytesRead;
				if ( byteCount > this.maximumLength ) {
					throw new SecurityException("Request body too large.");
				}
			}
			out.flush();
			cachedRequestBody = out.toByteArray();
		} finally {
			try {
				in.close();
			} catch ( IOException ex ) {
			}
			try {
				out.close();
			} catch ( IOException ex ) {
			}
		}
	}

	/**
	 * Compute the MD5 hash of the request body.
	 * 
	 * @return the MD5 hash, or <em>null</em> if there is no request content
	 * @throws IOException
	 *         if an IO exception occurs
	 * @throws SecurityException
	 *         if the request content length is larger than the configured
	 *         {@code maximumLength}
	 */
	public byte[] getContentMD5() throws IOException {
		byte[] digest = cachedMD5;
		if ( digest != null ) {
			return digest;
		}
		cacheRequestBody();
		if ( cachedRequestBody == null || cachedRequestBody.length < 1 ) {
			return null;
		}
		digest = DigestUtils.md5(cachedRequestBody);
		cachedMD5 = digest;
		return digest;
	}

	/**
	 * Compute the SHA1 hash of the request body.
	 * 
	 * @return the SHA1 hash, or <em>null</em> if there is no request content
	 * @throws IOException
	 *         if an IO exception occurs
	 * @throws SecurityException
	 *         if the request content length is larger than the configured
	 *         {@code maximumLength}
	 * @since 1.2
	 */
	public byte[] getContentSHA1() throws IOException {
		byte[] digest = cachedSHA1;
		if ( digest != null ) {
			return digest;
		}
		cacheRequestBody();
		if ( cachedRequestBody == null || cachedRequestBody.length < 1 ) {
			return null;
		}
		digest = DigestUtils.sha1(cachedRequestBody);
		cachedSHA1 = digest;
		return digest;
	}

	/**
	 * Compute the SHA256 hash of the request body.
	 * 
	 * @return the SHA256 hash, or <em>null</em> if there is no request content
	 * @throws IOException
	 *         if an IO exception occurs
	 * @throws SecurityException
	 *         if the request content length is larger than the configured
	 *         {@code maximumLength}
	 * @since 1.2
	 */
	public byte[] getContentSHA256() throws IOException {
		byte[] digest = cachedSHA256;
		if ( digest != null ) {
			return digest;
		}
		cacheRequestBody();
		if ( cachedRequestBody == null || cachedRequestBody.length < 1 ) {
			return null;
		}
		digest = DigestUtils.sha256(cachedRequestBody);
		cachedSHA256 = digest;
		return digest;
	}

	@Override
	public ServletInputStream getInputStream() throws IOException {
		if ( requestBodyCached ) {
			return new ServletInputStream() {

				final private ByteArrayInputStream byis = new ByteArrayInputStream(cachedRequestBody);

				@Override
				public int read() throws IOException {
					return byis.read();
				}

				@Override
				public boolean isFinished() {
					return byis.available() < 1;
				}

				@Override
				public boolean isReady() {
					return true;
				}

				@Override
				public void setReadListener(ReadListener listener) {
					// ignore
				}
			};
		}
		return super.getInputStream();
	}

}
