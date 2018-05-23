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

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

public class CredentialsActivity extends Activity{
    private static final String TAG = "waka-credentials";

    String watertrekUsernameKey = "watertrekUsername";
    String watertrekPasswordKey = "watertrekPassword";

    LinearLayout layout;
    EditText username;
    EditText password;
    Button submitButton;

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPreferences = getDefaultSharedPreferences(this);

        layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        username = new EditText(this);
        password = new EditText(this);
        submitButton = new Button(this);
        submitButton.setText("Submit");
        submitButton.setOnClickListener(buttonListener);

        layout.addView(username);
        layout.addView(password);
        layout.addView(submitButton);

        this.setContentView(layout);
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

            Intent intent = new Intent(CredentialsActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    };
}
