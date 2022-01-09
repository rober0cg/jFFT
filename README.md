# jFFT
Ejemplo aplicando la FFT (Transformada Rápida de Fourier) a la entrada online de Audio y con salida a terminal representando espectro por nota musical.

Partiendo de la implementación de la FFT del departamento "Computer Science" de la Universidad de Princeton (clases FFT y Complex, sólo modificadas con ligeros retoques para entendimiento u optimizar).


Compilación y ejecución:
- mvn package
- ./cpbin: copia el jar a bin
- ./run: ejecuta el jar y con opciones de la jvm para que funcione el cálculo del ObjectSize(Complex)... aunque en la versión final no se utiliza :(


Descripción de las principales clases:

MIC.java
Recoge entrada de audio con AudioInputStream + TargetDataLine.
Parámetros en AppProperties.java.
  RATE = 44100.0f;
  CHANNELS = 1;
  SAMPLE_SIZE = 16;
  BIG_ENDIAN = true;


FFT.java
Como entrada, array de Complex con las muestras de audio. Tiene que ser potencia de 2 (2^n).
Como salida, array de Complex que producen amplitud y fase. La frecuencia se obtiene de la posición del array.
Parámetros en AppProperties.java.
  BUFFER_SIZE = 32 * 1024;
El tamaño del buffer va asociado a precisión en la frecuencia y a consumos de cpu y memoria.


RES.java
Métodos para presentar el resultado de FFT.
Con la frecuencia de muestreo y el número de muestras se calcula el factor de frecuencia (lineal con la posición del array) y el factor de amplitud (distribución de la densidad de energía por muestra).
Por ejemplo, para un Frecuencia de muestredo de 44,1KHz, con un buffer_size de 16k:
  factorAmpl = 1.220703125e-4
  factorFreq = 2.691650390625 (Hz/posición del array)
Y con buffer de 32K
  factorAmpl = 6.103515625E-5
  factorFreq = 1.3458251953125


App.java
Programa principal que hilvana las principales clases: MIC, FFT y RES.

TODO:
- optimizaciones en RES::show3
- lectura de parámetros por fichero
- para nota... representación gráfica de señal de entrada y espectro en frecuencia!!
