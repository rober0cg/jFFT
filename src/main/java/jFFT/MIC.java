package jFFT;

//import org.apache.log4j.Logger;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.FileOutputStream;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.TargetDataLine;

public class MIC {
//    static final Logger log = Logger.getLogger( "MIC" );

    private AudioInputStream audioInputStream;
    private AudioFormat format;
    private TargetDataLine line;

    private int lngBufferInBytes;
    private int numBytesRead, sumBytesRead, numLinesRead;
    private byte[] data ;

    private FileOutputStream fos = null;

    public MIC() {
        super();
        LOG.info("MIC()");
    }

    public MIC(AudioFormat format) {
        LOG.info("MIC(format)");
        this.format = format;
    }

    public MIC(
            AudioFormat.Encoding encoding, float rate, int sampleSizeInBits, 
            int channels, boolean bigEndian
    ) {
        LOG.info("build(values)");
        this.format = new AudioFormat(
            encoding, rate, sampleSizeInBits, 
            channels, (sampleSizeInBits / 8) * channels, rate, bigEndian);
        logAudioFormat();
    }

    public MIC build(AudioFormat format) {
        LOG.info("build(format)");
        this.format = format;
        logAudioFormat();
        return this;
    }


    private void logAudioFormat() {
        LOG.debug("format.encoding         :" + format.getEncoding());
        LOG.debug("format.sampleRate       :" + format.getSampleRate());
        LOG.debug("format.channels         :" + format.getChannels());
        LOG.debug("format.frameRate        :" + format.getFrameRate());
        LOG.debug("format.frameSize        :" + format.getFrameSize());
        LOG.debug("format.sampleSizeInBits :" + format.getSampleSizeInBits());
        LOG.debug("format.bigEndian        :" + format.isBigEndian());
/*
format.encoding         :PCM_SIGNED
format.sampleRate       :44100.0
format.channels         :1
format.frameRate        :44100.0
format.frameSize        :2
format.sampleSizeInBits :16
format.bigEndian        :true
*/
    }


    public void init () {
        LOG.info("init BEGIN");

        DataLine.Info dlInfo = new DataLine.Info(TargetDataLine.class, format);
        if (!AudioSystem.isLineSupported(dlInfo)) {
            this.format = null;
            this.line = null;
            return ;
        }

        TargetDataLine tdl;
        try {
            tdl = (TargetDataLine) AudioSystem.getLine(dlInfo);
            tdl.open(format, tdl.getBufferSize());
            tdl.start();
        } catch (final Exception ex) {
            this.line = null;
            return ;
        }
        this.line = tdl;


        int frameSizeInBytes = format.getFrameSize();
        int bufferLengthInFrames = line.getBufferSize() / 10; // 10 lecturas por segundo
        lngBufferInBytes = bufferLengthInFrames * frameSizeInBytes;

        data = new byte[lngBufferInBytes];

        int numBytesRead=0, sumBytesRead=0, numLinesRead=0;

        if (false) {
            try {
                fos = new FileOutputStream("mic.raw");
            }
            catch ( IOException e ) {
                LOG.info("init w/out mic.raw" + e);
            }
        }

        LOG.info("init END");
        return ;
    }


    public byte[] read () {
        LOG.info("read BEGIN");

//        try {
            if ((numBytesRead = line.read(data, 0, lngBufferInBytes)) == -1) {
               return data;
            }
            numLinesRead++;
            sumBytesRead+=numBytesRead;

            try {
                if ( fos != null)
                    fos.write(data);
            }
            catch ( IOException e ) {
                LOG.info("read error write mic.raw " + e);
            }
            LOG.trace(
                String.format("line.read: #%d %d/%d",
                    numLinesRead, numBytesRead, sumBytesRead));
//        } catch (IOException ex) {
//            ex.printStackTrace();
//        }

        LOG.info("read END #" + numLinesRead);
        return data;
    }

    public void end() {
        try {
            if (fos != null)
                fos.close();
        }
        catch ( IOException e ) {
            LOG.info("read error close mic.raw " + e);
        }

        LOG.info("read");
    }
}

