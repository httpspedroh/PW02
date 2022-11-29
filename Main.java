import java.io.RandomAccessFile;
import java.util.Scanner;
import java.util.HashMap;

// ---------------------------------------------------------------------------------------------------------------- //

public class Main {

    static int globalId;
    static final String DEFAULT_FILE = "accounts.bin";
    static final String DEFAULT_FILE_HASH = "hash.bin";
    static final String DEFAULT_FILE_CITY = "indexByCity.bin";
    static final String DEFAULT_FILE_NAME = "indexByName.bin";
    static final String DEFAULT_FILE_BTREE = "btree.bin";

    public static HashMap<Character, Integer> makeFrequency(String filename) {
        var frequency = new HashMap<Character, Integer>();
        try {
            RandomAccessFile raf = new RandomAccessFile(DEFAULT_FILE, "rw");
            while (raf.getFilePointer() < raf.length()) {
                char c = (char) raf.readByte();
                frequency.merge(c, 1, Integer::sum);
            }
            raf.seek(0);
            raf.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return frequency;
    }

    public static void main(String[] args) throws Exception {

        RandomAccessFile raf = new RandomAccessFile(DEFAULT_FILE, "rw");

        // Carga inicial
        if (raf.length() == 0) {

            raf.writeInt(0);

            Inverted.create();
            Hash.create(4);
            BPlusTree.create(5);
        }

        raf.seek(0);
        globalId = raf.readInt();
        raf.close();

        // ----------------------------------------------------------- //

        Scanner scr = new Scanner(System.in);

        int option = -1;

        // ----------------------------------------------------------- //

        do {

            System.out.println("============= Menu =============");
            System.out.println("0 - SAIR");
            System.out.println("1 - CRIAR CONTA");
            System.out.println("2 - REALIZAR TRANSFERENCIA");
            System.out.println("3 - LER REGISTRO");
            System.out.println("4 - ATUALIZAR REGISTRO");
            System.out.println("5 - DELETAR REGISTRO");
            System.out.println("6 - ORDENAR REGISTROS");
            System.out.println("7 - LISTAR REGISTROS");
            System.out.println("8 - LZW");
            System.out.println("9 - HUFFMAN");
            System.out.println("================================\n");

            // ----------------------------------------------------------- //

            // Option verification
            do {

                try {

                    option = scr.nextInt();

                    if (option < 0 || option > 9)
                        System.out.println("x Opcao invalida!");
                } catch (Exception e) {

                    System.out.println("x Digite apenas numeros!");

                    scr.next();
                    break;
                }
            } while (option < 0 || option > 9);

            // Option execution
            switch (option) {

                case 0:
                    System.out.println("Saindo...");
                    break;

                // ----------------------------------------------------------- //

                case 1: {

                    BankAccount ba = new BankAccount();

                    System.out.println("\n========== CRIAR CONTA ==========");

                    System.out.print("> Digite o nome: ");
                    ba.setName(scr.next());

                    System.out.print("> Digite o CPF: ");
                    ba.setCpf(scr.next());

                    System.out.print("> Digite a cidade: ");
                    ba.setCity(scr.next());

                    System.out.print("> Digite o usuario: ");
                    ba.setUser(scr.next());

                    System.out.print("> Digite a senha: ");
                    ba.setPass(scr.next());

                    System.out.print("> Digite seu email: ");
                    ba.addEmail(scr.next());

                    boolean wishAdd = true;

                    do {

                        System.out.print("> Deseja adicionar mais emails? (s/n): ");
                        String answer = scr.next();

                        if (answer.equals("s")) {

                            System.out.print("> Digite seu email: ");
                            ba.addEmail(scr.next());
                        } else
                            wishAdd = false;
                    } while (wishAdd == true);

                    System.out.print("> Digite o saldo: ");
                    ba.setBalance(scr.nextFloat());

                    System.out.println("\n>>> Conta criada com sucesso!");
                    System.out.println("==================================\n");

                    Crud.create(ba);
                    break;
                }

                // ----------------------------------------------------------- //

                case 2: {

                    System.out.println("\n========== REALIZAR TRANSFERENCIA ==========");

                    System.out.print("> Digite o usuário da conta de origem: ");
                    String origin = scr.next();

                    System.out.print("> Digite o usuário da conta de destino: ");
                    String destiny = scr.next();

                    System.out.print("> Digite o valor da transferencia: ");
                    float value = scr.nextFloat();

                    BankAccount ba_origin = Crud.searchByUser(origin);
                    BankAccount ba_destiny = Crud.searchByUser(destiny);

                    if (ba_origin == null)
                        System.out.println("x Conta de origem nao encontrada!");
                    else if (ba_destiny == null)
                        System.out.println("x Conta de destino nao encontrada!");
                    else if (ba_origin.getBalance() < value)
                        System.out.println("x Saldo insuficiente!");
                    else {

                        ba_origin.setBalance(ba_origin.getBalance() - value);
                        ba_destiny.setBalance(ba_destiny.getBalance() + value);

                        ba_origin.setTransfers(ba_origin.getTransfers() + 1);
                        ba_destiny.setTransfers(ba_destiny.getTransfers() + 1);

                        Crud.update(ba_origin);
                        Crud.update(ba_destiny);

                        System.out.println("\n>>> Transferencia realizada com sucesso!");
                    }
                    break;
                }

                // ----------------------------------------------------------- //

                case 3: {

                    System.out.println("\n========== LER REGISTRO ==========");

                    System.out.print("> Digite o ID da conta desejada: ");
                    int id = scr.nextInt();

                    BankAccount ba = Crud.searchById(DEFAULT_FILE, id);

                    if (ba == null)
                        System.out.println("x Conta nao encontrada!");
                    else {

                        System.out.println("\n>>> Conta encontrada!");
                        System.out.println("ID: " + ba.getId());
                        System.out.println("Nome: " + ba.getName());
                        System.out.println("CPF: " + ba.getCpf());
                        System.out.println("Cidade: " + ba.getCity());
                        System.out.println("Usuario: " + ba.getUser());
                        System.out.println("Senha: " + ba.getPass());
                        System.out.println("Saldo: " + ba.getBalance());
                        System.out.println("Emails: " + ba.getEmails());
                        System.out.println("Transferencias: " + ba.getTransfers());
                        System.out.println("==================================\n");

                    }
                    break;
                }

                // ----------------------------------------------------------- //

                case 4: {

                    System.out.println("\n========== ATUALIZAR REGISTRO ==========");

                    System.out.print("> Digite o usuário da conta desejada: ");
                    String user = scr.next();

                    BankAccount ba = Crud.searchByUser(user);

                    if (ba == null)
                        System.out.println("x Conta nao encontrada!");
                    else {

                        System.out.println("\n>>> Conta encontrada!");
                        System.out.println("ID: " + ba.getId());
                        System.out.println("Nome: " + ba.getName());
                        System.out.println("CPF: " + ba.getCpf());
                        System.out.println("Cidade: " + ba.getCity());
                        System.out.println("Usuario: " + ba.getUser());
                        System.out.println("Senha: " + ba.getPass());
                        System.out.println("Saldo: " + ba.getBalance());
                        System.out.println("Emails: " + ba.getEmails());
                        System.out.println("Transferencias: " + ba.getTransfers());
                        System.out.println("==================================\n");

                        System.out.println("========== ATUALIZAR ==========");
                        System.out.println("0 - VOLTAR");
                        System.out.println("1 - NOME");
                        System.out.println("2 - CPF");
                        System.out.println("3 - CIDADE");
                        System.out.println("4 - USUARIO");
                        System.out.println("5 - SENHA");
                        System.out.println("6 - EMAIL");
                        System.out.println("7 - SALDO");
                        System.out.println("================================\n");

                        int updateOption = 0;

                        do {

                            try {

                                updateOption = scr.nextInt();

                                if (updateOption < 0 || updateOption > 7)
                                    System.out.println("x Opcao invalida!");
                            } catch (Exception e) {

                                System.out.println("x Digite apenas numeros!");

                                scr.next();
                            }
                        } while (updateOption < 0 || updateOption > 7);

                        switch (updateOption) {

                            case 0:
                                break;

                            case 1: {

                                System.out.print("> Digite o novo nome: ");
                                ba.setName(scr.next());
                                break;
                            }

                            case 2: {

                                System.out.print("> Digite o novo CPF: ");
                                ba.setCpf(scr.next());
                                break;
                            }

                            case 3: {

                                System.out.print("> Digite a nova cidade: ");
                                ba.setCity(scr.next());
                                break;
                            }

                            case 4: {

                                System.out.print("> Digite o novo usuario: ");
                                ba.setUser(scr.next());
                            }

                            case 5: {

                                System.out.print("> Digite a nova senha: ");
                                ba.setPass(scr.next());
                                break;
                            }

                            case 6: {

                                System.out.print("> Digite o novo email: ");

                                ba.emails.clear();
                                ba.addEmail(scr.next());

                                System.out.print("> Deseja adicionar mais um email? (s/n): ");
                                String answer = scr.next();

                                while (answer.equals("s")) {

                                    System.out.print("> Digite o novo email: ");
                                    ba.addEmail(scr.next());

                                    System.out.print("> Deseja adicionar mais um email? (s/n): ");
                                    answer = scr.next();
                                }
                                break;
                            }
                        }

                        if (updateOption != 0) {

                            Crud.update(ba);

                            System.out.println("\n>>> Conta atualizada com sucesso!");
                            break;
                        }
                    }
                    break;
                }

                // ----------------------------------------------------------- //

                case 5: {

                    System.out.println("\n========== DELETAR REGISTRO ==========");

                    System.out.print("> Digite o ID da conta desejada: ");
                    int id = scr.nextInt();

                    BankAccount ba = Crud.searchById(DEFAULT_FILE, id);

                    if (ba == null)
                        System.out.println("x Conta nao encontrada!");
                    else {

                        System.out.println("\n>>> Conta encontrada!");
                        System.out.println("ID: " + ba.getId());
                        System.out.println("Nome: " + ba.getName());
                        System.out.println("CPF: " + ba.getCpf());
                        System.out.println("Cidade: " + ba.getCity());
                        System.out.println("Usuario: " + ba.getUser());
                        System.out.println("Senha: " + ba.getPass());
                        System.out.println("Saldo: " + ba.getBalance());
                        System.out.println("Emails: " + ba.getEmails());
                        System.out.println("Transferencias: " + ba.getTransfers());
                        System.out.println("==================================\n");

                        System.out.print("> Deseja realmente deletar esta conta? (s/n): ");
                        String answer = scr.next();

                        if (answer.equals("s")) {

                            Crud.delete(ba);

                            System.out.println("\n>>> Conta ID " + ba.getId() + " deletada com sucesso!");
                        }
                    }
                    break;
                }

                // ----------------------------------------------------------- //

                case 6: {

                    System.out.println("\n========== ORDENAR ==========");

                    System.out.println("0 - VOLTAR");
                    System.out.println("1 - ID");
                    System.out.println("2 - NOME");
                    System.out.println("3 - CPF");
                    System.out.println("4 - CIDADE");
                    System.out.println("5 - USUARIO");
                    System.out.println("6 - SALDO");
                    System.out.println("================================\n");

                    int updateOption = 0;

                    do {

                        try {

                            updateOption = scr.nextInt();

                            if (updateOption < 0 || updateOption > 6)
                                System.out.println("x Opcao invalida!");
                        } catch (Exception e) {

                            System.out.println("x Digite apenas numeros!");

                            scr.next();
                        }
                    } while (updateOption < 0 || updateOption > 6);

                    System.out.println("\n========== ORDEM DE REGISTROS ==========");
                    System.out.println("> Digite a quantidade M de registros: ");

                    int m = 0;

                    do {

                        try {

                            m = scr.nextInt();

                            if (m < 0 || m > 10)
                                System.out.println("x Insira no minimo 0 e no maximo 10!");
                        } catch (Exception e) {

                            System.out.println("x Digite apenas numeros!");

                            scr.next();
                        }
                    } while (m < 0 || m > 10);

                    System.out.println("> Digite a quantidade N de caminhos: ");

                    int n = 0;

                    do {

                        try {

                            n = scr.nextInt();

                            if (n < 0 || n > 10)
                                System.out.println("x Insira no minimo 0 e no maximo 10!");
                        } catch (Exception e) {

                            System.out.println("x Digite apenas numeros!");

                            scr.next();
                        }
                    } while (n < 0 || n > 10);

                    switch (updateOption) {

                        case 0:
                            break;

                        case 1: {

                            Order.byId(m, n);
                            System.out.println("\n>>> Contas ordenadas por ID com sucesso!");
                            break;
                        }

                        case 2: {

                            Order.byName(m, n);
                            System.out.println("\n>>> Contas ordenadas por nome com sucesso!");
                            break;
                        }

                        case 3: {

                            Order.byCpf(m, n);
                            System.out.println("\n>>> Contas ordenadas por CPF com sucesso!");
                            break;
                        }

                        case 4: {

                            Order.byCity(m, n);
                            System.out.println("\n>>> Contas ordenadas por cidade com sucesso!");
                            break;
                        }

                        case 5: {

                            Order.byUser(m, n);
                            System.out.println("\n>>> Contas ordenadas por usuario com sucesso!");
                            break;
                        }

                        case 6: {

                            Order.byBalance(m, n);
                            System.out.println("\n>>> Contas ordenadas por saldo com sucesso!");
                            break;
                        }
                    }
                    break;
                }

                // ----------------------------------------------------------- //

                case 7: {

                    System.out.println("\n========== LISTAR REGISTROS ==========");

                    for (BankAccount ba : Order.readAll(DEFAULT_FILE)) {

                        System.out.println("ID: " + ba.getId());
                        System.out.println("Nome: " + ba.getName());
                        System.out.println("CPF: " + ba.getCpf());
                        System.out.println("Cidade: " + ba.getCity());
                        System.out.println("Usuario: " + ba.getUser());
                        System.out.println("Senha: " + ba.getPass());
                        System.out.println("Saldo: " + ba.getBalance());
                        System.out.println("Emails: " + ba.getEmails());
                        System.out.println("Transferencias: " + ba.getTransfers());
                        System.out.println("==================================\n");
                    }
                    break;
                }

                // ----------------------------------------------------------- //

                case 8: {

                    System.out.println("\n========== LZW ==========");

                    System.out.println("0 - VOLTAR");
                    System.out.println("1 - COMPACTAR");
                    System.out.println("2 - DESCOMPACTAR");

                    int lzwOption = 0;

                    do {

                        try {

                            lzwOption = scr.nextInt();

                            if (lzwOption < 0 || lzwOption > 2)
                                System.out.println("x Opcao invalida!");
                        } catch (Exception e) {

                            System.out.println("x Digite apenas numeros!");

                            scr.next();
                        }
                    } while (lzwOption < 0 || lzwOption > 2);

                    // -------------------------- //

                    switch (lzwOption) {

                        case 0:
                            break;

                        case 1: {

                            System.out.println("\n========== COMPACTAR ==========");
                            System.out.println("> Digite o nome do arquivo de saída: ");
                            String fileName = scr.next();

                            LZW.compress(Main.DEFAULT_FILE, fileName);

                            System.out.println("\n>>> Arquivo compactado com sucesso em \"" + fileName + "\"!");
                            break;
                        }

                        case 2: {

                            System.out.println("\n========== DESCOMPACTAR ==========");
                            System.out.println("> Digite o nome do arquivo compactado: ");
                            String fileName = scr.next();

                            LZW.decompress(fileName, Main.DEFAULT_FILE);

                            System.out.println(
                                    "\n>>> Arquivo descompactado com sucesso em \"" + Main.DEFAULT_FILE + "\"!");
                            break;
                        }
                    }
                }
                case 9:{
                    System.out.println("\n======= HUFFMAN ========="); 
                    var frequency = makeFrequency(DEFAULT_FILE);
                    var tree = new Huffman(frequency);
                    tree.traverse(Huffman.root, "");
                    try {
                        RandomAccessFile source = new RandomAccessFile(DEFAULT_FILE, "rw");
                        RandomAccessFile dest = new RandomAccessFile("h_compressed.bin", "rw");
                        RandomAccessFile desc = new RandomAccessFile("h_descompressed.bin", "rw");
                        Huffman.compress(source, dest);
                        Huffman.decompress(dest, desc);
            
                        source.seek(0);
                        dest.seek(0);
                        source.close();
                        dest.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    System.out.println("Arquivos h_compressed.bin, h_descompressed.bin criados");
                    System.out.println("Feito!");
                }
            }
        } while (option != 0);

        // ----------------------------------------------------------- //

        scr.close();
    }
}