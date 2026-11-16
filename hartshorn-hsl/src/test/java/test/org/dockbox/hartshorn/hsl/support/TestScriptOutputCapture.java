package test.org.dockbox.hartshorn.hsl.support;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.SequencedCollection;
import org.dockbox.hartshorn.hsl.compiler.runtime.HslRuntimeSupport;
import org.dockbox.hartshorn.hsl.runtime.ScriptOutputCapture;

public class TestScriptOutputCapture implements ScriptOutputCapture {

    private final SequencedCollection<OutputChunk> output = new LinkedList<>();

    @Override
    public synchronized void print(Object object) {
        this.output.add(new OutputChunk(LocalDateTime.now(), HslRuntimeSupport.stringify(object)));
    }

    public SequencedCollection<OutputChunk> output() {
        return List.copyOf(this.output);
    }

    public record OutputChunk(LocalDateTime timestamp, String output) {}
}
