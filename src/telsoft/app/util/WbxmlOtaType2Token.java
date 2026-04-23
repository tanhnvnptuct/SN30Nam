package telsoft.app.util;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2004</p>
 *
 * <p>Company: </p>
 *
 * @author DinhLV
 * @version 1.0
 */

public class WbxmlOtaType2Token {
	public WbxmlOtaType2Token() {
	}

    // 7.1 Element tokens
	// The following token codes represent tags in code page zero (0). All numbers are in hexadecimal.
	public static final byte WAP_PROVISIONINGDOC = 0x05;
	public static final byte CHARACTERISTIC = 0x06;
	public static final byte CHARACTERISTIC_ = (byte)0xC6;
	public static final byte PARM = 0x07;
	public static final byte PARM_ = (byte)0x87;

	// The following token codes represent tags in code page one (1). All numbers are in hexadecimal.
	public static final byte CHARACTERISTIC_CPO = 0x06;
	public static final byte parm_CPO = 0x07;

    // 7.2 Attribute Start Tokens
	// 7.2.1 Wap-provisioningdoc Attribute Start Tokens
	public static final byte VERSION = 0x45;
	public static final byte VERSION_10 = 0x46;

	// 7.2.2 Characteristic Attribute Start Tokens
	public static final byte TYPE = 0x50;
	public static final byte TYPE_PXLOGICAL = 0x51;
	public static final byte TYPE_PXPHYSICAL = 0x52;
	public static final byte TYPE_PORT = 0x53;
	public static final byte TYPE_VALIDITY = 0x54;
	public static final byte TYPE_NAPDEF = 0x55;
	public static final byte TYPE_BOOTSTRAP = 0x56;
	public static final byte TYPE_VENDORCONFIG = 0x57;
	public static final byte TYPE_CLIENTIDENTITY = 0x58;
	public static final byte TYPE_PXAUTHINFO = 0x59;
	public static final byte TYPE_NAPAUTHINFO = 0x5A;
	public static final byte TYPE_ACCESS = 0x5B;

	// The token codes in the following table represent tags in code page one (1). All numbers are in hexadecimal.
	public static final byte TYPE_CPO = 0x50;
	public static final byte TYPE_PORT_CPO = 0x53;
	public static final byte TYPE_CLIENTIDENTITY_CPO = 0x58;
	public static final byte TYPE_APPLICATION_CPO = 0x55;
	public static final byte TYPE_APPADDR_CPO = 0x56;
	public static final byte TYPE_APPAUTH_CPO = 0x57;
	public static final byte TYPE_RESOURCE_CPO = 0x59;

	//7.2.3 Parm Attribute Start Tokens
	public static final byte NAME = 0x05;
	public static final byte VALUE = 0x06;
	public static final byte NAME_NAME = 0x07;
	public static final byte NAME_NAP_ADDRESS = 0x08;
	public static final byte NAME_NAP_ADDRTYPE = 0x09;
	public static final byte NAME_CALLTYPE = 0x0A;
	public static final byte NAME_VALIDUNTIL = 0x0B;
	public static final byte NAME_AUTHTYPE = 0x0C;
	public static final byte NAME_AUTHNAME = 0x0D;
	public static final byte NAME_AUTHSECRET = 0x0E;
	public static final byte NAME_LINGER = 0x0F;
	public static final byte NAME_BEARER = 0x10;
	public static final byte NAME_NAPID = 0x11;
	public static final byte NAME_COUNTRY = 0x12;
	public static final byte NAME_NETWORK = 0x13;
	public static final byte NAME_INTERNET = 0x14;
	public static final byte NAME_PROXY_ID = 0x15;
	public static final byte NAME_PROXY_PROVIDER_ID = 0x16;
	public static final byte NAME_DOMAIN = 0x17;
	public static final byte NAME_PROVURL = 0x18;
	public static final byte NAME_PXAUTH_TYPE = 0x19;
	public static final byte NAME_PXAUTH_ID = 0x1A;
	public static final byte NAME_PXAUTH_PW = 0x1B;
	public static final byte NAME_STARTPAGE = 0x1C;
	public static final byte NAME_BASAUTH_ID = 0x1D;
	public static final byte NAME_BASAUTH_PW = 0x1E;
	public static final byte NAME_PUSHENABLED = 0x1F;
	public static final byte NAME_PXADDR = 0x20;
	public static final byte NAME_PXADDRTYPE = 0x21;
	public static final byte NAME_TO_NAPID = 0x22;
	public static final byte NAME_PORTNBR = 0x23;
	public static final byte NAME_SERVICE = 0x24;
	public static final byte NAME_LINKSPEED = 0x25;
	public static final byte NAME_DNLINKSPEED = 0x26;
	public static final byte NAME_LOCAL_ADDR = 0x27;
	public static final byte NAME_LOCAL_ADDRTYPE = 0x28;
	public static final byte NAME_CONTEXT_ALLOW = 0x29;
	public static final byte NAME_TRUST = 0x2A;

	public static final byte NAME_MASTER = 0x2B;
	public static final byte NAME_SID = 0x2C;
	public static final byte NAME_SOC = 0x2D;
	public static final byte NAME_WSP_VERSION = 0x2E;
	public static final byte NAME_PHYSICAL_PROXY_ID = 0x2F;
	public static final byte NAME_CLIENT_ID = 0x30;
	public static final byte NAME_DELIVERY_ERR_SDU = 0x31;
	public static final byte NAME_DELIVERY_ORDER = 0x32;
	public static final byte NAME_TRAFFIC_CLASS = 0x33;
	public static final byte NAME_MAX_SDU_SIZE = 0x34;
	public static final byte NAME_MAX_BITRATE_UPLINK = 0x35;
	public static final byte NAME_MAX_BITRATE_DNLINK = 0x36;
	public static final byte NAME_RESIDUAL_BER = 0x37;
	public static final byte NAME_SDU_ERROR_RATIO = 0x38;
	public static final byte NAME_TRAFFIC_HANDL_PRIO = 0x39;
	public static final byte NAME_TRANSFER_DELAY = 0x3A;
	public static final byte NAME_GUARANTEED_BITRATE_UPLINK = 0x3B;
	public static final byte NAME_GUARANTEED_BITRATE_DNLINK = 0x3C;
	public static final byte NAME_PXADDR_FQDN = 0x3D;
	public static final byte NAME_PROXY_PW = 0x3E;
	public static final byte NAME_PPGAUTH_TYPE = 0x3F;
	public static final byte NAME_PULLENABLED = 0x47;
	public static final byte NAME_DNS_ADDR = 0x48;
	public static final byte NAME_MAX_NUM_RETRY = 0x49;
	public static final byte NAME_FIRST_RETRY_TIMEOUT = 0x4A;
	public static final byte NAME_REREG_THRESHOLD = 0x4B;
	public static final byte NAME_T_BIT = 0x4C;
	public static final byte NAME_AUTH_ENTITY = 0x4E;
	public static final byte NAME_SPI = 0x4F;

	//The token codes in the following table represent tags in code page one (1). All numbers are in hexadecimal.
	public static final byte NAME_CPO = 0x05;
	public static final byte VALUE_CPO = 0x6;
	public static final byte NAME_NAME_CPO = 0x7;
	public static final byte NAME_INTERNET_CPO = 0x14;
	public static final byte NAME_STARTPAGE_CPO = 0x1C;
	public static final byte NAME_TO_NAPID_CPO = 0x22;
	public static final byte NAME_PORTNBR_CPO = 0x23;
	public static final byte NAME_SERVICE_CPO = 0x24;
	public static final byte NAME_AACCEPT_CPO = 0x2E;
	public static final byte NAME_AAUTHDATA_CPO = 0x2F;
	public static final byte NAME_AAUTHLEVEL_CPO = 0x30;
	public static final byte NAME_AAUTHNAME_CPO = 0x31;
	public static final byte NAME_AAUTHSECRET_CPO = 0x32;
	public static final byte NAME_AAUTHTYPE_CPO = 0x33;
	public static final byte NAME_ADDR_CPO = 0x34;
	public static final byte NAME_ADDRTYPE_CPO = 0x35;
	public static final byte NAME_APPID_CPO = 0x36;
	public static final byte NAME_APROTOCOL_CPO = 0x37;
	public static final byte NAME_PROVIDER_ID_CPO = 0x38;
	public static final byte NAME_TO_PROXY_CPO = 0x39;
	public static final byte NAME_URI_CPO = 0x3A;
	public static final byte NAME_RULE_CPO = 0x3B;

    // 7.3 Parameter Token Values
	// 7.3.1 ADDRTYPE Value
	public static final byte IPV4 = (byte)0x85;
	public static final byte IPV6 = (byte)0x86;
	public static final byte E164 = (byte)0x87;
	public static final byte ALPHA = (byte)0x88;
	public static final byte APN = (byte)0x89;
	public static final byte SCODE = (byte)0x8A;
	public static final byte TETRA_ITSI = (byte)0x8B;
	public static final byte MAN = (byte)0x8C;

	// The token codes in the following table represent tags in code page one (1). All numbers are in hexadecimal.
	public static final byte IPV6_CPO = (byte)0x86;
	public static final byte E164_CPO = (byte)0x87;
	public static final byte ALPHA_CPO = (byte)0x88;
	public static final byte APPSRV_CPO = (byte)0x8D;
	public static final byte OBEX_CPO = (byte)0x8E;

	// 7.3.2 CALLTYPE Value
	public static final byte ANALOG_MODEM = (byte)0x90;
	public static final byte V_120 = (byte)0x91;
	public static final byte V_110 = (byte)0x92;
	public static final byte X_31 = (byte)0x93;
	public static final byte BIT_TRANSPARENT = (byte)0x94;
	public static final byte DIRECT_ASYNCHRONOUS_DATA_SERVICE = (byte)0x95;

	// 7.3.3 AUTHTYPE/PXAUTH-TYPE Value
	public static final byte PAP = (byte)0x9A;
	public static final byte CHAP = (byte)0x9B;
	public static final byte HTTP_BASIC = (byte)0x9C;
	public static final byte HTTP_DIGEST = (byte)0x9D;
	public static final byte WTLS_SS = (byte)0x9E;
	public static final byte MD5 = (byte)0x9F;

	// 7.3.4 BEARER Value
	public static final byte GSM_USSD = (byte)0xA2;
	public static final byte GSM_SMS = (byte)0xA3;
	public static final byte ANSI_136_GUTS = (byte)0xA4;
	public static final byte IS_95_CDMA_SMS = (byte)0xA5;
	public static final byte IS_95_CDMA_CSD = (byte)0xA6;
	public static final byte IS_95_CDMA_PACKET = (byte)0xA7;
	public static final byte ANSI_136_CSD = (byte)0xA8;
	public static final byte ANSI_136_GPRS = (byte)0xA9;
	public static final byte GSM_CSD = (byte)0xAA;
	public static final byte GSM_GPRS = (byte)0xAB;
	public static final byte AMPS_CDPD = (byte)0xAC;
	public static final byte PDC_CSD = (byte)0xAD;
	public static final byte PDC_PACKET = (byte)0xAE;
	public static final byte IDEN_SMS = (byte)0xAF;
	public static final byte IDEN_CSD = (byte)0xB0;
	public static final byte IDEN_PACKET = (byte)0xB1;
	public static final byte FLEX_REFLEX = (byte)0xB2;
	public static final byte PHS_SMS = (byte)0xB3;
	public static final byte PHS_CSD = (byte)0xB4;
	public static final byte TETRA_SDS = (byte)0xB5;
	public static final byte TETRA_PACKET = (byte)0xB6;

	public static final byte ANSI_136_GHOST = (byte)0xB7;
	public static final byte MOBITEX_MPAK = (byte)0xB8;
	public static final byte CDMA2000_1X_SIMPLE_IP = (byte)0xB9;
	public static final byte CDMA2000_1X_MOBILE_IP = (byte)0xBA;

	// 7.3.5 LINKSPEED Value
	public static final byte AUTOBAUDING = (byte)0xC5;

	// 7.3.6 SERVICE Value
	public static final byte CL_WSP = (byte)0xCA;
	public static final byte CO_WSP = (byte)0xCB;
	public static final byte CL_SEC_WSP = (byte)0xCC;
	public static final byte CO_SEC_WSP = (byte)0xCD;
	public static final byte CL_SEC_WTA = (byte)0xCE;
	public static final byte CO_SEC_WTA = (byte)0xCF;
	public static final byte OTA_HTTP_TO = (byte)0xD0;
	public static final byte OTA_HTTP_TLS_TO = (byte)0xD1;
	public static final byte OTA_HTTP_PO = (byte)0xD2;
	public static final byte OTA_HTTP_TLS_PO = (byte)0xD3;

	// 7.3.7 AAUTHTYPE Value
	// The token codes in the following table represent attribute values in code page one (1). All numbers are in hexadecimal.
	public static final byte COMMA_CHARACTER = (byte)0x90;
	public static final byte HTTP_ = (byte)0x91;
	public static final byte BASIC = (byte)0x92;
	public static final byte DIGEST = (byte)0x93;

	// 7.3.8 AUTH-ENTITY Value
	// The token codes in the following table represent attribute values in code page one (0). All numbers are in hexadecimal.
	public static final byte AAA = (byte)0xE0;
	public static final byte HA = (byte)0xE1;

	// Others
	public static final byte NULL_TERMINATION_OF_CONTENT_TYPE_STRING = 0;
	public static final byte END = 0x01;
	public static final byte WBXML_v13 = 0x03; // WBXML version 1.3
	public static final byte PUBLIC_IDENTIFIER_WAPFORUM_DTD_PROV_10_EN = 0x0b; // The Public Identifier for "-//WAPFORUM//DTD PROV 1.0//EN"
	public static final byte CHARACTER_SET_UTF8 = 0x6a; // Character set UTF-8
	public static final byte STR_I = 0x03;
}
