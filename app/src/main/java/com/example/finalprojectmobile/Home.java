package com.example.finalprojectmobile;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.Inflater;

public class Home extends AppCompatActivity {

    static String username;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Intent intent = getIntent();
        username = intent.getStringExtra("username");

        Button viewInventory,addProduct,viewTransactions;

        viewInventory = (Button) findViewById(R.id.viewInventory);
        //addProduct = (Button) findViewById(R.id.addProduct);
        viewTransactions = (Button) findViewById(R.id.viewTransactions);
        TextView welcome = (TextView) findViewById(R.id.welcome);
        welcome.setText("Welcome "+username);

        viewInventory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                moveToProductList();
            }
        });

        viewTransactions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                moveToTransactionList();
            }
        });

        /*
        addProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addProductDialog();
            }
        });

         */

    }


    public void moveToProductList(){
        Intent intent = new Intent(this, ProductsList.class);
        startActivity(intent);
    }

    public void moveToTransactionList(){
        Intent intent = new Intent(this, TransactionList.class);
        startActivity(intent);
    }

    public void addProductDialog(){
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);

        dialog.setTitle("Add Product");
        dialog.setMessage("Please Enter the information of the product you want to add");
        dialog.setCancelable(false);




        LayoutInflater inflator = LayoutInflater.from(this);

        View dialogView = inflator.inflate(R.layout.add_product_dialog ,null);

        dialog.setView(dialogView);

        EditText name,quantity,imageUrl,info;

        name = (EditText)dialogView.findViewById(R.id.name);
        quantity = (EditText)dialogView.findViewById(R.id.quantity);
        imageUrl = (EditText)dialogView.findViewById(R.id.imageUrl);
        info = (EditText)dialogView.findViewById(R.id.info);




        dialog.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                /*
                createTransactionAdditionOfProduct(name.getText().toString(),
                        Integer.parseInt(quantity.getText().toString()
                        ), imageUrl.getText().toString(), info.getText().toString());


                 */
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            addProduct(new Product(name.getText().toString(),Integer.parseInt(quantity.getText().toString()),
                                    imageUrl.getText().toString()),info.getText().toString());

                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getApplicationContext(), "Product Successfully added", Toast.LENGTH_SHORT).show();
                                }
                            });


                        } catch (IOException | JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });

        dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        dialog.show();

    }


    static public void addProduct(Product product, String info) throws IOException, JSONException {

        URL url = new URL("https://mobilejsondatabase-default-rtdb.firebaseio.com/products/"+product.getName()+".json");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("PUT");
        con.setDoOutput(true);
        con.setRequestProperty("Content-Type", "application/json; utf-8");

        Map<String,String> map = new HashMap<>();

        map.put("name",product.getName());
        map.put("quantity", Integer.toString(product.getQuantity()));
        map.put("imageUrl", product.getImageUrl());


        String jsonString = new JSONObject(map).toString();
        //String jsonString = "{\""+product.getName()+"\":"+new JSONObject(map).toString()+"}";



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
        createTransactionAdditionOfProduct(product.getName(),product.getQuantity(),info);
    }



    static public void createTransactionAdditionOfProduct(String name, int quantity, String reason) throws IOException {

        String outline = "Product : "+name +" was added to the inventory with Quantity :"+quantity+"\n";

        URL url = new URL("https://mobilejsondatabase-default-rtdb.firebaseio.com/transactions.json");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");
        con.setDoOutput(true);
        con.setRequestProperty("Content-Type", "application/json; utf-8");

        Map<String,String> map = new HashMap<>();

        map.put("name",name);
        map.put("user", username);
        map.put("description", outline);
        map.put("datetime", new Date().toLocaleString());
        map.put("type", "addition");

        if(reason.equals(""))
            map.put("reason", "No Reason Was Provided");
        else
            map.put("reason", reason);


        String jsonString = new JSONObject(map).toString();


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

    static public void createTransactionDeletionOfProduct(String name, int quantity, String reason) throws IOException {

        String outline = "Product : "+name +" was deleted from the inventory with Quantity :"+quantity+"\n";

        URL url = new URL("https://mobilejsondatabase-default-rtdb.firebaseio.com/transactions.json");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");
        con.setDoOutput(true);
        con.setRequestProperty("Content-Type", "application/json; utf-8");

        Map<String,String> map = new HashMap<>();

        map.put("name",name);
        map.put("user", username);
        map.put("description", outline);
        map.put("datetime", new Date().toLocaleString());
        map.put("type", "deletion");
        map.put("reason", reason);

        String jsonString = new JSONObject(map).toString();


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


    static public void createTransactionEditOfProduct(String name, int quantity, boolean increased, String reason) throws IOException {
        String outline;
        if(increased)
            outline = "Product : "+name +" quantity was increased by "+quantity+"\n";
        else
            outline = "Product : "+name +" quantity was decreased by "+quantity+"\n";


        URL url = new URL("https://mobilejsondatabase-default-rtdb.firebaseio.com/transactions.json");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");
        con.setDoOutput(true);
        con.setRequestProperty("Content-Type", "application/json; utf-8");

        Map<String,String> map = new HashMap<>();

        map.put("name",name);
        map.put("user", username);
        map.put("description", outline);
        map.put("datetime", new Date().toLocaleString());
        map.put("type", "edit");
        map.put("reason", reason);

        String jsonString = new JSONObject(map).toString();


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