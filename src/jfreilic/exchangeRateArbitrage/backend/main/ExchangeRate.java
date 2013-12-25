package jfreilic.exchangeRateArbitrage.backend.main;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class ExchangeRate {

	@XmlElement(name = "Name")
	String currencies;
	
	@XmlElement(name = "Rate")
	double rate;
	
	@XmlElement(name = "Bid")
	double bid;
	
	@XmlElement(name = "Ask")
	double ask;

	public String getSource() {
		return currencies;
	}
	
	public double getBid() {
		return bid;
	}
	
	public double getAsk() {
		return ask;
	}
	
	public double getRate() {
		return rate;
	}
	
	public void setSource(String source) {
		this.currencies = source;
	}
	
	public void setBid(double bid) {
		this.bid = bid;
	}
	
	public void setAsk(double ask) {
		this.ask = ask;
	}

	
	public void setRate(double rate) {
		this.rate = rate;
	}

	@Override
	public String toString() {
		return "ExchangeRate [source=" + currencies + ",  bid="
				+ bid + ", ask=" + ask + ", rate=" + rate + "]";
	}
}
