package fr.aelysya.snackstockandroid;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import fr.aelysya.snackstockandroid.gestion.Item;
import fr.aelysya.snackstockandroid.gestion.ManagementStockListAdapter;
import fr.aelysya.snackstockandroid.gestion.SalesListAdapter;
import fr.aelysya.snackstockandroid.gestion.Stock;

public class ManagementActivity extends AppCompatActivity {


    private ListView stockItems;

    private TextView currentTabText, priceText;

    private EditText fromDate, toDate;

    private String currentTab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.management_view);

        currentTab = "none";

        stockItems = findViewById(R.id.stockItemsDisplay);
        currentTabText = findViewById(R.id.currentTabText);

        Button addProductButton = findViewById(R.id.add_product_button);
        Button cancelButton = findViewById(R.id.cancel_product_button);
        Button snackButton = findViewById(R.id.snack_button);
        Button boissonButton = findViewById(R.id.boisson_button);
        Button autreButton = findViewById(R.id.autre_button);
        Button ventesButton = findViewById(R.id.ventes_button);
        Button managementButton = findViewById(R.id.management_button);
        Button calcPriceButton = findViewById(R.id.calc_period_price_button);
        Spinner productType = findViewById(R.id.add_product_type);
        EditText addProductPrice = findViewById(R.id.add_product_price);
        EditText addProductName = findViewById(R.id.add_product_name);
        EditText addProductQuantity = findViewById(R.id.add_product_quantity);
        TextView productText = findViewById(R.id.add_product_text);
        TextView textPrice = findViewById(R.id.textPrice);
        fromDate = findViewById(R.id.from_date_field);
        toDate = findViewById(R.id.to_date_field);
        priceText = findViewById(R.id.period_price_text);

        String[] types = new String[]{
                "snack", "boisson", "autre"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, types);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        productType.setAdapter(adapter);

        productType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String selectedType = parentView.getItemAtPosition(position).toString();
                addProductPrice.setVisibility(selectedType.equals("autre") ? View.VISIBLE : View.INVISIBLE);
                textPrice.setVisibility(selectedType.equals("autre") ? View.VISIBLE : View.INVISIBLE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {}

        });

        try {
            readStockFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        calcPriceButton.setOnClickListener(view -> {
            try {
                calculateTotalPrice();
            } catch (IOException | ParseException e) {
                e.printStackTrace();
            }
        });

        managementButton.setOnClickListener(view -> {
            Intent newIntent = new Intent(ManagementActivity.this, MainActivity.class);
            ManagementActivity.this.startActivity(newIntent);
            finish();
        });

        addProductButton.setOnClickListener(view -> {
            TextView addProductText = findViewById(R.id.add_product_text);
            if(addProductName.getText().toString().equals("")){
                addProductText.setText("Donnez un nom au produit");
                return;
            }
            if(addProductQuantity.getText().toString().equals("")){
                addProductText.setText("Quantité invalide");
                return;
            }
            String price;
            if(productType.getSelectedItem().toString().equals("autre")){
                if(addProductPrice.getText().toString().equals("")){
                    addProductText.setText("Prix invalide");
                    return;
                } else {
                    price = addProductPrice.getText().toString();
                }
            } else {
                price = "0.60";
            }
            Stock.addItem(new Item(
                    addProductName.getText().toString(),
                    Integer.parseInt(addProductQuantity.getText().toString()),
                    price,
                    productType.getSelectedItem().toString()
                    ));
            try {
                updateStockFile();
            } catch (IOException e) {
                e.printStackTrace();
            }

            showTab(productType.getSelectedItem().toString());

            addProductQuantity.setText("");
            addProductPrice.setText("");
            addProductName.setText("");
            productType.setSelection(0);
        });
        cancelButton.setOnClickListener(view -> {
            productText.setText(R.string.add_product_text);
            addProductQuantity.setText("");
            addProductPrice.setText("");
            addProductName.setText("");
            productType.setSelection(0);
        });
        snackButton.setOnClickListener(view -> showTab("snack"));
        boissonButton.setOnClickListener(view -> showTab("boisson"));
        autreButton.setOnClickListener(view -> showTab("autre"));
        ventesButton.setOnClickListener(view -> {
            try {
                showSales();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Calcule le prix des ventes sur une période choisie
     * @throws IOException Fichier de ventes introuvables
     */
    public void calculateTotalPrice() throws IOException, ParseException {
        String fileName = "ventes.csv";
        File file = new File(getFilesDir().getAbsoluteFile(), fileName);
        CSVReader reader = new CSVReader(new FileReader(file));
        String[] row;
        double finalPrice = 0;
        int l;
        Date fromLimit, toLimit;
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");

        try{
            fromLimit = df.parse(fromDate.getText().toString());
        } catch (Exception e){
            fromLimit = df.parse("2000-01-01");
        }

        try{
            toLimit = df.parse(toDate.getText().toString());
        } catch (Exception e){
            toLimit = df.parse("2030-01-01");
        }

        String StringPrice;
        Date rowDate;
        boolean isHelloAsso;
        while((row = reader.readNext()) != null){
            String[] data = row[0].split(", ");
            l = data.length;
            rowDate = df.parse(data[l-2].substring(6));
            isHelloAsso = data[l-5].substring(11).equals("1");
            System.out.println(isHelloAsso);
            if((rowDate.after(fromLimit) || rowDate.equals(fromLimit)) && (rowDate.before(toLimit) || rowDate.equals(toLimit)) && !isHelloAsso){
                StringPrice = data[l-1].substring(12);
                finalPrice += Double.parseDouble(StringPrice);
            }
        }

        NumberFormat nf = NumberFormat.getNumberInstance();
        nf.setMaximumFractionDigits(1);


        String stringPrice = nf.format(finalPrice) + " €";
        priceText.setText(stringPrice.replace(",", "."));

        reader.close();
    }

    /**
     * Lit le fichier de stock au lancement de l'application
     * @throws IOException Fichier de stock introuvable
     *
     */
    public void readStockFile() throws IOException {
        Stock.clear();
        String fileName = "stock.csv";
        File file = new File(getFilesDir().getAbsoluteFile(), fileName);
        CSVReader reader = new CSVReader(new FileReader(file));
        String[] row;
        while((row = reader.readNext()) != null){
            String[] data = row[0].split(",");
            Item item = new Item(data[0], Integer.parseInt(data[1]), data[2], data[3]);
            Stock.addItem(item);
        }
        reader.close();
    }

    /**
     * Met à jour le stock
     * @throws IOException Fichier de stock introuvable
     */
    public void updateStockFile() throws IOException {
        String path = "stock.csv";
        File file = new File(getFilesDir().getAbsoluteFile(), path);
        CSVWriter writer = new CSVWriter(new FileWriter(file));

        for(Item i : Stock.getSnacksList()){
            writer.writeNext(i.getItemToStringArray());
        }

        for(Item i : Stock.getBoissonsList()){
            writer.writeNext(i.getItemToStringArray());
        }

        for(Item i : Stock.getAutresList()){
            writer.writeNext(i.getItemToStringArray());
        }

        writer.close();
    }

    /**
     * Affiche l'onglet choisi
     * @param type Nom de l'onglet à afficher
     */
    public void showTab(String type){
        List<Item> list = Stock.getListFromTypeString(type);

        stockItems.setAdapter(new ManagementStockListAdapter(this, list, this));
        currentTab = type;
        updateTabLabel();
    }

    /**
     * Affiche les ventes effectuées
     */
    public void showSales() throws IOException {
        String fileName = "ventes.csv";
        File file = new File(getFilesDir().getAbsoluteFile(), fileName);
        file.createNewFile();
        CSVReader reader = null;
        try {
            reader = new CSVReader(new FileReader(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        String[] row;
        List<String[]> saleList = new ArrayList<>();
        int l;
        try{
            while((row = reader.readNext()) != null){
                String[] saleData = new String[6];
                String[] data = row[0].split(", ");
                l = data.length;
                saleData[0] = getSalerNameFromId(data[0].substring(9));
                saleData[1] = data[l-2].substring(6);
                saleData[2] = data[l-1].substring(12);
                saleData[3] = data[l-4].substring(25);
                saleData[4] = data[l-3].substring(16);
                saleData[5] = data[l-5].substring(11);
                saleList.add(saleData);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        stockItems.setAdapter(new SalesListAdapter(this, saleList, this));
        stockItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String[] selectedSale = (String[]) adapterView.getItemAtPosition(i);
            }
        });
        currentTab = "vente";
        updateTabLabel();
    }

    /**
     * Met à jour le label situé en dessous des onglets avec le prix d'ajout d'un item dans le panier
     */
    public void updateTabLabel(){
        switch (currentTab) {
            case "snack" : currentTabText.setText(R.string.snack_button_text);
                break;
            case "boisson" : currentTabText.setText(R.string.boisson_button_text);
                break;
            case "autre" : currentTabText.setText(R.string.autres_current_tab_text);
                break;
            case "vente" : currentTabText.setText("Ventes");
                break;
            case "none" : currentTabText.setText(R.string.default_current_tab_text);
                break;
            default : throw new IllegalStateException("Unexpected value: " + currentTab);
        }
    }

    /**
     * Renvoie le nom du vendeur qui à effectué la vente selon son id
     */
    public String getSalerNameFromId(String id){
        String name;

        switch(id){
            case "0" : name = "Maxime"; break;
            case "1" : name = "Samuel"; break;
            case "2" : name = "Mathieu"; break;
            case "3" : name = "Clément"; break;
            case "4" : name = "Erwann"; break;
            case "5" : name = "Inès"; break;
            case "6" : name = "Mélissa"; break;
            case "7" : name = "Alexis"; break;
            default : name = "ID inconnu";
        }

        return name;
    }
}
