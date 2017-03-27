package com.aurea.caonb.egizatullin.utils.process;

import java.io.IOException;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.IntPredicate;

public final class ProcessUtils {

    public static int run(ProcessBuilder pb, IntPredicate exitCodePredicate, Consumer<Process> beforeExit) {
        Process p;
        try {
            p = pb.start();
        } catch (IOException e) {
            throw exception(e.getMessage(), pb, e);
        }

        beforeExit.accept(p);

        int exitCode;
        try {
            exitCode = p.waitFor();
        } catch (InterruptedException e) {
            throw exception(e.getMessage(), pb, e);
        }
        if(!exitCodePredicate.test(exitCode)) {
            throw exception("Incorrect exit code: " + exitCode, pb, null);
        }
        return exitCode;
    }


    private static RuntimeException exception(String message, ProcessBuilder pb, Exception ex) {
        StringBuilder b = new StringBuilder(256);
        b.append(message).append("\ncmdline:\n  ");
        for (String s : pb.command()) {
            b.append(s).append(' ');
        }
        b.append("\nenvs:\n");
        for (Map.Entry<String, String> e : pb.environment().entrySet()) {
            b.append("  ").append(e.getKey()).append('=').append(e.getValue()).append('\n');
        }
        b.append("\nworkdir:\n  ").append(pb.directory().toString());
        return new RuntimeException(b.toString(), ex);
    }

    private ProcessUtils() {
    }
}
