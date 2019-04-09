package am.ik.blog;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

import java.util.Set;

@ConfigurationProperties(prefix = "ingestor")
public class IngestorProps {

    private final Set<String> ignoredAddresses;

    private final Elasticsearch elasticsearch;

    public IngestorProps(Set<String> ignoredAddresses, Elasticsearch elasticsearch) {
        this.ignoredAddresses = ignoredAddresses;
        this.elasticsearch = elasticsearch;
    }

    boolean isIgnored(String address) {
        if (this.ignoredAddresses == null) {
            return false;
        }
        return this.ignoredAddresses.contains(address);
    }

    public Elasticsearch getElasticsearch() {
        return elasticsearch;
    }

    public static class Elasticsearch {

        private final String url;

        private final String username;

        private final String password;

        public Elasticsearch(String url, @DefaultValue("") String username, @DefaultValue("") String password) {
            this.url = url;
            this.username = username;
            this.password = password;
        }

        public String getUrl() {
            return url;
        }

        public String getUsername() {
            return username;
        }

        public String getPassword() {
            return password;
        }
    }
}
