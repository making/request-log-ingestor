package am.ik.blog;

import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.MediaType;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.OffsetDateTime;
import java.util.UUID;

@Component
public class ElasticsearchIngestor {

    private final Logger log = LoggerFactory.getLogger(ElasticsearchIngestor.class);

    private final IngestorProps props;

    private final WebClient webClient;

    public ElasticsearchIngestor(WebClient.Builder builder, IngestorProps props) {
        IngestorProps.Elasticsearch elasticsearch = props.getElasticsearch();
        this.props = props;
        this.webClient = builder
            .baseUrl(elasticsearch.getUrl())
            .defaultHeaders(headers -> headers.setBasicAuth(elasticsearch.getUsername(), elasticsearch.getPassword()))
            .build();
    }

    @StreamListener(Sink.INPUT)
    public void injest(Message<JsonNode> message) {
        UUID id = message.getHeaders().getId();
        JsonNode payload = message.getPayload();
        log.info("Received {}:{}", id, payload);
        if (payload.has("address") && this.props.isIgnored(payload.get("address").asText())) {
            log.info("Ignored {}", id);
            return;
        }
        if (id != null && payload.has("date")) {
            OffsetDateTime date = OffsetDateTime.parse(payload.get("date").asText());
            String index = "request-log-" + date.toLocalDate();
            // https://stackoverflow.com/a/51321602/5861829
            this.webClient.put()
                .uri(b -> b.pathSegment(index, "doc", id.toString()).build())
                .contentType(MediaType.APPLICATION_JSON)
                .syncBody(payload)
                .retrieve()
                .bodyToFlux(DataBuffer.class)
                .map(DataBufferUtils::release)
                .then()
                .block();
        }
    }
}
