package side;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.concurrent.LinkedBlockingQueue;

public class QueueInfo implements Runnable {

    private final Socket sensor;
    private LinkedBlockingQueue<Message> incomingMessage;
    OutputStream outToSensor;
    byte[] toSensor=new byte[1024];


    public QueueInfo(Socket sensor, LinkedBlockingQueue<Message> incomingMessage) {
        this.sensor = sensor;
        this.incomingMessage = incomingMessage;

    }


    @Override
    public void run() {

        while (true) {
            /*
                Burada gereksiz loopa sokmamam lazım
                wait-notify is a solution
             */

            /*
                Şimdi burada göndereceğim queue size'ı zaten direkt queuedan reference
                alıp yollicam.
                Bir tane queue'nun priority gibi basit bir class yapsam


             */
            try {
                // Bu hiç güzel bir busy waitingten kaçış değil ama iş görür şimdilik
                // Şuan saniyede bir gönderiyor.
                Thread.sleep(500);
                sendToClient();
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    private boolean calculateDifference(int current, int prev) {
        double percentageDiff;
        percentageDiff = ((double) Math.abs(current - prev) / (double)((current + prev) / 2)) * 100;
        return Double.parseDouble(new DecimalFormat("##.###").format(percentageDiff).replace(',', '.'))>3;
    }

    private void sendToClient() throws IOException {
        //Sensore göndermesi lazım queue bilgisi
        //toSensor = ByteBuffer.allocate(4).putInt(incomingMessage.size()).array();
        System.out.println("Queue yoğunluğunu gönderdim --> "+incomingMessage.size());
        outToSensor = sensor.getOutputStream();
        String message=""+incomingMessage.size();
        System.out.println(message);
        toSensor=message.getBytes();
        System.out.println("Kaç byte yolluyorum  "+toSensor.length);
        outToSensor.write(toSensor);
        //sensor.close();
    }
}
