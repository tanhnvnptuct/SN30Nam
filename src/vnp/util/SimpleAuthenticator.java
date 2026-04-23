package vnp.util;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

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

public class SimpleAuthenticator extends Authenticator {
	private PasswordAuthentication m_Authentication;

	public SimpleAuthenticator(String username, String password) {
		m_Authentication = new PasswordAuthentication(username, password);
	}

	protected PasswordAuthentication getPasswordAuthentication() {
		return m_Authentication;
	}

	public String getPassword() {
		return m_Authentication.getPassword();
	}

	public String getUserName() {
		return m_Authentication.getUserName();
	}
}
