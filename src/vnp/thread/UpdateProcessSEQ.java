package vnp.thread;

import java.sql.*;
import java.util.Date;
import java.text.SimpleDateFormat;
import smartlib.database.Database;

/**
 * <p>Title: He thong canh bao</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2013</p>
 *
 * <p>Company: Billing Center - Vinaphone</p>
 *
 * @author not attributable
 * @version 1.0
 */
public class UpdateProcessSEQ extends PortalThread {
    PreparedStatement stmt = null;
    ResultSet rs = null;

    protected void processSession() throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String sqlTopups =
                "SELECT max(id) FROM ios.topups partition (topups_" +
                sdf.format(new Date()) + ")";
        String sqlUpdateTopups =
                "update cdr_seq set value = ? where type = 'topups'";

        try {
            stmt = mcnMain.prepareCall(sqlTopups);
            rs = stmt.executeQuery();
            String strValue = "";
            if (rs.next()) {
                strValue = rs.getString(1);
            }
            if (!strValue.equalsIgnoreCase("")) {
                stmt = mcnMain.prepareCall(sqlUpdateTopups);
                stmt.setString(1, strValue);
                stmt.executeUpdate();
            }
            logMonitor("Update SEQ TOPUPS Successful! New SEQ is :" + strValue);
        } catch (Exception ex) {
            ex.printStackTrace();
            logMonitor(ex.getMessage());

        } finally {
            Database.closeObject(rs);
            Database.closeObject(stmt);
        }
    }

}
