package com.skichrome.go4lunch;

import com.skichrome.go4lunch.utils.MapMethods;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class MapMethodTests
{
    private MapMethods mapMethods;
    private List<String> apertureRaw;
    private List<String> aperture;
    private int[] calendarInstance = {Calendar.MONDAY, Calendar.TUESDAY, Calendar.WEDNESDAY, Calendar.THURSDAY,  Calendar.FRIDAY, Calendar.SATURDAY, Calendar.SUNDAY};
    private int indexOfDay = 0;

    @Before
    public void configureMapMethods()
    {
        this.mapMethods = new MapMethods();
        this.aperture = new ArrayList<>();
        this.apertureRaw = new ArrayList<>();

        this.apertureRaw.add("lundi: 10:00 – 14:30, 18:30 – 22:00");
        this.apertureRaw.add("mardi: 10:00 – 14:30, 18:30 – 22:00");
        this.apertureRaw.add("mercredi: 10:00 – 14:30, 18:30 – 22:00");
        this.apertureRaw.add("jeudi: 10:00 – 22:00");
        this.apertureRaw.add("vendredi: 10:00 – 14:30, 18:30 – 22:00");
        this.apertureRaw.add("samedi: 10:00 – 14:30, 18:30 – 22:00");
        this.apertureRaw.add("dimanche: 10:00 – 14:30, 18:30 – 22:00");

        this.aperture.add("10:00 – 14:30, 18:30 – 22:00");
        this.aperture.add("10:00 – 14:30, 18:30 – 22:00");
        this.aperture.add("10:00 – 14:30, 18:30 – 22:00");
        this.aperture.add("10:00 – 22:00");
        this.aperture.add("10:00 – 14:30, 18:30 – 22:00");
        this.aperture.add("10:00 – 14:30, 18:30 – 22:00");
        this.aperture.add("10:00 – 14:30, 18:30 – 22:00");
    }

    @Test
    public void shouldRemoveAllFirstCharacters()
    {
        String result = mapMethods.convertAperture(apertureRaw, calendarInstance[indexOfDay]);
        assertEquals("10:00 – 14:30, 18:30 – 22:00", result);
    }

    @Test
    public void shouldReturnMondayIfTodayIsMonday()
    {
        String result = mapMethods.convertAperture(apertureRaw, calendarInstance[indexOfDay]);
        assertEquals(aperture.get(indexOfDay), result);
    }

    @Test
    public void shouldReturnSundayIfTodayIsSunday()
    {
        indexOfDay = 6;
        String result = mapMethods.convertAperture(apertureRaw, calendarInstance[indexOfDay]);
        assertEquals(aperture.get(indexOfDay), result);
    }

    @Test
    public void shouldReturnCorrectDayOfWeek()
    {
        for (int i = 0; i < apertureRaw.size(); i++)
        {
            String result = mapMethods.convertAperture(apertureRaw, calendarInstance[i]);
            assertEquals(aperture.get(i), result);
        }
    }
}