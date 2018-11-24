package com.csvsoft.smark.util;

import org.apache.commons.collections.ExtendedProperties;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.resource.Resource;
import org.apache.velocity.runtime.resource.loader.ResourceLoader;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class StringResouceLoader extends ResourceLoader {
    @Override
    public void init(ExtendedProperties extendedProperties) {

    }

    @Override
    public InputStream getResourceStream(String s) throws ResourceNotFoundException {
        return new ByteArrayInputStream(s.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public boolean isSourceModified(Resource resource) {
        return false;
    }

    @Override
    public long getLastModified(Resource resource) {
        return 0;
    }
}


