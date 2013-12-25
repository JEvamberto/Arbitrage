package jfreilic.exchangeRateArbitrage.backend.main;

public class App {

	public static void main(String[] args) {
		ExchangeRateCache cache = new ExchangeRateCache();
		while(true){
			System.out.println(cache.getArbitrage() + "\n");
		}
	}
}
