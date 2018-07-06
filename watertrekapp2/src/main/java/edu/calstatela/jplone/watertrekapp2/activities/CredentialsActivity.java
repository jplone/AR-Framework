package edu.calstatela.jplone.watertrekapp2.activities;

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

    EditText usernameEditText;
    EditText passwordEditText;
    Button submitButton;

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get the sharedPreferences file where credentials are stored
        sharedPreferences = getDefaultSharedPreferences(this);


        // Setup Layout
        LinearLayout verticalLayout = new LinearLayout(this);
        verticalLayout.setOrientation(LinearLayout.VERTICAL);

        TextView sectionTitleTextView = new TextView(this);
        sectionTitleTextView.setText("Watertrek Credentials");

        LinearLayout usernameLayout = new LinearLayout(this);
        usernameLayout.setOrientation(LinearLayout.HORIZONTAL);
        TextView usernameTextView = new TextView(this);
        usernameTextView.setText("username: ");
        usernameEditText = new EditText(this);
        usernameEditText.setMinWidth(200);
        usernameLayout.addView(usernameTextView);
        usernameLayout.addView(usernameEditText);

        LinearLayout passwordLayout = new LinearLayout(this);
        passwordLayout.setOrientation(LinearLayout.HORIZONTAL);
        TextView passwordTextView = new TextView(this);
        passwordTextView.setText("password: ");
        passwordEditText = new EditText(this);
        passwordEditText.setMinWidth(200);
        passwordLayout.addView(passwordTextView);
        passwordLayout.addView(passwordEditText);

        submitButton = new Button(this);
        submitButton.setText("Submit");
        submitButton.setOnClickListener(buttonListener);

        verticalLayout.addView(sectionTitleTextView);
        verticalLayout.addView(usernameLayout);
        verticalLayout.addView(passwordLayout);
        verticalLayout.addView(submitButton);

        this.setContentView(verticalLayout);

        // If username and password have already been entered, fill in fields
        String usernameFromPreferences = sharedPreferences.getString(watertrekUsernameKey, "");
        usernameEditText.setText(usernameFromPreferences);
        String passwordFromPreferences = sharedPreferences.getString(watertrekPasswordKey, "");
        passwordEditText.setText(passwordFromPreferences);
    }

    Button.OnClickListener buttonListener = new Button.OnClickListener(){
        @Override
        public void onClick(View view) {
            String usernameString = usernameEditText.getText().toString();
            String passwordString = passwordEditText.getText().toString();
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
