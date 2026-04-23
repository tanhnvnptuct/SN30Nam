package vnp.util;

public class LotEnums {
	public enum MOResponseCode {
		Matched, NoMatched, NoCode, Unknown
	}

	public enum RewardAction {
		SendSMS, MinusMoney, ChangeSubType, None

	}
	public enum ProvinceProcessStatus{
		DATA_READY,DATA_PROCESSED,LOT_PROCESSED,SMS_PROCESSED,SYNC_READY,SYNC_PROCESSED
	}
}
