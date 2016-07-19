package com.mfusion.commons.entity.values;

public enum SchedulePlayType {

	TimeLine(1),
	Sequence(0);
	
	private int value = 0;

    private SchedulePlayType(int value) { 
        this.value = value;
    }

    public static SchedulePlayType valueOf(int value) {
        switch (value) {
        case 0:
            return Sequence;
        default:
            return TimeLine;
        }
    }

    public int value() {
        return this.value;
    }
}
