package vnp.util;

import telsoft.util.StringUtil;
import telsoft.util.DateUtil;

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
public class CommonInfo {
    public CacheInfo cache;
    public CommonInfo() {
    }

    public void setCache(CacheInfo cc) {
        cache = cc;
    }

    public String getISDN0(String strIMSI, String strDate) throws Exception {
        return cache.getHSSVValue0(strIMSI,
                                   strDate);
    }

    public String getISDN1(String strIMSI, String strDate) throws Exception {
        return cache.getHSSVValue1(strIMSI,
                                   strDate);
    }

    public String getISDN2(String strIMSI, String strDate) throws Exception {
        return cache.getHSSVValue2(strIMSI,
                                   strDate);
    }

    public String getISDN3(String strIMSI, String strDate) throws Exception {
        return cache.getHSSVValue3(strIMSI,
                                   strDate);
    }

    public String getISDN4(String strIMSI, String strDate) throws Exception {
        return cache.getHSSVValue4(strIMSI,
                                   strDate);
    }

    public String getISDN5(String strIMSI, String strDate) throws Exception {
        return cache.getHSSVValue5(strIMSI,
                                   strDate);
    }

    public String getISDN6(String strIMSI, String strDate) throws Exception {
        return cache.getHSSVValue6(strIMSI,
                                   strDate);
    }

    public String getISDN7(String strIMSI, String strDate) throws Exception {
        return cache.getHSSVValue7(strIMSI,
                                   strDate);
    }

    public String getISDN8(String strIMSI, String strDate) throws Exception {
        return cache.getHSSVValue8(strIMSI,
                                   strDate);
    }

    public String getISDN9(String strIMSI, String strDate) throws Exception {
        return cache.getHSSVValue9(strIMSI,
                                   strDate);
    }
}
