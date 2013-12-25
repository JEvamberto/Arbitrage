package jfreilic.exchangeRateArbitrage.backend.tests;


import jfreilic.exchangeRateArbitrage.backend.main.ExchangeRateCache;

import org.junit.BeforeClass;
import org.junit.Test;

public class ExchangeRateCacheTest {
	private static ExchangeRateCache cache;
	
	@BeforeClass
	public static void setUp() {
		cache = new ExchangeRateCache();
	}
	
	@Test
	public void test1() {
		for (int i= 0 ; i < 5; i++){
			cache = new ExchangeRateCache();
			System.out.println(cache.getArbitrage());
		}
	}
	
	@Test
	public void test2() {
		for (int i= 0 ; i < 5; i++){
			System.out.println(cache.getArbitrage());
		}
	}
}
