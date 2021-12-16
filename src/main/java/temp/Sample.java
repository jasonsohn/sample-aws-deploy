package temp;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.ValueMapper;

import java.util.Arrays;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;

public class Sample {
    public static void main(String[] args) {
        System.out.println("Sample Java Project in AWS");
        Properties props = new Properties();
        //카프카 스트림즈 애플리케이션을 유일할게 구분할 아이디
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "streams-linesplit");
        //스트림즈 애플리케이션이 접근할 카프카 브로커정보
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        //데이터를 어떠한 형식으로 Read/Write할지를 설정(키/값의 데이터 타입을 지정) - 문자열
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());

        //데이터의 흐름으로 구성된 토폴로지를 정의할 빌더
        final StreamsBuilder builder = new StreamsBuilder();

        //streams-*input에서 streams-*output으로 데이터 흐름을 정의한다.
        builder.<String, String>stream("streams-plaintext-input")
                .flatMapValues(new ValueMapper<String, Iterable<String>>() {
                    @Override
                    public Iterable<String> apply(String value) {
                        return Arrays.asList(value.split("\\W+"));
                    }
                })
                .to("streams-linesplit-output");

        /* ------- use the code below for Java 8 and uncomment the above ----
        builder.stream("streams-plaintext-input")
               .flatMapValues(value -> Arrays.asList(value.split("\\W+")))
               .to("streams-linesplit-output");
           ----------------------------------------------------------------- */

        //최종적인 토폴로지 생성
        final Topology topology = builder.build();
        //만들어진 토폴로지 확인
        final KafkaStreams streams = new KafkaStreams(topology, props);
        final CountDownLatch latch = new CountDownLatch(1);

        // attach shutdown handler to catch control-c
        Runtime.getRuntime().addShutdownHook(new Thread("streams-shutdown-hook") {
            @Override
            public void run() {
                streams.close();
                latch.countDown();
            }
        });

        try {
            streams.start();
            latch.await();
        } catch (Throwable e) {
            System.exit(1);
        }
        System.exit(0);




//
//        for (int i = 0; i < 10; i++) {
//            System.out.println("Test : " + i);
//            try {
//                Thread.sleep(1000); // 1초
//            } catch (InterruptedException e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            }
//        }

        System.out.println("Finished");
    }
}
