package example;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ChatApp {
    private static final String LOG_FILE_PATH = "logs/application.log";
    private static final int SERVER_PORT = 12345;  // Port for HTTP communication
    private static ChatApp instance;

    private JFrame frame;
    private JPanel sidebar;
    private JPanel chatPanel;
    private JTextArea messageArea;
    private JTextField messageInput;
    private DefaultListModel<String> chatListModel;
    private JList<String> chatList;
    private List<Chat> chats;
    private Chat selectedChat;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            if (!startHttpServer()) {
                log("Another instance is running. Sending parameters.");
                sendParamsToRunningInstance(args);
                return;
            }

            ChatApp app = ChatApp.getInstance();
            String context = args.length >= 1 ? args[0] : "";
            String text = args.length >= 2 ? args[1] : "";
            app.processInitialArguments(context, text);
        });
    }

    public static ChatApp getInstance() {
        if (instance == null) {
            instance = new ChatApp();
        }
        return instance;
    }

    private ChatApp() {
        chats = new ArrayList<>();
        frame = new JFrame("Chat App");
        frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setLayout(new BorderLayout());

        // Load the icon from the resources folder
        ImageIcon icon = new ImageIcon(Objects.requireNonNull(getClass().getResource("/icon.png")));
        frame.setIconImage(icon.getImage());

        initializeSidebar();
        initializeChatPanel();

        frame.add(sidebar, BorderLayout.WEST);
        frame.add(chatPanel, BorderLayout.CENTER);

        frame.setVisible(true);
    }

    private static boolean startHttpServer() {
        try {
            HttpServer server = HttpServer.create(new InetSocketAddress(SERVER_PORT), 0);
            server.createContext("/send", new HttpHandler() {
                @Override
                public void handle(HttpExchange exchange) throws IOException {
                    if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                        InputStream inputStream = exchange.getRequestBody();
                        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                        String requestBody = reader.readLine();
                        reader.close();

                        String[] params = requestBody.split(",", 2);
                        if (params.length >= 2) {
                            String context = params[0];
                            String text = params[1];
                            SwingUtilities.invokeLater(() -> {
                                ChatApp.getInstance().processInitialArguments(context, text);
                            });
                        }

                        exchange.sendResponseHeaders(200, 0);
                        OutputStream os = exchange.getResponseBody();
                        os.close();
                    }
                }
            });
            server.setExecutor(null);
            server.start();
            log("HTTP server started on port " + SERVER_PORT);
            return true;
        } catch (IOException e) {
            log("HTTP server could not be started: " + e.getMessage());
            return false;
        }
    }

    private static void sendParamsToRunningInstance(String[] args) {
        try {
            String context = args.length >= 1 ? args[0] : "";
            String text = args.length >= 2 ? args[1] : "";
            String requestBody = context + "," + text;

            HttpURLConnection connection = (HttpURLConnection) new URL("http://localhost:" + SERVER_PORT + "/send").openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);

            try (OutputStream os = connection.getOutputStream()) {
                os.write(requestBody.getBytes());
                os.flush();
            }

            if (connection.getResponseCode() == 200) {
                log("Parameters sent to the running instance successfully.");
            } else {
                log("Failed to send parameters to the running instance.");
            }
            connection.disconnect();
        } catch (IOException e) {
            log("Error: " + e.getMessage());
        }
    }

    public void processInitialArguments(String context, String text) {
        if (context != null && !context.isEmpty()) {
            Chat existingChat = chats.stream()
                    .filter(chat -> chat.getContexto().equals(context))
                    .findFirst()
                    .orElse(null);

            if (existingChat != null) {
                selectedChat = existingChat;
                chatList.setSelectedValue(existingChat.getName(), true);
            } else {
                Chat newChat = new Chat(context, context);
                chats.add(newChat);
                chatListModel.addElement(newChat.getName());
                selectedChat = newChat;
                chatList.setSelectedIndex(chats.size() - 1);
            }

            messageInput.setText(text);
            updateChatPanel();

            bringWindowToFront();
        }
    }

    private void initializeSidebar() {
        sidebar = new JPanel();
        sidebar.setLayout(new BorderLayout());
        sidebar.setPreferredSize(new Dimension(200, frame.getHeight()));

        chatListModel = new DefaultListModel<>();
        chatList = new JList<>(chatListModel);
        chatList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        // Custom renderer to add tooltips
        chatList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

                if (value != null) {
                    // Set the tooltip text to the full value
                    String text = value.toString();
                    setToolTipText(text);
                }

                return c;
            }
        });
        chatList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedIndex = chatList.getSelectedIndex();
                if (selectedIndex != -1) {
                    selectedChat = chats.get(selectedIndex);
                    updateChatPanel();
                }
            }
        });

        JButton newChatButton = new JButton("New Chat");
        newChatButton.addActionListener(e -> addNewChat());

        JButton deleteChatButton = new JButton("Delete Chat");
        deleteChatButton.addActionListener(e -> deleteSelectedChat());

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(1, 2));
        buttonPanel.add(newChatButton);
        buttonPanel.add(deleteChatButton);

        sidebar.add(new JScrollPane(chatList), BorderLayout.CENTER);
        sidebar.add(buttonPanel, BorderLayout.SOUTH);
    }

    private void initializeChatPanel() {
        chatPanel = new JPanel();
        chatPanel.setLayout(new BorderLayout());

        messageArea = new JTextArea();
        messageArea.setEditable(false);
        messageArea.setBorder(new EmptyBorder(10, 10, 10, 10));

        messageInput = new JTextField();
        messageInput.addActionListener(e -> sendMessage());

        chatPanel.add(new JScrollPane(messageArea), BorderLayout.CENTER);
        chatPanel.add(messageInput, BorderLayout.SOUTH);
    }

    private void addNewChat() {
        String chatName = " ";
        Chat newChat = new Chat(chatName, "");
        chats.add(newChat);
        chatListModel.addElement(chatName);
        chatList.setSelectedIndex(chats.size() - 1);
    }

    private void deleteSelectedChat() {
        int selectedIndex = chatList.getSelectedIndex();
        if (selectedIndex != -1) {
            chats.remove(selectedIndex);
            chatListModel.remove(selectedIndex);
            selectedChat = null;
            messageArea.setText("");
        }
    }

    private void updateChatPanel() {
        if (selectedChat != null) {
            StringBuilder chatContent = new StringBuilder();
            for (String message : selectedChat.getMessages()) {
                chatContent.append(message).append("\n");
            }
            messageArea.setText(chatContent.toString());
        }
    }

    private void sendMessage() {
        if (selectedChat != null && !messageInput.getText().trim().isEmpty()) {
            String message = "You: " + messageInput.getText().trim();
            selectedChat.addMessage(message);
            log("Message sent: " + message);
            updateChatPanel();
            messageInput.setText("");
        }
    }

    private void bringWindowToFront() {
        if (frame != null) {
            frame.setState(JFrame.NORMAL); // Garante que a janela não está minimizada
            frame.toFront();               // Move a janela para frente
            frame.requestFocus();          // Solicita foco para a janela
        }
    }


    private static void log(String message) {
        try {
            File logFile = new File(LOG_FILE_PATH);
            logFile.getParentFile().mkdirs(); // Create directories if they don't exist
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(logFile, true))) {
                writer.write(message);
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static class Chat {
        private final String name;
        private final String contexto;
        private final List<String> messages;

        public Chat(String name, String contexto) {
            this.name = name;
            this.contexto = contexto;
            this.messages = new ArrayList<>();
        }

        public String getName() {
            return name;
        }

        public String getContexto() {
            return contexto;
        }

        public List<String> getMessages() {
            return messages;
        }

        public void addMessage(String message) {
            messages.add(message);
        }
    }
}
