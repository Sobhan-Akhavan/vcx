package ir.vcx.util.httprequest;

import ir.vcx.exception.VCXException;
import ir.vcx.exception.VCXExceptionStatus;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.utils.URIBuilder;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.client.AsyncRestTemplate;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;

/**
 * Created by Sobhan at 8/10/2023 - VCX
 */

@Slf4j
@Component
public class RestRequest {

    private final RestTemplate restTemplate;
    private final AsyncRestTemplate asyncRestTemplate;

    public RestRequest(RestTemplate restTemplate, AsyncRestTemplate asyncRestTemplate) {
        this.restTemplate = restTemplate;
        this.asyncRestTemplate = asyncRestTemplate;
    }


    public <T> T post(String url, Map<String, ?> body, Map<String, String> header, Class<T> aClass) {

        HttpHeaders headers = null;
        if (header != null && !header.isEmpty()) {
            headers = new HttpHeaders();
            header.forEach(headers::add);
        }

        HttpEntity<Map<String, ?>> entity = new HttpEntity<>(body, headers);

        ResponseEntity<T> exchange = restTemplate.exchange(url, HttpMethod.POST, entity, aClass);

        if (!exchange.getStatusCode().is2xxSuccessful()) {
            log.error("response is not 200 {}", exchange.getStatusCode());
//            Sentry.addBreadcrumb("response is not 200" + exchange.getStatusCode());
        }

        return exchange.getBody();
    }

    /**
     * @return proxy with application/x-www-form-urlencoded body.
     */
    public <T> T post(String url, MultiValueMap<String, ?> body, Map<String, String> header, Class<T> aClass) {

        HttpHeaders headers = null;
        if (header != null && !header.isEmpty()) {
            headers = new HttpHeaders();
            header.forEach(headers::add);
        }

        HttpEntity<MultiValueMap<String, ?>> entity = new HttpEntity<>(body, headers);

        ResponseEntity<T> exchange = restTemplate.exchange(url, HttpMethod.POST, entity, aClass);

        if (!exchange.getStatusCode().is2xxSuccessful()) {
            log.error("response is not 200 {}", exchange.getStatusCode());
//            Sentry.addBreadcrumb("response is not 200" + exchange.getStatusCode());
        }

        return exchange.getBody();
    }

    public <T> ResponseEntity<T> postForEntity(String url, Map<String, ?> body, Map<String, String> header, Class<T> aClass) {

        HttpHeaders headers = null;
        if (header != null && !header.isEmpty()) {
            headers = new HttpHeaders();
            header.forEach(headers::add);
        }

        HttpEntity<Map<String, ?>> entity = new HttpEntity<>(body, headers);

        return restTemplate.postForEntity(url, entity, aClass);
    }


    public ResponseEntity<String> postFormData(String url, MultiValueMap<String, String> params) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(params, headers);

        return restTemplate.postForEntity(url, request, String.class);
    }


    public <T> T get(String url, Map<String, String> params, Map<String, String> header, Class<T> aClass) throws URISyntaxException {

        URIBuilder uriBuilder = new URIBuilder(url);

        if (params != null && params.size() > 0) {
            params.forEach(uriBuilder::addParameter);
        }

        HttpHeaders headers = null;
        if (header != null && !header.isEmpty()) {
            headers = new HttpHeaders();
            header.forEach(headers::add);
        }

        HttpEntity<Map<String, ?>> entity = new HttpEntity<>(null, headers);

        // return restTemplate.getForObject(url, aClass, entity);
        ResponseEntity<T> exchange = restTemplate.exchange(uriBuilder.build(), HttpMethod.GET, entity, aClass);

        if (!exchange.getStatusCode().is2xxSuccessful()) {
            log.error("response is not 200 {}", exchange.getStatusCode());
//            Sentry.addBreadcrumb("response is not 200" + exchange.getStatusCode());
        }

        return exchange.getBody();
    }

    public <T> T put(String url, Map<String, String> params, Map<String, String> header, Class<T> aClass) throws URISyntaxException {

        URIBuilder uriBuilder = new URIBuilder(url);

        if (params != null && params.size() > 0) {
            params.forEach(uriBuilder::addParameter);
        }

        HttpHeaders headers = null;
        if (header != null && !header.isEmpty()) {
            headers = new HttpHeaders();
            header.forEach(headers::add);
        }

        HttpEntity<Map<String, ?>> entity = new HttpEntity<>(null, headers);

        // return restTemplate.getForObject(url, aClass, entity);
        ResponseEntity<T> exchange = restTemplate.exchange(uriBuilder.build(), HttpMethod.PUT, entity, aClass);

        if (!exchange.getStatusCode().is2xxSuccessful()) {
            log.error("response is not 200 {}", exchange.getStatusCode());
//            Sentry.addBreadcrumb("response is not 200" + exchange.getStatusCode());
        }

        return exchange.getBody();
    }

    public void delete(String url, Map<String, String> params, MultiValueMap<String, String> header) throws URISyntaxException, VCXException {

        URIBuilder uriBuilder = new URIBuilder(url);

        if (params != null && params.size() > 0) {
            params.forEach(uriBuilder::addParameter);
        }


        HttpEntity<?> request = null;
        if (header != null && !header.isEmpty()) {
            request = new HttpEntity<>(header);
        }

        ResponseEntity<String> exchange = restTemplate.exchange(url, HttpMethod.DELETE, request, String.class);

        if (!exchange.getStatusCode().is2xxSuccessful()) {
            log.error("response is not 200 {}", exchange.getStatusCode());
//            Sentry.addBreadcrumb("response is not 200" + exchange.getStatusCode());

            if (exchange.getStatusCode().getReasonPhrase().equals("Not Found")) {
                throw new VCXException(VCXExceptionStatus.NOT_FOUND);
            }
            throw new VCXException(VCXExceptionStatus.UNKNOWN_ERROR);
        }

    }

    public InputStreamResource downloadFile(String url, ResponseExtractor<InputStreamResource> responseExtractor) throws IOException {
        // Optional Accept header
        RequestCallback requestCallback = request -> request
                .getHeaders()
                .setAccept(Arrays.asList(MediaType.APPLICATION_OCTET_STREAM, MediaType.ALL));

        return restTemplate.execute(url, HttpMethod.GET, requestCallback, responseExtractor);
    }

    public boolean isGetMethodAllowed(String url) {
        Set<HttpMethod> httpMethods = restTemplate.optionsForAllow(url);
        return httpMethods.contains(HttpMethod.GET);
    }

    public HttpHeaders getHeadersBeforeDownload(String url) {
        return restTemplate.headForHeaders(url);
    }


    public <T> void getAsync(String url, Map<String, String> params, Map<String, String> header, Class<T> aClass, AsyncListener<T> listener) throws URISyntaxException {

        URIBuilder uriBuilder = new URIBuilder(url);

        if (params != null && params.size() > 0) {
            params.forEach(uriBuilder::addParameter);
        }

        HttpHeaders headers = null;
        if (header != null && !header.isEmpty()) {
            headers = new HttpHeaders();
            header.forEach(headers::add);
        }

        HttpEntity<Map<String, ?>> entity = new HttpEntity<>(null, headers);

        ListenableFuture<ResponseEntity<T>> exchange = asyncRestTemplate.exchange(uriBuilder.build(), HttpMethod.GET, entity, aClass);

        exchange.addCallback(result -> {
            if (listener != null) {
                if (!result.getStatusCode().isError()) {
                    listener.onSuccess(result.getBody());
                } else {
                    listener.onFailure(new Exception(result.getStatusCode().getReasonPhrase()));
                }
            }
        }, ex -> {
            if (listener != null) {
                listener.onFailure(ex);
            }
        });

    }


    public interface AsyncListener<T> {
        void onSuccess(T response);

        void onFailure(Throwable e);
    }

}
