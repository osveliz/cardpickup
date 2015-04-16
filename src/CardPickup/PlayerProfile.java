package CardPickup;

public class PlayerProfile {
	
	private String name;
	private Hand currentHand;
	private int currentLocation;

	PlayerProfile(String agentName){
		this.name = agentName;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Hand getCurrentHand() {
		return currentHand;
	}

	public void setCurrentHand(Hand currentHand) {
		this.currentHand = currentHand;
	}
	
	public void addCardToHand(Card card){
        currentHand.addHoleCard(card);
    }

	public int getCurrentLocation() {
		return currentLocation;
	}

	public void setCurrentLocation(int currentLocation) {
		this.currentLocation = currentLocation;
	}
	
	public int getHandSize(){
		return currentHand.getNumHole();
	}
	
}