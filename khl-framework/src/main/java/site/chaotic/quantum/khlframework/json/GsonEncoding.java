package site.chaotic.quantum.khlframework.json;

import com.google.gson.Gson;
import org.springframework.http.MediaType;
import org.springframework.lang.Nullable;
import org.springframework.util.MimeType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class GsonEncoding {
    static final List<MimeType> mimeTypes = Stream.of(new MimeType("application", "json"),
            new MimeType("application", "*+json"))
            .collect(Collectors.toUnmodifiableList());

    static final byte[]                 NEWLINE_SEPARATOR = {'\n'};
    static final Map<MediaType, byte[]> STREAM_SEPARATORS;

    static {
        STREAM_SEPARATORS = new HashMap<>();
        STREAM_SEPARATORS.put(MediaType.APPLICATION_STREAM_JSON, NEWLINE_SEPARATOR);
        STREAM_SEPARATORS.put(MediaType.parseMediaType("application/stream+x-jackson-smile"), new byte[0]);
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    static boolean supportsMimeType(@Nullable MimeType mimeType) {
        return (mimeType == null || GsonEncoding.mimeTypes.stream().anyMatch(m -> m.isCompatibleWith(mimeType)));
    }

    static boolean isTypeAdapterAvailable(Gson gson, Class<?> clazz) {
        try {
            gson.getAdapter(clazz);
            return true;
        } catch(final IllegalArgumentException e) {
            return false;
        }
    }

}
