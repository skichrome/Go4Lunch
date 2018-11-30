package com.skichrome.go4lunch;

import com.skichrome.go4lunch.utils.MapMethods;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class MapMethodTests
{
    private List<String> mApertureRaw;
    private List<String> mAperture;

    private List<String> mApertureSundayClosedRaw;
    private List<String> mApertureSundayClosed;

    private int[] mCalendarInstance = {Calendar.MONDAY, Calendar.TUESDAY, Calendar.WEDNESDAY, Calendar.THURSDAY,  Calendar.FRIDAY, Calendar.SATURDAY, Calendar.SUNDAY};
    private int mIndexOfDay = 0;

    @Before
    public void configureMapMethods()
    {
        this.mAperture = new ArrayList<>();
        this.mApertureRaw = new ArrayList<>();

        this.mApertureSundayClosed = new ArrayList<>();
        this.mApertureSundayClosedRaw = new ArrayList<>();

        this.mApertureRaw.add("lundi: 10:00 – 14:30, 18:30 – 22:00");
        this.mApertureRaw.add("mardi: 10:00 – 14:30, 18:30 – 22:00");
        this.mApertureRaw.add("mercredi: 10:00 – 14:30, 18:30 – 22:00");
        this.mApertureRaw.add("jeudi: 10:00 – 22:00");
        this.mApertureRaw.add("vendredi: 10:00 – 14:30, 18:30 – 22:00");
        this.mApertureRaw.add("samedi: 10:00 – 14:30, 18:30 – 22:00");
        this.mApertureRaw.add("dimanche: 10:00 – 14:30, 18:30 – 22:00");

        this.mAperture.add("10:00 – 14:30, 18:30 – 22:00");
        this.mAperture.add("10:00 – 14:30, 18:30 – 22:00");
        this.mAperture.add("10:00 – 14:30, 18:30 – 22:00");
        this.mAperture.add("10:00 – 22:00");
        this.mAperture.add("10:00 – 14:30, 18:30 – 22:00");
        this.mAperture.add("10:00 – 14:30, 18:30 – 22:00");
        this.mAperture.add("10:00 – 14:30, 18:30 – 22:00");

        this.mApertureSundayClosedRaw.add("lundi: 10:00 – 14:30, 18:30 – 22:00");
        this.mApertureSundayClosedRaw.add("mardi: 10:00 – 14:30, 18:30 – 22:00");
        this.mApertureSundayClosedRaw.add("mercredi: 10:00 – 14:30, 18:30 – 22:00");
        this.mApertureSundayClosedRaw.add("jeudi: 10:00 – 22:00");
        this.mApertureSundayClosedRaw.add("vendredi: 10:00 – 14:30, 18:30 – 22:00");
        this.mApertureSundayClosedRaw.add("samedi: 10:00 – 14:30, 18:30 – 22:00");
        this.mApertureSundayClosedRaw.add("dimanche: Fermé");

        this.mApertureSundayClosed.add("10:00 – 14:30, 18:30 – 22:00");
        this.mApertureSundayClosed.add("10:00 – 14:30, 18:30 – 22:00");
        this.mApertureSundayClosed.add("10:00 – 14:30, 18:30 – 22:00");
        this.mApertureSundayClosed.add("10:00 – 22:00");
        this.mApertureSundayClosed.add("10:00 – 14:30, 18:30 – 22:00");
        this.mApertureSundayClosed.add("10:00 – 14:30, 18:30 – 22:00");
        this.mApertureSundayClosed.add("Closed Today");
    }

    @Test
    public void shouldRemoveAllFirstCharacters()
    {
        String result = MapMethods.convertAperture(mApertureRaw, mCalendarInstance[mIndexOfDay]);
        assertEquals("10:00 – 14:30, 18:30 – 22:00", result);
    }

    @Test
    public void shouldReturnMondayIfTodayIsMonday()
    {
        String result = MapMethods.convertAperture(mApertureRaw, mCalendarInstance[mIndexOfDay]);
        assertEquals(mAperture.get(mIndexOfDay), result);
    }

    @Test
    public void shouldReturnSundayIfTodayIsSunday()
    {
        mIndexOfDay = 6;
        String result = MapMethods.convertAperture(mApertureRaw, mCalendarInstance[mIndexOfDay]);
        assertEquals(mAperture.get(mIndexOfDay), result);
    }

    @Test
    public void shouldReturnCorrectDayOfWeek()
    {
        for (int i = 0; i < mApertureRaw.size(); i++)
        {
            String result = MapMethods.convertAperture(mApertureRaw, mCalendarInstance[i]);
            assertEquals(mAperture.get(i), result);
        }
    }

    @Test
    public void shouldReturnClosedIfNoOpenHoursIsSpecified()
    {
        mIndexOfDay = 6;
        String result = MapMethods.convertAperture(mApertureSundayClosedRaw, mCalendarInstance[mIndexOfDay]);
        assertEquals(mApertureSundayClosed.get(mIndexOfDay), result);
    }
}