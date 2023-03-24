package User;
import Server.Servidor;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.io.File;
import java.io.FileOutputStream;
import java.net.UnknownHostException;
import java.util.ArrayList;
public class Usuario {
    public static void main(String[] args) throws UnknownHostException {
        InetAddress enderecoServidor = InetAddress.getByName("localhost");
        int portaServidor = 12345;

        Usuario cliente = new Usuario(enderecoServidor, portaServidor);
        cliente.listarArquivos();
        cliente.baixarArquivo("arquivo.txt");
    }
    private DatagramSocket cliente;
    private ArrayList<String> arquivos;
    private InetAddress enderecoServidor;
    private int portaServidor;

        private final int MAX_SIZE = 1460;

        public Usuario(InetAddress enderecoServidor, int portaServidor) {
            arquivos = new ArrayList<String>();
            this.enderecoServidor = enderecoServidor;
            this.portaServidor = portaServidor;

            // Cria o socket do cliente
            try {
                cliente = new DatagramSocket();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void listarArquivos() {
            // Envia mensagem de solicitação de lista de arquivos ao servidor
            String mensagem = "listar";
            byte[] buffer = mensagem.getBytes();
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, enderecoServidor, portaServidor);
            try {
                cliente.send(packet);

                // Recebe lista de arquivos do servidor
                buffer = new byte[MAX_SIZE];
                packet = new DatagramPacket(buffer, buffer.length);
                cliente.receive(packet);
                mensagem = new String(packet.getData(), 0, packet.getLength());
                arquivos.clear();
                for (String arquivo : mensagem.split("\n")) {
                    arquivos.add(arquivo);
                }

                // Imprime lista de arquivos na tela
                System.out.println("Arquivos disponíveis:");
                for (String arquivo : arquivos) {
                    System.out.println(arquivo);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    public void baixarArquivo(String nomeArquivo) {
            // Verifica se o arquivo existe na lista de arquivos disponíveis
            if (!arquivos.contains(nomeArquivo)) {
                System.out.println("Arquivo não encontrado.");
                return;
            }

            // Envia senha para autenticação no servidor
            String senha = "senha123";
            byte[] buffer = senha.getBytes();
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, enderecoServidor, portaServidor);
            try {
                cliente.send(packet);

                // Envia nome do arquivo solicitado ao servidor
                buffer = nomeArquivo.getBytes();
                packet = new DatagramPacket(buffer, buffer.length, enderecoServidor, portaServidor);
                cliente.send(packet);

                // Recebe arquivo do servidor
                buffer = new byte[MAX_SIZE];
                packet = new DatagramPacket(buffer, buffer.length);
                cliente.receive(packet);
                String resposta = new String(packet.getData(), 0, packet.getLength());
                if (resposta.equals("erro")) {
                    System.out.println("Erro ao receber arquivo.");
                    return;
                }
                FileOutputStream fos = new FileOutputStream(new File(nomeArquivo));
                while (true) {
                    cliente.receive(packet);
                    if (packet.getLength() == 0) {
                        break;
                    }
                    fos.write(packet.getData(), 0, packet.getLength());
                }
                fos.close();

                System.out.println("Arquivo " + nomeArquivo + " baixado com sucesso.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

}

