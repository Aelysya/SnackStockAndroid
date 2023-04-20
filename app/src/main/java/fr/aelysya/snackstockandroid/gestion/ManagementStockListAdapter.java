package fr.aelysya.snackstockandroid.gestion;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.util.List;

import fr.aelysya.snackstockandroid.MainActivity;
import fr.aelysya.snackstockandroid.ManagementActivity;
import fr.aelysya.snackstockandroid.R;

public class ManagementStockListAdapter extends BaseAdapter {

    private List<Item> itemList;
    private LayoutInflater inflater;
    private Context context;
    private ManagementActivity activity;

    public ManagementStockListAdapter(Context c, List<Item> l, ManagementActivity act){
        this.context = c;
        this.itemList = l;
        inflater = LayoutInflater.from(c);
        activity = act;
    }

    @Override
    public int getCount(){
        return itemList.size();
    }

    @Override
    public Item getItem(int pos){
        return itemList.get(pos);
    }

    @Override
    public long getItemId(int pos) {
        return pos;
    }

    @Override
    public View getView(int pos, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.management_stock_item, null);
            holder = new ViewHolder();
            holder.itemName = convertView.findViewById(R.id.stockItemName);
            holder.itemPrice = convertView.findViewById(R.id.stockItemPrice);
            holder.itemQuantity = convertView.findViewById(R.id.stockItemQuantity);
            holder.addButton = convertView.findViewById(R.id.stockItemAddButton);
            holder.removeButton = convertView.findViewById(R.id.stockItemRemoveButton);
            holder.supprButton = convertView.findViewById(R.id.stockItemSupprButton);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Item i = this.itemList.get(pos);
        holder.itemName.setText(i.getNAME());
        holder.itemQuantity.setText(Integer.toString(i.getQuantity()));
        holder.itemPrice.setText(i.getTYPE().equals("autre") ? i.getPRICE() + " €" : "");

        holder.supprButton.setOnClickListener(view ->{
            Stock.supprItem(i);
            activity.showTab(i.getTYPE());

            try {
                activity.updateStockFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        holder.removeButton.setOnClickListener(view ->{
            if(i.getQuantity() - 1 >= 0){
                Stock.removeQuantityFromItem(i, 1);
                holder.itemQuantity.setText(Integer.toString(i.getQuantity()));
            }
            //activity.showTab(i.getTYPE());

            try {
                activity.updateStockFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        holder.addButton.setOnClickListener(view ->{
            Stock.addOneToItem(i);
            holder.itemQuantity.setText(Integer.toString(i.getQuantity()));
            //activity.showTab(i.getTYPE());
            try {
                activity.updateStockFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        return convertView;
    }

    static class ViewHolder{
        TextView itemName, itemPrice, itemQuantity;
        Button addButton, removeButton, supprButton;
    }

}
