package com.ibm.is.sappack.cw.app.data.bdr;

public enum FieldUsageUseMode {
	READ(0), //deprecated
	WRITE(1), //deprecated
	FOLLOWUP(2), 
	UNUSED(3), //deprecated
	INSCOPE(4), 
	NOTINSCOPE(5),
	BLANK(6),
	
	UNSPECIFIED(-1);
	
	private int value;
	
	private FieldUsageUseMode(int value) {
		this.value = value;
	}
	
	public static FieldUsageUseMode fromInt(final int useMode) {
		switch (useMode) {
			case 0: return READ; //deprecated
			case 1: return WRITE; //deprecated
			case 2: return FOLLOWUP; 
			case 3: return UNUSED; //deprecated
			case 4: return INSCOPE;
			case 5: return NOTINSCOPE;
			case 6: return BLANK;
			case -1: return UNSPECIFIED;
			default:
				throw new IllegalArgumentException();
		}
	}
	
	public static FieldUsageUseMode fromString(final String useMode) {
		FieldUsageUseMode mode = FieldUsageUseMode.BLANK;
		for (FieldUsageUseMode tmpMode : FieldUsageUseMode.values()) {
			if (tmpMode.toString().equals(useMode)) {
				mode = tmpMode;
				break;
			}
		}
		return mode;
	}
	
	public static int toInt(FieldUsageUseMode useMode) {
		return useMode.value;
	}
}
