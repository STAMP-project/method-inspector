package eu.stamp_project.inspector;

import com.google.gson.*;
import java.lang.reflect.Type;

public class MethodSerializer implements JsonSerializer<MethodEntry> {

    @Override
    public JsonElement serialize(MethodEntry entry, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject tree = context.serialize(entry, typeOfSrc).getAsJsonObject();
        if(!entry.hasLines()) {
            tree.remove("from");
            tree.remove("to");
        }
        return tree;
    }

}
