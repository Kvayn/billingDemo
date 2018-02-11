package billing.demo.cli;

import java.util.Map;

public class Config{
	private Map<String, String> metrics;
	private Map<String, String> discounts;

	public Map<String, String> getMetrics(){
		return metrics;
	}
	public void setMetrics(Map<String, String> metrics){
		this.metrics = metrics;
	}
	public Map<String, String> getDiscounts(){
		return discounts;
	}
	public void setDiscounts(Map<String, String> discounts){
		this.discounts = discounts;
	}
}