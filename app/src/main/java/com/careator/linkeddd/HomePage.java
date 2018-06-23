package com.careator.linkeddd;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.linkedin.platform.APIHelper;
import com.linkedin.platform.errors.LIApiError;
import com.linkedin.platform.listeners.ApiListener;
import com.linkedin.platform.listeners.ApiResponse;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

public class HomePage extends AppCompatActivity {
    LinearLayout profiledetails;
        private static final String host = "api.linkedin.com";
    private static final String url = "https://" + host
            + "/v1/people/~:" +
            "(id,email-address,first-name,last-name,phone-numbers,public-profile-url,picture-urls::(original))";

    private ProgressDialog progress;
    private TextView user_name, user_email, share, profile;
    private ImageView profile_pic;
    Button continuetolinkedin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        // Initialize the progressbar
         progress = new ProgressDialog(this);
        progress.setMessage("Retrieve data...");
        progress.setCanceledOnTouchOutside(false);
        progress.show();

        profile = findViewById(R.id.profile);
        share = findViewById(R.id.share);

        user_email = (TextView) findViewById(R.id.email);
        user_name = (TextView) findViewById(R.id.name);
        profile_pic = (ImageView) findViewById(R.id.profile_pic);
        profiledetails= findViewById(R.id.profiledetails);
        continuetolinkedin= findViewById(R.id.continuetolinkedin);

        //profiledetails.setVisibility(View.GONE);

        continuetolinkedin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(HomePage.this, com.careator.linkeddd.MainActivity.class);
                startActivity(intent);
            }
        });

        linkededinApiHelper();

    }

    public void linkededinApiHelper() {
        APIHelper apiHelper = APIHelper.getInstance(getApplicationContext());
        apiHelper.getRequest(HomePage.this, url, new ApiListener() {
            @Override
            public void onApiSuccess(ApiResponse result) {
                try {
                    showResult(result.getResponseDataAsJson());
                    progress.dismiss();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onApiError(LIApiError error) {

            }
        });
    }

    public void showResult(JSONObject response) {

        try {
            user_name.setText("FIRST NAME: "+response.get("firstName").toString());
            user_email.setText("LAST NAME: "+response.get("lastName").toString());
            profile.setText("EMAIL ID: "+response.get("emailAddress").toString());
            share.setText("SHARE: "+response.get("publicProfileUrl").toString());

            String img_url = response.getString("pictureUrl");

            Glide.with(this).load(img_url).into(profile_pic);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
