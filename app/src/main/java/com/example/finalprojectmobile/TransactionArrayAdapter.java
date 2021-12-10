package com.example.finalprojectmobile;




import android.content.Context;
import android.graphics.drawable.Icon;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;

public class TransactionArrayAdapter extends ArrayAdapter<Transaction> {

    public List<Transaction> transactionList = new ArrayList<Transaction>();

    static class TransactionViewHolder {
        ImageView icon;
        TextView transactionName,type,user,description,date;
    }

    public TransactionArrayAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    @Override
    public void add(Transaction object) {
        transactionList.add(object);
        super.add(object);
    }



    @Override
    public int getCount() {
        return this.transactionList.size();
    }

    @Override
    public Transaction getItem(int index) {
        return this.transactionList.get(index);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        TransactionViewHolder viewHolder;

        LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        row = inflater.inflate(R.layout.transaction_list_item, parent, false);
        viewHolder = new TransactionViewHolder();
        viewHolder.icon = (ImageView) row.findViewById(R.id.icon);
        viewHolder.transactionName = (TextView) row.findViewById(R.id.transactionTitle);
        viewHolder.description = (TextView) row.findViewById(R.id.description);
        viewHolder.user = (TextView) row.findViewById(R.id.user);
        viewHolder.date = (TextView) row.findViewById(R.id.date);


        row.setTag(viewHolder);


        Transaction transaction = getItem(position);

        //viewHolder.icon.setImageIcon(Icon.);
        if(transaction.getType().equals("addition"))
            viewHolder.icon.setImageResource(android.R.drawable.ic_input_add);
        else if(transaction.getType().equals("deletion"))
            viewHolder.icon.setImageResource(R.drawable.delete);
        else{
            if(transaction.getDescription().contains("increased"))
                viewHolder.icon.setImageResource(R.drawable.increase);
            else
                viewHolder.icon.setImageResource(R.drawable.decrease);
        }



        viewHolder.transactionName.setText(transaction.getProductName());
        viewHolder.description.setText("Reason : " +transaction.getReason());
        viewHolder.user.setText(transaction.getUser());
        viewHolder.date.setText(transaction.getDate());

        return row;
    }






}



