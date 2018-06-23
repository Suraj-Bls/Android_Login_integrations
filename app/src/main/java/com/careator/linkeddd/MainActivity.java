package com.careator.linkeddd;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.linkedin.platform.APIHelper;
import com.linkedin.platform.LISessionManager;
import com.linkedin.platform.errors.LIApiError;
import com.linkedin.platform.errors.LIAuthError;
import com.linkedin.platform.listeners.ApiListener;
import com.linkedin.platform.listeners.ApiResponse;
import com.linkedin.platform.listeners.AuthListener;
import com.linkedin.platform.utils.Scope;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private static final String host = "api.linkedin.com";
    private static final String url = "https://" + host
            + "/v1/people/~:" +
            "(id,email-address,first-name,last-name,phone-numbers,public-profile-url,picture-urls::(original))";

    private ProgressDialog progress;
    private TextView user_name, user_email, share, profile;
    private ImageView profile_pic;


    public static final String PACKAGE = "com.careator.linkeddd";
    Button linkedInGoogle;

    private LinearLayout gmailprofiledisplay;
    private LinearLayout gmaillikedinlogbut;
    LinearLayout fbprofiledisplay;
    LinearLayout fbbuttondisplay;
    LinearLayout linkedinprofiledisplay;

    Button linkedinlogout;
    private Button signout;
    private SignInButton googleSignIn;
    private ImageView imageview;
    private TextView email;
    private TextView name;
    private GoogleApiClient googleApiClient;
    private static final int REQ_CODE_GOOGLE = 9011;
    private static final int REQ_CODE2 = 9999;
    ProgressDialog mDialog;
    CallbackManager callbackManager;
    TextView textView1, textView2, textView3;
    ImageView imageView;
    LoginButton fbLogin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //FacebookSdk.sdkInitialize(this.getApplicationContext());

        //Test code thats it.

        setContentView(R.layout.activity_main);


        //  computePakageHash();
        googleSignIn = findViewById(R.id.googleSignIn);
        linkedInGoogle = findViewById(R.id.linkedInGoogle);
        fbLogin = findViewById(R.id.fbLogin);

        gmailprofiledisplay = findViewById(R.id.gmailprofiledisplay);
        gmaillikedinlogbut = findViewById(R.id.gmaillikedinlogbut);
        signout = findViewById(R.id.signout);
        name = findViewById(R.id.nametv);
        email = findViewById(R.id.emailtv);
        imageview = findViewById(R.id.imageview);

        callbackManager = CallbackManager.Factory.create();

        textView1 = findViewById(R.id.textView1);
        textView2 = findViewById(R.id.textView2);
        textView3 = findViewById(R.id.textView3);
        imageView = findViewById(R.id.imageView);
        fbprofiledisplay = findViewById(R.id.fbprofiledisplay);
        linkedinprofiledisplay = findViewById(R.id.linkedinprofiledisplay);
        fbbuttondisplay = findViewById(R.id.fbbuttondisplay);
        linkedinlogout = findViewById(R.id.linkedinlogout);

        //progress = new ProgressDialog(this);
        //progress.setMessage("Retrieve data...");
        //progress.setCanceledOnTouchOutside(false);
        //progress.show();

        profile = findViewById(R.id.profile);
        share = findViewById(R.id.share);

        user_email = (TextView) findViewById(R.id.email);
        user_name = (TextView) findViewById(R.id.name);
        profile_pic = (ImageView) findViewById(R.id.profile_pic);

        //linkededinApiHelper();

        fbprofiledisplay.setVisibility(View.GONE);
        //TODO fb profile display=gone (SET gone initially)
        linkedinprofiledisplay.setVisibility(View.GONE);
        //TODO Linkedin profile display=gone (SET gone initially)
        gmailprofiledisplay.setVisibility(View.GONE);
        //TODO Gmail profile disp=GONE (SET gone initially)


        fbLogin.setReadPermissions(Arrays.asList("public_profile", "email", "user_birthday", "user_friends"));

        googleSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInWithGoogle();
            }
        });

        linkedInGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginWithLinkedIn(v);
            }
        });


        fbLogin.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                mDialog = new ProgressDialog(MainActivity.this);
                mDialog.setMessage("Obtaining Data");
                mDialog.show();
                //TODO Fb profile display visible on clicking login button
                fbprofiledisplay.setVisibility(View.VISIBLE);
                gmaillikedinlogbut.setVisibility(View.GONE);

                String acesstoken = loginResult.getAccessToken().getToken();

                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        mDialog.dismiss();
                        Log.d("response", response.toString());
                        getData(object);
                        // TODO fb success login

                    }
                });

                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,email,birthday,friends");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });

        if (AccessToken.getCurrentAccessToken() != null) {
            textView1.setText(AccessToken.getCurrentAccessToken().getUserId());
        }

        // initial gmailprof disp

        GoogleSignInOptions signInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        googleApiClient = new GoogleApiClient.Builder(this).enableAutoManage(this, this).addApi(Auth.GOOGLE_SIGN_IN_API, signInOptions).build();

        signout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });

    }


    public void linkededinApiHelper() {
        APIHelper apiHelper = APIHelper.getInstance(getApplicationContext());
        apiHelper.getRequest(MainActivity.this, url, new ApiListener() {
            @Override
            public void onApiSuccess(ApiResponse result) {
                try {
                    showResult(result.getResponseDataAsJson());
                    //progress.dismiss();
                    //TODO Linkedin Profile display= Visible on clicking login
                    linkedinprofiledisplay.setVisibility(View.VISIBLE);
                    gmaillikedinlogbut.setVisibility(View.GONE);
                    fbbuttondisplay.setVisibility(View.GONE);

                    linkedinlogout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //TODO Linkedin profiledisplay=GONE on clicking linkedin logout button
                            linkedinprofiledisplay.setVisibility(View.GONE);
                            gmaillikedinlogbut.setVisibility(View.VISIBLE);
                            fbbuttondisplay.setVisibility(View.VISIBLE);
                        }
                    });

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

            user_email.setText(response.get("emailAddress").toString());
            user_name.setText(response.get("firstName").toString());
            share.setText(response.get("lastName").toString());
            profile.setText(response.get("publicProfileUrl").toString());

            String img_url = response.getString("pictureUrl");

            Glide.with(this).load(img_url).into(profile_pic);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void getData(JSONObject object) {
        try {
            URL profile_picture = new URL("https://graph.facebook.com/" + object.getString("id") + "/picture?width=250&height=250");

            Picasso.with(this).load(profile_picture.toString()).into(imageView);
            textView1.setText("EMAIL ID:  "+object.getString("email"));
            textView2.setText("DOB: "+object.getString("birthday"));
            textView3.setText("FRIENDS: " + object.getJSONObject("friends").getJSONObject("summary").getString("total_count"));

            fbLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //TODO Fbprofile display=Gone on clicking logout button. SET back the login buttons
                    fbprofiledisplay.setVisibility(View.GONE);
                    gmaillikedinlogbut.setVisibility(View.VISIBLE);
                }
            });

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


//    public void computePakageHash()
//    {
//        try{
//            PackageInfo info= getPackageManager().getPackageInfo("com.careator.linkeddd", PackageManager.GET_SIGNATURES);
//            for (Signature signature:info.signatures){
//                MessageDigest md= MessageDigest.getInstance("SHA");
//                md.update(signature.toByteArray());
//                Log.d("KeyHash", Base64.encodeToString(md.digest(),Base64.DEFAULT));
//            }
//        }
//        catch (Exception e){
//            Log.e("TAG",e.getMessage());
//        }
//    }


    // Authenticate with linkedin and intialize Session.
    public void loginWithLinkedIn(View view) {
        LISessionManager.getInstance(getApplicationContext())
                .init(this, buildScope(), new AuthListener() {
                    @Override
                    public void onAuthSuccess() {

                        Toast.makeText(getApplicationContext(), "success",
                                Toast.LENGTH_LONG).show();
                        // TODO linkedIn success login
                        //linkededinApiHelper();

                    }

                    @Override
                    public void onAuthError(LIAuthError error) {
                        Toast.makeText(getApplicationContext(), "failed "
                                        + error.toString(),
                                Toast.LENGTH_LONG).show();
                    }
                }, true);
    }

    // handle the respone by calling LISessionManager and start new activity


    private static Scope buildScope() {
        return Scope.build(Scope.R_BASICPROFILE, Scope.R_EMAILADDRESS, Scope.W_SHARE, Scope.RW_COMPANY_ADMIN);
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


    private void signInWithGoogle() {

        Intent intent1 = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        startActivityForResult(intent1, REQ_CODE_GOOGLE);
        //TODO Google start activity
    }

    private void signOut() {
        Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {
                updateUI(false);
            }
        });

    }

    private void handleResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            GoogleSignInAccount account = result.getSignInAccount();
            String namea = account.getDisplayName();
            String emaila = account.getEmail();
            String image_url = account.getPhotoUrl().toString();


            name.setText("USER NAME: "+namea);
            email.setText("EMAIL ID:  "+emaila);
            Glide.with(this).load(image_url).into(imageview);
            updateUI(true);

            Toast.makeText(this, "LOGIN SUCCESSFULL", Toast.LENGTH_SHORT).show();

        } else {
            Toast.makeText(this, "LOGIN FAILED", Toast.LENGTH_SHORT).show();
            updateUI(false);
        }
    }

    private void updateUI(boolean isLogin) {
        if (isLogin) {

            //TODO SET GMAIL profile display= VISIBLE on clicking login button

            gmailprofiledisplay.setVisibility(View.VISIBLE);
            gmaillikedinlogbut.setVisibility(View.GONE);
            fbbuttondisplay.setVisibility(View.GONE);
        } else {

            //TODO SET Gmail profile display=GONE on clicking signout button

            gmailprofiledisplay.setVisibility(View.GONE);
            gmaillikedinlogbut.setVisibility(View.VISIBLE);
            fbbuttondisplay.setVisibility(View.VISIBLE);

        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // google
        if (requestCode == REQ_CODE_GOOGLE) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleResult(result);
            // TODO  on success Google
        } else if (requestCode == 64206) {
            // facebook
            callbackManager.onActivityResult(requestCode, resultCode, data);
        } else if (requestCode == 3672){
            // LinkedIn
            LISessionManager.getInstance(getApplicationContext())
                    .onActivityResult(this,
                            requestCode, resultCode, data);

            Intent intent = new Intent(MainActivity.this, HomePage.class);
            startActivity(intent);
        }
    }


}
