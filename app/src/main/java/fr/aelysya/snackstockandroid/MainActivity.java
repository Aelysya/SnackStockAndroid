package fr.aelysya.snackstockandroid;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import fr.aelysya.snackstockandroid.gestion.Cart;
import fr.aelysya.snackstockandroid.gestion.CartListAdapter;
import fr.aelysya.snackstockandroid.gestion.Item;
import fr.aelysya.snackstockandroid.gestion.Stock;
import fr.aelysya.snackstockandroid.gestion.StockListAdapter;

public class MainActivity extends AppCompatActivity {

    private ListView stockItems, cartItems;

    private TextView currentTabText, priceText;

    private EditText freeConsoField, freeMenuField;

    private String currentTab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_view);

        currentTab = "none";

        stockItems = findViewById(R.id.stockItemsDisplay);
        cartItems = findViewById(R.id.cartItemsDisplay);
        currentTabText = findViewById(R.id.currentTabText);
        priceText = findViewById(R.id.priceText);
        freeConsoField = findViewById(R.id.freeConsoField);
        freeMenuField = findViewById(R.id.freeMenuField);

        Button confirmButton = findViewById(R.id.confirm_button);
        Button cancelButton = findViewById(R.id.cancel_button);
        Button snackButton = findViewById(R.id.snack_button);
        Button boissonButton = findViewById(R.id.boisson_button);
        Button autreButton = findViewById(R.id.ventes_button);
        Button managementButton = findViewById(R.id.management_button);

        try {
            readStockFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        managementButton.setOnClickListener(view -> {
            LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
            View popupView = inflater.inflate(R.layout.ask_password, null);

            int width = 350;
            int height = 250;
            boolean focusable = true;
            final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

            popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

            Button confirmSaleButton = popupView.findViewById(R.id.confirmPassButton);
            Button cancelSaleButton = popupView.findViewById(R.id.cancelPassButton);
            EditText passwordField = popupView.findViewById(R.id.passwordField);
            TextView topText = popupView.findViewById(R.id.topText);
            CheckBox isHelloAsso = popupView.findViewById(R.id.isHelloAsso);
            isHelloAsso.setVisibility(View.INVISIBLE);
            topText.setText(R.string.ask_pass_text);

            cancelSaleButton.setOnClickListener(v -> popupWindow.dismiss());
            confirmSaleButton.setOnClickListener(v -> {
                String enteredPassword = passwordField.getText().toString();
                if(enteredPassword.equals(Stock.getMdp())){
                    Intent newIntent = new Intent(MainActivity.this, ManagementActivity.class);
                    startActivity(newIntent);
                    finish();
                    popupWindow.dismiss();
                } else {
                    topText.setText(R.string.wrong_pass_text);
                }
            });
        });

        confirmButton.setOnClickListener(view -> {
            if(Double.parseDouble(priceText.getText().toString().substring(0, priceText.getText().toString().length()-2)) >= 0 && Cart.getNumberOfItems() > 0){
                LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
                View popupView = inflater.inflate(R.layout.ask_password, null);

                int width = 350;
                int height = 250;
                boolean focusable = true;
                final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

                popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

                Button confirmSaleButton = popupView.findViewById(R.id.confirmPassButton);
                Button cancelSaleButton = popupView.findViewById(R.id.cancelPassButton);
                EditText passwordField = popupView.findViewById(R.id.passwordField);
                TextView topText = popupView.findViewById(R.id.topText);
                CheckBox isHelloAsso = popupView.findViewById(R.id.isHelloAsso);

                cancelSaleButton.setOnClickListener(v -> popupWindow.dismiss());
                confirmSaleButton.setOnClickListener(v -> {
                    String enteredPassword = passwordField.getText().toString();
                    String passwordBeginning = enteredPassword.substring(0, 4);
                    System.out.println(passwordBeginning);
                    if(passwordBeginning.equals(Stock.getMdp()) && enteredPassword.length() == 5){
                        try {
                            char id = enteredPassword.charAt(4);
                            System.out.println(id);
                            if(id >= '0' && id <= '9'){
                                confirmSale(id, isHelloAsso.isChecked());
                                popupWindow.dismiss();
                            } else {
                                topText.setText(R.string.wrong_pass_text);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        topText.setText(R.string.wrong_pass_text);
                    }
                });
            } else {
                Context context = getApplicationContext();
                CharSequence text = "Prix invalide";
                int duration = Toast.LENGTH_SHORT;

                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
            }
        });

        cancelButton.setOnClickListener(view -> {
            clearCart();
            showTab(currentTab);
        });
        snackButton.setOnClickListener(view -> showTab("snack"));
        boissonButton.setOnClickListener(view -> showTab("boisson"));
        autreButton.setOnClickListener(view -> showTab("autre"));

        freeConsoField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                computePrice();
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        freeMenuField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                computePrice();
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });
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
        file.createNewFile();
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

        stockItems.setAdapter(new StockListAdapter(this, list, this));
        stockItems.setFastScrollAlwaysVisible(list.size() >= 7);
        currentTab = type;
        updateTabLabel();
    }

    /**
     * Affiche le panier
     */
    public void showCart(){
        List<Item> list = new ArrayList<>();
        list.addAll(Cart.getAutresList());
        list.addAll(Cart.getBoissonsList());
        list.addAll(Cart.getSnacksList());

        cartItems.setAdapter(new CartListAdapter(this, list, this));
        computePrice();
    }

    /**
     * Met à jour le label situé en dessous des onglets avec le prix d'ajout d'un item dans le panier
     */
    public void updateTabLabel(){
        int nbSnacks = 0;
        for (Item i : Cart.getSnacksList()){
            nbSnacks += i.getQuantity();
        }

        int nbBoissons = 0;
        for (Item i : Cart.getBoissonsList()){
            nbBoissons += i.getQuantity();
        }

        switch (currentTab) {
            case "snack" : currentTabText.setText(nbBoissons > nbSnacks ? "Snacks - 0.40 €" : "Snacks - 0.60 €");
                break;
            case "boisson" : currentTabText.setText(nbSnacks > nbBoissons ? "Boissons - 0.40 €" : "Boissons - 0.60 €");
                break;
            case "autre" : currentTabText.setText(R.string.autres_current_tab_text);
                break;
            case "none" : currentTabText.setText(R.string.default_current_tab_text);
                break;
            default : throw new IllegalStateException("Unexpected value: " + currentTab);
        }
    }

    /**
     * Calcule le prix selon le contenu du panier
     */
    public void computePrice(){
        List<Item> snackList = Cart.getSnacksList();
        List<Item> boissonList = Cart.getBoissonsList();
        List<Item> autreList = Cart.getAutresList();

        double prixSnacks = 0.0;
        int nbSnacks = 0;
        for (Item i : snackList){
            prixSnacks += i.getQuantity() * Double.parseDouble(i.getPRICE());
            nbSnacks += i.getQuantity();
        }

        double prixBoissons = 0.0;
        int nbBoissons = 0;
        for (Item i : boissonList){
            prixBoissons += i.getQuantity() * Double.parseDouble(i.getPRICE());
            nbBoissons += i.getQuantity();
        }

        double prixAutres = 0.0;
        for (Item i : autreList){
            prixAutres += i.getQuantity() * Double.parseDouble(i.getPRICE());
        }

        double prix = prixSnacks + prixBoissons + prixAutres;

        while(nbSnacks > 0 && nbBoissons > 0){
            prix -= 0.2;
            nbSnacks--;
            nbBoissons--;
        }

        try{
            int amountOfFreeConso = 0;
            if(!freeConsoField.getText().toString().equals("")){
                amountOfFreeConso = Integer.parseInt(freeConsoField.getText().toString());
            }
            prix -= amountOfFreeConso * 0.6;
        } catch (NumberFormatException ignored) {
        }

        try{
            int amountOfFreeMenu = 0;
            if(!freeMenuField.getText().toString().equals("")){
                amountOfFreeMenu = Integer.parseInt(freeMenuField.getText().toString());
            }
            prix -= amountOfFreeMenu;
        } catch (NumberFormatException ignored) {
        }

        NumberFormat nf = NumberFormat.getNumberInstance();
        nf.setMaximumFractionDigits(1);

        String finalPrice = nf.format(prix) + " €";
        priceText.setText(finalPrice.replace(",", "."));
        updateTabLabel();
    }

    /**
     * Confirme l'achat des items du panier
     * @throws IOException Fichier de stock introuvable
     */
    public void confirmSale(char id, boolean isHelloAsso) throws IOException {
        for(Item i : Cart.getSnacksList()){
            Stock.removeQuantityFromItem(i, i.getQuantity());
        }

        for(Item i : Cart.getBoissonsList()){
            Stock.removeQuantityFromItem(i, i.getQuantity());
        }

        for(Item i : Cart.getAutresList()){
            Stock.removeQuantityFromItem(i, i.getQuantity());
        }

        updateSalesFile(id, isHelloAsso);
        freeMenuField.setText("0");
        freeConsoField.setText("0");
        updateStockFile();
        clearCart();
        showTab(currentTab);
    }

    /**
     * Ajoute dans un fichier la vente récemment effectuée
     * @throws IOException Fichier de ventes introuvable
     */
    public void updateSalesFile(char id, boolean isHelloAsso) throws IOException {
        String path = "ventes.csv";
        File file = new File(getFilesDir().getAbsoluteFile(), path);
        file.createNewFile();
        CSVWriter writer = new CSVWriter(new FileWriter(file, true));

        writer.writeNext(Cart.getContentToStringArray(freeConsoField.getText().toString(), freeMenuField.getText().toString(), priceText.getText().toString(), id, isHelloAsso));
        writer.close();
    }

    /**
     * Vide le panier et reset le compteur de prix
     */
    public void clearCart(){
        Cart.clear();
        computePrice();
        showCart();
        freeConsoField.setText("0");
        freeMenuField.setText("0");
    }


}