package fr.mist.models.annihilation.type;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.time.Instant;
import java.util.List;
import java.util.stream.StreamSupport;

public record AnnihilationInfo(
		List<Instant> lastEvents
) {
	public static class AnnihilationDeserializer implements JsonDeserializer<AnnihilationInfo> {
		@Override
		public AnnihilationInfo deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext ctx)
				throws JsonParseException {
			JsonObject obj = json.getAsJsonObject();
			if (!obj.has("lastEvents")) {
				return new AnnihilationInfo(List.of());
			}

			JsonArray arr = obj.getAsJsonArray("lastEvents");
			List<Instant> instants = StreamSupport.stream(arr.spliterator(), false)
					.map(JsonElement::getAsString)
					.map(Instant::parse)
					.toList();

			return new AnnihilationInfo(instants);
		}
	}
}
