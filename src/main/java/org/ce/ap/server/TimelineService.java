package main.java.org.ce.ap.server;

import java.util.ArrayList;

public interface TimelineService {
    public void update(Tweet tweet);
    public ArrayList<Tweet> refresh();
}
