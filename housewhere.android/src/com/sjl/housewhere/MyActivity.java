package com.sjl.housewhere;


import android.app.Activity;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.sjl.housewhere.model.Estate;
import com.sjl.housewhere.model.EstateRepository;

import java.util.List;

public class MyActivity extends Activity {
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        spikeDatabase();
    }

    private void spikeDatabase() {
        EstateRepository estateRepository = new EstateRepository(this);
        LinearLayout linearLayout = (LinearLayout)findViewById(R.id.main_panel);
        List<Estate> fiveEstates = estateRepository.getFiveEstates();
        for (Estate estate : fiveEstates){
            TextView textView = new TextView(this);
            textView.setText(estate.toString());
            textView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            linearLayout.addView(textView);
        }
    }

}
