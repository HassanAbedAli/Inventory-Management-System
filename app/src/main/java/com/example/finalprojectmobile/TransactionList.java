package com.example.finalprojectmobile;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class TransactionList extends AppCompatActivity {

    List<Transaction> transactions;
    List<Transaction> notTemp = new ArrayList<>();
    ListView listView;
    TransactionArrayAdapter adapter;
    boolean closest = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_list);

        listView = (ListView) findViewById(R.id.transactionList);
        transactions = new ArrayList<Transaction>();

        adapter = new TransactionArrayAdapter(this, R.layout.transaction_list_item);

        listView.setAdapter(adapter);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    getTransactions();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            for(int i=transactions.size()-1; i>0; i--){
                                adapter.add(transactions.get(i));
                                notTemp.add(transactions.get(i));
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

        Button sort = (Button) findViewById(R.id.sort);
        sort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sortDifferently(closest);
                closest=!closest;
            }
        });
    }

    public void getTransactions() throws IOException, JSONException {
        URL url = new URL("https://mobilejsondatabase-default-rtdb.firebaseio.com/transactions.json");
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


        JSONObject object = new JSONObject(content);


        Iterator<String> keysIterator = object.keys();
        String[] keys = new String[object.length()];

        int j=0;

        while (keysIterator.hasNext()) {
            keys[j] = keysIterator.next();

            JSONObject part = object.getJSONObject(keys[j]);

            transactions.add(new Transaction(part.getString("name"),part.getString("type"),part.getString("user"),part.getString("description"),part.getString("datetime"), part.getString("reason")));

            System.out.println(transactions.get(j).toString());
            j++;
        }
    }

    public void sortDifferently(boolean closestDate){
        adapter.transactionList.clear();
        adapter.clear();
        if(closestDate){
            for(int i=notTemp.size()-1; i>0; i--){
                adapter.add(notTemp.get(i));
            }
        }
        else{
            for(int i=0; i<notTemp.size(); i++){
                adapter.add(notTemp.get(i));
            }
        }

    }

}