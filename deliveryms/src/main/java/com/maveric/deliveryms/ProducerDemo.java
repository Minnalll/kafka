package com.maveric.deliveryms;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;
import java.util.concurrent.Future;

public class ProducerDemo {
    private static final Logger Log = LoggerFactory.getLogger(ProducerDemo.class);

    public static void main(String[] args) throws Exception {
        System.out.println("****starting kafka client");
        Properties properties = new Properties();
        //broker info to connect to
        properties.put("bootstrap.servers", "localhost:9092");
        //lets producer client retry 3 times
        properties.put("retries", 3);
        // key serializer(fully qualified classname)
        properties.put("key.serializer", StringSerializer.class.getName());
        //value serializer (fully qualified classname)
        properties.put("value.serializer", DeliveryMessageSerializer.class.getName());
        properties.put("acks", "all");

        KafkaProducer<String, DeliveryMessage> producer = new KafkaProducer<>(properties);
        String orderID = "order-1";
        DeliveryMessage msg = new DeliveryMessage(orderID, DeliveryStatus.DELIVERED);
        ProducerRecord<String, DeliveryMessage> record = new ProducerRecord<>("deliveries", orderID, msg);
  /*
        Future<RecordMetadata> future = producer.send(record);
        RecordMetadata metaData = future.get();
        System.out.println("record details topic=" + metaData.topic() + "-partition=" + metaData.partition() + "-offset=" + metaData.offset());
*/
        producer.send(record,(metaData,exception)->{
           if(exception!=null){
              exception.printStackTrace();
               return ;
           }
            System.out.println("record details topic="+metaData.topic()+"-"+metaData.partition()+"-"+metaData.offset());

        });

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("****producer exiting");
            producer.close();
        }));

        producer.close();
        System.out.println("bye");
    }
}

