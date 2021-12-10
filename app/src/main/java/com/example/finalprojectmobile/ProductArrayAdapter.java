package com.example.finalprojectmobile;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductArrayAdapter extends ArrayAdapter<Product> implements Filterable {

    public List<Product> productList = new ArrayList<Product>();
    public List<Product> productListFiltered = new ArrayList<>();
    public List<Product> allProducts = new ArrayList<>();

    static class ProductViewHolder {
        ImageView productImg;
        TextView productName;
        TextView productQuantity;
        Button edit, delete;
    }

    public ProductArrayAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    @Override
    public void add(Product object) {
        productList.add(object);
        allProducts.add(object);
        super.add(object);
    }

    public void remove(int position) {
        super.remove(productList.get(position));
        productList.remove(position);
        allProducts.remove(position);
    }


    @Override
    public int getCount() {
        return this.productList.size();
    }

    @Override
    public Product getItem(int index) {
        return this.productList.get(index);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ProductViewHolder viewHolder;

        LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        row = inflater.inflate(R.layout.product_list_item, parent, false);
        viewHolder = new ProductViewHolder();
        viewHolder.productImg = (ImageView) row.findViewById(R.id.productImg);
        viewHolder.productName = (TextView) row.findViewById(R.id.productName);
        viewHolder.productQuantity = (TextView) row.findViewById(R.id.productQuantity);


        viewHolder.edit = (Button) row.findViewById(R.id.edit);
        viewHolder.delete = (Button) row.findViewById(R.id.delete);

        viewHolder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDeleteDialog(ProductArrayAdapter.super.getContext(), position);
            }
        });

        viewHolder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showEditDialog(ProductArrayAdapter.super.getContext(), position);
            }
        });


        row.setTag(viewHolder);


        Product product = getItem(position);

            new Thread(new Runnable() {
                @Override
                public void run() {
                    if(product.image==null){
                        Bitmap img = null;
                        try {
                            img = downloadBitmap(product.getImageUrl());
                            product.image=img;
                            Bitmap finalImg = img;
                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    viewHolder.productImg.setImageBitmap(finalImg);
                                }
                            });
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    else{
                        viewHolder.productImg.setImageBitmap(product.image);
                    }


            }}).start();

        viewHolder.productName.setText(product.getName());
        viewHolder.productQuantity.setText("Quantity : " +Integer.toString(product.getQuantity()));
        return row;
    }

    /*
    public Bitmap decodeToBitmap(byte[] decodedByte) {
        return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
    }

     */


    public void showDeleteDialog(Context context, int index) {

        AlertDialog.Builder alertDialogBuilderLabelDelete = new AlertDialog.Builder(context);

        alertDialogBuilderLabelDelete.setCancelable(false);
        alertDialogBuilderLabelDelete.setTitle("Delete Product");
        alertDialogBuilderLabelDelete.setMessage("Are you sure you want to delete this product? If yes Type the Reason Below");


        EditText reason = new EditText(context);
        alertDialogBuilderLabelDelete.setView(reason);

        alertDialogBuilderLabelDelete.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });

        alertDialogBuilderLabelDelete.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(context, "Successfully Deleted Item", Toast.LENGTH_LONG).show();
                try {
                    if(reason.getText().toString().equals(""))
                        deleteProduct(productList.get(index).getName(),productList.get(index).getQuantity(), "No Reason was Provided");
                    else
                        deleteProduct(productList.get(index).getName(),productList.get(index).getQuantity(), reason.getText().toString());

                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                remove(index);
            }
        });
        alertDialogBuilderLabelDelete.show();
    }

    public void showEditDialog(Context context, int index) {
        AlertDialog.Builder alertDialogBuilderLabelEdit = new AlertDialog.Builder(context);

        alertDialogBuilderLabelEdit.setCancelable(false);
        alertDialogBuilderLabelEdit.setTitle("Change Quantity");
        alertDialogBuilderLabelEdit.setMessage("Increase or Decrease Current Amount by :");
        alertDialogBuilderLabelEdit.setCancelable(true);

        LayoutInflater inflator = LayoutInflater.from(this.getContext());

        View dialogView = inflator.inflate(R.layout.edit_amount ,null);

        alertDialogBuilderLabelEdit.setView(dialogView);

        EditText input,reason;

        input = (EditText) dialogView.findViewById(R.id.amount);
        reason = (EditText) dialogView.findViewById(R.id.reason);


        //EditText input = new EditText(context);
        //input.setInputType(InputType.TYPE_CLASS_NUMBER);
        //alertDialogBuilderLabelEdit.setView(input);

        alertDialogBuilderLabelEdit.setNegativeButton("Decrease", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                int change;
                if (input.getText().toString().equals(""))
                    change = 0;
                else
                    change = Integer.parseInt(input.getText().toString());

                int nonTempQuantity = productList.get(index).getQuantity();
                productList.get(index).setQuantity(productList.get(index).getQuantity() - change);

                String reasonText;
                if(reason.getText().toString().equals(""))
                    reasonText = "No Reason was provided";
                else
                    reasonText = reason.getText().toString();

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Home.createTransactionEditOfProduct(productList.get(index).getName(),change,false, reasonText);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();


                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            ProductsList.editProductQuantity(productList.get(index).getName(), nonTempQuantity,
                                    change, false);

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();


            }
        });

        alertDialogBuilderLabelEdit.setPositiveButton("Increase", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                int change;
                if (input.getText().toString().equals(""))
                    change = 0;
                else
                    change = Integer.parseInt(input.getText().toString());

                int nonTempQuantity = productList.get(index).getQuantity();
                productList.get(index).setQuantity(productList.get(index).getQuantity() + change);
                String reasonText;
                if(reason.getText().toString().equals(""))
                    reasonText = "No Reason was provided";
                else
                    reasonText = reason.getText().toString();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Home.createTransactionEditOfProduct(productList.get(index).getName(),change,true, reasonText);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            ProductsList.editProductQuantity(productList.get(index).getName(), nonTempQuantity,
                                    change, true);

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();


            }
        });
        alertDialogBuilderLabelEdit.show();

    }


    private Bitmap downloadBitmap(String url) throws IOException {

        HttpURLConnection urlConnection = null;
        try {
            URL uri = new URL(url);
            urlConnection = (HttpURLConnection) uri.openConnection();


            InputStream inputStream = urlConnection.getInputStream();
            if (inputStream != null) {
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                return bitmap;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;

    }


    static public void deleteProduct(String name, int quantity, String reason) throws IOException, JSONException {

        new Thread(new Runnable() {
            @Override
            public void run() {
                URL url = null;
                try {
                    url = new URL("https://mobilejsondatabase-default-rtdb.firebaseio.com/products/"+name+".json");
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                HttpURLConnection con = null;
                try {
                    con = (HttpURLConnection) url.openConnection();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    con.setRequestMethod("DELETE");
                } catch (ProtocolException e) {
                    e.printStackTrace();
                }
                con.setDoOutput(true);
                con.setRequestProperty("Content-Type", "application/json; utf-8");


                try(BufferedReader br = new BufferedReader(
                        new InputStreamReader(con.getInputStream(), "utf-8"))) {
                    StringBuilder response = new StringBuilder();
                    String responseLine = null;
                    while ((responseLine = br.readLine()) != null) {
                        response.append(responseLine.trim());
                    }
                    System.out.println(response.toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    Home.createTransactionDeletionOfProduct(name,quantity,reason);
                } catch (IOException e) {
                    e.printStackTrace();
                }


            }
        }).start();

    }


    /*

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {


            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                System.out.println("in perform filtering");

                FilterResults filterResults = new FilterResults();

                List<Product> filtered = getFilteredList(constraint);
                filterResults.count = filtered.size();
                filterResults.values = filtered;
                System.out.println(filterResults.values.toString());
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {

                productListFiltered = (List<Product>) results.values;

                allProducts = productList;
                productList.clear();
                ProductArrayAdapter.super.clear();

                productList = productListFiltered;
                ProductArrayAdapter.super.addAll(productList);

                notifyDataSetChanged();
            }
        };
        return filter;
    }

     */

    public List<Product> getFilteredList(CharSequence constraint){

            System.out.println("in getFiltered");
            System.out.println("all products" +allProducts.toString());
            List<Product> filteredList = new ArrayList<>();
            if(constraint == null || constraint.length() == 0){
                for(int i=0; i<allProducts.size(); i++){
                    filteredList.add(allProducts.get(i));
                }
            }
            else{
                String searchStr = constraint.toString().toLowerCase();
                System.out.println(searchStr);

                for(Product product:allProducts) {
                    if (product.getName().toLowerCase().contains(searchStr)) {
                        filteredList.add(product);
                    }
                }
            }
        return filteredList;
    }



}

