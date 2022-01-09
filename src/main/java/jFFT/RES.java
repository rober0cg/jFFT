package jFFT;


public class RES {

// constantes
    static final double freqA = 442.0;
    static final int ESCALAS = 9;
    static final int NOTAS_ESCALA = 12;
    static final String l1="AABCCDDEFFGG";
    static final String l2=" #  # #  # #";
    static final String l3="ABBCDDEEFGGA";
    static final String l4=" b  b b  b b";
    static final String lx[]= {
        "AABCCDDEFFGG",
        " #  # #  # #",
        "ABBCDDEEFGGA",
        " b  b b  b b"
    };


// trabajo
    static double fA; // LA 440, 442, 432...
// escala en la frecuencia de LA: valores y etiquetas
    static final double[] fx = new double[NOTAS_ESCALA];
    static double fVi; // 50% del ratio de nota a nota por debajo
    static double fVs; // 50% del ratio de nota a nota por encima
    static final String[] sx = new String[NOTAS_ESCALA];
// las n escalas, con frecuencia con márgenes inferior y superior, y etiquetas
    static final double[] fy = new double[NOTAS_ESCALA*ESCALAS];
    static final double[] fyi = new double[NOTAS_ESCALA*ESCALAS];
    static final double[] fys = new double[NOTAS_ESCALA*ESCALAS];
    static final String[] sy = new String[NOTAS_ESCALA*ESCALAS];

    static int lr;
    static double factorAmpl;
    static double factorFreq;
    static double max;




    static void init () {

// La a 440
        fA=freqA;

// margenes para localizar nota
        fVi = Math.pow(2.0, (double)(-0.5/((double)NOTAS_ESCALA)));
        fVs = Math.pow(2.0, (double)(+0.5/((double)NOTAS_ESCALA)));

// 1 escala
        for (int x=0; x<NOTAS_ESCALA; x++){
            fx[x] = fA * Math.pow(2.0, (double)x/((double)NOTAS_ESCALA));
        }

        sx[ 0]="A";
        sx[ 1]="A#/Bb";
        sx[ 2]="B";
        sx[ 3]="C";
        sx[ 4]="C#/Db";
        sx[ 5]="D";
        sx[ 6]="D#/Eb";
        sx[ 7]="E";
        sx[ 8]="F";
        sx[ 9]="F#/Gb";
        sx[10]="G";
        sx[11]="G#/Ab";


// n escalas
        for ( int x=0; x<ESCALAS; x++) {
            double rx = Math.pow(2.0, (double)(x-4));
            for (int y=0; y<NOTAS_ESCALA; y++) {
                int j=(NOTAS_ESCALA*x)+y;
                fy[j] = fx[y] * rx;
                sy[j] = sx[y] + " oct"+x;

                fyi[j] = fy[j] * fVi;
                fys[j] = fy[j] * fVs;

                System.out.println(
                    String.format(
                        "fy[%02d] = %7.2f - %-10s   ( %7.2f < %7.2f )",
                        j, fy[j], sy[j], fyi[j], fys[j]
                    )
                );
            }
        }


    }





    private static void analizaEntrada ( Complex[] r, int rate) {
		LOG.info("analizaEntrada BEGIN");

        lr=r.length;
		LOG.debug("analizaEntrada r.length #" + lr + " items");
		LOG.debug("analizaEntrada rate      " + rate + " Hz");

        factorAmpl = 2.0 / lr ;
        factorFreq = (double)rate / (double)lr ;
        LOG.debug("analizaEntrada factorAmpl = "+factorAmpl);
        LOG.debug("analizaEntrada factorFreq = "+factorFreq);

        double xmax=0.0;
        int imax=0;
        for (int i=0 ; i<lr/2 ; i++) {
            double amp= r[i].amplitude() ;
            if ( amp > xmax) {
                xmax = amp;
                imax = i;
            }
        }
        max = xmax;
        LOG.debug("analizaEntrada maximo["+imax+"]="+xmax*factorAmpl);

		LOG.info("analizaEntrada END");
    }


/*
 * show1
 * Muestra buffer resultado en crudo
 *   filtrando >50% amplitud máxima
 *   traduciendo posición a frecuencia
 *
 */
	public static void show1( Complex[] r, int rate) {
		LOG.info("show BEGIN");

        analizaEntrada(r, rate);

        System.out.println("-------------------");
        for (int i=0 ; i<lr/2 ; i++) {
            double amp = r[i].amplitude();
            double pha = r[i].phase();
            if ( amp > (max*0.5) ) {
                System.out.println(
                    "y[" + i + "]=" +
                        String.format ( "%+5.2f amp  %+5.2f Hz  %+5.0f gr",
                            amp * factorAmpl,
                            (double)i * factorFreq,
                            pha / (Math.PI/180.0)
                        )
                );
            }
        }
//        System.out.println("-------------------");
		LOG.info("show END");
		return ;
	}


/*
 * show2
 * Muestra nota y octava
 *   filtrando >50% amplitud máxima
 *
 */
	public static void show2( Complex[] r, int rate) {
		LOG.info("show BEGIN");

        analizaEntrada(r, rate);

        System.out.println("-------------------");
        for (int i=0 ; i<lr/2 ; i++) {
            double amp = r[i].amplitude();
            if ( amp > (max*0.5) ) {
//                double pha = r[i].phase() ;
                double freq = ((double)i) * factorFreq;
                String nota = "";
                for (int y=0; y<(ESCALAS*NOTAS_ESCALA); y++) {
                    if ( (fyi[y] <= freq) && (freq < fys[y]) ) {
                        nota = sy[y];
                    }
                }

                System.out.println(
                    "y[" + i + "]=" +
                        String.format ( "%+5.2f amp  %+5.2f Hz  %s",
                            amp * factorAmpl,
                            freq,
                            nota
                        )
                ) ;
            }
        }
//        System.out.println("-------------------");
		LOG.info("show END");
		return ;
    }

/*
 * show3
 * Muestra gráfica modo texto
 *   base log2
 *   amplitud lineal 0..32k -> 0..9
 *
 */
	public static void show3( Complex[] r, int rate) {
		LOG.info("show BEGIN");

        analizaEntrada(r, rate);

// acumulados de amplitud por rango de frecuencia
        double flt, fgt;
        double[] fr = new double[NOTAS_ESCALA*ESCALAS];
        flt=0.0;
        for (int y=0; y<fr.length; y++) {
            fr[y]=0.0;
        }
        fgt=0.0;


        System.out.print("\033[H\033[2J");
        System.out.flush();
        System.out.println("-------------------");

// acumulador por nota
// TODO: optimizable
// TODO: apoyándonos en orden de las frecuencias en el buffer de entrada
// TODO: y así evitar el producto cartesiano!!
        for (int i=0 ; i<lr/2 ; i++) {
            double amp = r[i].amplitude() ;
            if ( amp > (max*0.0) ) {
                double freq = ((double)i) * factorFreq ;
                amp *= factorAmpl;
                if ( freq <= fyi[0] )
                    flt += amp;
                if ( fys[fr.length-1] < freq )
                    fgt += amp;
                for (int y=0; y<fr.length; y++) {
                    if ( (fyi[y] <= freq) && (freq < fys[y]) ) {
                        fr[y] += amp;
                        break;
                    }
                }
             }
        }

//        System.out.println("fr[lt] = " + flt) ;
//        for (int y=0; y<fr.length; y++) {
//            System.out.println("fr[" + y + "] = " + fr[y]) ;
//        }
//        System.out.println("fr[lt] = " + fgt) ;



        for(int l=10; l>=0; l--) {
            char[] fc = new char[(NOTAS_ESCALA*ESCALAS)+2];
            double ref=(double)(l*10000);

            fc[0]=value(flt,ref);
            for (int i=0; i<(NOTAS_ESCALA*ESCALAS); i++)
                fc[i+1] = value(fr[i],ref);
            fc[(NOTAS_ESCALA*ESCALAS)+1]=value(fgt,ref);

            System.out.println(fc);

        }


        for (int i=0; i<lx.length; i++) {
            System.out.print('<');
            for (int k=0; k<ESCALAS; k++) System.out.print(lx[i]);
            System.out.println('>');
        }

		LOG.info("show END");
		return ;
    }


    static char value (double a, double ref) {
        char c=' ';
        if ( a > ref ) {
            if      ( a > 100000) c='H';
            else if ( a >  90000) c='9';
            else if ( a >  80000) c='8';
            else if ( a >  70000) c='7';
            else if ( a >  60000) c='6';
            else if ( a >  50000) c='5';
            else if ( a >  40000) c='4';
            else if ( a >  30000) c='3';
            else if ( a >  20000) c='2';
            else if ( a >  10000) c='1';
            else if ( a >   8000) c='0';
            else                  c=' ';
        }
        else c=' ';
        return c;
    }

}


