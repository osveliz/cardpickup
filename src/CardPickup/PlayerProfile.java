package CardPickup;

/**
 * Auxiliary class for player management.
 * @author Marcus G.
 */
public class PlayerProfile {
	
	private String name;
	private Hand currentHand;
	private int currentLocation;

    /**
     * Constructor
     * @param agentName Agent Name
     */

	PlayerProfile(String agentName){
		this.name = agentName;
	}

    /**
     * Standard accessor method
     * @return agent name
     */
	public String getName() {
		return name;
	}

    /**
     * Standard mutate method
     * @param name new name
     */
	public void setName(String name) {
		this.name = name;
	}

    /**
     * Standard accessor method
     * @return the current hand
     */
	public Hand getCurrentHand() {
		return currentHand;
	}

    /**
     * Standard mutate method
     * @param currentHand the new hand
     */
	public void setCurrentHand(Hand currentHand) {
		this.currentHand = currentHand;
	}

    /**
     * Adds a card to the hand
     * @param card card to be added
     */
	public void addCardToHand(Card card){
        currentHand.addHoleCard(card);
    }

    /**
     * Standard accessor method
     * @return current location
     */
	public int getCurrentLocation() {
		return currentLocation;
	}

    /**
     * Standard mutate method
     * @param currentLocation new location
     */
	public void setCurrentLocation(int currentLocation) {
		this.currentLocation = currentLocation;
	}

    /**
     * Returns the size of the hand
     * @return the size of the hand
     */
	public int getHandSize(){
		return currentHand.getNumHole();
	}
}