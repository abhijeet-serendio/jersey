/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2012-2013 Oracle and/or its affiliates. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * http://glassfish.java.net/public/CDDL+GPL_1_1.html
 * or packager/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at packager/legal/LICENSE.txt.
 *
 * GPL Classpath Exception:
 * Oracle designates this particular file as subject to the "Classpath"
 * exception as provided by Oracle in the GPL Version 2 section of the License
 * file that accompanied this code.
 *
 * Modifications:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 *
 * Contributor(s):
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */
package org.glassfish.jersey.message.internal;


import java.util.Map;
import javax.inject.Singleton;

import javax.ws.rs.RuntimeType;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.stream.XMLInputFactory;
import javax.xml.transform.TransformerFactory;

import org.glassfish.jersey.internal.ServiceFinderBinder;
import org.glassfish.jersey.spi.HeaderDelegateProvider;

import org.glassfish.hk2.api.PerThread;
import org.glassfish.hk2.utilities.binding.AbstractBinder;

/**
 * Binding definitions for the default set of message related providers (readers,
 * writers, header delegates).
 *
 * @author Marek Potociar (marek.potociar at oracle.com)
 * @author Libor Kramolis (libor.kramolis at oracle.com)
 */
public class MessagingBinders {

    /**
     * Message body providers injection binder.
     */
    public static class MessageBodyProviders extends AbstractBinder {

        private final Map<String, Object> applicationProperties;

        private final RuntimeType runtimeType;

        public MessageBodyProviders(Map<String, Object> applicationProperties, RuntimeType runtimeType) {
            this.applicationProperties = applicationProperties;
            this.runtimeType = runtimeType;
        }

        @Override
        protected void configure() {

            // Message body providers (both readers & writers)
            bindSingletonWorker(ByteArrayProvider.class);
            bindSingletonWorker(DataSourceProvider.class);
            bindSingletonWorker(DocumentProvider.class);
            bindSingletonWorker(FileProvider.class);
            bindSingletonWorker(FormMultivaluedMapProvider.class);
            bindSingletonWorker(FormProvider.class);
            bindSingletonWorker(InputStreamProvider.class);
            bindSingletonWorker(BasicTypesMessageProvider.class);
            bindSingletonWorker(ReaderProvider.class);
            bindSingletonWorker(RenderedImageProvider.class);
            bindSingletonWorker(StringMessageProvider.class);

            bindSingletonWorker(XmlJaxbElementProvider.App.class);
            bindSingletonWorker(XmlJaxbElementProvider.Text.class);
            bindSingletonWorker(XmlJaxbElementProvider.General.class);

            bindSingletonWorker(XmlCollectionJaxbProvider.App.class);
            bindSingletonWorker(XmlCollectionJaxbProvider.Text.class);
            bindSingletonWorker(XmlCollectionJaxbProvider.General.class);

            bindSingletonWorker(XmlRootElementJaxbProvider.App.class);
            bindSingletonWorker(XmlRootElementJaxbProvider.Text.class);
            bindSingletonWorker(XmlRootElementJaxbProvider.General.class);

            // Message body readers
            bind(SourceProvider.StreamSourceReader.class).to(MessageBodyReader.class).in(Singleton.class);
            bind(SourceProvider.SaxSourceReader.class).to(MessageBodyReader.class).in(Singleton.class);
            bind(SourceProvider.DomSourceReader.class).to(MessageBodyReader.class).in(Singleton.class);
            bind(XmlRootObjectJaxbProvider.App.class).to(MessageBodyReader.class).in(Singleton.class);
            bind(XmlRootObjectJaxbProvider.Text.class).to(MessageBodyReader.class).in(Singleton.class);
            bind(XmlRootObjectJaxbProvider.General.class).to(MessageBodyReader.class).in(Singleton.class);
            /*
             * TODO: com.sun.jersey.core.impl.provider.entity.EntityHolderReader
             */

            install(new ServiceFinderBinder<MessageBodyReader>(MessageBodyReader.class, applicationProperties, runtimeType));
            // Message body writers
            bind(StreamingOutputProvider.class).to(MessageBodyWriter.class).in(Singleton.class);
            bind(SourceProvider.SourceWriter.class).to(MessageBodyWriter.class).in(Singleton.class);
            install(new ServiceFinderBinder<MessageBodyWriter>(MessageBodyWriter.class, applicationProperties, runtimeType));
            install(new ServiceFinderBinder<HeaderDelegateProvider>(HeaderDelegateProvider.class, applicationProperties, runtimeType));

            // XML factory injection points
            bindFactory(DocumentBuilderFactoryInjectionProvider.class).to(DocumentBuilderFactory.class).in(PerThread.class);
            bindFactory(SaxParserFactoryInjectionProvider.class).to(SAXParserFactory.class).in(PerThread.class);
            bindFactory(XmlInputFactoryInjectionProvider.class).to(XMLInputFactory.class).in(PerThread.class);
            bindFactory(TransformerFactoryInjectionProvider.class).to(TransformerFactory.class).in(PerThread.class);
        }

        private <T extends MessageBodyReader & MessageBodyWriter> void bindSingletonWorker(Class<T> worker) {
            bind(worker).to(MessageBodyReader.class).to(MessageBodyWriter.class).in(Singleton.class);
        }
    }

    /**
     * Header delegate provider injection binder.
     */
    public static class HeaderDelegateProviders extends AbstractBinder {

        @Override
        protected void configure() {
            bind(CacheControlProvider.class).to(HeaderDelegateProvider.class).in(Singleton.class);
            bind(CookieProvider.class).to(HeaderDelegateProvider.class).in(Singleton.class);
            bind(DateProvider.class).to(HeaderDelegateProvider.class).in(Singleton.class);
            bind(EntityTagProvider.class).to(HeaderDelegateProvider.class).in(Singleton.class);
            bind(LinkProvider.class).to(HeaderDelegateProvider.class).in(Singleton.class);
            bind(LocaleProvider.class).to(HeaderDelegateProvider.class).in(Singleton.class);
            bind(MediaTypeProvider.class).to(HeaderDelegateProvider.class).in(Singleton.class);
            bind(NewCookieProvider.class).to(HeaderDelegateProvider.class).in(Singleton.class);
            bind(StringHeaderProvider.class).to(HeaderDelegateProvider.class).in(Singleton.class);
            bind(UriProvider.class).to(HeaderDelegateProvider.class).in(Singleton.class);
        }
    }
}
