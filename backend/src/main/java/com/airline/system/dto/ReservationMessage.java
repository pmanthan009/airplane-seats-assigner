public class ReservationMessage {
    private int flightNo;
    private int row;
    private String seat; // e.g., "A", "B"

    // Getters and Setters required for Spring JSON mapping
    public int getFlightNo() { return flightNo; }
    public void setFlightNo(int flightNo) { this.flightNo = flightNo; }
    public int getRow() { return row; }
    public void setRow(int row) { this.row = row; }
    public String getSeat() { return seat; }
    public void setSeat(String seat) { this.seat = seat; }
}