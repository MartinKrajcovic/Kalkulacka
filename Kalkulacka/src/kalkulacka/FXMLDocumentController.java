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

/**
 *
 * @author mkrajcovic
 */
public class FXMLDocumentController implements Initializable {
    @FXML private Label walletFeeLabel;			//popis stavu penazenky dole vlavo
    @FXML private TextField newDepo;			//pole pre zadanie noveho vkladu
    @FXML private TextField purchase;			//pole pre zadanie ciastky nakupu
    @FXML private TextField actualValueBTC;		//pole pre zadanie aktualnej ceny btc
    @FXML private Button addToWallet;			//tlacidlo "Add" pre pridanie vkladu
    @FXML private Button buyFromWallet;			//tlacidlo "Buy" pre nakup btc
    @FXML private TextField withdraw;			//pole pre zadanie poctu btc na predaj
    @FXML private TextField actualValueBTC2;	//pole pre zadanie aktualnej ceny btc
    @FXML private Button sellBTC;				//tlacidlo "Sell" pre predaj btc
    @FXML private Label walletEuroLabel;		//popis stavu penazenky pre eura
    @FXML private Label walletBTCLabel;			//popis stavu penazenky pre btc
    @FXML private Label withdrawAllTextButton;	//popis "Withdraw all", pre nacitanie vsetkych btc
    static double staticWalletFee;	//staticka premenna pre presun hodnoty poplatku medzi triedami
    static boolean changed = false;	//staticky priznak pre overenie presunu hodnoty poplatku medzi triedami
    Wallet wallet;	//deklaracia objektu triedy Wallet, s ktorym sa pracuje pocas celeho chodu programu
    
       
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
     * 
     */
    public void openScene2(){
        try{
            FXMLLoader fl = new FXMLLoader(getClass().getResource("feeSet.fxml"));
            Parent rootl = (Parent)fl.load();
            Stage stage2 = new Stage();
            stage2.setScene(new Scene(rootl));
            stage2.setResizable(false);
            stage2.setTitle("Wallet Fee");
            stage2.setAlwaysOnTop(true);
            stage2.showAndWait();
            
            wallet.setWalletFee(staticWalletFee, changed);
            walletFeeLabel.setText(wallet.getWalletFee() + "%");
        } catch (IOException e) {
            System.err.print("unable to open stage2 (settings)");
        }
    }  
    
    @FXML
    private void openJew(ActionEvent event) {
        openScene2();
    }
    
    @FXML
    private void addToWallet(ActionEvent event) {
        /*
        implementacia parsovacieho algoritmu triedy Wallet
        */
        double walletEuroDeposit = Wallet.parseAndControl(newDepo.getText().trim());
        wallet.setWalletEur(wallet.getWalletEur() + walletEuroDeposit);
        walletEuroLabel.setText(wallet.getWalletEur() + "€");
        newDepo.setText("");
    }
    
    /**
     * Tato funkcia sa spusti pri stlaceni tlacidla "Buy".
     * 
     *[1] Overuje sa podmienka -> ak nemam nastaveny poplatok penazenky a chcem robit
     *    nejake operacie, otvori sa mi okno pre nastavenie poplatku penazenky a program
     *    ihned vyjde z tejto funkcie. 
     *[2] Do novej premennej si ulozim sumu v eurach, ktora sa stiahne z GUI pola v podobe,
     *    orezaneho textu, ktory je parsovacim algoritmom triedy Wallet prekonvertovany na cislo.
     *[3] Overuje sa podmienka -> ak nemam tolko prostriedkov v penazenke, tak text pola v GUI
     *    zmeni farbu na cervenu, cim si ziada napravu a nasledne vyjdem z funkcie tohto tlacidla.
     *[4] Do novej premennej si ulozim aktualny kurz bitcoinu v eurach, ktory sa stiahne z GUI
     *    pola v podobe orezaneho textu, ktory je parsovacim algoritmom triedy Wallet
     *    prekonvertovany na cislo.
     *[5] Aktualizujem stav penazenky pre hodnotu prostriedkov v eurach tym, ze od aktualnej ciastky,
     *    ktora sa nachadza v penazenke odratam ciastku urcenu na nakup bitcoinu.
     *[6] Do novej premennej si ulozim vypocet poplatku v eurach a to nasledovne: nakupnu sumu, ktorou
     *    sa chystam kupit bitcoin vynasobim poplatkom penazenky. (poplatok penazenky sa deli 100-mi, 
     *    vzhladom na to ako je do GUI tento poplatok zadavany)
     *[7] Do premennej, kde mam ulozenu nakupnu sumu v eurach si ulozim tuto sumu ponizenu o vyratany
     *    poplatok.
     *[8] Do premennej kde mam ulozeny kurz bitcoinu v eurach ulozim nakup vydeleny kurzom a tym
     *    vykonam premenu ciastky z eur na ciastku v bitcoinoch.
     *[9] Aktualizujem stav penazenky pre hodnotu prostriedkov v bitcoinoch tym, ze k aktualnemu
     *    poctu bitcoinov priratam pocet bitcoinov, ktore vysli z predchadzajucej premeny.
     *[10]Nasledne aktualizujem indikator na aktualny stav prostriedkov penazenky v eurach, v GUI.
     *[11]Aktualizujem indikator na aktualny stav prostriedkov penazenky v bitcoinoch, v GUI
     *[12]Nakoniec vyprazdnim textove polia v GUI a aktualizujem farbu textoveho pola pre ciastku
     *    nakupu spat na ciernu.
     */
    @FXML
    private void buyFromWallet(ActionEvent event) {
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
        
        double poplatok = nakup * (wallet.getWalletFee() / 100);
        nakup = nakup - poplatok;
        btc = nakup / btc;
        
        wallet.setWalletBTC(wallet.getWalletBTC() + btc);
        
        walletEuroLabel.setText(wallet.getWalletEur() + "€");
        walletBTCLabel.setText(wallet.getWalletBTC() + "BTC"); 
        
        purchase.setText("");
        actualValueBTC.setText("");
        purchase.setStyle("-fx-text-inner-color: black;");      
    }

    @FXML
    private void withdrawAll(MouseEvent event) {
        withdraw.setText(wallet.getWalletBTC() + "");
    }
    
    @FXML
    private void sellBitcoin(ActionEvent event) {
        if (wallet.getWalletFee() <= 0) {//ked nie je nastaveny poplatok penazenky
            openScene2();
            return;         //vyjde sa z funkcie
        }
        String text = withdraw.getText().trim();
        String text2= actualValueBTC2.getText().trim();
        
        if (!(text.isEmpty() || text2.isEmpty())) {
            
            if (text.matches("\\d+\\.\\d+") || text.matches("\\d+\\,\\d+") || text.matches("\\d+")
                   && text2.matches("\\d+\\.\\d+") || text2.matches("\\d+\\,\\d+") || text2.matches("\\d+")) {
                
                if (text.contains(",")) {
                    text = text.replace(',', '.');
                }
                else if (text2.contains(",")){
                    text2 = text2.replace(',', '.');
                }
                
                double predaj = Double.parseDouble(text);
                double btcKurz = Double.parseDouble(text2);
                double eur;
                
                if (wallet.getWalletBTC() < predaj) {
                    withdraw.setStyle("-fx-text-inner-color: red;");
                    return;
                }
                
                wallet.setWalletBTC(wallet.getWalletBTC() - predaj);
                
                eur = predaj * btcKurz; //premena predavanych btc na eura
                double poplatok = predaj * (wallet.getWalletFee() / 100);
                double walletEur = eur - poplatok;
            
                wallet.setWalletEur(wallet.getWalletEur() + walletEur);
                walletEuroLabel.setText(wallet.getWalletEur() + "€");
            
                walletBTCLabel.setText(wallet.getWalletBTC() + "BTC");
                
            }
        }
        withdraw.setText("");
        actualValueBTC2.setText("");
        withdraw.setStyle("-fx-text-inner-color: black;");
    }

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
