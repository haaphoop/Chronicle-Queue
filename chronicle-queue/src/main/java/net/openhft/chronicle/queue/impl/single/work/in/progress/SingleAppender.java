/*
 * Copyright 2015 Higher Frequency Trading
 *
 * http://www.higherfrequencytrading.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.openhft.chronicle.queue.impl.single.work.in.progress;

import net.openhft.chronicle.bytes.Bytes;
import net.openhft.chronicle.bytes.NativeBytes;
import net.openhft.chronicle.bytes.WriteBytesMarshallable;
import net.openhft.chronicle.queue.ChronicleQueue;
import net.openhft.chronicle.queue.ExcerptAppender;
import net.openhft.chronicle.wire.Wire;
import net.openhft.chronicle.wire.WriteMarshallable;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.function.Function;

/**
 * Created by peter.lawrey on 30/01/15.
 */
public class SingleAppender implements ExcerptAppender {

    @NotNull
    private final ChronicleQueue chronicle;
    private final Bytes buffer = NativeBytes.nativeBytes();
    private final Wire wire;

    private long lastWrittenIndex = -1;

    public SingleAppender(ChronicleQueue chronicle, Function<Bytes, Wire> bytesToWire) {
        this.chronicle = chronicle;
        wire = bytesToWire.apply(buffer);
    }


/*    @Override
    public void writeDocument(@NotNull Consumer<WireOut> writer) {
        buffer.clear();
        writer.accept(wire);
    //    buffer.flip();
        lastWrittenIndex = chronicle.appendDocument(buffer);
    }*/

    @Override
    public long writeDocument(@NotNull WriteMarshallable writer) throws IOException {
        buffer.clear();

        writer.writeMarshallable(wire);

        // todo
        //    buffer.flip();
        //   lastWrittenIndex = chronicle.appendDocument(writer);

        return lastWrittenIndex;
    }

    @Override
    public long writeBytes(@NotNull WriteBytesMarshallable marshallable) throws IOException {
        return 0;
    }

    @Override
    public long writeBytes(@NotNull Bytes<?> bytes) throws IOException {
        return 0;
    }

    /**
     * @return the last index generated by this appender
     * @throws IllegalStateException if the last index has not been set
     */
    @Override
    public long index() {
        if (lastWrittenIndex == -1) {
            String message = "No document has been written using this appender, so the " +
                    "lastWrittenIndex() is not available.";
            throw new IllegalStateException(message);
        }
        return lastWrittenIndex;
    }

    @Override
    public long cycle() {
        return -1;
    }

    @NotNull
    public ChronicleQueue chronicle() {
        return chronicle;
    }

    @Override
    public ChronicleQueue queue() {
        return chronicle;
    }
}
