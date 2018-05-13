package multicast;

import multicast.converter.MessageConverter;
import multicast.message.IntroduceResponce;
import multicast.network.UdpPacket;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class IntroduceService extends Thread {
    MessageConverter messageConverter = new MessageConverter();
    private String addr;

    @Override
    public void run() {
        try {
            addr = InetAddress.getLocalHost().getHostAddress();
            System.out.println(addr);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        IntroduceResponce introduceResponce = new IntroduceResponce();
        introduceResponce.setIp(addr);
        introduceResponce.setPort(8080);


        UdpPacket udpPacket = messageConverter.serialize(introduceResponce);

        System.out.println(udpPacket);

        //1. Pobierz lokalny adres IP
        //2. Pobierz port
        //3. Utwórz pakiet {"ip": xxxx, "port":9999}
        //4. Przekonwertuj powyższy string na byte[]
        //5. Wyślij byte[] na adres multicast
        //6. Uśpij wątek na 10s
    }
}
