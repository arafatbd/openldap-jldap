
package com.novell.ldap.rfc2251;

import java.io.*;
import com.novell.ldap.asn1.*;

/**
 *       DelResponse ::= [APPLICATION 11] LDAPResult
 */
public class RfcDelResponse extends RfcLDAPResult {

	//*************************************************************************
	// Constructors for DelResponse
	//*************************************************************************

	/**
	 * The only time a client will create a DelResponse is when it is
	 * decoding it from an InputStream
	 */
	public RfcDelResponse(ASN1Decoder dec, InputStream in, int len)
		throws IOException
	{
		super(dec, in, len);
	}

	//*************************************************************************
	// Accessors
	//*************************************************************************

	/**
	 * Override getIdentifier to return an application-wide id.
	 */
	public ASN1Identifier getIdentifier()
	{
		return new ASN1Identifier(ASN1Identifier.APPLICATION, true,
			                       RfcProtocolOp.DEL_RESPONSE);
	}

}

