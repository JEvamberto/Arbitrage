package jfreilic.exchangeRateArbitrage.backend.main;

import java.util.ArrayList;
import java.util.List;

public class Converter {
	
	public Converter() {
		// TODO Auto-generated constructor stub
	}
	
	public static List<List<String>> findArbitrages(double[][] exchangeRates){
		for (int i=0;i<exchangeRates.length;i++){
			for (int j=0;j<exchangeRates[0].length;j++){
				exchangeRates[i][j] = exchangeRates[i][j]!=0 ? -Math.log(exchangeRates[i][j]) : 0;
			}
		}
		double[][] pathDistance = new double[exchangeRates.length][exchangeRates[0].length];
		
		
		
		
		
		
	}

}
