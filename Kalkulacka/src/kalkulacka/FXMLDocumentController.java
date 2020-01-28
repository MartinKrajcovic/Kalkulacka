package kalkulacka;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import penazenka.Wallet;

public class FXMLDocumentController implements Initializable {
	
    @FXML private TextField newDepo;			//pole pre zadanie noveho vkladu
    @FXML private TextField purchase;			//pole pre zadanie ciastky nakupu
    @FXML private TextField actualValueBTC;		//pole pre zadanie aktualnej ceny btc
    @FXML private Button addButton;				//tlacidlo "Add" pre pridanie vkladu
    @FXML private Button buyButton;				//tlacidlo "Buy" pre nakup btc
    @FXML private TextField withdraw;			//pole pre zadanie poctu btc na predaj
    @FXML private Label withdrawAllTextButton;	//popis "Withdraw all", pre nacitanie vsetkych btc
    @FXML private TextField actualValueBTC2;	//pole pre zadanie aktualnej ceny btc
    @FXML private Button sellButton;			//tlacidlo "Sell" pre predaj btc
    @FXML private Label walletFeeLabel;			//popis stavu penazenky dole vlavo
    @FXML private Label walletEuroLabel;		//popis stavu penazenky pre eura
    @FXML private Label walletBTCLabel;			//popis stavu penazenky pre btc
    
    static double staticWalletFee;	//staticka premenna pre presun hodnoty poplatku medzi triedami
    static boolean changed = false;	//staticky priznak pre overenie presunu hodnoty poplatku medzi triedami
    Wallet wallet;					//deklaracia objektu Wallet, s ktorym sa pracuje pocas celeho chodu programu
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
    	/*
    	 * Nizsie je uvedena deserializacia objektu typu Wallet zo suboru "obj.dat".
    	 * Pokial bol stav kalkulacky aspon raz ulozeny, tak objekt nadobudne spat
    	 * vsetky svoje hodnoty a pokracuje sa tam, kde sme skoncili.
    	 * Pokial vsak tento subor neexistuje, resp. ked nebol stav programu este
    	 * nikdy ulozeny, tak sa vytvori novy objekt typu Wallet.
    	 */
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("obj.dat"))) {
            wallet = (Wallet) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("unable to load saved object..");
            wallet = new Wallet();
        }
        //Uvodne nastavenie popisov penazenky pre hodnoty poplatku, eur a btc
        walletFeeLabel.setText(wallet.getWalletFee() + "%");
        walletEuroLabel.setText(wallet.getWalletEur() + "€");
        walletBTCLabel.setText(wallet.getWalletBTC() + "BTC");  
    }  
    
    /**
     * Tato metoda otvara novu fxml scenu (nove okno), ktorym je okno
     * pre nastavenie poplatku penazenky.
     */
    public void openScene2(){
        try{
            FXMLLoader fl = new FXMLLoader(getClass().getResource("feeSet.fxml"));
            Parent rootl = (Parent)fl.load();
            Stage stage2 = new Stage();
            stage2.setScene(new Scene(rootl));
            stage2.setResizable(false);		//pouzivatel nevie zvacsovat/zmensovat okno
            stage2.setTitle("Wallet Fee");	//nastavenie titulku okna
            stage2.setAlwaysOnTop(true);	//nastavenie okna, aby bolo vzdy v popredi
            stage2.showAndWait();			//hlavne vlakno (chod programu - prve okno) caka s vykonavanim
            								//prikazov az kym sa toto okno pre zadanie poplatku nezavrie
            
            //nastavenie poplatku penazenky a zobrazenie nastaveneho poplatku penazenky v hlavnom okne
            wallet.setWalletFee(staticWalletFee, changed);
            walletFeeLabel.setText(wallet.getWalletFee() + "%");
        } catch (IOException e) {
            System.err.print("unable to open stage2 (settings)");
        }
    }  
    
    /**
     * Tato metoda spusta okno pre zadanie poplatku penazenky stlacenim 
     * tlacidla "set wallet fee" na liste s moznostami.
     */
    @FXML
    private void openJew(ActionEvent event) {
        openScene2();	//volanie metody definovanej vyssie
    }
    
    /**
     * Tato metoda sa spusta stlacenim tlacidla "Add" a sluzi na zaevidovanie
     * noveho (pociatocneho) vkladu do penazenky. Hodnota, ktoru pouzivatel 
     * vlozi v grafickom rozhrani do prveho textoveho pola, sa programom
     * stiahne a dana informacia sa parsovacou technikou prevedie na cislo,
     * pokial je zadane v spravnom tvare. Ak vsetko prejde uspesne, tato
     * ciastka sa pripise do stavu penazenky.
     */
    @FXML
    private void addToWallet(ActionEvent event) {
        double walletEuroDeposit = Wallet.parseAndControl(newDepo.getText().trim());
        wallet.setWalletEur(wallet.getWalletEur() + walletEuroDeposit);	//aktualizacia prostriedkov v penazenke
        walletEuroLabel.setText(wallet.getWalletEur() + "€");	//nastavenie popisu penazenky pre eura
        newDepo.setText("");	//vyprazdnenie textoveho pola v GUI
    }
    
    /*
     * ULOHA c.1
     * 
     * Doplnit textove tlacidlo "Buy all" do GUI a spravit na to funkciu,
     * ktora po stlaceni doplni do textoveho pola "purchase" maximum
     * (cely obsah) penazenky v eurach.
     */
    
    /**
     * Tato metoda sa spusta stlacenim tlacidla "Buy" a sluzi na simulaciu nakupu
     * bitcoinu za specifikovanu ciastku eur. Najskor metoda overuje rozne
     * podmienky ako je napr. nastavenie poplatku penazenky, alebo ci su vyplnene
     * vsetky potrebne polia, ci ciastku, za ktoru chce pouzivatel nakupit ma
     * vobec v penazenke k dispozicii. Ciastky stiahnute z textovych poli sa 
     * prekonvertuju parsovacou technikou na spravny tvar cisel a potom nastava
     * premena jednotiek: Najskor sa aktualizuje hodnota eur v penazenke od ktorej
     * je odratana ciastka zadana do textoveho pola pre nakup. V dalsom kroku
     * sa nakupna cena ponizi o poplatok specifikovany penazenkou. V poslednom kroku
     * sa premeni tato euro hodnota na bitcoin podla aktualneho (zadaneho) kurzu.
     */
    @FXML
    private void buyBitcoin(ActionEvent event) {
        if (wallet.getWalletFee() <= 0) {
            openScene2(); 
            return;
        }  
        else if (purchase.getText().isEmpty() || actualValueBTC.getText().isEmpty())
        	return;
        
        double nakup = Wallet.parseAndControl(purchase.getText().trim());
        if (wallet.getWalletEur() < nakup) { 
            purchase.setStyle("-fx-text-inner-color: red;");
            return;
        }
        double btc = Wallet.parseAndControl(actualValueBTC.getText().trim());
        wallet.setWalletEur(wallet.getWalletEur() - nakup);  
        
        //premena nakupnej ciastky eur na btc
        double poplatok = nakup * (wallet.getWalletFee() / 100);
        nakup = nakup - poplatok;
        btc = nakup / btc;
        
        //aktualizacia hodnoty penazenky pre btc a nastavenie popisov
        wallet.setWalletBTC(wallet.getWalletBTC() + btc);
        walletEuroLabel.setText(wallet.getWalletEur() + "€");
        walletBTCLabel.setText(wallet.getWalletBTC() + "BTC"); 
        
        //vyprazdnenie textovych poli
        purchase.setText("");
        actualValueBTC.setText("");
        purchase.setStyle("-fx-text-inner-color: black;");      
    }

    /**
     * Tato metoda sa spusta stlacenim textu "Withdraw all" pod textovym
     * polom pre zadanie poctu btc v sekcii predaja (prava cast GUI).
     * Metoda doplna toto textove pole celym obsahom btc v penazenke,
     * aby nebolo nutne prepisovat ciastku s velkym poctom desatinnych miest.
     */
    @FXML
    private void withdrawAll(MouseEvent event) {
        withdraw.setText(wallet.getWalletBTC() + "");
    }
    
    /**
     * Tato metoda sa spusta stlacenim tlacidla "Sell" v pravej casti uzivatelskeho
     * okna. Metoda najskor overuje, ci je nastaveny poplatok penazenky, neskor
     * stiahne udaje z textovych poli, parsovacou technikou ich prevedie na cisla a
     * preveri, ci ciastka btc na predaj neprevysuje hodnotu, ktoru penazenka vlastni.
     * Metoda nasledne vykona premenu jednotiek z btc na eura podla poplatku, ktory
     * ma penazenka nastaveny a aktualizuje stav penazenky. 
     */
    @FXML
    private void sellBitcoin(ActionEvent event) {
        if (wallet.getWalletFee() <= 0) {//ked nie je nastaveny poplatok penazenky
            openScene2();
            return;         //vyjde sa z funkcie
        }
        
        /* 
         * ULOHA c.2
         * 
         * doplnit overenie podmienky - ak co i len jedno textove
         * pole je prazdne tak stlacenie tlacidla sposoby vystup
         * z funkcie.
         */
        double predaj = Wallet.parseAndControl(withdraw.getText().trim());
        double btcKurz = Wallet.parseAndControl(actualValueBTC2.getText().trim());
        double eur, poplatok, walletEur;
 
        if (wallet.getWalletBTC() < predaj) {
        	withdraw.setStyle("-fx-text-inner-color: red;");
        	return;
        }
                
        wallet.setWalletBTC(wallet.getWalletBTC() - predaj);
        
        //premena predavanych btc na eura
        eur = predaj * btcKurz; 
        poplatok = predaj * (wallet.getWalletFee() / 100);
        walletEur = eur - poplatok;
            
        //aktualizacia hodnoty penazenky pre eura a nastavenie popisov
        wallet.setWalletEur(wallet.getWalletEur() + walletEur);
        walletEuroLabel.setText(wallet.getWalletEur() + "€");
        walletBTCLabel.setText(wallet.getWalletBTC() + "BTC");
        
        /*
         * ULOHA c.3
         * 
         * definovat funkcie na vyprazdnovanie textovych poli
         * kedze sa kod na viacerych miestach opakuje.
         */
        //vyprazdnenie textovych poli
        withdraw.setText("");
        actualValueBTC2.setText("");
        withdraw.setStyle("-fx-text-inner-color: black;");
    }

    /**
     * Tato metoda Serializuje objekty typu Wallet, ktory program pouziva.
     * T.j. vsetky jeho hodnoty budu zachovane, ulozenim do suboru, z
     * ktoreho je ho mozne opat pri spusteni programu nacitat.
     */
    @FXML
    private void saveWallet(ActionEvent event) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("obj.dat"))) {
           oos.writeObject(wallet);
           System.out.println("objekt bol ulozeny");
        } catch (IOException e) {
            System.err.println("unable to save the object");
        }
    }
}
