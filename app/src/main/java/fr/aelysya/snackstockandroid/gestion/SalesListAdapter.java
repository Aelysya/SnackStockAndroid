package fr.aelysya.snackstockandroid.gestion;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.opencsv.CSVReader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import fr.aelysya.snackstockandroid.ManagementActivity;
import fr.aelysya.snackstockandroid.R;

public class SalesListAdapter extends BaseAdapter {

    private List<String[]> saleList;
    private LayoutInflater inflater;
    private Context context;
    private ManagementActivity activity;

    public SalesListAdapter(Context c, List<String[]> l, ManagementActivity act){
        this.context = c;
        this.saleList = l;
        inflater = LayoutInflater.from(c);
        activity = act;
    }

    @Override
    public int getCount(){
        return saleList.size();
    }

    @Override
    public String[] getItem(int pos){
        return saleList.get(pos);
    }

    @Override
    public long getItemId(int pos) {
        return pos;
    }

    @Override
    public View getView(int pos, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.sale, null);
            holder = new ViewHolder();
            holder.salerName = convertView.findViewById(R.id.saler_name);
            holder.saleDate = convertView.findViewById(R.id.sale_date);
            holder.salePrice = convertView.findViewById(R.id.sale_price);
            holder.saleType = convertView.findViewById(R.id.sale_type);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        String[] sale = saleList.get(pos);

        holder.salerName.setText(sale[0]);
        holder.saleDate.setText(sale[1]);
        holder.salePrice.setText(sale[2]);
        holder.saleType.setText(sale[5].equals("0") ? "E" : "HA");

        return convertView;
    }

    static class ViewHolder{
        TextView salerName, saleDate, salePrice, saleType;
    }

}
