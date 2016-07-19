package com.mfusion.commons.entity.values;

public enum BlockType {
	
	Single(1),
	Interval(0);
	
	private int value = 0;

    private BlockType(int value) {
        this.value = value;
    }

    public static BlockType valueOf(int value) {
        switch (value) {
        case 0:
            return Interval;
        default:
            return Single;
        }
    }

    public int value() {
        return this.value;
    }
}
