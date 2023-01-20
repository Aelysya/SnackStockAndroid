package fr.aelysya.snackstockandroid.gestion;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Stock {
    /**
     * Liste des item de type snack
     */
    private static final List<Item> snacksList;

    /**
     * Liste des item de type boisson
     */
    private static final List<Item> boissonsList;

    /**
     * Liste des item de type autre
     */
    private static final List<Item> autresList;

    /**
     * Mot de passe administrateur
     */
    private static final String mdp = "7347";

    static {
        snacksList = new ArrayList<>();
        boissonsList = new ArrayList<>();
        autresList = new ArrayList<>();
    }

    /**
     * Ajoute un item au stock
     * @param item L'item à ajouter
     */
    public static void addItem(Item item){
        getListFromTypeString(item.getTYPE()).add(item);
    }

    /**
     * Supprime un item du stock
     * @param item L'item à supprimer
     */
    public static void supprItem(Item item){
        getListFromTypeString(item.getTYPE()).removeIf(i -> (Objects.equals(item.getNAME(), i.getNAME())));
    }

    /**
     * Ajoute 1 à la quantité d'un item en stock
     * @param item L'item dont il faut augmenter la quantité
     */
    public static void addOneToItem(Item item){
        for(Item i : getListFromTypeString(item.getTYPE())){
            if(i.getNAME().equals(item.getNAME())){
                i.addOneToQuantity();
            }
        }
    }

    /**
     * Retire un certaine quantité d'un item en stock
     * @param item L'item dont il faut réduire la quantité
     * @param qty La quantité à enlever au stock
     */
    public static void removeQuantityFromItem(Item item, int qty){
        boolean isMenuBastOf = item.getNAME().equals("Menu Bast-of");
        for(Item i : getListFromTypeString(item.getTYPE())) {
            if (i.getNAME().equals(item.getNAME())) {
                i.removeFromQuantity(qty);
            }
        }
        if(isMenuBastOf){
            for(Item i : getListFromTypeString("snack")) {
                if (i.getNAME().equals("Kinder bueno")) {
                    i.removeFromQuantity(qty);
                }
            }
            for(Item i : getListFromTypeString("boisson")) {
                if (i.getNAME().equals("Ice Tea")) {
                    i.removeFromQuantity(qty);
                }
            }
        }

        //Update des Menu Bast-of
        int buenoQty = getItemQuantityFromName("Kinder bueno", "snack");
        int iceTeaQty = getItemQuantityFromName("Ice Tea", "boisson");
        for(Item i : getListFromTypeString("autre")) {
            if (i.getNAME().equals("Menu Bast-of")) {
                i.setQuantity(Math.min(buenoQty, iceTeaQty));
            }
        }
    }

    /**
     * Donne la liste contenant le type d'item demandé
     * @param type Type de la liste demandée
     * @return La liste demandée
     */
    public static List<Item> getListFromTypeString(String type){
        List<Item> returnedList;
        switch (type) {
            case "snack" : returnedList = snacksList;
            break;
            case "boisson" : returnedList = boissonsList;
                break;
            default : returnedList = autresList;
        }

        return returnedList;
    }

    /**
     * Vide le stock
     */
    public static void clear(){
        snacksList.clear();
        boissonsList.clear();
        autresList.clear();
    }

    public static List<Item> getSnacksList(){
        return snacksList;
    }

    public static List<Item> getBoissonsList(){
        return boissonsList;
    }

    public static List<Item> getAutresList(){
        return autresList;
    }

    /**
     * Recherche la quantité en stock d'un objet
     * @param name Nom de l'objet recherché
     * @param type Type de l'objet recherché
     * @return La quantité de l'objet recherché
     */
    public static int getItemQuantityFromName(String name, String type){
        int qty = 0;
        for(Item i : getListFromTypeString(type)) {
            if (i.getNAME().equals(name)) {
                qty = i.getQuantity();
            }
        }
        return qty;
    }

    public static String getMdp(){
        return mdp;
    }
}
