
package com.novell.asn1.ldap;

import com.novell.asn1.*;

/**
 *       AttributeValueAssertion ::= SEQUENCE {
 *               attributeDesc   AttributeDescription,
 *               assertionValue  AssertionValue }
 *
 */
public class AttributeValueAssertion extends ASN1Sequence {

	/**
	 *
	 */
	public AttributeValueAssertion(AttributeDescription ad, AssertionValue av)
	{
		super(2);
		add(ad);
		add(av);
	}

}

