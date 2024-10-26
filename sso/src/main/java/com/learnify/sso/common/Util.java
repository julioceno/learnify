package com.learnify.sso.common;

import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

public class Util {
    public static URI createUri(final String id) {
        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(id)
                .toUri();

        return uri;
    }
}
