package jFFT;

import java.lang.instrument.Instrumentation;

public class ObjectSize {
    private static volatile Instrumentation instrumentation;
    public static void premain(String args, Instrumentation inst) {
    	LOG.info("ObjectSize:premain");
        instrumentation = inst;
    }
    public static long getObjectSize(Object o) {
    	LOG.info("ObjectSize:getObjectSize");
        return instrumentation.getObjectSize(o);
    }
}
