import java.util.ArrayList;
import java.util.List;

public class SlidingWindowProtocol {

    private static final int WINDOW_SIZE = 4; // tamanho da janela
    private static final int TIMEOUT_MS = 1000; // tempo limite em milissegundos

    private int sequenceNumber = 0; // número de sequência inicial
    private int base = 0; // base da janela deslizante
    private List<Integer> sentPackets = new ArrayList<>(); // lista de números de sequência dos pacotes enviados
    private List<Integer> receivedPackets = new ArrayList<>(); // lista de números de sequência dos pacotes recebidos

    public void sendPacket(Packet packet) {
        if (sentPackets.size() - receivedPackets.size() < WINDOW_SIZE) { // verifica se a janela deslizante tem espaço livre
            packet.setSquenceNumber(sequenceNumber);
            sentPackets.add(sequenceNumber);
            System.out.println("Enviando pacote " + sequenceNumber);
            sequenceNumber++;
        } else {
            System.out.println("Janela cheia, não é possível enviar pacote");
        }
    }

    public void receivePacket(Packet packet) {
        if (packet.getSequenceNumber() >= base && packet.getSequenceNumber() < base + WINDOW_SIZE) { // verifica se o pacote está dentro da janela
            System.out.println("Recebido pacote " + packet.getSequenceNumber());
            if (!receivedPackets.contains(packet.getSequenceNumber())) { // verifica se o pacote ainda não foi recebido antes
                receivedPackets.add(packet.getSequenceNumber());
            }
            // avança a base da janela para o próximo pacote não confirmado
            while (receivedPackets.contains(base)) {
                receivedPackets.remove(Integer.valueOf(base));
                sentPackets.remove(Integer.valueOf(base));
                base++;
            }
        } else {
            System.out.println("Pacote descartado");
        }
    }

    public void startTimer() {
        new Thread(() -> {
            try {
                Thread.sleep(TIMEOUT_MS);
                // reenvia os pacotes não confirmados
                for (int i = base; i < sequenceNumber; i++) {
                    if (!receivedPackets.contains(i)) {
                        System.out.println("Timeout, reenviando pacote " + i);
                        // aqui seria chamado o método de reenvio de pacote
                    }
                }
            } catch (InterruptedException e) {
                // ignorar
            }
        }).start();
    }

}
