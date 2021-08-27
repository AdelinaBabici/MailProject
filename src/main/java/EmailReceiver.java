import javax.mail.*;
import java.util.Properties;

public class EmailReceiver {
    public static void main(String[] args) {
        String protocol = "imaps";
        String host = "imap.gmail.com";
        String port = "465";

        String userName = "adelinababici81@gmail.com";
        String password = "Asdf1234:*";
        EmailReceiver receiver = new EmailReceiver();
        receiver.downloadEmails(protocol,host,port,userName,password);
    }

    private Properties getServerProperties(String protocol, String host, String port){
        Properties properties = new Properties();
        properties.put(String.format("mail.imap.host", protocol), host);
        properties.put(String.format("mail.imap.port", protocol), port);
        properties.put("mail.imap.ssl.enable", "true");
        return properties;
    }
    public void downloadEmails(String protocol, String host, String port, String userName, String password){
        Properties properties = getServerProperties(protocol,host,port);
        Session session = Session.getDefaultInstance(properties);

        try{
            Store store = session.getStore(protocol);
            store.connect(host, userName, password);

            Folder folderInbox = store.getFolder("INBOX");
            folderInbox.open(Folder.READ_ONLY);

            Message[] messages = folderInbox.getMessages();

            for (int i = 0; i < messages.length; i++){
                Message msg  = messages[i];
                Address[] fromAddress = msg.getFrom();
                String from = fromAddress[0].toString();
                String subject = msg.getSubject();
                String sentDate = msg.getSentDate().toString();
                String contentType = msg.getContentType();
                String messageContent = "";

                if(contentType.contains("text/plain") || contentType.contains("text/html")){
                    try{
                        Object content = msg.getContent();
                        if(content != null){
                            messageContent = content.toString();
                        }
                    }catch (Exception ex){
                        messageContent = "[Error downloading content]";
                        ex.printStackTrace();
                    }
                }
                System.out.println("Message nr. " + (i + 1) + ":");
                System.out.println("\t From: " + from);
                System.out.println("\t Subject: " + subject);
                System.out.println("\t Sent Date: " + sentDate);
                System.out.println("\t Message: " + messageContent);

            }
            folderInbox.close(false);
            store.close();

        }catch (NoSuchProviderException ex){
            System.out.println("No provider for protocol: " + protocol);
            ex.printStackTrace();
        }catch (MessagingException ex){
            System.out.println("Could not connect to the message store");
            ex.printStackTrace();
        }
    }


}
