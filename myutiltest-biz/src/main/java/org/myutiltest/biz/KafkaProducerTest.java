package org.myutiltest.biz;

import java.util.Properties;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

public class KafkaProducerTest {
	public final static String TOPIC = "JAVA_TOPIC";

	public static void main(String[] args) {
		Properties props = new Properties();
		props.put("bootstrap.servers", "localhost:9092");
		props.put("acks", "all");
		props.put("retries", 0);
		props.put("batch.size", 16384);
		props.put("linger.ms", 1);
		props.put("buffer.memory", 33554432);
		props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
		props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

		Producer<String, String> kafkaProducer = new KafkaProducer<>(props);

		for (int i = 1; i < 1000; i++) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			String key = String.valueOf("key" + i);
			String data = "hello kafka message:" + key;
			kafkaProducer.send(new ProducerRecord<>(TOPIC, key, data), new Callback() {
				@Override
				public void onCompletion(RecordMetadata recordMetadata, Exception e) {
					// do sth
					System.out.println(recordMetadata.offset());
				}
			});
			//System.out.println(data);
		}
		
		kafkaProducer.close();
	}
}
