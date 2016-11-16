import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

// GUI layout 
import javax.swing.JFrame;
import java.awt.Dimension;
import javax.swing.JPanel;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;

// GUI elements
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JTextField;

// Click event
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

// Help links:
//    https://www.cloudmqtt.com/docs-java.html 
//    http://stackoverflow.com/questions/22715682/subscribe-and-read-mqtt-message-using-paho



public class MQTTSamplePaho {

    MqttClient mqttClient;
    String topic_current  = "temp/current";
    String topic_wanted   = "temp/wanted";
    String content        = "34-from-java";
    int qos               = 1;
    String broker         = "tcp://m20.cloudmqtt.com:17667";

    String mqtt_un        = "x";
    char[] mqtt_pass      = new char[] {'x', 'x', 'x'};
    //MQTT client id to use for the device. "" will generate a client id automatically
    String clientId       = "mqtt-bt-test-java";

    JFrame frm = new JFrame();
    JLabel txtTemperature = new JLabel("-");
    JTextField fldTemp;

    public static void main(String[] args){

        MQTTSamplePaho app = new MQTTSamplePaho();
        app.createGui();
        app.startMQTTClient();
        app.setTemperatureText("0");
    }


    public void setTemperatureText(String txt){
        txt = "Sensor temperature: " + txt + " c";
        txtTemperature.setText(txt);
    }

    public void createGui(){
        frm.setTitle("Sensor Monitor");
        frm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frm.setMinimumSize(new Dimension(500, 300));
        frm.setVisible(true);

        JPanel panel = new JPanel(new GridBagLayout());
        frm.getContentPane().add(panel);

        GridBagConstraints c = new GridBagConstraints();
//        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 0;

        panel.add(txtTemperature, c);

        JLabel labelSetField = new JLabel("Set wanted temperature:");
        c.gridy++;
        panel.add(labelSetField, c);


        fldTemp = new JTextField(10);
        c.gridx++;
        panel.add(fldTemp, c);

        JButton btnSetTemp = new JButton("Set");
        c.gridy++;
        panel.add(btnSetTemp, c);

        btnSetTemp.addMouseListener(new MouseAdapter(){
            public void mouseClicked(MouseEvent e){
                publishTemperature();
            }
        });
    }

    public void publishTemperature(){
        try{
            System.out.println("Message: " + fldTemp.getText());
            MqttMessage message = new MqttMessage(fldTemp.getText().getBytes());
            mqttClient.publish(topic_wanted, message);
        } catch(MqttException me) {
            me.printStackTrace();
        }
    }

    public void startMQTTClient(){

        MemoryPersistence persistence = new MemoryPersistence();

        try {
            mqttClient = new MqttClient(broker, clientId, persistence);
//            mqttClient = new MqttClient(broker, clientId);
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);
            connOpts.setUserName(mqtt_un);
            connOpts.setPassword(mqtt_pass);
            mqttClient.connect(connOpts);
            mqttClient.subscribe(topic_current, qos);

            mqttClient.setCallback(new MqttCallback(){
                public void messageArrived(String topic, MqttMessage msg) throws Exception {
//                    System.out.println("Recived:" + topic);
//                    System.out.println("Recived:" + new String(msg.getPayload()));

                    String txt = new String(msg.getPayload());
                    //txt += " c";
                    //txtTemperature.setText(txt);
                    setTemperatureText(txt);
                }
                public void deliveryComplete(IMqttDeliveryToken arg0) { System.out.println("Delivery complete..."+arg0); }
                public void connectionLost(Throwable arg0){ System.out.println("Connection lost..."); }
            });
        } catch(MqttException me) {
            System.out.println("reason "+me.getReasonCode());
            System.out.println("msg "+me.getMessage());
            System.out.println("loc "+me.getLocalizedMessage());
            System.out.println("cause "+me.getCause());
            System.out.println("excep "+me);
            me.printStackTrace();
        }
    }
}