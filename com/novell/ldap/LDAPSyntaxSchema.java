/* **************************************************************************
 * $OpenLDAP$
 *
 * Copyright (C) 1999, 2000, 2001 Novell, Inc. All Rights Reserved.
 *
 * THIS WORK IS SUBJECT TO U.S. AND INTERNATIONAL COPYRIGHT LAWS AND
 * TREATIES. USE, MODIFICATION, AND REDISTRIBUTION OF THIS WORK IS SUBJECT
 * TO VERSION 2.0.1 OF THE OPENLDAP PUBLIC LICENSE, A COPY OF WHICH IS
 * AVAILABLE AT HTTP://WWW.OPENLDAP.ORG/LICENSE.HTML OR IN THE FILE "LICENSE"
 * IN THE TOP-LEVEL DIRECTORY OF THE DISTRIBUTION. ANY USE OR EXPLOITATION
 * OF THIS WORK OTHER THAN AS AUTHORIZED IN VERSION 2.0.1 OF THE OPENLDAP
 * PUBLIC LICENSE, OR OTHER PRIOR WRITTEN CONSENT FROM NOVELL, COULD SUBJECT
 * THE PERPETRATOR TO CRIMINAL AND CIVIL LIABILITY.
 ******************************************************************************/

package com.novell.ldap;
import com.novell.ldap.client.SchemaParser;
import com.novell.ldap.client.AttributeQualifier;
import java.util.Enumeration;
import java.io.IOException;

/**
 * Represents a syntax definition in the directory schema.
 *
 * <p>The LDAPSyntaxSchema class represents the definition of a syntax.  It is
 * used to discover the known set of syntaxes in effect for the subschema. </p>
 *
 * <p>Although this extends LDAPSchemaElement, it does not use the name or
 * obsolete members. Therefore, calls to the getName method always return
 * null and to the isObsolete method always returns false. There is also no
 * matching getSyntaxNames method in LDAPSchema. Note also that adding and
 * removing syntaxes is not typically a supported feature of LDAP servers.</p>
 */

public class LDAPSyntaxSchema
                extends LDAPSchemaElement
{

    /**
     * Constructs a syntax for adding to or deleting from the schema.
     *
     * <p>Adding and removing syntaxes is not typically a supported
     * feature of LDAP servers. Novell eDirectory does not allow syntaxes to
     * be added or removed.</p>
     *
     * @param oid         The unique object identifier of the syntax - in
     *                    dotted numerical format.</br></br>
     *
     * @param description An optional description of the syntax.
     */
    public LDAPSyntaxSchema(String oid,
                           String description)
    {
        super(LDAPSchema.schemaTypeNames[LDAPSchema.SYNTAX]);
        super.oid = oid;
      	super.description = description;
        super.setValue(formatString());
        return;
    }

    /**
     * Constructs a syntax from the raw string value returned on a schema
     * query for LDAPSyntaxes.
     *
     * @param raw           The raw string value returned from a schema
     *                      query for ldapSyntaxes.
     */
    public LDAPSyntaxSchema(String raw)
    {
        super(LDAPSchema.schemaTypeNames[LDAPSchema.SYNTAX]);
    	try{
			SchemaParser parser = new SchemaParser( raw );

    		if( parser.getID() != null)
        		super.oid = parser.getID();
        	if( parser.getDescription() != null)
        		super.description = parser.getDescription();
        	Enumeration qualifiers = parser.getQualifiers();
        	AttributeQualifier attrQualifier;
        	while(qualifiers.hasMoreElements()){
        		attrQualifier = (AttributeQualifier) qualifiers.nextElement();
        		setQualifier(attrQualifier.getName(), attrQualifier.getValues());
         	}
            super.setValue( formatString() );
		}
        catch( IOException e){
        }
        return;
    }

	/**
    * Returns a string in a format suitable for directly adding to a
    * directory, as a value of the particular schema element class.
    *
    * @return A string representation of the syntax's definition.
    */
   protected String formatString() {

      StringBuffer valueBuffer = new StringBuffer("( ");
      String token;
      String[] strArray;

      if( (token = getID()) != null){
        valueBuffer.append(token);
      }
      if( (token = getDescription()) != null){
        valueBuffer.append(" DESC ");
        valueBuffer.append("'" + token + "'");
      }

      Enumeration en;
      if( (en = getQualifierNames()) != null){
      	String qualName;
       	String[] qualValue;
      	while( en.hasMoreElements() ) {
       		qualName = (String)en.nextElement();
         	valueBuffer.append( " " + qualName + " ");
          	if((qualValue = getQualifier( qualName )) != null){
           		if( qualValue.length > 1)
             			valueBuffer.append("( ");
                       	for(int i = 0; i < qualValue.length; i++ ){
                          	if( i > 0 )
                           		valueBuffer.append(" ");
                        	valueBuffer.append( "'" + qualValue[i] + "'");
                      	}
                       if( qualValue.length > 1)
                       	valueBuffer.append(" )");
            	}
      	}
      }
      valueBuffer.append(" )");
      return valueBuffer.toString();

   }

    // #######################################################################
    //   The following are deprecated and will be removed in fall of 2003
    // #######################################################################

    /**
     * @deprecated replaced by {@link #toString}.  This method
     * has been renamed to toString in IETF draft 17 of the Java LDAP
     * API (draft-ietf-ldapext-ldap-java-api-xx.txt) and will be removed
     *  in fall of 2003.
     */
    public String getValue() {
        return this.toString();
    }

   /**
    * @deprecated replaced by {@link LDAPSchema#add}.  This method
    * has been move to the object LDAPSchema in IETF draft 17 of the Java LDAP
    * API (draft-ietf-ldapext-ldap-java-api-xx.txt) and will be removed
    *  in fall of 2003.
    */
  public void add(LDAPConnection ld) throws LDAPException {
    try{
        add(ld,"");
    }
    catch(LDAPException e){
        throw e;
    }
  }

   /**
    * @deprecated replaced by {@link LDAPSchema#add}.  This method
    * has been move to the object LDAPSchema in IETF draft 17 of the Java LDAP
    * API (draft-ietf-ldapext-ldap-java-api-xx.txt) and will be removed
    *  in fall of 2003.
    */
  public void add(LDAPConnection ld, String dn) throws LDAPException {
    try{
        String attrSubSchema[] = { "subschemaSubentry" };
        LDAPSearchResults sr = ld.search( dn,
	                  LDAPConnection.SCOPE_BASE,
					  "objectclass=*",
					  attrSubSchema,
					  false);
        if(sr != null && sr.hasMoreElements()){
            String schemaDN;
	        LDAPEntry ent = sr.next();
	        LDAPAttributeSet attrSet = ent.getAttributeSet();
	        Enumeration en = attrSet.getAttributes();
	        LDAPAttribute attr;
	        if(en.hasMoreElements()){
	            attr = (LDAPAttribute) en.nextElement();
	            Enumeration enumString = attr.getStringValues();
	            if(enumString.hasMoreElements()){
                    schemaDN = (String) enumString.nextElement();
                    String[] attrSearchName= { "ldapSyntaxes" };
	                sr = ld.search( schemaDN,
	                        LDAPConnection.SCOPE_BASE,
	                        "objectclass=*",
			                attrSearchName,
			                false);
	                String attrName;
	                if(sr != null && sr.hasMoreElements()){
	                    ent = sr.next();
		                attrSet = ent.getAttributeSet();
		                en = attrSet.getAttributes();
		                while(en.hasMoreElements()){
		                    attr = (LDAPAttribute) en.nextElement();
                            attrName = attr.getName();
		                    if(attrName.equalsIgnoreCase("ldapSyntaxes")){
                            // add the value to the syntax values
							LDAPAttribute newValue = new LDAPAttribute(
                                        "ldapSyntaxes",getValue());
                            LDAPModification lModify = new LDAPModification(
                                LDAPModification.ADD,newValue);
                            ld.modify(schemaDN,lModify);
                        }
		                continue;
                    }
	            }
	        }
          }
	    }
      }

    catch( LDAPException e){
      throw e;
    }
  }

  /**
    * @deprecated replaced by {@link LDAPSchema#remove}.  This method
    * has been move to the object LDAPSchema in IETF draft 17 of the Java LDAP
    * API (draft-ietf-ldapext-ldap-java-api-xx.txt) and will be removed
    *  in fall of 2003.
    */
  public void remove(LDAPConnection ld) throws LDAPException {
    try{
        remove(ld,"");
    }
    catch(LDAPException e){
        throw e;
    }
  }
  /**
    * @deprecated replaced by {@link LDAPSchema#remove}.  This method
    * has been move to the object LDAPSchema in IETF draft 17 of the Java LDAP
    * API (draft-ietf-ldapext-ldap-java-api-xx.txt) and will be removed
    *  in fall of 2003.
    */
  public void remove(LDAPConnection ld, String dn) throws LDAPException {
    try{
        String attrSubSchema[] = { "subschemaSubentry" };
        LDAPSearchResults sr = ld.search( dn,
	                            LDAPConnection.SCOPE_BASE, "objectclass=*",
					            attrSubSchema, false);
	    if(sr != null && sr.hasMoreElements()){
            String schemaDN;
	        LDAPEntry ent = sr.next();
	        LDAPAttributeSet attrSet = ent.getAttributeSet();
	        Enumeration en = attrSet.getAttributes();
	        LDAPAttribute attr;
	        if(en.hasMoreElements()){
	            attr = (LDAPAttribute) en.nextElement();
	            Enumeration enumString = attr.getStringValues();
	            if(enumString.hasMoreElements()){
                    schemaDN = (String) enumString.nextElement();
                    String[] attrSearchName= { "ldapSyntaxes" };
	                sr = ld.search( schemaDN,
	                        LDAPConnection.SCOPE_BASE, "objectclass=*",
			                attrSearchName, false);
	                String attrName;
	                if(sr != null && sr.hasMoreElements()){
	                    ent = sr.next();
		                attrSet = ent.getAttributeSet();
		                en = attrSet.getAttributes();
		                while(en.hasMoreElements()){
		                    attr = (LDAPAttribute) en.nextElement();
                            attrName = attr.getName();
		                    if(attrName.equalsIgnoreCase("ldapSyntaxes")){
                                // remove the value from the attributes values
                                LDAPAttribute newValue = new LDAPAttribute(
                                	"ldapSyntaxes",getValue());
                            	LDAPModification lModify = new LDAPModification(
                                	LDAPModification.DELETE,newValue);
                            	ld.modify(schemaDN,lModify);
                            }
		                    continue;
                      }
	                }
	            }
            }
	    }
      }

      catch( LDAPException e){
        throw e;
      }
  }

  /**
    * @deprecated replaced by {@link LDAPSchema#modify}.  This method
    * has been move to the object LDAPSchema in IETF draft 17 of the Java LDAP
    * API (draft-ietf-ldapext-ldap-java-api-xx.txt) and will be removed
    *  in fall of 2003.
    */
  public void modify(LDAPConnection ld, LDAPSchemaElement newValue) throws LDAPException {
    try{
        modify(ld, newValue, "");
    }
    catch(LDAPException e){
        throw e;
    }
  }

  /**
    * @deprecated replaced by {@link LDAPSchema#modify}.  This method
    * has been move to the object LDAPSchema in IETF draft 17 of the Java LDAP
    * API (draft-ietf-ldapext-ldap-java-api-xx.txt) and will be removed
    *  in fall of 2003.
    */
  public void modify(LDAPConnection ld, LDAPSchemaElement newValue, String dn) throws LDAPException {
    try{
        String attrSubSchema[] = { "subschemaSubentry" };
        LDAPSearchResults sr = ld.search( dn,
	                            LDAPConnection.SCOPE_BASE, "objectclass=*",
					            attrSubSchema, false);
	    if(sr != null && sr.hasMoreElements()){
            String schemaDN;
	        LDAPEntry ent = sr.next();
	        LDAPAttributeSet attrSet = ent.getAttributeSet();
	        Enumeration en = attrSet.getAttributes();
	        LDAPAttribute attr;
	        if(en.hasMoreElements()){
	            attr = (LDAPAttribute) en.nextElement();
	            Enumeration enumString = attr.getStringValues();
	            if(enumString.hasMoreElements()){
                    schemaDN = (String) enumString.nextElement();
                    String[] attrSearchName= { "ldapSyntaxes" };
	                sr = ld.search( schemaDN, LDAPConnection.SCOPE_BASE,
	                      "objectclass=*", attrSearchName, false);
	                String attrName;
	                if(sr != null && sr.hasMoreElements()){
	                    ent = sr.next();
		                attrSet = ent.getAttributeSet();
		                en = attrSet.getAttributes();
		            while(en.hasMoreElements()){
		                attr = (LDAPAttribute) en.nextElement();
                        attrName = attr.getName();
		                if(attrName.equalsIgnoreCase("ldapSyntaxes")){
                        	// modify the attribute
                            LDAPAttribute modValue = new LDAPAttribute(
                                        "ldapSyntaxes", newValue.getValue());
							LDAPModificationSet mods = new LDAPModificationSet();
                            mods.add(LDAPModification.DELETE, modValue);
							mods.add(LDAPModification.ADD, modValue);
                            ld.modify(schemaDN,mods);
                    }
		            continue;
              }
	         }
	        }
          }
	    }
    }

    catch( LDAPException e){
      throw e;
    }
  }
}
