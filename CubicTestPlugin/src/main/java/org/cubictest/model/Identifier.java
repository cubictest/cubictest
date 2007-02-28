package org.cubictest.model;

public class Identifier {

	public final static int MAX_PROBABILITY = 100;
	private String value;
	private int probability;
	private IdentifierType type;

	public void setType(IdentifierType type){
		this.type = type;
	}

	public IdentifierType getType() {
		return type;
	}

	public void setValue(String value){
		this.value = value;
	}
	public String getValue(){
		return value;
	}

	public void setProbability(int probability){
		if(probability > MAX_PROBABILITY){
			this.probability = MAX_PROBABILITY;
		}else if(probability < -MAX_PROBABILITY){
			this.probability = -MAX_PROBABILITY;
		}else{
			this.probability = probability;
		}
	}
	public int getProbability(){
		return probability;
	}

}
