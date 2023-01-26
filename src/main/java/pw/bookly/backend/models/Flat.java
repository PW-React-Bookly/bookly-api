package pw.bookly.backend.models;

public class Flat{

    private String itemExternalId;
    private String address;
    private int numberOfPeople;
    private float pricePerNight;

    private String description;


    public String getItemExternalId() {
        return itemExternalId;
    }

    public void setItemExternalId(String itemExternalId) {
        this.itemExternalId = itemExternalId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getNumberOfPeople() {
        return numberOfPeople;
    }

    public void setNumberOfPeople(int numberOfPeople) {
        this.numberOfPeople = numberOfPeople;
    }

    public float getPricePerNight() {
        return pricePerNight;
    }

    public void setPricePerNight(float pricePerNight) {
        this.pricePerNight = pricePerNight;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
