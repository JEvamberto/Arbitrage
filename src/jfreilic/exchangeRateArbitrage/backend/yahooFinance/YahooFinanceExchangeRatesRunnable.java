package jfreilic.exchangeRateArbitrage.backend.yahooFinance;

import java.util.Map;

import jfreilic.exchangeRateArbitrage.backend.main.ExchangeRate;
import jfreilic.exchangeRateArbitrage.backend.main.ExchangeRates;

import org.apache.http.client.HttpClient;

public class YahooFinanceExchangeRatesRunnable extends Thread{
	String[] currencies;
	int currencyToGet;
	double[][] adjMat;
	Map<String, Integer> currencyIndexMap;
	HttpClient client;
	
	public YahooFinanceExchangeRatesRunnable(String[] currencies,
			Map<String, Integer> currencyIndexMap, int currencyToGet,
			double[][] adjMat, HttpClient client){
		this.currencies = currencies;
		this.currencyIndexMap = currencyIndexMap;
		this.currencyToGet = currencyToGet;
		this.adjMat = adjMat;
		this.client = client;
	}
	
	@Override
	public void run() {
		String request = buildRequestString(currencyToGet);
        ExchangeRates rates = YahooFinance.sendRequest(client, request);
		exchangeRatesToAdjacenyMatrix(rates);
	}
	
	private String buildRequestString(int sourceCurrency){
		String request = YahooFinance.requestPrefix;
		for (int j = 0; j < currencies.length; j++){
			if (sourceCurrency != j){
				request += YahooFinance.ratePrefix+currencies[sourceCurrency]+currencies[j]+YahooFinance.rateSuffix;
			}
		}
		request += YahooFinance.requestSuffix;
		return request;
	}
	
	private void exchangeRatesToAdjacenyMatrix(ExchangeRates currencyRates) {
		for (ExchangeRate rate : currencyRates.getExchangeRates()){
			String[] countries = rate.getSource().split(" ");
			if (countries.length == 3){
				String source = countries[0];
				String dest = countries[2];
				if (!source.equals(dest)){
					int startCurrency = currencyIndexMap.get(source);
					assert(startCurrency == currencyToGet);
					int destCurrency = currencyIndexMap.get(dest);
					adjMat[startCurrency][destCurrency] = rate.getRate();
				}
			}
		}
	}
}
