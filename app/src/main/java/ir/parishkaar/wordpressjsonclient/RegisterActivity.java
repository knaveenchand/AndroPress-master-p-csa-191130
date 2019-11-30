package ir.parishkaar.wordpressjsonclient;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import ir.parishkaar.wordpressjsonclient.app.AppConfig;
import ir.parishkaar.wordpressjsonclient.helper.SQLiteHandler;
import ir.parishkaar.wordpressjsonclient.helper.SessionManager;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = RegisterActivity.class.getSimpleName();

    private Button btnRegister;
    private Button btnLinkToLogin;
    private EditText inputFullName;
    private EditText inputEmail;
    private EditText inputPassword, reinputPassword;

    private ProgressDialog pDialog;
    private SessionManager session;
    private SQLiteHandler db;

    private String noncerecd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        inputFullName = (EditText) findViewById(R.id.name);
        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        reinputPassword  = (EditText) findViewById(R.id.reenterpassword);
        btnRegister = (Button) findViewById(R.id.btnRegister);
        btnLinkToLogin = (Button) findViewById(R.id.btnLinkToLoginScreen);

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        // Session manager
        session = new SessionManager(getApplicationContext());

        // SQLite database handler
        db = new SQLiteHandler(getApplicationContext());

        // Check if user is already logged in or not
        if (session.isLoggedIn()) {
            // User is already logged in. Take him to main activity
            Intent intent = new Intent(RegisterActivity.this,
                    MainActivity.class);
            startActivity(intent);
            finish();
        }

        // Register Button Click event
        btnRegister.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String name = inputFullName.getText().toString().trim();
                String email = inputEmail.getText().toString().trim();
                String password = inputPassword.getText().toString().trim();
                String confirmPassword = reinputPassword.getText().toString().trim();

                if (!name.isEmpty() && !email.isEmpty() && !password.isEmpty() && (password.equals(confirmPassword))) {

                    getNonce();
                    registerUser(name, email, password);
                } else {
                    String errorReg="Error. ";
                    if (name.isEmpty()) {
                         errorReg += "Name is empty";
                    }
                    if (email.isEmpty()) {
                        errorReg += "Email is empty.";
                    }
                    if (password.isEmpty()) {
                        errorReg += "Password is empty.";
                    }
                    if (!(password.equals(confirmPassword))) {
                        errorReg += "Reentered password is not matching with the password.";
                    }
                    Toast.makeText(getApplicationContext(),
                            errorReg, Toast.LENGTH_LONG)
                            .show();
                }
//                InputMethodManager inputManager = (InputMethodManager)
//                        getSystemService(Context.INPUT_METHOD_SERVICE);
//
//                inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
//                        InputMethodManager.HIDE_NOT_ALWAYS);
            }

        });

        // Link to Login Screen
        btnLinkToLogin.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),
                        LoginActivity.class);
                startActivity(i);
                finish();
            }
        });


    }

    /**
     * Function to store user in WordPress API will post params(tag, name,
     * email, password) to register url
     * */

    private void getNonce () {
        String tag_string_reqNonce = "req_register_nonce";

        StringRequest nonceReq = new StringRequest(Request.Method.GET,
                AppConfig.URL + AppConfig.MIDPOINT_REGISTER, new Response.Listener<String>() {
            @Override
            public void onResponse (String response) {
                Log.d(TAG, "Nonce Response: " + response.toString());

                try {
                    JSONObject jObj = new JSONObject(response);
                    noncerecd = jObj.getString("nonce");

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        },  new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Mid Registration Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
            }

        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();

                return params;
            }

        };

        // Adding request to request queue
        MyApplication.getInstance().addToRequestQueue(nonceReq, tag_string_reqNonce);


    }

    private void registerUser(final String name, final String email,
                              final String password) {

        if (noncerecd != null) {

            // Tag used to cancel the request
            String tag_string_req = "req_register";

            pDialog.setMessage("Registering ...");
            showDialog();

            StringRequest strReq = new StringRequest(Request.Method.POST,
                    AppConfig.URL + AppConfig.ENDPOINT_REGISTER + "&username=" + name + "&nonce=" + noncerecd + "&display_name=" + name + "&notify=no&email=" + email + "&user_pass=" + password, new Response.Listener<String>() {

//            ?username=john&email=john@domain.com&nonce=8bdfeb4e16&display_name=John&notify=both&user_pass=YOUR-PASSWORD
//            http://localhost/api/user/register/?username=john&email=john@domain.com&nonce=8bdfeb4e16&display_name=John&notify=no


                @Override
                public void onResponse(String response) {
                    Log.d(TAG, "Register Response: " + response.toString());
                    hideDialog();

                    try {
                        JSONObject jObj = new JSONObject(response);
                        String status = jObj.getString("status");
                        String cookie = jObj.getString("cookie");
                        String cookiename = jObj.getString("cookie_name");

//                    int status = jObj.getInt("status");
                        if (status == "ok") {
                            // User successfully stored in WordPress

                            // Now store the user in SQLite
//                        String user_id = jObj.getString("user_id");

                            JSONObject user = jObj.getJSONObject("user");
                            String user_id = user.getString("id");
                            String name = user.getString("username");
                            String email = user.getString("email");
//                        String created_at = user.getString("created_at");


                            // Inserting row in users table
                            db.addUser(name, email, user_id, cookie, cookiename);

                            Toast.makeText(getApplicationContext(), "User successfully registered. Try login now!", Toast.LENGTH_LONG).show();

                            // Launch login activity
                            Intent intent = new Intent(
                                    RegisterActivity.this,
                                    LoginActivity.class);
                            startActivity(intent);
                            finish();
                        } else if (status == "error") {

                            // Error occurred in registration. Get the error
                            // message
                            String errorMsg = "Error";
                            Toast.makeText(getApplicationContext(),
                                    errorMsg, Toast.LENGTH_LONG).show();
                        } else {

                            // Error occurred in registration. Get the error
                            // message
                            String errorMsg = "Error";
                            Toast.makeText(getApplicationContext(),
                                    errorMsg, Toast.LENGTH_LONG).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e(TAG, "Registration Error: " + error.getMessage());
                    Toast.makeText(getApplicationContext(),
                            error.getMessage(), Toast.LENGTH_LONG).show();
                    hideDialog();
                }
            }) {

                @Override
                protected Map<String, String> getParams() {
                    // Posting params to register url
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("username", name);
                    params.put("email", email);
                    params.put("pass", password);

                    return params;
                }

            };

            // Adding request to request queue
            MyApplication.getInstance().addToRequestQueue(strReq, tag_string_req);
        }
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

}
