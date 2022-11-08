package site.chaotic.quantum.kookframework.json;

import com.google.gson.Gson;
import org.reactivestreams.Publisher;
import org.springframework.core.ResolvableType;
import org.springframework.core.codec.DecodingException;
import org.springframework.core.codec.Hints;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.codec.HttpMessageDecoder;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.MimeType;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;

public class GsonDecoder implements HttpMessageDecoder<Object> {

    private static final int MAX_IN_MEMORY_SIZE = 2000 * 1000000;

    private final Gson gson;

    public GsonDecoder(Gson gson) {
        this.gson = gson;
    }

    @Override
    @NonNull
    public Map<String, Object> getDecodeHints(
            @NonNull final ResolvableType resolvableType,
            @NonNull final ResolvableType elementType,
            @NonNull final ServerHttpRequest request,
            @NonNull final ServerHttpResponse response) {
        return Hints.none();
    }

    @Override
    public boolean canDecode(final ResolvableType elementType, final MimeType mimeType) {
        if (CharSequence.class.isAssignableFrom(elementType.toClass())) {
            return false;
        }
        if (!GsonEncoding.supportsMimeType(mimeType)) {
            return false;
        }
        return GsonEncoding.isTypeAdapterAvailable(gson, elementType.getRawClass());
    }

    @Override
    public Object decode(@NonNull DataBuffer dataBuffer, @NonNull ResolvableType targetType,
                         @Nullable MimeType mimeType, @Nullable Map<String, Object> hints) throws DecodingException {

        return decodeInternal(dataBuffer, targetType, hints);
    }

    private Object decodeInternal(final DataBuffer dataBuffer, final ResolvableType targetType, @Nullable Map<String, Object> hints) {
        try {
            return gson.fromJson(new InputStreamReader(dataBuffer.asInputStream()), targetType.getRawClass());
        } finally {
            DataBufferUtils.release(dataBuffer);
        }
    }


    @Override
    @NonNull
    public Flux<Object> decode(@NonNull Publisher<DataBuffer> input, @NonNull ResolvableType elementType,
                               @Nullable MimeType mimeType, @Nullable Map<String, Object> hints) {
        return Flux.from(input).map(d -> decodeInternal(d, elementType, hints));
    }

    @Override
    @NonNull
    public Mono<Object> decodeToMono(@NonNull final Publisher<DataBuffer> inputStream,
                                     @NonNull final ResolvableType elementType,
                                     final MimeType mimeType, final Map<String, Object> hints) {
        return DataBufferUtils.join(inputStream, MAX_IN_MEMORY_SIZE)
                .flatMap(dataBuffer -> Mono.justOrEmpty(decode(dataBuffer, elementType, mimeType, hints)));
    }

    @Override
    @NonNull
    public List<MimeType> getDecodableMimeTypes() {
        return GsonEncoding.mimeTypes;
    }
}
