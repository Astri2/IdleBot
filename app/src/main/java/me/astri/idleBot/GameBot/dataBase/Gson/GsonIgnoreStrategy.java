package me.astri.idleBot.GameBot.dataBase.Gson;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;

public class GsonIgnoreStrategy implements ExclusionStrategy {

    @Override
    public boolean shouldSkipClass(final Class<?> clazz) {
        return clazz.getAnnotation(GsonIgnored.class) != null;
    }

    @Override
    public boolean shouldSkipField(final FieldAttributes f) {
        return f.getAnnotation(GsonIgnored.class) != null;
    }
}
