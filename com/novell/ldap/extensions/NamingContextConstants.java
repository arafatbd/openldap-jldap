/* **************************************************************************
 * $Id: NamingContextConstants.java,v 1.13 2000/08/08 21:28:51 javed Exp $
 *
 * Copyright (C) 1999, 2000 Novell, Inc. All Rights Reserved.
 * 
 * THIS WORK IS SUBJECT TO U.S. AND INTERNATIONAL COPYRIGHT LAWS AND
 * TREATIES. USE, MODIFICATION, AND REDISTRIBUTION OF THIS WORK IS SUBJECT
 * TO VERSION 2.0.1 OF THE OPENLDAP PUBLIC LICENSE, A COPY OF WHICH IS
 * AVAILABLE AT HTTP://WWW.OPENLDAP.ORG/LICENSE.HTML OR IN THE FILE "LICENSE"
 * IN THE TOP-LEVEL DIRECTORY OF THE DISTRIBUTION. ANY USE OR EXPLOITATION
 * OF THIS WORK OTHER THAN AS AUTHORIZED IN VERSION 2.0.1 OF THE OPENLDAP
 * PUBLIC LICENSE, OR OTHER PRIOR WRITTEN CONSENT FROM NOVELL, COULD SUBJECT
 * THE PERPETRATOR TO CRIMINAL AND CIVIL LIABILITY. 
 ***************************************************************************/
package com.novell.ldap.ext; 

import org.ietf.ldap.*;
import com.novell.asn1.*;
import java.io.IOException;
 
/**
 * public class NamingContextConstants
 *
 * This class contains a collection of constants used throughout the 
 * Extension code.
 */
public class NamingContextConstants {
   
    /**
     * The following are list of constants identifying the LDAP Request and Repsonse OIDs
     *
     */
    public static final String CREATE_NAMING_CONTEXT_REQ    = "2.16.840.1.113719.1.27.100.3";
    public static final String CREATE_NAMING_CONTEXT_RES    = "2.16.840.1.113719.1.27.100.4";
    public static final String MERGE_NAMING_CONTEXT_REQ     = "2.16.840.1.113719.1.27.100.5";
    public static final String MERGE_NAMING_CONTEXT_RES     = "2.16.840.1.113719.1.27.100.6";
    public static final String ADD_REPLICA_REQ              = "2.16.840.1.113719.1.27.100.7";
    public static final String ADD_REPLICA_RES              = "2.16.840.1.113719.1.27.100.8";
    public static final String REFRESH_SERVER_REQ           = "2.16.840.1.113719.1.27.100.9";
    public static final String REFRESH_SERVER_RES           = "2.16.840.1.113719.1.27.100.10";
    public static final String DELETE_REPLICA_REQ           = "2.16.840.1.113719.1.27.100.11";
    public static final String DELETE_REPLICA_RES           = "2.16.840.1.113719.1.27.100.12";
    public static final String NAMING_CONTEXT_COUNT_REQ     = "2.16.840.1.113719.1.27.100.13";
    public static final String NAMING_CONTEXT_COUNT_RES     = "2.16.840.1.113719.1.27.100.14";
	public static final String CHANGE_REPLICA_TYPE_REQ		= "2.16.840.1.113719.1.27.100.15";
	public static final String CHANGE_REPLICA_TYPE_RES		= "2.16.840.1.113719.1.27.100.16";
    public static final String GET_REPLICA_INFO_REQ         = "2.16.840.1.113719.1.27.100.17";
    public static final String GET_REPLICA_INFO_RES         = "2.16.840.1.113719.1.27.100.18";
	public static final String LIST_REPLICAS_REQ            = "2.16.840.1.113719.1.27.100.19";
	public static final String LIST_REPLICAS_RES    		= "2.16.840.1.113719.1.27.100.20";
    public static final String RECEIVE_ALL_UPDATES_REQ		= "2.16.840.1.113719.1.27.100.21";
	public static final String RECEIVE_ALL_UPDATES_RES		= "2.16.840.1.113719.1.27.100.22";
    public static final String SEND_ALL_UPDATES_REQ		    = "2.16.840.1.113719.1.27.100.23";
	public static final String SEND_ALL_UPDATES_RES		    = "2.16.840.1.113719.1.27.100.24";
    public static final String NAMING_CONTEXT_SYNC_REQ	    = "2.16.840.1.113719.1.27.100.25";
	public static final String NAMING_CONTEXT_SYNC_RES	    = "2.16.840.1.113719.1.27.100.26";
	public static final String SCHEMA_SYNC_REQ		        = "2.16.840.1.113719.1.27.100.27";
	public static final String SCHEMA_SYNC_RES	    	    = "2.16.840.1.113719.1.27.100.28";
    public static final String ABORT_NAMING_CONTEXT_OP_REQ  = "2.16.840.1.113719.1.27.100.29";
	public static final String ABORT_NAMING_CONTEXT_OP_RES	= "2.16.840.1.113719.1.27.100.30";
	public static final String GET_IDENTITY_NAME_REQ        = "2.16.840.1.113719.1.27.100.31";
    public static final String GET_IDENTITY_NAME_RES		= "2.16.840.1.113719.1.27.100.32";
    public static final String GET_EFFECTIVE_PRIVILEGES_REQ	= "2.16.840.1.113719.1.27.100.33";
    public static final String GET_EFFECTIVE_PRIVILEGES_RES	= "2.16.840.1.113719.1.27.100.34";
	public static final String SET_REPLICATION_FILTER_REQ   = "2.16.840.1.113719.1.27.100.35";
    public static final String SET_REPLICATION_FILTER_RES	= "2.16.840.1.113719.1.27.100.36";
    public static final String GET_REPLICATION_FILTER_REQ	= "2.16.840.1.113719.1.27.100.37";
    public static final String GET_REPLICATION_FILTER_RES   = "2.16.840.1.113719.1.27.100.38";
    public static final String CREATE_ORPHAN_NAMING_CONTEXT_REQ     = "2.16.840.1.113719.1.27.100.39";
    public static final String CREATE_ORPHAN_NAMING_CONTEXT_RES	    = "2.16.840.1.113719.1.27.100.40";
    public static final String REMOVE_ORPHAN_NAMING_CONTEXT_REQ	    = "2.16.840.1.113719.1.27.100.41";
    public static final String REMOVE_ORPHAN_NAMING_CONTEXT_RES     = "2.16.840.1.113719.1.27.100.42";
    

    /**
     * Naming Context operation flags
     *
     */
    public static final int LDAP_ENSURE_SERVERS_UP = 1;
    
    
    /**
     * Replica Type constants supported are listed below
     *
     */
    public static final int LDAP_RT_MASTER          = 0;
    public static final int LDAP_RT_SECONDARY       = 1;
    public static final int LDAP_RT_READONLY        = 2;
    public static final int LDAP_RT_SUBREF          = 3;
    public static final int LDAP_RT_SPARSE_WRITE    = 4;
    public static final int LDAP_RT_SPARSE_READ     = 5; 
    
    /**
     * Replica States supported are listed below
     */
     
    public static final int LDAP_RS_ON              = 0;
    public static final int LDAP_RS_NEW_REPLICA     = 1;
    public static final int LDAP_RS_DYING_REPLICA   = 2;
    public static final int LDAP_RS_LOCKED          = 3;
    public static final int LDAP_RS_TRANSITION_ON   = 6;
    public static final int LDAP_RS_DEAD_REPLICA    = 7;
    public static final int LDAP_RS_BEGIN_ADD       = 8;
    public static final int LDAP_RS_MASTER_START    = 11;
    public static final int LDAP_RS_MASTER_DONE     = 12;
    public static final int LDAP_RS_SS_0            = 48;   // Replica splitting 0
    public static final int LDAP_RS_SS_1            = 49;   // Replica splitting 1
    public static final int LDAP_RS_JS_0            = 64;   // Replica joining 0
    public static final int LDAP_RS_JS_1            = 65;   // Replica joining 1
    public static final int LDAP_RS_JS_2            = 66;   // Replica joining 2
    
    /** Priviliges are identified by a combination of the following flags
     *
     */
    public static final int LDAP_DS_ATTR_COMPARE    = 0x0001;
    public static final int LDAP_DS_ATTR_READ       = 0x0002;
    public static final int LDAP_DS_ATTR_WRITE      = 0x0004;
    public static final int LDAP_DS_ATTR_SELF       = 0x0008;
    public static final int LDAP_DS_ATTR_SUPERVISOR = 0x0020;
    public static final int LDAP_DS_ATTR_INHERIT_CTL= 0x0040;

    /** Values for flags used in the replica info class structure */
    public static final int LDAP_DS_FLAG_BUSY       = 0x0001;
    public static final int LDAP_DS_FLAG_BOUNDARY   = 0x0002;

    
    public NamingContextConstants()  {}   
}
