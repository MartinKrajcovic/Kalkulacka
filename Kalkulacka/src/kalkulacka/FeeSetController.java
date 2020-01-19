package kalkulacka;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class FeeSetController implements Initializable {

    @FXML private TextField feeField;	//textove pole, kam sa vklada poplatok
    @FXML private Button submitButton;	//tlacidlo, ktorym sa nastavuje poplatok

    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }    

    /**
     * Tato metoda sa vykona, po stlaceni tlacidla "Submit"
     * v okne, kde sa nastavuje poplatok penazenky.
     */
    @FXML
    private void setWalletFee(ActionEvent event) {
    	/*
    	 * 1. Tuto funkciu treba upravit tak, aby sa zmenil text na cervenu, ked
    	 * 	  je input neplatny.
    	 * 
    	 * 2. Treba upravit to, ze ak je ten input neplatny, tak nech nezavrie 
    	 *    okno, ale este pred tym vystupi z funkcie tlacidla, nech si to
    	 *    pouzivatel moze opravit.
    	 */
    	
    	//Do tejto premennej sa ulozi cislo, ktore je ako text stihnute z pola v uzivatelskom okne nastavenia poplatku
        FXMLDocumentController.staticWalletFee = parseAndControl(feeField.getText().trim());
        
        //Ak sa do premennej vlozi zaporne cislo, tak doslo k chybe a teda poplatok nebol regulerne zmeneni
        FXMLDocumentController.changed = (FXMLDocumentController.staticWalletFee < 0) ? false : true;
        
        //Uzavretie uzivatelskeho okna pre nastavenie poplatku penazenky.
        Stage stage = (Stage) submitButton.getScene().getWindow();
        stage.close(); 
    }
    
    /**
     * Ide o prepisanu metodu z triedy Wallet.
     * Tato metoda pri neuspesnom parsovani textu
     * na desatinne cislo vrati hodnotu -1.
     */
    public double parseAndControl(String text) {
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
            return -1;
        }
    }
}
