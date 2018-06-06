package edu.calstatela.jplone.watertrekapp;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

public class CredentialsActivity extends Activity{
    private static final String TAG = "waka-credentials";

    String watertrekUsernameKey = "watertrekUsername";
    String watertrekPasswordKey = "watertrekPassword";

    EditText username;
    EditText password;
    Button submitButton;

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get the sharedPreferences file where credentials are stored
        sharedPreferences = getDefaultSharedPreferences(this);

        // If password has already been entered, just skip directly to MainActivity
//        if(sharedPreferences.contains(watertrekUsernameKey) && sharedPreferences.contains(watertrekPasswordKey))
//            launchMainActivity();

        // Setup Layout
        LinearLayout verticalLayout = new LinearLayout(this);
        verticalLayout.setOrientation(LinearLayout.VERTICAL);

        TextView sectionTitleTextView = new TextView(this);
        sectionTitleTextView.setText("Watertrek Credentials");

        LinearLayout usernameLayout = new LinearLayout(this);
        usernameLayout.setOrientation(LinearLayout.HORIZONTAL);
        TextView usernameTextView = new TextView(this);
        usernameTextView.setText("username: ");
        username = new EditText(this);
        username.setMinWidth(200);
        usernameLayout.addView(usernameTextView);
        usernameLayout.addView(username);

        LinearLayout passwordLayout = new LinearLayout(this);
        passwordLayout.setOrientation(LinearLayout.HORIZONTAL);
        TextView passwordTextView = new TextView(this);
        passwordTextView.setText("password: ");
        password = new EditText(this);
        password.setMinWidth(200);
        passwordLayout.addView(passwordTextView);
        passwordLayout.addView(password);

        submitButton = new Button(this);
        submitButton.setText("Submit");
        submitButton.setOnClickListener(buttonListener);

        verticalLayout.addView(sectionTitleTextView);
        verticalLayout.addView(usernameLayout);
        verticalLayout.addView(passwordLayout);
        verticalLayout.addView(submitButton);

        this.setContentView(verticalLayout);
    }

    Button.OnClickListener buttonListener = new Button.OnClickListener(){
        @Override
        public void onClick(View view) {
            String usernameString = username.getText().toString();
            String passwordString = password.getText().toString();
            Log.d(TAG, "username: " + usernameString + "   password: " + passwordString);

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(watertrekUsernameKey, usernameString);
            editor.putString(watertrekPasswordKey, passwordString);
            editor.apply();

            launchMainActivity();
        }
    };

    private void launchMainActivity(){
        Intent intent = new Intent(CredentialsActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
