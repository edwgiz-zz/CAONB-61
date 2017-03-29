package com.aurea.caonb.egizatullin.utils.jackson;

import static java.nio.charset.StandardCharsets.UTF_8;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.zip.GZIPInputStream;

public class UngzipRawSerializer extends JsonSerializer<byte[]> {

    @Override
    public void serialize(byte[] value, JsonGenerator gen, SerializerProvider serializers)
        throws IOException {
        gen.writeRaw(':');

        InputStreamReader r = new InputStreamReader(
            new GZIPInputStream(new ByteArrayInputStream(value)), UTF_8);
        char[] buff = new char[512];
        for (; ; ) {
            int read = r.read(buff);
            if (read == -1) {
                break;
            }
            gen.writeRaw(buff, 0, read);
        }
    }
}