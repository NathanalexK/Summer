package mg.itu.prom16.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import mg.itu.prom16.serializer.LocalDateAdapter;

import java.time.LocalDate;

public class MyJSON {
    private final Gson gson;

    public MyJSON() {
        this.gson = new GsonBuilder()
            .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
            .create()
        ;
    }

    public Gson getGson() {
        return this.gson;
    }
}
