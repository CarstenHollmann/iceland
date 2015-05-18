/**
 * Copyright 2015 52°North Initiative for Geospatial Open Source
 * Software GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.n52.iceland.request;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;

import javax.servlet.http.HttpServletRequest;

import org.n52.iceland.util.http.HTTPHeaders;
import org.n52.iceland.util.net.IPAddress;
import org.n52.iceland.util.net.ProxyChain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.MoreObjects;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.net.InetAddresses;

/**
 * Holds information about a user's request that can be evaluated during request
 * processing (e.g. security info)
 * 
 * @author Shane StClair <shane@axiomalaska.com>
 * 
 * @since 4.0.0
 */
public class RequestContext {
    private static final Logger LOG = LoggerFactory.getLogger(RequestContext.class);
    private Optional<IPAddress> address = Optional.absent();
    private Optional<String> token = Optional.absent();
    private Optional<ProxyChain> proxyChain = Optional.absent();

    public Optional<IPAddress> getIPAddress() {
        return address;
    }

    public Optional<ProxyChain> getForwardedForChain() {
        return proxyChain;
    }
    
    public void setForwaredForChain(ProxyChain chain) {
        this.proxyChain = Optional.fromNullable(chain);
    }

    public void setForwaredForChain(Optional<ProxyChain> chain) {
        this.proxyChain = Preconditions.checkNotNull(chain);
    }

    public void setIPAddress(IPAddress ip) {
        this.address = Optional.fromNullable(ip);
    }

    public void setIPAddress(Optional<IPAddress> ip) {
        this.address = Preconditions.checkNotNull(ip);
    }

    public Optional<String> getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = Optional.fromNullable(Strings.emptyToNull(token));
    }

    public void setToken(Optional<String> token) {
        this.token = Preconditions.checkNotNull(token);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this).omitNullValues()
                .add("address", getIPAddress().orNull())
                .add("token", getToken().orNull())
                .add("proxyChain", getForwardedForChain().orNull())
                .toString();
    }

    public static RequestContext fromRequest(HttpServletRequest req) {
        RequestContext rc = new RequestContext();
        rc.setIPAddress(getIPAddress(req));
        rc.setForwaredForChain(ProxyChain.fromForwardedForHeader(req.getHeader(HTTPHeaders.X_FORWARDED_FOR)));
        rc.setToken(req.getHeader(HTTPHeaders.AUTHORIZATION));
        return rc;

    }

    private static IPAddress getIPAddress(HttpServletRequest req) {
        InetAddress addr = null;
        try {
            addr = InetAddresses.forString(req.getRemoteAddr());
        } catch (IllegalArgumentException e) {
            LOG.warn("Ignoring invalid IP address: " + req.getRemoteAddr(), e);
        }

        if (addr instanceof Inet4Address) {
            Inet4Address inet4Address = (Inet4Address) addr;
            return new IPAddress(inet4Address);
        } else if (addr instanceof Inet6Address) {
            Inet6Address inet6Address = (Inet6Address) addr;
            if (InetAddresses.isCompatIPv4Address(inet6Address)) {
                return new IPAddress(InetAddresses.getCompatIPv4Address(inet6Address));
            } else if (InetAddresses.toAddrString(addr).equals("::1")) {
                // ::1 is not handled by InetAddresses.isCompatIPv4Address()
                return new IPAddress("127.0.0.1");
            } else {
                LOG.warn("Ignoring not v4 compatible IP address: {}",
                         req.getRemoteAddr());
            }
        } else {
            LOG.warn("Ignoring unknown InetAddress: {}", addr);
        }
        return null;
    }
}