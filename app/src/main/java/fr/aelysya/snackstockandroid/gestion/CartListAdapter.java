package fr.aelysya.snackstockandroid.gestion;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import fr.aelysya.snackstockandroid.MainActivity;
import fr.aelysya.snackstockandroid.R;

public class CartListAdapter extends BaseAdapter {

    private List<Item> itemList;
    private LayoutInflater inflater;
    private Context context;
    private MainActivity mainActivity;

    public CartListAdapter(Context c, List<Item> l, MainActivity ma){
        this.context = c;
        this.itemList = l;
        inflater = LayoutInflater.from(c);
        mainActivity = ma;
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
            convertView = inflater.inflate(R.layout.cart_item, null);
            holder = new ViewHolder();
            holder.itemName = convertView.findViewById(R.id.stockItemName);
            holder.itemQuantity = convertView.findViewById(R.id.stockItemQuantity);
            holder.addButton = convertView.findViewById(R.id.stockItemAddButton);
            holder.removeButton = convertView.findViewById(R.id.cartItemRemoveButton);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Item i = this.itemList.get(pos);
        holder.itemName.setText(i.getNAME());
        holder.itemQuantity.setText(Integer.toString(i.getQuantity()));

        holder.addButton.setOnClickListener(view ->{
            int currentQuantity = i.getQuantity();
            int maxQuantity = 0;
            for(Item item : Stock.getListFromTypeString(i.getTYPE())){
                if(i.getNAME().equals(item.getNAME())){
                    maxQuantity = item.getQuantity();
                }
            }
            if(currentQuantity + 1 <= maxQuantity){
                holder.itemQuantity.setText(Integer.toString(currentQuantity + 1));
                Cart.addOneToItem(i);
            }
            mainActivity.showCart();
        });

        holder.removeButton.setOnClickListener(view ->{
            if(i.getQuantity() - 1 == 0){
                Cart.supprItem(i);
            } else {
                Cart.removeOneFromItem(i);
                holder.itemQuantity.setText(Integer.toString(i.getQuantity() - 1));
            }
            mainActivity.showCart();
        });

        return convertView;
    }

    static class ViewHolder{
        TextView itemName, itemQuantity;
        Button addButton, removeButton;
    }

}
