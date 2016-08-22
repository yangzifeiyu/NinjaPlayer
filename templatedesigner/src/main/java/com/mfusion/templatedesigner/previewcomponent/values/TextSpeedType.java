package com.mfusion.templatedesigner.previewcomponent.values;

public enum TextSpeedType {
	Slow(100),
	Medium(150),
	Fast(200),
	VeryFast(350);
	
	private int value = 0;

    private TextSpeedType(int value) {    //    ������private�ģ�����������
        this.value = value;
    }

    public static TextSpeedType valueOf(int value) {    //    ��д�Ĵ�int��enum��ת������
        switch (value) {
        case 100:
            return Slow;
        case 200:
            return Fast;
        case 350:
            return VeryFast;
        default:
            return Medium;
        }
    }

    public int value() {
        return this.value;
    }
}
