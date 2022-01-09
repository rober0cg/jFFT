package jFFT;

import java.io.IOException;
//import java.io.PipedInputStream;
//import java.io.PipedOutputStream;
//import java.io.ObjectInputStream;
//import java.io.ObjectOutputStream;

import java.util.Scanner;

import java.nio.ByteBuffer;

public class App
{
    private static AppProperties ap;
    private static MIC mic;
    private static RES res;
    private static volatile boolean FIN=false;
    private static final Complex[] EOT = new Complex[1];

	public static void main( String[] args )
        throws IOException, InterruptedException
    {
        LOG.init(LOG.LOG_LEVEL.ALL);
        LOG.info("main BEGIN");

        LOG.info("main(" + args + ")");
        for (String arg : args) {
            LOG.debug("main args: " + arg);
        }

// Inicialización aplicación
        ap = new AppProperties();
        ap.log();

        final int nb = ap.NUM_BUFFERS ;
//        final int bs = (int) (ap.RATE / ap.PER_SECOND) ;
        final int bs = ap.BUFFER_SIZE ;
        final int cs = (int)ObjectSize.getObjectSize((Object)new Complex(0,0)) ;
        final int fm = (int)ap.RATE;

        LOG.info("main Complex.getObjectSize " + cs );

        EOT[0] = new Complex(0,0);

// Inicializar MIC
        mic = new MIC(ap.ENCODING, ap.RATE, ap.SAMPLE_SIZE, ap.CHANNELS, ap.BIG_ENDIAN );
        mic.init();

        res = new RES();
        res.init();

        vBlock ( cs, bs, nb, fm );
// TODO: RETOMAR vPiped ( cs, bs, nb, fm )
// con 3 threads: lectura entrada, aplicar FFT y mostrar resultado

		LOG.info("main END");
        return;
}



/*
 *	version Bloque:
 *	  1 threads:
 *		recoge micro, ejecuta FFT y pinta resultado
 *  cs tamaño de la clase Complex
 *  bs tamaño del bloque múltiplo de 2 para FFT
 *  nb numero de bloques
 */
    static void vBlock ( final int cs, final int bs, final int nb, final int fm)
        throws IOException, InterruptedException {
        LOG.info("vBlock BEGIN");

		/*Thread for writing data from MIC to pipe*/
		Thread threadMicFFTRes =new Thread(new Runnable() {
			@Override
			public void run() {
				runMicFFTRes (bs, fm) ;
			}
		});


		/*Start thread*/
		threadMicFFTRes.start();

// bucle esperando 'q' - quit
        int c;
        do {
//            Scanner sc = new Scanner(System.in);
            c = System.in.read();
//            c = (int)sc.next().charAt(0);
            LOG.debug ("main QUIT");
        } while ( (char)c != 'q') ;
        FIN = true;

//        threadMicFFTRes.stop();

		/*Join Thread*/
		threadMicFFTRes.join();

        LOG.info("vBlock END");
    }


	private static void runMicFFTRes ( int size, int rate )
	{
		LOG.info("runMIC BEGIN");

// buffer circular inicializado a 0
		Complex[] prv = new Complex[size];
		for (int i=0; i<size ; i++)
		    prv[i] = new Complex(0,0);

		Complex[] buf = new Complex[size];

		int iprv=0, ib=0, nFFT=0, nMIC=0;
        while (!FIN) {
// lectura micro
			byte[] data = mic.read();
            nMIC++;
            int ld = data.length;
			LOG.debug("runMicFFTRes mic.read  #" + nMIC + " - " + ld + " bytes");

// añadir a buffer circular
// TODO: soporte 1, 3, 4 bytes por muestra, LITTLE_ENDIAN...
            for (int i=0; i<ld; i+=2) {
                ByteBuffer bb = ByteBuffer.wrap(data,i,2);
                int r= (int)bb.getShort();
                prv[iprv].setReal((double)r);
                prv[iprv].setImag((double)0);
                iprv++;
                if (iprv>=size) iprv=0;
            }

// copiar buffer circular en entrada desde última pocisión
            for (int i=0, j=iprv; i<size; i++, j++) {
                if (j>=size) j=0;
                buf[i] = prv[j];
            }

            Complex[] out = FFT.fft(buf);
            nFFT++;
            int lr = out.length;
            ib=0;
			LOG.debug("runMicFFTRes FFT #" + nFFT + " - " + lr + " items");

            res.show3(out, rate);

            monitor();

        }

		LOG.info("runMicFFTRes END");
		return ;
	}


    static void monitor () {
        Runtime runtime = Runtime.getRuntime();
        long maxMemory = runtime.maxMemory();
        long allocatedMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();
        long totalFreeMemory = freeMemory + (maxMemory - allocatedMemory);
        System.out.println("monitor");
        System.out.println("maxMemory  = "+maxMemory/1024);
        System.out.println("allocated  = "+allocatedMemory/1024);
        System.out.println("freeMemory = "+freeMemory);
        System.out.println("totalFreeM = "+totalFreeMemory);
        System.out.println("-------");
    }

}
