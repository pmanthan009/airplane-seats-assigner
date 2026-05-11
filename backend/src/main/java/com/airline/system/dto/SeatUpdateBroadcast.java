public class SeatUpdateBroadcast {
    private int flightNo;
    private int row;
    private String seat;
    private String status;

    public SeatUpdateBroadcast(int flightNo, int row, String seat, String status) {
        this.flightNo = flightNo;
        this.row = row;
        this.seat = seat;
        this.status = status;
    }

    // Getters required for Spring JSON mapping
    public int getFlightNo() { return flightNo; }
    public int getRow() { return row; }
    public String getSeat() { return seat; }
    public String getStatus() { return status; }
}