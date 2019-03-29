package com.skichrome.go4lunch;

import com.skichrome.go4lunch.controllers.fragments.SettingFragment;

import org.junit.Before;
import org.junit.Test;

import java.util.Calendar;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class NotificationUnitTests
{
    private Calendar mCalendarNow;
    private Calendar mCalendarMidDay;

    @Before
    public void configureDates()
    {
        mCalendarMidDay = SettingFragment.configureMidDayCalendar();
        mCalendarNow = Calendar.getInstance();
    }

    @Test
    public void shouldReturnDifferenceBetweenMorningAndMidDay()
    {
        mCalendarNow.set(Calendar.HOUR_OF_DAY, 10);

        long delta = SettingFragment.getDeltaCalendar(mCalendarNow, mCalendarMidDay);

        assertEquals(mCalendarMidDay.getTimeInMillis() - mCalendarNow.getTimeInMillis(), delta);
    }

    @Test
    public void shouldReturnDifferenceBetweenAfternoonAndNexMidDay()
    {
        mCalendarNow.set(Calendar.HOUR_OF_DAY, 15);

        long delta = SettingFragment.getDeltaCalendar(mCalendarNow, mCalendarMidDay);

        assertEquals(mCalendarMidDay.getTimeInMillis() - mCalendarNow.getTimeInMillis(), delta);
        assertTrue(delta > 0);
    }

    @Test
    public void shouldWorkForAllTimeOfDay()
    {
        for (int hour = 0; hour < 24; hour++)
        {
            for (int minute = 0; minute <= 60; minute++)
            {
                for (int day = mCalendarNow.get(Calendar.DAY_OF_MONTH); day <= mCalendarNow.get(Calendar.DAY_OF_MONTH) + 1; day++)
                {
                    mCalendarNow.set(Calendar.DAY_OF_MONTH, day);
                    mCalendarNow.set(Calendar.HOUR_OF_DAY, hour);
                    mCalendarNow.set(Calendar.MINUTE, minute);

                    long delta = SettingFragment.getDeltaCalendar(mCalendarNow, mCalendarMidDay);

                    assertEquals(mCalendarMidDay.getTimeInMillis() - mCalendarNow.getTimeInMillis(), delta);
                    assertTrue(delta > 0);
                }
            }
        }
    }
}