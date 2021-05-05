package site.chaotic.quantum.khlframework.json;

import com.google.gson.Gson;
import org.reactivestreams.Publisher;
import org.springframework.core.ResolvableType;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.MediaType;
import org.springframework.http.codec.HttpMessageEncoder;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.MimeType;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class GsonEncoder implements HttpMessageEncoder<Object> {

    private final Gson gson;

    public GsonEncoder(Gson gson) {
        this.gson = gson;
    }

    @Override
    @NonNull
    public List<MediaType> getStreamingMediaTypes() {
        return Collections.singletonList(MediaType.APPLICATION_STREAM_JSON);
    }

    @Override
    public boolean canEncode(final ResolvableType elementType, final MimeType mimeType) {
        Class<?> clazz = elementType.toClass();
        if (!GsonEncoding.supportsMimeType(mimeType)) {
            return false;
        }
        if (Object.class == clazz) {
            return true;
        }
        if (!String.class.isAssignableFrom(elementType.resolve(clazz))) {
            return GsonEncoding.isTypeAdapterAvailable(gson, clazz);
        }
        return false;
    }

    @Override
    @NonNull
    public Flux<DataBuffer> encode(@NonNull final Publisher<?> inputStream,
                                   @NonNull final DataBufferFactory bufferFactory,
                                   @NonNull final ResolvableType elementType,
                                   final MimeType mimeType,
                                   final Map<String, Object> hints) {
        Assert.notNull(inputStream, "'inputStream' must not be null");
        Assert.notNull(bufferFactory, "'bufferFactory' must not be null");
        Assert.notNull(elementType, "'elementType' must not be null");

        if (inputStream instanceof Mono) {
            return Mono.from(inputStream)
                    .map(value -> encodeValue(value, bufferFactory, elementType, mimeType, hints))
                    .flux();
        } else {
            byte[] separator = streamSeparator(mimeType);
            if (separator != null) { // streaming
                try {
                    return Flux.from(inputStream)
                            .map(value -> encodeStreamingValue(value, bufferFactory, hints, separator));
                } catch (Exception ex) {
                    return Flux.error(ex);
                }
            } else { // non-streaming
                ResolvableType listType = ResolvableType.forClassWithGenerics(List.class, elementType);
                return Flux.from(inputStream)
                        .collectList()
                        .map(list -> encodeValue(list, bufferFactory, listType, mimeType, hints))
                        .flux();
            }

        }
    }

    @Nullable
    private byte[] streamSeparator(@Nullable MimeType mimeType) {
        for (MediaType streamingMediaType : this.getStreamingMediaTypes()) {
            if (streamingMediaType.isCompatibleWith(mimeType)) {
                return GsonEncoding.STREAM_SEPARATORS.getOrDefault(streamingMediaType, GsonEncoding.NEWLINE_SEPARATOR);
            }
        }
        return null;
    }


    @Override
    @NonNull
    public List<MimeType> getEncodableMimeTypes() {
        return GsonEncoding.mimeTypes;
    }


    @Override
    @NonNull
    public DataBuffer encodeValue(@NonNull Object value,
                                  DataBufferFactory bufferFactory,
                                  @NonNull ResolvableType valueType,
                                  @Nullable MimeType mimeType,
                                  @Nullable Map<String, Object> hints) {
        byte[] bytes = gson.toJson(value).getBytes();
        DataBuffer buffer = bufferFactory.allocateBuffer(bytes.length);
        buffer.write(bytes);
        return buffer;
    }


    private DataBuffer encodeStreamingValue(Object value, DataBufferFactory bufferFactory, @Nullable Map<String, Object> hints, byte[] separator) {
        byte[] bytes = gson.toJson(value).getBytes();
        int offset;
        int length;
        offset = 0;
        length = bytes.length;
        DataBuffer buffer = bufferFactory.allocateBuffer(length + separator.length);
        buffer.write(bytes, offset, length);
        buffer.write(separator);
        return buffer;
    }

}
