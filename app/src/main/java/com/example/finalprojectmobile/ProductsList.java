package com.example.finalprojectmobile;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.ListView;
import android.widget.SearchView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ProductsList extends AppCompatActivity {
    List<Product> products;
    List<Product> filteredList;
    ProductArrayAdapter adapter;
    ListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products_list);
        products = new ArrayList<Product>();
        FloatingActionButton btn = (FloatingActionButton) findViewById(R.id.addProductButton);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addProductDialog();
            }
        });

        listView = (ListView) findViewById(R.id.productsList);

        adapter = new ProductArrayAdapter(this, R.layout.product_list_item);

        listView.setAdapter(adapter);



        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    getProducts();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            for(int i=0; i<products.size(); i++){
                                adapter.add(products.get(i));
                                //adapter.allProducts.add(products.get(i));
                            }
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        EditText search = (EditText) findViewById(R.id.search);

        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                adapter.productList.clear();
                adapter.clear();
                adapter.productList = adapter.getFilteredList(charSequence);
                System.out.println(adapter.productList);
                adapter.addAll(adapter.productList);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }



    public void getProducts() throws IOException, JSONException {
        URL url = new URL("https://mobilejsondatabase-default-rtdb.firebaseio.com/products.json");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");

        System.out.println("After set Method");


        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        String content = "";
        while ((inputLine = in.readLine()) != null) {
            content = content + inputLine;
        }
        in.close();
        con.disconnect();


        JSONObject object = new JSONObject(content);


        Iterator<String> keysIterator = object.keys();
        String[] keys = new String[object.length()];

        int j=0;

        while (keysIterator.hasNext())
        {
            keys[j] = keysIterator.next();

            JSONObject part = object.getJSONObject(keys[j]);

            products.add(new Product(part.getString("name"),Integer.parseInt(part.get("quantity").toString()),part.getString("imageUrl")));

            j++;
        }

    }

    static public void editProductQuantity(String productName, int currentAmount, int amountToAddOrSubtract, boolean increase) throws IOException {
        URL url = new URL("https://mobilejsondatabase-default-rtdb.firebaseio.com/products.json");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("PATCH");
        con.setDoOutput(true);
        con.setRequestProperty("Content-Type", "application/json; utf-8");

        Map<String,String> map = new HashMap<>();

        int quantity=0;

        if(increase)
            quantity=currentAmount+amountToAddOrSubtract;
        else
            quantity=currentAmount-amountToAddOrSubtract;

        System.out.println(quantity);

        String jsonString = "{\""+productName+"/quantity" + "\":"+quantity+" }";

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



    public void addProductDialog(){
        androidx.appcompat.app.AlertDialog.Builder dialog = new AlertDialog.Builder(this);

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
                            Home.addProduct(new Product(name.getText().toString(),Integer.parseInt(quantity.getText().toString()),
                                    imageUrl.getText().toString()),info.getText().toString());
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                    adapter.add(new Product(name.getText().toString(),Integer.parseInt(quantity.getText().toString()),
                                                            imageUrl.getText().toString()));
                                            }
                                        });
                                }
                            }).start();
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


}


