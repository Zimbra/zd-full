/*
 * 
 */
package org.jivesoftware.wildfire.net;

/**
 * A TLSStatus enum describing the current handshaking state of this TLS connection.
 *
 * This source file originally from the Tigase project (http://www.tigase.org). Used with permission
 * (DO NOT REMOVE ATTRIBUTION!)
 * 
 * @author Artur Hefczyc 
 */
public enum TLSStatus {

	/**
	 * ust send data to the remote side before handshaking can continue.
	 */
	NEED_WRITE,

	/**
	 * Need to receive data from the remote side before handshaking can continue.
	 */
	NEED_READ,

	/**
	 * Not be able to unwrap the incoming data because there were not enough source bytes available
	 * to make a complete packet.
	 */
	UNDERFLOW,

	/**
	 * The operation just closed this side of the SSLEngine, or the operation could not be completed
	 * because it was already closed.
	 */
	CLOSED,

	/**
	 * Handshaking is OK.
	 */
	OK;
}
