package fi.minedu.oiva.backend.util;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.net.URI;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class ControllerUtil {

    private ControllerUtil() {}

    public static <T> CompletableFuture<ResponseEntity> respondWith(final CompletableFuture<Optional<T>> itemOptFuture, final ResponseEntity success, final ResponseEntity failure) {
        return itemOptFuture.handle((itemOpt, throwable) -> (throwable == null) ? itemOpt.map(i -> success).orElse(failure) : get500());
    }

    public static <T> HttpEntity<T> getOr404(final Optional<T> itemOpt) {
        return itemOpt.map(item -> ok(item)).orElse(notFound());
    }

    public static <T> HttpEntity<T> getOr400(final Optional<T> itemOpt) {
        return itemOpt.map(item -> ok(item)).orElse(badRequest());
    }

    public static <T> CompletableFuture<HttpEntity<T>> getOr404(final CompletableFuture<Optional<T>> itemOptFuture) {
        return itemOptFuture.handle((itemOpt, throwable) ->
            (throwable == null) ? itemOpt.map(i -> ok(i)).orElse(notFound()) : notFound());
    }

    public static <T, E> HttpEntity<E> getOr404(final Optional<T> itemOpt, final Function<T, HttpEntity<E>> transformer) {
        return itemOpt.map(transformer::apply).orElse(notFound());
    }

    public static <T> HttpEntity<T> getOr404_(final Optional<T> itemOpt, final Function<T, T> transformer) {
        return itemOpt.map(transformer::apply).map(item -> ok(item)).orElse(notFound());
    }

    public static ResponseEntity<?> newOrBust(final Optional<Long> newOpt, final String pathTo, final String strTpl) {
        return newOpt.map(newId -> {
            String path = String.format(strTpl, pathTo, newId);
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.add("Location", path);
            return new ResponseEntity<>(responseHeaders, HttpStatus.CREATED);
        }).orElse(badRequest());
    }

    public static ResponseEntity<?> newOrBust(final Optional<Long> newOpt, String pathTo) {
        return newOrBust(newOpt, pathTo, "%s/%d");
    }

    public static ResponseEntity<?> deleteOrBust(final Optional<Long> deletedOpt) {
        return deletedOpt.map(deletedId -> noContent()).orElse(badRequest());
    }

    public static <T> ResponseEntity<T> ok() {
        return new ResponseEntity<>(HttpStatus.OK);
    }

    public static <T> ResponseEntity<T> ok(T item) {
        return new ResponseEntity<>(item, HttpStatus.OK);
    }

    public static <T> ResponseEntity<T> created() {
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    public static <T> ResponseEntity<T> notImplemented() {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    public static <T> ResponseEntity<T> badRequest() {
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    public static <T> ResponseEntity<T> notFound() {
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    public static <T> ResponseEntity<T> noContent() {
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    public static <T> ResponseEntity<T> get500() {
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @SuppressWarnings("unchecked")
    public static <T> ResponseEntity<T> get500(final String code, final String title) {
        Map<String, Object> msgMap = new HashMap<>();
        msgMap.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        msgMap.put("code", code);
        msgMap.put("title", title);
        return (ResponseEntity<T>) new ResponseEntity<>(msgMap, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public static <T> ResponseEntity<T> getHealthCheckStatus(final HttpStatus status, final String description) {
        final Map<String, Object> message = new HashMap<>();
        message.put("status", status.value());
        message.put("description", description);
        return (ResponseEntity<T>) new ResponseEntity<>(message, status);
    }

    public static URI buildURI(final String... fragments) {
        return URI.create(Arrays.asList(fragments).stream().collect(Collectors.joining("/")));
    }

    public static String[] options(final String with) {
        return StringUtils.split(with, ",");
    }

    public static String[] options(final Class<?>... with) {
        return null == with ? new String[0] : options(Arrays.asList(with).stream().map(Class::getSimpleName).collect(Collectors.joining(",")));
    }
}
