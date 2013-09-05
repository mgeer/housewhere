package com.sjl.housewhere;

import android.content.pm.ApplicationInfo;
import android.test.AndroidTestCase;
import com.sjl.housewhere.model.EstateRepository;

public class Spike extends AndroidTestCase {
    public void testName() throws Exception {
        ApplicationInfo applicationInfo = mContext.getApplicationInfo();
        String className = applicationInfo.className;
        EstateRepository estateRepository = new EstateRepository(mContext);
        estateRepository.getFiveEstates();
    }
}
