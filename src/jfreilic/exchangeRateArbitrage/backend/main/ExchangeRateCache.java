package jfreilic.exchangeRateArbitrage.backend.main;

import java.util.ArrayList;
import java.util.List;

import jfreilic.exchangeRateArbitrage.backend.yahooFinance.YahooFinance;


public class ExchangeRateCache {
	
	private String[] currencies;
	private List<String> prevCycle;
	private List<String> prevCountryPairs;
	private double[][] adjMat;

	public ExchangeRateCache() {
		currencies = YahooFinance.currencyArray();
		prevCycle = new ArrayList<String>();
		prevCountryPairs = new ArrayList<String>();
	}
	
	public List<String> getArbitrage() {
		if (!checkCycleForArbitrage(prevCycle)) {
			adjMat = YahooFinance.getExchangeRates();
			List<Integer> cycleIndex = BellmanFord.negativeWeightCycle(adjMat, 0);
			for (Integer x : cycleIndex) {
				prevCycle.add(currencies[x]);
			}
			prevCountryPairs = new ArrayList<String>();
		}
		return new ArrayList<String>(prevCycle);
	}
	
	private boolean checkCycleForArbitrage(List<String> cycle) {
		if (cycle.size() == 0) {
			return false;
		}
		buildCountryPairsList(cycle);
		double cycleExchangeRate = computeCycleExchangeRate(prevCountryPairs);
		if (cycleExchangeRate < 0) {
			return true;
		} else {
			return false;
		}
	}
	
	private void buildCountryPairsList(List<String> cycle) {
		if (prevCountryPairs.size() == 0){
			String prev = null;
			for (String country : cycle) {
				if (prev != null) {
					prevCountryPairs.add(prev+country);
				}
				prev = country;
			}
		}
	}
	
	private double computeCycleExchangeRate(List<String> countryPairs) {
		ExchangeRates rates = YahooFinance.getExchangeRates(countryPairs);
		double cycleExchangeRate = 0;
		for (ExchangeRate rate : rates.getExchangeRates()) {
			String[] countries = rate.getSource().split(" ");
			if (countries.length == 3){
				String source = countries[0];
				String dest = countries[2];
				if (!source.equals(dest)) {
					double exchangeRate = rate.getRate();
					cycleExchangeRate += -Math.log(exchangeRate);
				}
			}
		}
		return cycleExchangeRate;
	}
}
