package com.uclan.mstocklmayr;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import com.googlecode.tesseract.android.TessBaseAPI;

public class MyActivity extends Activity {
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        //test
        TessBaseAPI api = null;

        Button btn_test = (Button) findViewById(R.id.btn_test);
        btn_test.setOnClickListener(btn_click);
        Log.v("DEBUG", "Init my activity...");
    }

    View.OnClickListener btn_click = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(MyActivity.this, CaptureActivity.class);
            startActivity(intent);
        }
    };
}
