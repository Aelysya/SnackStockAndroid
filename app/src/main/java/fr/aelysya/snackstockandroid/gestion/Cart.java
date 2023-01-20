package fr.aelysya.snackstockandroid.gestion;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Cart {
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
        Item newItem = new Item(item);
        newItem.setQuantity(1);
        getListFromTypeString(item.getTYPE()).add(newItem);
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
     */
    public static void removeOneFromItem(Item item){
        for(Item i : getListFromTypeString(item.getTYPE())){
            if(i.getNAME().equals(item.getNAME())){
                i.removeFromQuantity(1);
            }
        }
    }

    /**
     * Vide le panier
     */
    public static void clear(){
        snacksList.clear();
        boissonsList.clear();
        autresList.clear();
    }

    public static int getNumberOfItems(){
        return snacksList.size() + boissonsList.size() + autresList.size();
    }

    public static String[] getContentToStringArray(String freeConso, String freeMenu, String price, char id, boolean isHelloAsso){
        StringBuilder s = new StringBuilder();

        s.append("Vendeur: ").append(id).append(", ");

        for(Item i : snacksList){
            s.append(i.getNAME()).append(": ");
            s.append(i.getQuantity()).append(", ");
        }

        for(Item i : boissonsList){
            s.append(i.getNAME()).append(": ");
            s.append(i.getQuantity()).append(", ");
        }

        for(Item i : autresList){
            s.append(i.getNAME()).append(": ");
            s.append(i.getQuantity()).append(", ");
        }

        s.append("HelloAsso: ").append(isHelloAsso ? "1" : "0").append(", ");

        s.append("consommations gratuites: ").append(freeConso).append(", ");
        s.append("menus gratuits: ").append(freeMenu).append(", ");

        String date = String.valueOf(java.time.LocalDate.now());
        s.append("date: ").append(date).append(", ");
        s.append("prix total: ").append(price, 0, price.length()-2);

        return new String[]{s.toString()};
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
            case "autre" : returnedList = autresList;
                break;
            default : throw new IllegalArgumentException();
        }

        return returnedList;
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
}
