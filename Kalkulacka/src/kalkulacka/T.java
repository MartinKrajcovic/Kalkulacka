package kalkulacka;

import java.util.Formatter;

public class T {
	
	/**
	 * Tato metoda sluzi na zaokruhlenie celeho datoveho typu double na
	 * pozadovanu presnost desatinnych miest.
	 */
	public static double formatDouble(double num, int decimalPoint) {
		Formatter fmt = new Formatter();
		String precision = "%." + decimalPoint + "f";
		String str = fmt.format(precision, num).toString();
		fmt.close();
		if (str.contains(","))
			str = str.replace(',', '.');
		return Double.parseDouble(str);
	}	
}
