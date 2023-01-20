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

import java.util.List;

import fr.aelysya.snackstockandroid.MainActivity;
import fr.aelysya.snackstockandroid.R;

public class StockListAdapter extends BaseAdapter {

    private List<Item> itemList;
    private LayoutInflater inflater;
    private Context context;
    private MainActivity activity;

    public StockListAdapter(Context c, List<Item> l, MainActivity act){
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
            convertView = inflater.inflate(R.layout.stock_item, null);
            holder = new ViewHolder();
            holder.itemImg = convertView.findViewById(R.id.stockItemImg);
            holder.itemName = convertView.findViewById(R.id.stockItemName);
            holder.itemPrice = convertView.findViewById(R.id.stockItemPrice);
            holder.itemQuantity = convertView.findViewById(R.id.stockItemQuantity);
            holder.addButton = convertView.findViewById(R.id.stockItemAddButton);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Item i = this.itemList.get(pos);
        holder.itemName.setText(i.getNAME());
        holder.itemQuantity.setText(Integer.toString(i.getQuantity()));
        holder.itemPrice.setText(i.getTYPE().equals("autre") ? i.getPRICE() + " €" : "");


        //TODO faire en sorte d'aller chercher les images dans les fichiers locaux de la tablette
        String imgString = i.getNAME().substring(0, 1).toLowerCase() + i.getNAME().substring(1);
        int imageId = this.getMipmapResIdByName(imgString);

        holder.itemImg.setImageResource(imageId);

        holder.addButton.setOnClickListener(view ->{
            if(i.getQuantity() != 0){
                boolean alreadyInCart = false;
                List<Item> listCart = Cart.getListFromTypeString(i.getTYPE());
                for(Item item : listCart){
                    if (item.getNAME().equals(i.getNAME())) {
                        alreadyInCart = true;
                        break;
                    }
                }
                if(!alreadyInCart){
                    Cart.addItem(i);
                } else {
                    int currentCartQuantity = 0;
                    for(Item item : Cart.getListFromTypeString(i.getTYPE())){
                        if(i.getNAME().equals(item.getNAME())){
                            currentCartQuantity = item.getQuantity();
                        }
                    }
                    int maxQuantity = 0;
                    for(Item item : Stock.getListFromTypeString(i.getTYPE())){
                        if(i.getNAME().equals(item.getNAME())){
                            maxQuantity = item.getQuantity();
                        }
                    }
                    if(currentCartQuantity + 1 <= maxQuantity){
                        Cart.addOneToItem(i);
                    }
                }
            }
            activity.showCart();
        });

        return convertView;
    }

    public int getMipmapResIdByName(String resName)  {
        String pkgName = context.getPackageName();
        int resID = context.getResources().getIdentifier(resName, "mipmap", pkgName);
        Log.i("CustomListView", "Res Name: "+ resName+"==> Res ID = "+ resID);
        return resID;
    }

    static class ViewHolder{
        ImageView itemImg;
        TextView itemName, itemPrice, itemQuantity;
        Button addButton;
    }

}
