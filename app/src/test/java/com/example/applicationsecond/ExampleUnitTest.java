package com.example.applicationsecond;

import com.example.applicationsecond.utils.Utils;
import com.google.android.gms.maps.model.LatLng;

import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void addZeroBehindOneNumber() {
        String string = "5";
        string = Utils.addZeroToDate(string);
        assertEquals("05", string);
    }

    @Test
    public void dontAddZeroBehindSeveralNumbers() {
        String str = "52";
        str = Utils.addZeroToDate(str);
        assertNotEquals("052", str);
        assertEquals("52", str);
    }

    @Test
    public void getParenthesisContentTest() {
        String str = "This is (example for test)";
        String newStr = Utils.getParenthesesContent(str);
        assertNotNull(newStr);
        assertEquals("example for test", newStr);
    }

    @Test
    public void getLatLngOfPlaceTest() {
        String latLngStr = "21.02,2.03";
        LatLng latLng = Utils.getLatLngOfPlace(latLngStr);
        assertNotNull(latLng);
        String latLngContent = Utils.getParenthesesContent(latLng.toString());
        assertEquals(latLngStr, latLngContent);
    }

    @Test
    public void resizeUsernameTest() {
        String username = "henrijacques";
        String newUsername = Utils.resizeUsername(username);
        assertEquals("henrijacqu.", newUsername);
        String anotherName = Utils.resizeUsername("joel");
        assertEquals("joel", anotherName);
    }
}