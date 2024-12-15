public class Donation {
    private int id;
    private String donorName;
    private double amount;
    private String donationDate;

    // Constructeurs
    public Donation() {}

    public Donation(int id, String donorName, double amount, String donationDate) {
        this.id = id;
        this.donorName = donorName;
        this.amount = amount;
        this.donationDate = donationDate;
    }

    // Getters et Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDonorName() {
        return donorName;
    }

    public void setDonorName(String donorName) {
        this.donorName = donorName;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getDonationDate() {
        return donationDate;
    }

    public void setDonationDate(String donationDate) {
        this.donationDate = donationDate;
    }
}
