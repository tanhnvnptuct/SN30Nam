package telsoft.cache;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Vector;
import telsoft.util.StringUtil;
import telsoft.sql.Database;

public class ObjectDateIndex2 extends BTreeDateIndex {
    protected String mstrSQLData = "";
    protected String mstrKeyName = "";
    protected String[] arrValueName;
    protected String mstrEffectDateName = "";
    protected String mstrUntilDateName = "";
    protected boolean mblnCompareInt = false;
    protected String[] mastrOtherConditionHeaderInt;
    protected long[] maiOtherConditionValueInt;
    protected String[] mastrOtherConditionHeaderValue;
    protected String[] mastrOtherConditionValueValue;

    public void setCompareInt(boolean bln) throws Exception {
        this.mblnCompareInt = bln;
    }

    public void setSQL(String strSQL) throws Exception {
        this.mstrSQLData = strSQL;
    }

    public void setKeyName(String strKeyName) throws Exception {
        this.mstrKeyName = strKeyName;
    }

    public void setValueName(String strValueName) throws Exception {
        StringUtil.replaceAll(strValueName, ";", ",");
        this.arrValueName = StringUtil.toStringArray(strValueName, ",");
    }

    public void setEffectDateName(String strEffectDateName) throws Exception {
        this.mstrEffectDateName = strEffectDateName;
    }

    public void setUntilDateName(String strUntilDateName) throws Exception {
        this.mstrUntilDateName = strUntilDateName;
    }

    public void loadCache(Connection connection) throws Exception {
        PreparedStatement stmtCache = null;
        ResultSet rsCache = null;
        try {
            clear();
            System.out.println("Connect");
            stmtCache = connection.prepareStatement(this.mstrSQLData);
            System.out.println("Query........");
            rsCache = stmtCache.executeQuery();
            System.out.println("Convert to Data");
            Vector vtData = Database.convertToVector(rsCache);
            System.out.println("Query OK");
            while (rsCache.next()) {
                String[] str = new String[4];
                str[0] = rsCache.getString(1);
                str[1] = rsCache.getString(2);
                str[2] = rsCache.getString(3);
                str[3] = rsCache.getString(4);

//                CacheObjectDate cache = new CacheObjectDate();
//                cache.mblnCompareInt = false;
//                cache.strKey = rsCache.getString(1);
//                cache.strEffect = rsCache.getString(3);
//                cache.strUntil = StringUtil.nvl(rsCache.getString(4), "");
//                cache.vtValue = new Vector();
//                cache.vtValue.addElement(rsCache.getString(1));
                add(str);
            }
            System.out.println("Add OK");
        } finally {
            Database.closeObject(stmtCache);
            Database.closeObject(rsCache);
        }
    }

    public CacheObjectDate getObject(String strKey, String strDate,
                                     String[] strFieldName, long[] iValue) throws
            Exception {
        this.mastrOtherConditionHeaderInt = strFieldName;
        this.maiOtherConditionValueInt = iValue;
        return getObject(strKey, strDate, false);
    }

    public CacheObjectDate getObject(String strKey, String strDate,
                                     String[] strFieldName, String[] strValue) throws
            Exception {
        this.mastrOtherConditionHeaderValue = strFieldName;
        this.mastrOtherConditionValueValue = strValue;
        return getObject(strKey, strDate, false);
    }

    public CacheNode get(String strKey, String strDate, boolean blnExact) throws
            Exception {
        CacheObjectDate cache = new CacheObjectDate();
        cache.strKey = strKey;
        cache.strEffect = strDate;
        cache.strUntil = strDate;
        cache.arrOtherConditionHeaderInt = this.mastrOtherConditionHeaderInt;
        cache.arrOtherConditionValueInt = this.maiOtherConditionValueInt;
        cache.arrOtherConditionHeaderValue = this.
                                             mastrOtherConditionHeaderValue;
        cache.arrOtherConditionValueValue = this.mastrOtherConditionValueValue;
        return get(cache, blnExact);
    }

    public CacheObjectDate getObject(String strKey, String strDate) throws
            Exception {
        return (CacheObjectDate) get(strKey, strDate);
    }

    public CacheObjectDate getObject(String strKey, String strDate,
                                     boolean blnExact) throws Exception {
        return (CacheObjectDate) get(strKey, strDate, blnExact);
    }
}
