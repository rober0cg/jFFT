package jFFT;

//import org.apache.log4j.Logger;

import javax.sound.sampled.AudioFormat;

public class AppProperties {
//    protected static final Logger log = Logger.getLogger( "jMicFFT1.AppProperties" );

    public final AudioFormat.Encoding ENCODING = AudioFormat.Encoding.PCM_SIGNED;
    public final float RATE = 44100.0f;
    public final int CHANNELS = 1;
    public final int SAMPLE_SIZE = 16;
    public final boolean BIG_ENDIAN = true;
    public final int PER_SECOND = 10;
    public final int BUFFER_SIZE = 32 * 1024; // buffer mayor acumula muestras pasadas
    public final int NUM_BUFFERS = 8;

//    public AppProperties() {
        // TODO Auto-generated constructor stub
//    }
    public void log () {
        LOG.debug("log.ENCODING    = " + this.ENCODING );
        LOG.debug("log.RATE        = " + this.RATE );
        LOG.debug("log.CHANNELS    = " + this.CHANNELS );
        LOG.debug("log.SAMPLE_SIZE = " + this.SAMPLE_SIZE );
        LOG.debug("log.BIG_ENDIAN  = " + this.BIG_ENDIAN );
        LOG.debug("log.PER_SECOND  = " + this.PER_SECOND );
        LOG.debug("log.BUFFER_SIZE = " + this.BUFFER_SIZE );
        LOG.debug("log.NUM_BUFFERS = " + this.NUM_BUFFERS );
    }

}
