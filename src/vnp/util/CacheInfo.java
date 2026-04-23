package vnp.util;

import java.util.*;
import java.sql.*;
import telsoft.cache.CacheObject;
import telsoft.cache.ObjectDateIndex;
import telsoft.cache.CacheObjectDate;
import telsoft.cache.ObjectIndex;
import telsoft.util.StringUtil;


/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class CacheInfo {
    protected ObjectIndex HSSVIndex0 = new ObjectIndex();
    protected CacheObject CacheHSSV0;
    protected ObjectIndex HSSVIndex1 = new ObjectIndex();
    protected CacheObject CacheHSSV1;
    protected ObjectIndex HSSVIndex2 = new ObjectIndex();
    protected CacheObject CacheHSSV2;
    protected ObjectIndex HSSVIndex3 = new ObjectIndex();
    protected CacheObject CacheHSSV3;
    protected ObjectIndex HSSVIndex4 = new ObjectIndex();
    protected CacheObject CacheHSSV4;
    protected ObjectIndex HSSVIndex5 = new ObjectIndex();
    protected CacheObject CacheHSSV5;
    protected ObjectIndex HSSVIndex6 = new ObjectIndex();
    protected CacheObject CacheHSSV6;
    protected ObjectIndex HSSVIndex7 = new ObjectIndex();
    protected CacheObject CacheHSSV7;
    protected ObjectIndex HSSVIndex8 = new ObjectIndex();
    protected CacheObject CacheHSSV8;
    protected ObjectIndex HSSVIndex9 = new ObjectIndex();
    protected CacheObject CacheHSSV9;

    protected Connection mcn;

    public void setConnection(Connection cn) throws Exception {
        mcn = cn;
    }

    public void initHSSVIndex0(boolean blnCompareInt) throws Exception {
        HSSVIndex0.setKeyName("msisdn");
        HSSVIndex0.setValueName(
                "total_point");
        HSSVIndex0.setSQL(
                "SELECT msisdn, total_point FROM student WHERE msisdn like '%0' AND status = '1' ORDER BY msisdn DESC, total_point DESC");
        HSSVIndex0.setCompareInt(blnCompareInt);
        HSSVIndex0.loadCache(mcn);
    }

    public String getHSSVValue0(String strKey, String strValue) throws
            Exception {
        CacheHSSV0 = HSSVIndex0.getObject(strKey);
        if (CacheHSSV0 != null) {
            return StringUtil.nvl(CacheHSSV0.getValue(strValue), "");
        } else {
            return "";
        }
    }

    public void initHSSVIndex1(boolean blnCompareInt) throws Exception {
        HSSVIndex1.setKeyName("msisdn");
        HSSVIndex1.setValueName(
                "total_point");
        HSSVIndex1.setSQL(
                "SELECT msisdn, total_point FROM student WHERE msisdn like '%1' AND status = '1' ORDER BY msisdn DESC, total_point DESC");
        HSSVIndex1.setCompareInt(blnCompareInt);
        HSSVIndex1.loadCache(mcn);
    }

    public String getHSSVValue1(String strKey, String strValue) throws
            Exception {
        CacheHSSV1 = HSSVIndex1.getObject(strKey);
        if (CacheHSSV1 != null) {
            return StringUtil.nvl(CacheHSSV1.getValue(strValue), "");
        } else {
            return "";
        }
    }

    public void initHSSVIndex2(boolean blnCompareInt) throws Exception {
        HSSVIndex2.setKeyName("msisdn");
        HSSVIndex2.setValueName(
                "total_point");
        HSSVIndex2.setSQL(
                "SELECT msisdn, total_point FROM student WHERE msisdn like '%2' AND status = '1' ORDER BY msisdn DESC, total_point DESC");
        HSSVIndex2.setCompareInt(blnCompareInt);
        HSSVIndex2.loadCache(mcn);
    }

    public String getHSSVValue2(String strKey, String strValue) throws
            Exception {
        CacheHSSV2 = HSSVIndex2.getObject(strKey);
        if (CacheHSSV2 != null) {
            return StringUtil.nvl(CacheHSSV2.getValue(strValue), "");
        } else {
            return "";
        }
    }

    public void initHSSVIndex3(boolean blnCompareInt) throws Exception {
        HSSVIndex3.setKeyName("msisdn");
        HSSVIndex3.setValueName(
                "total_point");
        HSSVIndex3.setSQL(
                "SELECT msisdn, total_point FROM student WHERE msisdn like '%3' AND status = '1' ORDER BY msisdn DESC, total_point DESC");
        HSSVIndex3.setCompareInt(blnCompareInt);
        HSSVIndex3.loadCache(mcn);
    }

    public String getHSSVValue3(String strKey, String strValue) throws
            Exception {
        CacheHSSV3 = HSSVIndex3.getObject(strKey);
        if (CacheHSSV3 != null) {
            return StringUtil.nvl(CacheHSSV3.getValue(strValue), "");
        } else {
            return "";
        }
    }

    public void initHSSVIndex4(boolean blnCompareInt) throws Exception {
        HSSVIndex4.setKeyName("msisdn");
        HSSVIndex4.setValueName(
                "total_point");
        HSSVIndex4.setSQL(
                "SELECT msisdn, total_point FROM student WHERE msisdn like '%4' AND status = '1' ORDER BY msisdn DESC, total_point DESC");
        HSSVIndex4.setCompareInt(blnCompareInt);
        HSSVIndex4.loadCache(mcn);
    }

    public String getHSSVValue4(String strKey, String strValue) throws
            Exception {
        CacheHSSV4 = HSSVIndex4.getObject(strKey);
        if (CacheHSSV4 != null) {
            return StringUtil.nvl(CacheHSSV4.getValue(strValue), "");
        } else {
            return "";
        }
    }

    public void initHSSVIndex5(boolean blnCompareInt) throws Exception {
        HSSVIndex5.setKeyName("msisdn");
        HSSVIndex5.setValueName(
                "total_point");
        HSSVIndex5.setSQL(
                "SELECT msisdn, total_point FROM student WHERE msisdn like '%5' AND status = '1' ORDER BY msisdn DESC, total_point DESC");
        HSSVIndex5.setCompareInt(blnCompareInt);
        HSSVIndex5.loadCache(mcn);
    }

    public String getHSSVValue5(String strKey, String strValue) throws
            Exception {
        CacheHSSV5 = HSSVIndex5.getObject(strKey);
        if (CacheHSSV5 != null) {
            return StringUtil.nvl(CacheHSSV5.getValue(strValue), "");
        } else {
            return "";
        }
    }

    public void initHSSVIndex6(boolean blnCompareInt) throws Exception {
        HSSVIndex6.setKeyName("msisdn");
        HSSVIndex6.setValueName(
                "total_point");
        HSSVIndex6.setSQL(
                "SELECT msisdn, total_point FROM student WHERE msisdn like '%6' AND status = '1' ORDER BY msisdn DESC, total_point DESC");
        HSSVIndex6.setCompareInt(blnCompareInt);
        HSSVIndex6.loadCache(mcn);
    }

    public String getHSSVValue6(String strKey, String strValue) throws
            Exception {
        CacheHSSV6 = HSSVIndex6.getObject(strKey);
        if (CacheHSSV6 != null) {
            return StringUtil.nvl(CacheHSSV6.getValue(strValue), "");
        } else {
            return "";
        }
    }

    public void initHSSVIndex7(boolean blnCompareInt) throws Exception {
        HSSVIndex7.setKeyName("msisdn");
        HSSVIndex7.setValueName(
                "total_point");
        HSSVIndex7.setSQL(
                "SELECT msisdn, total_point FROM student WHERE msisdn like '%7' AND status = '1' ORDER BY msisdn DESC, total_point DESC");
        HSSVIndex7.setCompareInt(blnCompareInt);
        HSSVIndex7.loadCache(mcn);
    }

    public String getHSSVValue7(String strKey, String strValue) throws
            Exception {
        CacheHSSV7 = HSSVIndex7.getObject(strKey);
        if (CacheHSSV7 != null) {
            return StringUtil.nvl(CacheHSSV7.getValue(strValue), "");
        } else {
            return "";
        }
    }

    public void initHSSVIndex8(boolean blnCompareInt) throws Exception {
        HSSVIndex8.setKeyName("msisdn");
        HSSVIndex8.setValueName(
                "total_point");
        HSSVIndex8.setSQL(
                "SELECT msisdn, total_point FROM student WHERE msisdn like '%8' AND status = '1' ORDER BY msisdn DESC, total_point DESC");
        HSSVIndex8.setCompareInt(blnCompareInt);
        HSSVIndex8.loadCache(mcn);
    }

    public String getHSSVValue8(String strKey, String strValue) throws
            Exception {
        CacheHSSV8 = HSSVIndex8.getObject(strKey);
        if (CacheHSSV8 != null) {
            return StringUtil.nvl(CacheHSSV8.getValue(strValue), "");
        } else {
            return "";
        }
    }

    public void initHSSVIndex9(boolean blnCompareInt) throws Exception {
        HSSVIndex9.setKeyName("msisdn");
        HSSVIndex9.setValueName(
                "total_point");
        HSSVIndex9.setSQL(
                "SELECT msisdn, total_point FROM student WHERE msisdn like '%9' AND status = '1' ORDER BY msisdn DESC, total_point DESC");
        HSSVIndex9.setCompareInt(blnCompareInt);
        HSSVIndex9.loadCache(mcn);
    }

    public String getHSSVValue9(String strKey, String strValue) throws
            Exception {
        CacheHSSV9 = HSSVIndex9.getObject(strKey);
        if (CacheHSSV9 != null) {
            return StringUtil.nvl(CacheHSSV9.getValue(strValue), "");
        } else {
            return "";
        }
    }
}
