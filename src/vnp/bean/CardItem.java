package vnp.bean;

import java.math.BigDecimal;

public class CardItem {
	Long _id;
	int _nofCodes;

	public int get_nofCodes() {
		return _nofCodes;
	}

	public void set_nofCodes(int _nofCodes) {
		this._nofCodes = _nofCodes;
	}

	public Long get_id() {
		return _id;
	}

	public void set_id(Long _id) {
		this._id = _id;
	}

	String _msisdn;
	int _amount;
	int _subtype;

	/**
	 * @return the _subtype
	 */
	public int get_subtype() {
		return _subtype;
	}

	/**
	 * @param _subtype
	 *            the _subtype to set
	 */
	public void set_subtype(int _subtype) {
		this._subtype = _subtype;
	}

	/**
	 * @return the _msisdn
	 */
	public String get_msisdn() {
		return _msisdn;
	}

	/**
	 * @param _msisdn
	 *            the _msisdn to set
	 */
	public void set_msisdn(String _msisdn) {
		this._msisdn = _msisdn;
	}

	/**
	 * @return the _amount
	 */
	public int get_amount() {
		return _amount;
	}

	/**
	 * @param _amount
	 *            the _amount to set
	 */
	public void set_amount(int _amount) {
		this._amount = _amount;
	}
	
	int _adddays;
	public int get_addday() {
		return _adddays;
	}

	/**
	 * @param _amount
	 *            the _amount to set
	 */
	public void set_adddays(int _adddays) {
		this._adddays = _adddays;
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(_msisdn);
		sb.append("|");
		sb.append(_amount);
		sb.append("|");
		sb.append(_subtype);
		return sb.toString();
	}

}
