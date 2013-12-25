package jfreilic.exchangeRateArbitrage.backend.yahooFinance;

import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import jfreilic.exchangeRateArbitrage.backend.main.ExchangeRates;


import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;


public class YahooFinance {
	
	public static final String requestPrefix = "http://query.yahooapis.com/v1/public/yql?q=" +
			"select%20%2a%20from%20yahoo.finance.xchange%20where%20pair%20in"+
			"%20%28%22USDUSD%22,";
	
	public static final String requestSuffix = "%20%22USDUSD%22%29&env=store://datatables.org/alltableswithkeys";
	public static final String ratePrefix = "%20%22";
	public static final String rateSuffix = "%22,";
	public static final String firstRatePrefix = "%20%28%22";
	public static final String firstRateSuffix = rateSuffix;
	public static final String lastRatePrefix = ratePrefix;
	public static final String lastRateSuffix = "%22%29";
	
	public static final String[] allCurrencies =
		{"AFN", "ALL", "DZD", "USD", "EUR", "AOA", "XCD", "XCD", "XCD", "ARS", "AMD",
		"AWG", "AUD", "EUR", "AZN", "BSD", "BHD", "BDT", "BBD", "BYR", "EUR", "BZD",
		"XOF", "BMD", "BTN", "BOB", "BAM", "BWP", "NOK", "BRL", "USD", "BND", "BGN",
		"XOF", "BIF", "KHR", "XAF", "CAD", "CVE", "KYD", "XAF", "XAF", "CLP", "CNY",
		"AUD", "AUD", "COP", "KMF", "XAF", "CDF", "NZD", "CRC", "HRK", "CUP", "EUR",
		"CZK", "DKK", "DJF", "XCD", "DOP", "ECS", "EGP", "SVC", "XAF", "ERN", "EUR",
		"ETB", "EUR", "FKP", "DKK", "FJD", "EUR", "EUR", "EUR", "EUR", "XAF", "GMD",
		"GEL", "EUR", "GHS", "GIP", "GBP", "EUR", "DKK", "XCD", "EUR", "USD", "QTQ",
		"GGP", "GNF", "GWP", "GYD", "HTG", "AUD", "HNL", "HKD", "HUF", "ISK", "INR",
		"IDR", "IRR", "IQD", "EUR", "GBP", "ILS", "EUR", "XOF", "JMD", "JPY", "GBP",
		"JOD", "KZT", "KES", "AUD", "KPW", "KRW", "KWD", "KGS", "LAK", "LVL", "LBP",
		"LSL", "LRD", "LYD", "CHF", "LTL", "EUR", "MOP", "MKD", "MGF", "MWK", "MYR",
		"MVR", "XOF", "EUR", "USD", "EUR", "MRO", "MUR", "EUR", "MXN", "USD", "MDL",
		"EUR", "MNT", "EUR", "XCD", "MAD", "MZN", "MMK", "NAD", "AUD", "NPR", "EUR",
		"ANG", "XPF", "NZD", "NIO", "XOF", "NGN", "NZD", "AUD", "USD", "NOK", "OMR",
		"PKR", "USD", "PAB", "PGK", "PYG", "PEN", "PHP", "NZD", "PLN", "XPF", "EUR",
		"USD", "QAR", "EUR", "RON", "RUB", "RWF", "SHP", "XCD", "XCD", "EUR", "XCD",
		"WST", "EUR", "STD", "SAR", "XOF", "RSD", "SCR", "SLL", "SGD", "EUR", "EUR",
		"SBD", "SOS", "ZAR", "GBP", "SSP", "EUR", "LKR", "SDG", "SRD", "NOK", "SZL",
		"SEK", "CHF", "SYP", "TWD", "TJS", "TZS", "THB", "XOF", "NZD", "TOP", "TTD",
		"TND", "TRY", "TMT", "USD", "AUD", "GBP", "UGX", "UAH", "AED", "UYU", "USD",
		"USD", "UZS", "VUV", "EUR", "VEF", "VND", "USD", "USD", "XPF", "MAD", "YER",
		"ZMW", "ZWD"};
	
	private static String[] currencies = new HashSet<>(Arrays.asList(allCurrencies)).toArray(new String[0]);
	private static boolean sortedCurrencies = false;
	
	public static String[] currencyArray() {			
		if (sortedCurrencies == false) {
			Arrays.sort(currencies);
			sortedCurrencies = true;
		}
		return currencies.clone();
	}
	
	public static Map<String, Integer> currencyToIndexMap() {
		String[] currencies = currencyArray();
		Map<String, Integer> currencyIndexMap = new HashMap<>(currencies.length);
		for (int i = 0; i < currencies.length; i++){
			currencyIndexMap.put(currencies[i], i);
		}
		return currencyIndexMap;
	}
	
	public static Map<String, Integer> currencyToIndexMap(String[] currencies) {
		Map<String, Integer> currencyIndexMap = new HashMap<>(currencies.length);
		for (int i = 0; i < currencies.length; i++){
			currencyIndexMap.put(currencies[i], i);
		}
		return currencyIndexMap;
	}
	
	public static double[][] getExchangeRates() {
		String[] currencies = currencyArray();
		int size = currencies.length;
		Map<String, Integer> currencyIndexMap = currencyToIndexMap(currencies);
		double[][] adjacenyMatrix = new double[size][size];		
		HttpClientBuilder clientBuilder = createClientBuilder(size, size);
		YahooFinanceExchangeRatesRunnable[] runnables = new YahooFinanceExchangeRatesRunnable[size];
		CloseableHttpClient[] clients = new CloseableHttpClient[size];
		
		for (int i = 0; i < size; i++){
			clients[i] = clientBuilder.build();
			runnables[i] = new YahooFinanceExchangeRatesRunnable(currencies,
					currencyIndexMap, i, adjacenyMatrix, clients[i]);
			runnables[i].start();
		}
		
		for (int i = 0; i < size; i++){
			try {
				runnables[i].join();
				//clients[i].close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return adjacenyMatrix;
	}
	
	private static HttpClientBuilder createClientBuilder(int maxConnections, int maxRoutes) {
		PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
		cm.setMaxTotal(maxConnections);
		cm.setDefaultMaxPerRoute(maxRoutes);
		// Increase max connections for localhost:80 to 50
		HttpHost localhost = new HttpHost("localhost", 80);
		cm.setMaxPerRoute(new HttpRoute(localhost), 50);
		return HttpClientBuilder.create().setConnectionManager(cm);
	}
	
	public static ExchangeRates sendRequest(HttpClient client, String request){
		HttpGet method = new HttpGet(request);
        try {
			HttpResponse response = client.execute(method);
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				HttpEntity entity= response.getEntity();
				InputStream is = entity.getContent();
				JAXBContext jaxbContext = JAXBContext.newInstance(ExchangeRates.class);
				Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
				ExchangeRates rates = (ExchangeRates) jaxbUnmarshaller.unmarshal(is);
				return rates;
			} else {
				System.out.println("Request status " + response.getStatusLine());
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
        return null;
	}
	
	public static ExchangeRates getExchangeRates(List<String> countryPairs){
		String request = YahooFinance.requestPrefix;
		for (String pair : countryPairs) {
			request += YahooFinance.ratePrefix+pair+YahooFinance.rateSuffix;
		}
		request += YahooFinance.requestSuffix;
		HttpClient client = HttpClientBuilder.create().build();
		return YahooFinance.sendRequest(client, request);
	}

	/* some other request strings of interest
		request = "http://query.yahooapis.com/v1/public/yql?q=" +
				"select%20%2a%20from%20yahoo.finance.xchange%20where%20pair%20in"+
				"%20%28%22USDEUR%22,%20%22USDJPY%22,%20%22USDBGN%22,"+
				"%20%22USDCZK%22,%20%22USDDKK%22,%20%22USDGBP%22,%20%22USDHUF%22,%20%22USDLTL%22,"+
				"%20%22USDLVL%22,%20%22USDPLN%22,%20%22USDRON%22,%20%22USDSEK%22,%20%22USDCHF%22,"+
				"%20%22USDNOK%22,%20%22USDHRK%22,%20%22USDRUB%22,%20%22USDTRY%22,%20%22USDAUD%22,"+
				"%20%22USDBRL%22,%20%22USDCAD%22,%20%22USDCNY%22,%20%22USDHKD%22,%20%22USDIDR%22,"+
				"%20%22USDILS%22,%20%22USDINR%22,%20%22USDKRW%22,%20%22USDMXN%22,%20%22USDMYR%22,"+
				"%20%22USDNZD%22,%20%22USDPHP%22,%20%22USDSGD%22,%20%22USDTHB%22,%20%22USDZAR%22,"+
				"%20%22USDISK%22%29&env=store://datatables.org/alltableswithkeys";
				
		request = "http://finance.yahoo.com/webservice/v1/symbols/allcurrencies/quote";
	 */
}
