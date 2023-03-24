package Server;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.io.File;
import java.util.ArrayList;

public class Servidor {

    public static void main(String[] args) {
        int porta = 5000;
        String senha = "12345";
        Servidor servidor = new Servidor(porta, senha);
    }
    private DatagramSocket servidor;
    private ArrayList<String> arquivos;
    private String senha;

    private final int MAX_SIZE = 1460;

    public Servidor(int porta, String senha) {
        this.senha = senha;
        arquivos = new ArrayList<String>();

        // Cria o servidor na porta especificada
        try {
            servidor = new DatagramSocket(porta);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Espera por conexões e processa as mensagens recebidas
        while (true) {
            try {
                byte[] buffer = new byte[MAX_SIZE];
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                servidor.receive(packet);
                String mensagem = new String(packet.getData(), 0, packet.getLength());

                if (mensagem.equals(senha)) {
                    // Cliente autenticado, recebe arquivo
                    receberArquivo(packet.getAddress(), packet.getPort());
                } else {
                    // Cliente solicita lista de arquivos
                    enviarListaArquivos(packet.getAddress(), packet.getPort());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void enviarListaArquivos(InetAddress endereco, int porta) throws IOException {
        // Cria lista de arquivos disponíveis
        File diretorio = new File(".");
        File[] listaArquivos = diretorio.listFiles();
        arquivos.clear();
        for (File arquivo : listaArquivos) {
            if (arquivo.isFile()) {
                arquivos.add(arquivo.getName());
            }
        }

        // Envia lista de arquivos para o cliente
        String mensagem = String.join("\n", arquivos);
        byte[] buffer = mensagem.getBytes();
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, endereco, porta);
        servidor.send(packet);
    }

    private void receberArquivo(InetAddress endereco, int porta) throws IOException {
        // Recebe arquivo do cliente
        byte[] buffer = new byte[MAX_SIZE];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        servidor.receive(packet);

        // Salva o arquivo no servidor
        String nomeArquivo = new String(packet.getData(), 0, packet.getLength());
        File arquivo = new File(nomeArquivo);
        FileOutputStream fos = new FileOutputStream(arquivo);
        while (true) {
            servidor.receive(packet);
            if (packet.getLength() == 0) {
                break;
            }
            fos.write(packet.getData(), 0, packet.getLength());
        }
        fos.close();
    }
}

