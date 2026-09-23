package backend;
public interface Bookable {
    void checkOut(String studentId);
    void returnItem();
    double calculatePenalty(int delayedDays);
}
