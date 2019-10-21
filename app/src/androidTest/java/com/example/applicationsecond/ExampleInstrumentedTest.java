package com.example.applicationsecond;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import androidx.test.platform.app.InstrumentationRegistry;

import com.example.applicationsecond.activities.MainActivity;
import com.example.applicationsecond.utils.Utils;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();

        assertEquals("com.example.applicationsecond", appContext.getPackageName());
    }

    @Rule
    public ActivityTestRule<MainActivity> activityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void checkInternetConnectionTest() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) activityTestRule.getActivity().getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
            assertTrue(Utils.isNetworkAvailable(activityTestRule.getActivity().getApplicationContext()));
        } else {
            assertFalse(Utils.isNetworkAvailable(activityTestRule.getActivity().getApplicationContext()));
        }
    }
}
