package com.example.finalprojectmobile;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;

public class Login extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Button login = (Button) findViewById(R.id.login);
        Button register = (Button) findViewById(R.id.register);

        EditText username,password;

        username = (EditText)findViewById(R.id.username);
        password = (EditText)findViewById(R.id.password);


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            authenticateLogin(username.getText().toString(),password.getText().toString());
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }).start();

            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                moveToRegisterScreen();
            }
        });
    }





    public void login(String username){

        Intent intent = new Intent(this, Home.class);
        intent.putExtra("username", username);
        startActivity(intent);
    }

    public void moveToRegisterScreen(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }






    public void authenticateLogin(String username, String password) throws IOException, JSONException {
        URL url = new URL("https://mobilejsondatabase-default-rtdb.firebaseio.com/users/"+username+".json");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");



        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        String content = "";
        while ((inputLine = in.readLine()) != null) {
            content = content + inputLine;
        }
        in.close();
        con.disconnect();
        System.out.println(content);
        JSONObject object=null;
        try{
             object = new JSONObject(content);
        }
        catch (JSONException e){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(Login.this);

                    dialog.setMessage("Wrong Username, please try again");

                    dialog.setCancelable(true);

                    dialog.show();

                }
            });

            return;
        }
        if(BCrypt.checkpw(password, object.getString("password")))
            login(username);
        else{

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(Login.this);

                    dialog.setMessage("Wrong Password, please try again");

                    dialog.setCancelable(true);

                    dialog.show();
                }
            });

            }

        }
}