package vnp.util;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;

/**
 * <p>Title: He thong doi soat so lieu</p>
 *
 * <p>Description: He thong doi soat so lieu thue bao tra truoc</p>
 *
 * <p>Copyright: Copyright (c) 2013</p>
 *
 * <p>Company: Billing Center - Vinaphone</p>
 *
 * @author Nguyen Ngoc Tuan
 * @version 1.0
 */
public abstract class TextFile
{
    public String mstrFilePath = new String();

    public TextFile()
    {
    }

    public abstract void openFile(String strFilePath, int intBuffer)
            throws Exception;

    public abstract void closeFile()
            throws Exception;

    public abstract void safeCloseFile()
            throws Exception;
}
