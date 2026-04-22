import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class EventBrowsingService {

    public List<Event> getUpcomingEvents() {
        return new ArrayList<>();
    }

    public List<Event> searchEvents(String category, Date date, String location, float price, String artist) {
        return new ArrayList<>();
    }

    public EventDetails getEventDetails(int eventId) {
        return new EventDetails();
    }
}