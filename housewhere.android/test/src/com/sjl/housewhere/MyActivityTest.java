package com.sjl.housewhere;

import android.app.Application;
import android.test.ActivityInstrumentationTestCase2;
import com.sjl.housewhere.model.Estate;
import com.sjl.housewhere.model.EstateRepository;
import junit.framework.Assert;

import java.util.List;

/**
 * This is a simple framework for a test of an Application.  See
 * {@link android.test.ApplicationTestCase ApplicationTestCase} for more information on
 * how to write and extend Application tests.
 * <p/>
 * To run this test, you can type:
 * adb shell am instrument -w \
 * -e class com.sjl.housewhere.MyActivityTest \
 * com.sjl.housewhere.tests/android.test.InstrumentationTestRunner
 */
public class MyActivityTest extends ActivityInstrumentationTestCase2<MyActivity> {

    public MyActivityTest() {
        super("com.sjl.housewhere", MyActivity.class);
    }

}

