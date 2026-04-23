package vnp.bean;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class CodeResponse extends BaseRespose implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public CodeResponse() {
		super("sucess");

	}

	// @Column(name = "LOTDATE", nullable = false)
	private Date lotDate;
	private List<BigDecimal> codes;

	public List<BigDecimal> getCodes() {
		return codes;
	}

	public void setCodes(List<BigDecimal> codes) {
		this.codes = codes;
	}

	public Date getLotDate() {
		return lotDate;
	}

	public void setLotDate(Date lotDate) {
		this.lotDate = lotDate;
	}
}
