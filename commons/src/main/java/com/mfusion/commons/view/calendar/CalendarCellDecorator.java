package com.mfusion.commons.view.calendar;

import java.util.Date;

public interface CalendarCellDecorator {
  void decorate(CalendarCellView cellView, Date date);
    void decorate(CalendarCellView cellView, MonthCellDescriptor cell);
}
