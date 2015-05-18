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
package org.n52.iceland.binding;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.n52.iceland.coding.CodingRepository;
import org.n52.iceland.coding.OperationKey;
import org.n52.iceland.decode.Decoder;
import org.n52.iceland.decode.DecoderKey;
import org.n52.iceland.decode.OperationDecoderKey;
import org.n52.iceland.encode.Encoder;
import org.n52.iceland.encode.EncoderKey;
import org.n52.iceland.encode.ExceptionEncoderKey;
import org.n52.iceland.encode.OperationEncoderKey;
import org.n52.iceland.event.ServiceEventBus;
import org.n52.iceland.event.events.ExceptionEvent;
import org.n52.iceland.exception.HTTPException;
import org.n52.iceland.exception.ows.concrete.InvalidAcceptVersionsParameterException;
import org.n52.iceland.exception.ows.concrete.InvalidServiceOrVersionException;
import org.n52.iceland.exception.ows.concrete.InvalidServiceParameterException;
import org.n52.iceland.exception.ows.concrete.MissingServiceParameterException;
import org.n52.iceland.exception.ows.concrete.MissingVersionParameterException;
import org.n52.iceland.exception.ows.concrete.NoEncoderForKeyException;
import org.n52.iceland.exception.ows.concrete.VersionNotSupportedException;
import org.n52.iceland.ogc.ows.CompositeOwsException;
import org.n52.iceland.ogc.ows.OwsExceptionReport;
import org.n52.iceland.request.AbstractServiceRequest;
import org.n52.iceland.request.GetCapabilitiesRequest;
import org.n52.iceland.request.RequestContext;
import org.n52.iceland.response.AbstractServiceResponse;
import org.n52.iceland.service.ServiceConfiguration;
import org.n52.iceland.service.operator.ServiceOperator;
import org.n52.iceland.service.operator.ServiceOperatorKey;
import org.n52.iceland.service.operator.ServiceOperatorRepository;
import org.n52.iceland.util.http.HTTPStatus;
import org.n52.iceland.util.http.HTTPUtils;
import org.n52.iceland.util.http.MediaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TODO JavaDoc
 * 
 * @author Christian Autermann <c.autermann@52north.org>
 * 
 * @since 4.0.0
 */
public abstract class SimpleBinding extends Binding {
    private static final Logger LOG = LoggerFactory.getLogger(SimpleBinding.class);
    public static final String QUALITY = "q";

    protected boolean isUseHttpResponseCodes() {
        return ServiceConfiguration.getInstance().isUseHttpStatusCodesInKvpAndPoxBinding();
    }

    protected RequestContext getRequestContext(HttpServletRequest req) {
        return RequestContext.fromRequest(req);
    }

    protected boolean isVersionSupported(String service, String acceptVersion) {
        return getServiceOperatorRepository().isVersionSupported(service, acceptVersion);
    }

    protected boolean isServiceSupported(String service) {
        return getServiceOperatorRepository().isServiceSupported(service);
    }

    protected ServiceOperatorRepository getServiceOperatorRepository() {
        return ServiceOperatorRepository.getInstance();
    }

    protected <F, T> Decoder<F, T> getDecoder(DecoderKey key) {
        return CodingRepository.getInstance().getDecoder(key);
    }

    protected <F, T> Encoder<F, T> getEncoder(EncoderKey key) {
        return CodingRepository.getInstance().getEncoder(key);
    }

    protected boolean hasDecoder(DecoderKey key) {
        return CodingRepository.getInstance().hasDecoder(key);
    }

    protected boolean hasEncoder(EncoderKey key) {
        return CodingRepository.getInstance().hasEncoder(key);
    }

    protected boolean hasDecoder(OperationKey key, MediaType mediaType) {
        return hasDecoder(new OperationDecoderKey(key, mediaType));
    }

    protected boolean hasEncoder(OperationKey key, MediaType mediaType) {
        return hasEncoder(new OperationEncoderKey(key, mediaType));
    }

    protected boolean hasEncoder(AbstractServiceResponse response, MediaType mediaType) {
        return hasEncoder(response.getOperationKey(), mediaType);
    }

    protected MediaType chooseResponseContentType(AbstractServiceResponse response, List<MediaType> acceptHeader,
            MediaType defaultContentType) throws HTTPException {
        /*
         * TODO get a list of response content types and check against
         * wildcards/qualities
         */
        if (!acceptHeader.isEmpty()) {
            if (!response.isSetContentType()) {
                for (MediaType mt : acceptHeader) {
                    MediaType mediaType = mt.withoutParameter(QUALITY);
                    if (defaultContentType.isCompatible(mediaType)) {
                        return defaultContentType;
                    } else if (hasEncoder(response, mediaType)) {
                        return mediaType;
                    }
                }
                // no encoder for any accept header content type
                throw new HTTPException(HTTPStatus.NOT_ACCEPTABLE);
            } else {
                for (MediaType mt : acceptHeader) {
                    MediaType mediaType = mt.withoutParameter(QUALITY);
                    if (response.getContentType().isCompatible(mediaType)) {
                        return response.getContentType();
                    }
                }
                // incompatible response content type and accept header
                throw new HTTPException(HTTPStatus.NOT_ACCEPTABLE);
            }
        } else {
            if (!response.isSetContentType()) {
                return defaultContentType;
            } else {
                MediaType mediaType = response.getContentType()
                        .withoutParameter(QUALITY);
                if (hasEncoder(response, mediaType)) {
                    return mediaType;
                }
            }
            // no encoder for response content type
            throw new HTTPException(HTTPStatus.NOT_ACCEPTABLE);
        }
    }

    protected MediaType chooseResponseContentTypeForExceptionReport(
            List<MediaType> acceptHeader, MediaType defaultContentType)
            throws HTTPException {
        /*
         * TODO get a list of response content types and check against
         * wildcards/qualities
         */
        if (acceptHeader.isEmpty()) {
            return defaultContentType;
        }
        for (MediaType mt : acceptHeader) {
            MediaType mediaType = mt.withoutParameter(QUALITY);
            if (defaultContentType.isCompatible(mediaType)) {
                return defaultContentType;
            } else if (hasEncoder(new ExceptionEncoderKey(mediaType))) {
                return mediaType;
            }
        }
        throw new HTTPException(HTTPStatus.NOT_ACCEPTABLE);
    }

    protected ServiceOperator getServiceOperator(ServiceOperatorKey sokt) throws OwsExceptionReport {
        return getServiceOperatorRepository().getServiceOperator(sokt);
    }

    protected ServiceOperator getServiceOperator(AbstractServiceRequest<?> request) throws OwsExceptionReport {
        checkServiceOperatorKeyTypes(request);
        for (ServiceOperatorKey sokt : request.getServiceOperatorKeyType()) {
            ServiceOperator so = getServiceOperator(sokt);
            if (so != null) {
                return so;
            }
        }
        // no operator found
        if (request instanceof GetCapabilitiesRequest) {
            throw new InvalidAcceptVersionsParameterException(((GetCapabilitiesRequest) request).getAcceptVersions());
        } else {
            throw new InvalidServiceOrVersionException(request.getService(), request.getVersion());
        }
    }

    protected void checkServiceOperatorKeyTypes(AbstractServiceRequest<?> request) throws OwsExceptionReport {
        CompositeOwsException exceptions = new CompositeOwsException();
        for (ServiceOperatorKey sokt : request.getServiceOperatorKeyType()) {
            if (sokt.hasService()) {
                if (sokt.getService().isEmpty()) {
                    exceptions.add(new MissingServiceParameterException());
                } else if (!getServiceOperatorRepository().isServiceSupported(sokt.getService())) {
                    exceptions.add(new InvalidServiceParameterException(sokt.getService()));
                }
            }
            if (request instanceof GetCapabilitiesRequest) {
                GetCapabilitiesRequest gcr = (GetCapabilitiesRequest) request;
                if (gcr.isSetAcceptVersions()) {
                    boolean hasSupportedVersion = false;
                    for (String acceptVersion : gcr.getAcceptVersions()) {
                        if (isVersionSupported(request.getService(), acceptVersion)) {
                            hasSupportedVersion = true;
                        }
                    }
                    if (!hasSupportedVersion) {
                        exceptions.add(new InvalidAcceptVersionsParameterException(gcr.getAcceptVersions()));
                    }
                }
            } else {
                if (sokt.hasVersion()) {
                    if (sokt.getVersion().isEmpty()) {
                        exceptions.add(new MissingVersionParameterException());
                    } else if (!isVersionSupported(sokt.getService(), sokt.getVersion())) {
                        exceptions.add(new VersionNotSupportedException());
                    }
                }
            }
        }
        exceptions.throwIfNotEmpty();
    }

    protected void writeResponse(HttpServletRequest request, HttpServletResponse response,
            AbstractServiceResponse serviceResponse) throws HTTPException, IOException {
        MediaType contentType =
                chooseResponseContentType(serviceResponse, HTTPUtils.getAcceptHeader(request), getDefaultContentType());
        HTTPUtils.writeObject(request, response, contentType, serviceResponse);
    }

    protected Object encodeResponse(AbstractServiceResponse response, MediaType contentType) throws OwsExceptionReport {
        OperationEncoderKey key = new OperationEncoderKey(response.getOperationKey(), contentType);
        Encoder<Object, AbstractServiceResponse> encoder = CodingRepository.getInstance().getEncoder(key);
        if (encoder == null) {
            throw new NoEncoderForKeyException(key);
        }
        return encoder.encode(response);
    }

    protected void writeOwsExceptionReport(HttpServletRequest request, HttpServletResponse response,
            OwsExceptionReport oer) throws HTTPException {
        try {
            ServiceEventBus.fire(new ExceptionEvent(oer));
            MediaType contentType =
                    chooseResponseContentTypeForExceptionReport(HTTPUtils.getAcceptHeader(request),
                            getDefaultContentType());
            Object encoded = encodeOwsExceptionReport(oer, contentType);
            if (isUseHttpResponseCodes() && oer.hasStatus()) {
                response.setStatus(oer.getStatus().getCode());
            }
            HTTPUtils.writeObject(request, response, contentType, encoded);
        } catch (IOException e) {
            throw new HTTPException(HTTPStatus.INTERNAL_SERVER_ERROR, e);
        } catch (OwsExceptionReport e) {
            throw new HTTPException(HTTPStatus.INTERNAL_SERVER_ERROR, e);
        }
    }

    protected abstract MediaType getDefaultContentType();

    protected Object encodeOwsExceptionReport(OwsExceptionReport oer, MediaType contentType) throws OwsExceptionReport, HTTPException {
        Encoder<Object, OwsExceptionReport> encoder = getEncoder(new ExceptionEncoderKey(contentType));
        if (encoder == null) {
            LOG.error("Can't find OwsExceptionReport encoder for Content-Type {}", contentType);
            throw new HTTPException(HTTPStatus.UNSUPPORTED_MEDIA_TYPE);
        }
        return encoder.encode(oer);
    }

}