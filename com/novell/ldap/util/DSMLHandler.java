package com.novell.ldap.ldif_dsml;

import com.novell.ldap.*;
import com.novell.ldap.message.LDAPSearchRequest;
import com.novell.ldap.message.LDAPCompareRequest;

import java.util.ArrayList;

import org.xml.sax.*;

/*package public*/
class DSMLHandler implements ContentHandler, ErrorHandler
{
    public ArrayList queue = new ArrayList();
    /* variables used for message information */
    private LDAPMessage message;
    private LDAPAttribute attr;
    private ArrayList attributes = new ArrayList();
    private LDAPSearchConstraints searchCons;
    private String dn, filter;
    private StringBuffer value;
    private boolean typesOnly;
    private int scope;

    /* The following values are valid states for the parser: tags are in
    comments*/
    private static final int START=0;
    private static final int BATCH_REQUEST=1;       //batchRequest

    /* The following are possible states from the batchRequest state */
    private static final int AUTH_REQUEST=2;        //authRequest
    private static final int MODIFY_REQUEST=3;      //modifyRequest
    private static final int SEARCH_REQUEST=4;      //searchRequest
    private static final int ADD_REQUEST=5;         //addRequest
    private static final int DELETE_REQUEST=6;      //deleteRequest
    private static final int MODIFY_DN_REQUEST=7;   //modifyDNRequest
    private static final int COMPARE_REQUEST=8;     //compareRequest
    private static final int EXTENDED_REQUEST=9;    //extendedRequest

    /* The following are possible states from filter, compare and search */
    private static final int ASSERTION = 10;        //assertion
    private static final int VALUE = 11;            //value
    private static final int ATTRIBUTES = 12;       //attributes
    private static final int ATTRIBUTE  = 13;       //attribute
    private static final int FILTER = 14;           //filter
    private static final int SUBSTRINGS = 15;       //substrings
    private static final int FINAL = 16;            //final


    private static final int BATCH_RESPONSE=17;
    /* The folling are possible states from the BatchResponse state
        .... SearchResponse ...*/

    /** state contains the internal parsing state **/
    private int state = START;
    private static java.util.HashMap requestTags = null;
    static {
        if (requestTags == null){
            requestTags = new java.util.HashMap(18, 9999);
            //High load factor means optimized for lookup time
            requestTags.put("batchRequest", new Integer(BATCH_REQUEST));
            requestTags.put("authRequest",  new Integer(AUTH_REQUEST));
            requestTags.put("modifyRequest",new Integer(MODIFY_REQUEST));
            requestTags.put("searchRequest",new Integer(SEARCH_REQUEST));
            requestTags.put("addRequest",   new Integer(ADD_REQUEST));
            requestTags.put("deleteRequest",new Integer(DELETE_REQUEST));
            requestTags.put("modifyDNRequest",  new Integer(MODIFY_DN_REQUEST));
            requestTags.put("compareRequest",   new Integer(COMPARE_REQUEST));
            requestTags.put("extendedRequest",  new Integer(EXTENDED_REQUEST));
            requestTags.put("batchResponse",new Integer(BATCH_RESPONSE));
            requestTags.put("assertion",    new Integer(ASSERTION));
            requestTags.put("value",        new Integer(VALUE));
            requestTags.put("attributes",   new Integer(ATTRIBUTES));
            requestTags.put("attribute",    new Integer(ATTRIBUTE));
            requestTags.put("filter",       new Integer(FILTER));
            requestTags.put("substrings",   new Integer(SUBSTRINGS));
            requestTags.put("final",        new Integer(FINAL));
        }
    }

    // SAX calls this method when it encounters an element
    public void startElement(String strNamespaceURI,
                             String strLocalName,
                             String strQName,
                             Attributes attrs) throws SAXException
    {
        Integer elementTag = (Integer)requestTags.get(strQName);
        if (elementTag == null){
            throw new SAXException("Element name, \"" + strQName
                                + "\" not recognized");
        }
        int tag = elementTag.intValue();

        switch (state){
            // The following values are valid states for the parser:
            case START:
                // we can now read a Batch_Request tag or Batch_Response tag
                if (tag == BATCH_REQUEST || tag == BATCH_RESPONSE){
                    state = tag;
                } else {
                    throw new SAXException("Element name, \"" + strQName
                                + "\" not recognized");
                }
                break;
            case BATCH_REQUEST:
                state = tag;
                parseTagAttributes( tag, attrs );
                break;
            case SEARCH_REQUEST:
                if (tag == ATTRIBUTES){
                    this.attributes.clear();
                    state = tag;
                }
                break;
            case AUTH_REQUEST:
            case MODIFY_REQUEST:
            case ADD_REQUEST:
            case DELETE_REQUEST:
            case MODIFY_DN_REQUEST:
            case COMPARE_REQUEST:
                if (tag == ASSERTION) {
                    attr = new LDAPAttribute( attrs.getValue("name") );
                    state = tag;
                }
                else //I may not have to check for this if decide to validate
                    throw new SAXException("incomplete compareRequest tag");
                break;
            case ASSERTION:
                if (tag == VALUE){
                    state = tag;
                    value = new StringBuffer();
                }
                else //I may not have to check for this if decide to validate
                    throw new SAXException("incomplete assertion tag");
                break;
            case ATTRIBUTES:
                //list of attribute names
                if (tag == ATTRIBUTE){
                    //add a search attributes name
                    attributes.add(attrs.getValue("name"));
                    state = tag;
                }
                break;
            case FILTER:
                break;
            case SUBSTRINGS:
                break;
            case FINAL:
                break;
            case EXTENDED_REQUEST:
                break;
        }
        return;
    }

    private void parseTagAttributes( int tag, Attributes attr )
            throws SAXException
    {

        switch (tag){
            case SEARCH_REQUEST:
                String temp;
                int timeLimit, deref, sizeLimit;

                //Get dereferencing Aliases
                temp = attr.getValue("derefAliases");
                if (temp == null){
                    deref = LDAPSearchConstraints.DEREF_ALWAYS;
                } else if (temp.equals("neverDerefAliases")){
                    deref = LDAPSearchConstraints.DEREF_NEVER;
                } else if (temp.equals("derefInSearching")){
                    deref = LDAPSearchConstraints.DEREF_SEARCHING;
                } else if (temp.equals("derefFindingBaseObj")){
                    deref = LDAPSearchConstraints.DEREF_FINDING;
                } else if (temp.equals("derefAlways")){
                    deref = LDAPSearchConstraints.DEREF_ALWAYS;
                } else throw new SAXException (
                        "unknown attribute in searchRequest, " + temp);

                //get timelimit
                temp = attr.getValue("timelimit");
                if (temp != null){
                    timeLimit = Integer.parseInt(temp);
                } else {
                    timeLimit = 0;
                }

                //get sizeLimit
                temp = attr.getValue("sizeLimit");
                if (temp != null){
                    sizeLimit = Integer.parseInt(temp);
                } else {
                    sizeLimit = 0;
                }

                //put the above fields into a searchConstraints object
                searchCons = new LDAPSearchConstraints(
                        timeLimit,
                        timeLimit,  //serverTimeLimit
                        deref,      //dereference int
                        sizeLimit,  //maxResults
                        false,      //doReferrals
                        0,          //batchSize
                        (LDAPReferralHandler) null, //referralHandler,
                        0);

                //the following are parameters to LDAPSearchRequest
                dn = attr.getValue("dn");

                temp = attr.getValue("typesOnly");
                if (temp == null){
                    typesOnly = false;
                } else if ( new Boolean(temp).booleanValue() == true ){
                    typesOnly = true;
                } else if ( new Boolean(temp).booleanValue() == false){
                    typesOnly = false;
                } else {
                    throw new SAXException(
                            "Invalid value for attribute 'typesOnly',"+
                            temp);
                }

                //Get Scope
                temp = attr.getValue("scope");
                if (temp == null){
                    scope = LDAPConnection.SCOPE_BASE;
                } else if (temp.equals("baseObject")){
                    scope = LDAPConnection.SCOPE_BASE;
                } else if (temp.equals("singleLevel")){
                    scope = LDAPConnection.SCOPE_ONE;
                } else if (temp.equals("wholeSubtree")){
                    scope = LDAPConnection.SCOPE_SUB;
                } else throw new SAXException(
                        "Invalid value for attribute 'scope', "+ temp);

                filter = null;
                break;
            case AUTH_REQUEST:
                break;
            case MODIFY_REQUEST:
                dn = attr.getValue("dn");
                break;
            case ADD_REQUEST:
                dn = attr.getValue("dn");
                break;
            case DELETE_REQUEST:
                dn = attr.getValue("dn");
                break;
            case MODIFY_DN_REQUEST:
                dn = attr.getValue("dn");
                break;
            case COMPARE_REQUEST:
                /* We cannot create a CompareRequest until we have the value
                 assestion, which is another state */
                dn = attr.getValue("dn");
                break;
            case EXTENDED_REQUEST:
                break;
        }
        return;
    }

    // SAX calls this method to pass in character data
    // stored between the start and end tags of a particular element
    public void characters(char[] a,
                           int s,
                           int l)
    {
        switch (state){
            case VALUE:
                value.append(a, s, l);
                break;
        }
        return;
    }

    // SAX calls this method when the end-tag for an element is encountered
    public void endElement(String strNamespaceURI,
                           String strLocalName,
                           String strQName) throws SAXException
    {

        Integer elementTag = (Integer)requestTags.get(strQName);
        if (elementTag == null){
            throw new SAXException("Element name, \"" + strQName
                                + "\" not recognized");
        }
        int tag = elementTag.intValue();

        switch (state){
            case BATCH_REQUEST:
            case BATCH_RESPONSE:
                state = START;
                break;
            case SEARCH_REQUEST:
                //queue up search
                state = BATCH_REQUEST;
                try {
                    message = new LDAPSearchRequest(dn, scope, filter,
                            (String[]) attributes.toArray(
                                    new String[ attributes.size() ] ),
                            typesOnly, searchCons );
                } catch (LDAPException e) {
                    throw new SAXException( e );
                }
                queue.add(message);
                break;
            case ATTRIBUTES:
                state = SEARCH_REQUEST;
                break;
            case ATTRIBUTE:
                state = ATTRIBUTES;
                break;
            case AUTH_REQUEST:
                //bind
                state = BATCH_REQUEST;
                break;
            case MODIFY_REQUEST:
                //queue up modify
                state = BATCH_REQUEST;
                break;
            case ADD_REQUEST:
                //queue up add
                state = BATCH_REQUEST;
                break;
            case DELETE_REQUEST:
                //queue up delete
                state = BATCH_REQUEST;
                break;
            case MODIFY_DN_REQUEST:
                //queue up modify
                state = BATCH_REQUEST;
                break;
            case COMPARE_REQUEST:
                //queue up compare
                try {
                    message = new LDAPCompareRequest(dn, attr.getName(),
                            attr.getByteValue(), null);
                    queue.add(message);
                } catch (LDAPException e) {
                    throw new SAXException(e);
                }
                state = BATCH_REQUEST;
                break;
            case ASSERTION:
                //attrs is already complete.
                state = COMPARE_REQUEST;
                break;
            case VALUE:
                attr.addValue(value.toString());
                state = ASSERTION;
                break;
            case FILTER:
                //RfcFilter filter = new RfcFilter(
                break;
            case EXTENDED_REQUEST:
                //queue up x-operation
                state = BATCH_REQUEST;
                break;
        }
        return;
    }

    public void warning(SAXParseException e) throws SAXException
    {
        System.out.println("warning: " + e.toString());
        throw e;
    }
    public void error(SAXParseException e) throws SAXException
    {
        System.out.println("error: " + e.toString());
        throw e;
    }
    public void fatalError(SAXParseException e) throws SAXException
    {
        System.out.println("fatal error: " + e.toString());
        throw e;
    }

    ////////////////////////////////////////////////////////////////////
    // Implementation of ContentHandler interface.
    ////////////////////////////////////////////////////////////////////


    /**
     * Receive a Locator object for document events.
     *
     * <p>By default, do nothing.  Application writers may override this
     * method in a subclass if they wish to store the locator for use
     * with other document events.</p>
     *
     * @param locator A locator for all SAX document events.
     * @see org.xml.sax.ContentHandler#setDocumentLocator
     * @see org.xml.sax.Locator
     */
    public void setDocumentLocator (Locator locator)
    {
	    // no op
        return;
    }


    /**
     * Receive notification of the beginning of the document.
     *
     * <p>By default, do nothing.  Application writers may override this
     * method in a subclass to take specific actions at the beginning
     * of a document (such as allocating the root node of a tree or
     * creating an output file).</p>
     *
     * @exception org.xml.sax.SAXException Any SAX exception, possibly
     *            wrapping another exception.
     * @see org.xml.sax.ContentHandler#startDocument
     */
    public void startDocument ()
	throws SAXException
    {
	    // no op
        return;
    }


    /**
     * Receive notification of the end of the document.
     *
     * <p>By default, do nothing.  Application writers may override this
     * method in a subclass to take specific actions at the end
     * of a document (such as finalising a tree or closing an output
     * file).</p>
     *
     * @exception org.xml.sax.SAXException Any SAX exception, possibly
     *            wrapping another exception.
     * @see org.xml.sax.ContentHandler#endDocument
     */
    public void endDocument ()
	throws SAXException
    {
	// no op
    }


    /**
     * Receive notification of the start of a Namespace mapping.
     *
     * <p>By default, do nothing.  Application writers may override this
     * method in a subclass to take specific actions at the start of
     * each Namespace prefix scope (such as storing the prefix mapping).</p>
     *
     * @param prefix The Namespace prefix being declared.
     * @param uri The Namespace URI mapped to the prefix.
     * @exception org.xml.sax.SAXException Any SAX exception, possibly
     *            wrapping another exception.
     * @see org.xml.sax.ContentHandler#startPrefixMapping
     */
    public void startPrefixMapping (String prefix, String uri)
	throws SAXException
    {
	    // no op
        return;
    }


    /**
     * Receive notification of the end of a Namespace mapping.
     *
     * <p>By default, do nothing.  Application writers may override this
     * method in a subclass to take specific actions at the end of
     * each prefix mapping.</p>
     *
     * @param prefix The Namespace prefix being declared.
     * @exception org.xml.sax.SAXException Any SAX exception, possibly
     *            wrapping another exception.
     * @see org.xml.sax.ContentHandler#endPrefixMapping
     */
    public void endPrefixMapping (String prefix)
	throws SAXException
    {
	// no op
    }


    /**
     * Receive notification of ignorable whitespace in element content.
     *
     * <p>By default, do nothing.  Application writers may override this
     * method to take specific actions for each chunk of ignorable
     * whitespace (such as adding data to a node or buffer, or printing
     * it to a file).</p>
     *
     * @param ch The whitespace characters.
     * @param start The start position in the character array.
     * @param length The number of characters to use from the
     *               character array.
     * @exception org.xml.sax.SAXException Any SAX exception, possibly
     *            wrapping another exception.
     * @see org.xml.sax.ContentHandler#ignorableWhitespace
     */
    public void ignorableWhitespace (char ch[], int start, int length)
	throws SAXException
    {
	    // no op
        return;
    }


    /**
     * Receive notification of a processing instruction.
     *
     * <p>By default, do nothing.  Application writers may override this
     * method in a subclass to take specific actions for each
     * processing instruction, such as setting status variables or
     * invoking other methods.</p>
     *
     * @param target The processing instruction target.
     * @param data The processing instruction data, or null if
     *             none is supplied.
     * @exception org.xml.sax.SAXException Any SAX exception, possibly
     *            wrapping another exception.
     * @see org.xml.sax.ContentHandler#processingInstruction
     */
    public void processingInstruction (String target, String data)
	throws SAXException
    {
	    // no op
        return;
    }


    /**
     * Receive notification of a skipped entity.
     *
     * <p>By default, do nothing.  Application writers may override this
     * method in a subclass to take specific actions for each
     * processing instruction, such as setting status variables or
     * invoking other methods.</p>
     *
     * @param name The name of the skipped entity.
     * @exception org.xml.sax.SAXException Any SAX exception, possibly
     *            wrapping another exception.
     * @see org.xml.sax.ContentHandler#processingInstruction
     */
    public void skippedEntity (String name)
	throws SAXException
    {
	    // no op
        return;
    }

}
