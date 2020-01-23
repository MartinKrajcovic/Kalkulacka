package penazenka;

import java.io.Serializable;
/**
 * Tato trieda sluzi ako struktura pre uchovavanie vlastnosti penazenky.
 * Tato trieda implementuje znackovacie rozhranie Serializable, aby jej
 * objekty mohli byt serializovane a deserializovane (ukladane do suboru).
 * Tato trieda obsahuje aj doplnkove metody, pre efektivnejsie spracovanie
 * uzivatelskeho vstupu ako je napriklad parsovanie textu na cisla.
 * @author mkrajcovic
 */
public class Wallet implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private double walletFee;	//tato premenna bude uchovavat nastaveny poplatok penazenky uzivatelom
    private double walletEur;	//tato premenna bude uchovavat ciastku financii v eurach
    private double walletBTC;	//tato premenna bude uchovavat ciastku financii v bitcoinoch
    
    /**
     * Tato metoda nastavuje hodnotu poplatku penazenky ak ju v programe
     * zmenil pouzivatel.
     * @param walletFee - urcuje poplatok, ktory ma drzat penazenka.
     * @param changed  - urcuje ci bol poplatok aktualizovany alebo nie.
     * Ak ano, znamena to, ze sa poplatok zmeni, ak nie, zostane taky
     * aky bol doteraz.
     */
    public void setWalletFee(double walletFee, boolean changed) {
        if (changed)
            this.walletFee = walletFee;
        else
            this.walletFee = 0;
        /*
         * ULOHA è.4
         * 
         * Dosadit podmienku, aby sa nam nemenila zadana hodnota
         * poplatku, ked dojde k neplatnemu vstupu.
         */
    }
    /**
     * Tato metoda vracia aktualnu hodnotu poplatku, ktoru ma penazenka
     * nastavenu v programe. Ak penazenka nema nastaveny ziaden poplatok
     * tak bude vratena nula.
     * @return - vracia poplatok penazenky v percentach.
     */
    public double getWalletFee() {
        return this.walletFee;
    }
    /**
     * Tato metoda nastavuje hodnotu financii v eurach pre tuto penazenku.
     * @param walletEur - urcuje konkretnu sumu v eurach, ktora bude ulozena.
     */
    public void setWalletEur(double walletEur) {
        this.walletEur = walletEur;
    }
    /**
     * Tato metoda vracia aktualnu hodnotu financii v eurach, ktoru penazenka
     * momentalne vlastni.
     * @return - vracia aktualnu cenu v eurach.
     */
    public double getWalletEur() {
        return this.walletEur;
    }
    /**
     * Tato metoda nastavuje hodnotu financii v bitcoinoch pre tuto penazenku.
     * @param walletBTC - urcuje konkretnu sumu v bitcoinoch, ktora bude ulozena.
     */
    public void setWalletBTC(double walletBTC) {
        this.walletBTC = walletBTC;
    }
    /**
     * Tato metoda vracia aktualnu hodnotu financii v bitcoinoch, ktoru penazenka
     * momentalne vlastni.
     * @return - vracia aktualnu cenu v bitcoinoch.
     */
    public double getWalletBTC() {
        return this.walletBTC;
    }
    /**
     * Tato metoda sluzi na parsovanie textoveho retazca na pozadovany typ
     * desatinneho cisla. Metoda postupne overuje rozne podmienky, ktore
     * musia byt splnene, aby sa text mohol spravene prekonvertovat na cislo. 
     * @param text - je retazec, ktory odovzdame funkcii, aby ho previedla.
     * @return - pokial su splnene vsetky podmienky a program nevyhodi vynimku,
     * tak bude vratene pozadovane desatinne cislo, v opacnom pripade vrati nulu.
     * 
     * Tato metoda je staticka, takze nie je nutne vytvarat dalsi objekt na to,
     * aby bola niekde v programe pouzita.
     */
    public static double parseAndControl(String text) {
        try {
            if (!text.isEmpty()) {
                if (text.matches("\\d+\\.\\d+")
                 || text.matches("\\d+\\,\\d+")
                 || text.matches("\\d+")) {
                    if (text.contains(","))
                        text = text.replace(',', '.');
                }
            }
            return Double.parseDouble(text);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
