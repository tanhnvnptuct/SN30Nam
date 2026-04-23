package vnp.util;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import smartlib.util.FileUtil;

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
public class TextFileWriter
        extends TextFile
{
    public int mintCount = 0;
    public FileOutputStream mFile = null;
    public BufferedOutputStream mBuffer = null;
    //////////////////////////////////////////////////////////
    public void openFile(String strFilePath, int intBuffer)
            throws Exception
    {
        openFile(strFilePath, intBuffer, false);
    }

    public void openFile(String strFilePath, int intBuffer, boolean blnAppend)
            throws Exception
    {
        mintCount = 0;
        mstrFilePath = strFilePath;
        try
        {
            mFile = new FileOutputStream(strFilePath, blnAppend);
            mBuffer = new BufferedOutputStream(mFile, intBuffer); // 5M
        }
        catch (Exception e)
        {
            safeCloseFile();
            throw e;
        }
    }

    public long getCount()
    {
        return mintCount;
    }

    public void addText(String strText)
            throws Exception
    {
        mintCount++;
        strText += '\n';
        try
        {
            mBuffer.write(strText.getBytes());
        }
        catch (Exception e)
        {
            throw e;
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    public void clear()
            throws Exception
    {
        if ( (mstrFilePath != null) && !mstrFilePath.equals(""))
        {
            try
            {
                closeFile();
            }
            catch (Exception e)
            {
                safeCloseFile();
                throw e;
            }
            finally
            {
                FileUtil.deleteFile(mstrFilePath);
            }
        }
    }

    public void closeFile()
            throws Exception
    {
        mintCount = 0;
        try
        {
            mBuffer.flush();
            mBuffer.close();
            mFile.close();
        }
        catch (Exception e)
        {
            throw e;
        }
        finally
        {
            safeCloseFile();
        }
    }

    public void safeCloseFile()
            throws Exception
    {
        mintCount = 0;
        FileUtil.safeClose(mBuffer);
        FileUtil.safeClose(mFile);
    }
}
