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
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button register = (Button) findViewById(R.id.register);
        Button login = (Button)findViewById(R.id.login);

        EditText username,password,repeatPassword;

        username = (EditText)findViewById(R.id.username);
        password = (EditText)findViewById(R.id.password);
        repeatPassword = (EditText)findViewById(R.id.repeatPassword);

        register.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View view) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        if(username.getText().toString().equals("") || password.getText().toString().equals("")){
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);

                                    dialog.setMessage("Username or Password is empty");

                                    dialog.setCancelable(true);

                                    dialog.show();
                                }
                            });
                            return;
                        }
                        if(password.getText().toString().equals(repeatPassword.getText().toString())){

                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    boolean exists = false;
                                    try {
                                        exists = checkIfUserExists(username.getText().toString());
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    System.out.println(exists);
                                    if(exists){
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);

                                                dialog.setMessage("Username Already Exists");

                                                dialog.setCancelable(true);

                                                dialog.show();
                                            }
                                        });
                                        return;
                                    }
                                    else{
                                        try {
                                            registerUser(username.getText().toString(),password.getText().toString());
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }

                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);

                                                dialog.setMessage("Account Successfully created");

                                                dialog.setCancelable(true);

                                                dialog.show();
                                            }
                                        });
                                    }

                                }
                            }).start();
                        }
                        else{
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);

                                    dialog.setMessage("Passwords dont Match, please try again");

                                    dialog.setCancelable(true);

                                    dialog.show();
                                }
                            });
                        }

                    }
                }).start();

            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                moveToLoginScreen();
            }
        });
    }


    public void moveToLoginScreen(){
        Intent intent = new Intent(this, Login.class);
        startActivity(intent);
    }


    public boolean checkIfUserExists(String username) throws IOException, JSONException {

            String urlText = "https://mobilejsondatabase-default-rtdb.firebaseio.com/users.json?orderBy" +
                    "=\"username\"&equalTo=\""+username+"\"";
            URL url = new URL(urlText);
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

            JSONObject obj = new JSONObject(content);
            System.out.println(obj.toString());
            System.out.println(obj.length());
            if(obj.length()>0)
                return true;
            else
                return false;
    }





    public void registerUser(String username, String password) throws IOException, JSONException {

        URL url = new URL("https://mobilejsondatabase-default-rtdb.firebaseio.com/users/"+username+".json");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("PUT");
        con.setDoOutput(true);
        con.setRequestProperty("Content-Type", "application/json; utf-8");

        Map<String,String> map = new HashMap<>();

        map.put("username",username);
        map.put("password", BCrypt.hashpw(password, BCrypt.gensalt()));


        String jsonString = new JSONObject(map).toString();



        System.out.println(jsonString);


        try(OutputStream os = con.getOutputStream()) {
            byte[] input = jsonString.getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        try(BufferedReader br = new BufferedReader(
                new InputStreamReader(con.getInputStream(), "utf-8"))) {
            StringBuilder response = new StringBuilder();
            String responseLine = null;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
            System.out.println(response.toString());
        }
    }


}