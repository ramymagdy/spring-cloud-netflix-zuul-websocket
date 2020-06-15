/*
 * Copyright 2002-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.mthizo247.cloud.netflix.zuul.web.proxytarget;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.netflix.zuul.filters.ZuulProperties;
import org.springframework.core.Ordered;
import org.springframework.core.env.Environment;
import org.springframework.util.Assert;
import org.springframework.web.util.UriComponentsBuilder;

import com.github.mthizo247.cloud.netflix.zuul.web.socket.ZuulWebSocketProperties;

/**
 * @author Ronald Mthombeni
 * @author Salman Noor
 */
public abstract class AbstractProxyTargetResolver implements ProxyTargetResolver, Ordered {
    protected ZuulProperties zuulProperties;
    protected int order = 0;
	private Environment environment;

    public AbstractProxyTargetResolver(ZuulProperties zuulProperties, Environment environment) {
        Assert.notNull(zuulProperties, "zuulProperties must not be null");
        this.zuulProperties = zuulProperties;
        this.environment = environment;
    }

    protected ZuulProperties.ZuulRoute resolveRoute(ZuulWebSocketProperties.WsBrokerage wsBrokerage) {

        ZuulProperties.ZuulRoute zuulRoute = zuulProperties.getRoutes().get(wsBrokerage.getRouteId());
        zuulRoute = zuulRoute == null ? zuulProperties.getRoutes().get(wsBrokerage.getId()) : zuulRoute;
        return zuulRoute;
    }

    protected URI resolveUri(ServiceInstance serviceInstance) {
        Map<String, Object> metadata = new HashMap<>();
        for (Map.Entry<String, String> entry : serviceInstance.getMetadata().entrySet()) {
            metadata.put(entry.getKey(), entry.getValue());
        }

        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUri(serviceInstance.getUri());
        String configPath = environment.getProperty("configPath");
        if (configPath != null) {
            uriBuilder.path(configPath);
        }

        return uriBuilder.build().toUri();
    }

    @Override
    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }
}
