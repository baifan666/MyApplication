package com.example.baifan.myapplication.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;

import com.example.baifan.myapplication.R;
import com.example.baifan.myapplication.application.ExitApplication;

public class EvaluateActivity extends Activity {
    private ImageView back;
    private RatingBar ratingBar;
    private EditText score;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_evaluate);
        //将该Activity添加到ExitApplication实例中，
        ExitApplication.getInstance().addActivity(this);
        back = (ImageView) findViewById(R.id.backImg); //返回
        score = (EditText)findViewById(R.id.score);
        ratingBar = (RatingBar) findViewById(R.id.ratingBar1);
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                score.setText(String.valueOf(rating));
            }
        });
    }

}
