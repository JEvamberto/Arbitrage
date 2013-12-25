package jfreilic.exchangeRateArbitrage.backend.main;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
 
@XmlRootElement(name="query")
@XmlAccessorType(XmlAccessType.FIELD)
public class ExchangeRates {
	
    @XmlElementWrapper(name="results")
    @XmlElement(name="rate")
    private List<ExchangeRate> exchangeRates = null;
 
    @Override
	public String toString() {
    	String string = "";
    	for (ExchangeRate rate : this.exchangeRates){
    		string += rate.toString();
    	}
		return string;
	}
    
	public List<ExchangeRate> getExchangeRates() {
        return exchangeRates;
    }
 
    public void setExchangeRates(List<ExchangeRate> exchangeRates) {
        this.exchangeRates = exchangeRates;
    }
}
