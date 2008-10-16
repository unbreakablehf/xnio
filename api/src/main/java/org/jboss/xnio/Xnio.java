/*
 * JBoss, Home of Professional Open Source
 * Copyright 2008, JBoss Inc., and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.jboss.xnio;

import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.Executor;
import java.net.SocketAddress;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.security.AccessController;
import java.security.PrivilegedAction;
import org.jboss.xnio.channels.TcpChannel;
import org.jboss.xnio.channels.UdpChannel;
import org.jboss.xnio.channels.StreamChannel;
import org.jboss.xnio.channels.StreamSourceChannel;
import org.jboss.xnio.channels.StreamSinkChannel;

/**
 * The XNIO entry point class.
 *
 * @apiviz.landmark
 */
public abstract class Xnio implements Closeable {

    private static final String NIO_IMPL_CLASS_NAME = "org.jboss.xnio.nio.NioXnio";
    private static final String PROVIDER_CLASS;
    private static final int mask = Modifier.STATIC | Modifier.PUBLIC;

    static {
        String providerClassName = NIO_IMPL_CLASS_NAME;
        try {
            providerClassName = AccessController.doPrivileged(new PrivilegedAction<String>() {
                public String run() {
                    return System.getProperty("xnio.provider", NIO_IMPL_CLASS_NAME);
                }
            });
        } catch (Throwable t) {
            // ignored
        }
        PROVIDER_CLASS = providerClassName;
    }

    /**
     * Create an instance of the default XNIO provider.  The class name of this provider can be specified through the
     * {@code xnio.provider} system property.  Any failure to create the XNIO provider will cause an {@code java.io.IOException}
     * to be thrown.
     *
     * @return an XNIO instance
     * @throws IOException the the XNIO provider could not be created
     */
    public static Xnio create() throws IOException {
        final Xnio result;
        try {
            Class<? extends Xnio> xnioClass = Class.forName(PROVIDER_CLASS).asSubclass(Xnio.class);
            final Method method = xnioClass.getDeclaredMethod("create");
            if ((method.getModifiers() & mask) != mask) {
                throw new NoSuchMethodException("Not public and static");
            }
            result = (Xnio) method.invoke(null);
        } catch (ClassCastException e) {
            final IOException ioe = new IOException("The XNIO provider class \"" + PROVIDER_CLASS + "\" is not really an XNIO provider");
            ioe.initCause(e);
            throw ioe;
        } catch (ClassNotFoundException e) {
            final IOException ioe = new IOException("The XNIO provider class \"" + PROVIDER_CLASS + "\" was not found");
            ioe.initCause(e);
            throw ioe;
        } catch (IllegalAccessException e) {
            final IOException ioe = new IOException("The XNIO provider class \"" + PROVIDER_CLASS + "\" was not instantiatable due to an illegal access exception");
            ioe.initCause(e);
            throw ioe;
        } catch (InvocationTargetException e) {
            final Throwable cause = e.getCause();
            if (cause instanceof IOException) {
                throw (IOException) cause;
            } else if (cause instanceof RuntimeException) {
                throw (RuntimeException) cause;
            } else {
                final IOException ioe = new IOException("The XNIO provider class \"" + PROVIDER_CLASS + "\" create() method threw an exception");
                ioe.initCause(cause);
                throw ioe;
            }
        } catch (NoSuchMethodException e) {
            final IOException ioe = new IOException("The XNIO provider class \"" + PROVIDER_CLASS + "\" does not have an accessible no-argument static create() method");
            ioe.initCause(e);
            throw ioe;
        } catch (ExceptionInInitializerError e) {
            final IOException ioe = new IOException("The XNIO provider class \"" + PROVIDER_CLASS + "\" was not instantiatable due to an error in initialization");
            ioe.initCause(e);
            throw ioe;
        }
        return result;
    }

    /**
     * Construct an XNIO provider instance.
     */
    protected Xnio() {
    }

    /**
     * Create a TCP server.  The server will bind to the given addresses.  If none are specified, then the operating
     * system will assign a port and the server will attempt to bind to all addresses with that port number.  The
     * provider's executor will be used to execute handler methods.
     *
     * @param executor the executor to use to execute the handlers
     * @param handlerFactory the factory which will produce handlers for inbound connections
     * @param bindAddresses the addresses to bind to
     *
     * @return a factory that can be used to configure the new TCP server
     */
    public ConfigurableFactory<Closeable> createTcpServer(Executor executor, IoHandlerFactory<? super TcpChannel> handlerFactory, SocketAddress... bindAddresses) {
        throw new UnsupportedOperationException("TCP Server");
    }

    /**
     * Create a TCP server.  The server will bind to the given addresses.    If none are specified, then the operating
     * system will assign a port and the server will attempt to bind to all addresses with that port number.  The
     * provider's default executor will be used to execute handler methods.
     *
     * @param handlerFactory the factory which will produce handlers for inbound connections
     * @param bindAddresses the addresses to bind to
     *
     * @return a factory that can be used to configure the new TCP server
     */
    public ConfigurableFactory<Closeable> createTcpServer(IoHandlerFactory<? super TcpChannel> handlerFactory, SocketAddress... bindAddresses) {
        throw new UnsupportedOperationException("TCP Server");
    }

    /**
     * Create a configurable TCP connector.  The connector can be configured before it is actually created.
     *
     * @param executor the executor to use to execute the handlers
     *
     * @return a factory that can be used to configure the new TCP connector
     */
    public ConfigurableFactory<CloseableTcpConnector> createTcpConnector(Executor executor) {
        throw new UnsupportedOperationException("TCP Connector");
    }

    /**
     * Create a configurable TCP connector.  The connector can be configured before it is actually created.  The
     * provider's default executor will be used to execute handler methods.
     *
     * @return a factory that can be used to configure the new TCP connector
     */
    public ConfigurableFactory<CloseableTcpConnector> createTcpConnector() {
        throw new UnsupportedOperationException("TCP Connector");
    }

    /**
     * Create a UDP server.  The server will bind to the given addresses.  The UDP server can be configured to be
     * multicast-capable; this should only be done if multicast is needed, since some providers have a performance
     * penalty associated with multicast.
     *
     * @param multicast {@code true} if the UDP server should be multicast-capable
     * @param executor the executor to use to execute the handlers
     * @param handlerFactory the factory which will produce handlers for each channel
     * @param bindAddresses the addresses to bind
     *
     * @return a factory that can be used to configure the new UDP server
     */
    public ConfigurableFactory<Closeable> createUdpServer(Executor executor, boolean multicast, IoHandlerFactory<? super UdpChannel> handlerFactory, SocketAddress... bindAddresses) {
        throw new UnsupportedOperationException("UDP Server");
    }

    /**
     * Create a UDP server.  The server will bind to the given addresses.  The provider's default executor will be used to
     * execute handler methods.
     *
     * @param multicast {@code true} if the UDP server should be multicast-capable
     * @param handlerFactory the factory which will produce handlers for each channel
     * @param bindAddresses the addresses to bind
     *
     * @return a factory that can be used to configure the new UDP server
     */
    public ConfigurableFactory<Closeable> createUdpServer(boolean multicast, IoHandlerFactory<? super UdpChannel> handlerFactory, SocketAddress... bindAddresses) {
        throw new UnsupportedOperationException("UDP Server");
    }

    /**
     * Create a pipe "server".  The provided handler factory is used to supply handlers for the server "end" of the
     * pipe. The returned channel source is used to establish connections to the server.
     *
     * @param executor the executor to use to execute the handlers
     * @param handlerFactory the server handler factory
     *
     * @return the client channel source
     */
    public ChannelSource<StreamChannel> createPipeServer(Executor executor, IoHandlerFactory<? super StreamChannel> handlerFactory) {
        throw new UnsupportedOperationException("Pipe Server");
    }

    /**
     * Create a pipe "server".  The provided handler factory is used to supply handlers for the server "end" of the
     * pipe. The returned channel source is used to establish connections to the server.  The provider's default executor will be used to
     * execute handler methods.
     *
     * @param handlerFactory the server handler factory
     *
     * @return the client channel source
     */
    public ChannelSource<StreamChannel> createPipeServer(IoHandlerFactory<? super StreamChannel> handlerFactory) {
        throw new UnsupportedOperationException("Pipe Server");
    }

    /**
     * Create a one-way pipe "server".  The provided handler factory is used to supply handlers for the server "end" of
     * the pipe. The returned channel source is used to establish connections to the server.  The data flows from the
     * server to the client.
     *
     * @param executor the executor to use to execute the handlers
     * @param handlerFactory the server handler factory
     *
     * @return the client channel source
     */
    public ChannelSource<StreamSourceChannel> createPipeSourceServer(Executor executor, IoHandlerFactory<? super StreamSinkChannel> handlerFactory) {
        throw new UnsupportedOperationException("One-way Pipe Server");
    }

    /**
     * Create a one-way pipe "server".  The provided handler factory is used to supply handlers for the server "end" of
     * the pipe. The returned channel source is used to establish connections to the server.  The data flows from the
     * server to the client.  The provider's default executor will be used to
     * execute handler methods.
     *
     * @param handlerFactory the server handler factory
     *
     * @return the client channel source
     */
    public ChannelSource<StreamSourceChannel> createPipeSourceServer(IoHandlerFactory<? super StreamSinkChannel> handlerFactory) {
        throw new UnsupportedOperationException("One-way Pipe Server");
    }

    /**
     * Create a one-way pipe "server".  The provided handler factory is used to supply handlers for the server "end" of
     * the pipe. The returned channel source is used to establish connections to the server.  The data flows from the
     * client to the server.
     *
     * @param executor the executor to use to execute the handlers
     * @param handlerFactory the server handler factory
     *
     * @return the client channel source
     */
    public ChannelSource<StreamSinkChannel> createPipeSinkServer(Executor executor, IoHandlerFactory<? super StreamSourceChannel> handlerFactory) {
        throw new UnsupportedOperationException("One-way Pipe Server");
    }

    /**
     * Create a one-way pipe "server".  The provided handler factory is used to supply handlers for the server "end" of
     * the pipe. The returned channel source is used to establish connections to the server.  The data flows from the
     * client to the server.  The provider's default executor will be used to
     * execute handler methods.
     *
     * @param handlerFactory the server handler factory
     *
     * @return the client channel source
     */
    public ChannelSource<StreamSinkChannel> createPipeSinkServer(IoHandlerFactory<? super StreamSourceChannel> handlerFactory) {
        throw new UnsupportedOperationException("One-way Pipe Server");
    }

    /**
     * Create a single pipe connection.
     *
     * @param executor the executor to use to execute the handlers
     * @param leftHandler the handler for the "left" side of the pipe
     * @param rightHandler the handler for the "right" side of the pipe
     *
     * @return the future connection
     */
    public IoFuture<Closeable> createPipeConnection(Executor executor, IoHandler<? super StreamChannel> leftHandler, IoHandler<? super StreamChannel> rightHandler) {
        throw new UnsupportedOperationException("Pipe Connection");
    }

    /**
     * Create a single pipe connection.  The provider's default executor will be used to
     * execute handler methods.
     *
     * @param leftHandler the handler for the "left" side of the pipe
     * @param rightHandler the handler for the "right" side of the pipe
     *
     * @return the future connection
     */
    public IoFuture<Closeable> createPipeConnection(IoHandler<? super StreamChannel> leftHandler, IoHandler<? super StreamChannel> rightHandler) {
        throw new UnsupportedOperationException("Pipe Connection");
    }

    /**
     * Create a single one-way pipe connection.
     *
     * @param executor the executor to use to execute the handlers
     * @param sourceHandler the handler for the "source" side of the pipe
     * @param sinkHandler the handler for the "sink" side of the pipe
     *
     * @return the future connection
     */
    public IoFuture<Closeable> createOneWayPipeConnection(Executor executor, IoHandler<? super StreamSourceChannel> sourceHandler, IoHandler<? super StreamSinkChannel> sinkHandler) {
        throw new UnsupportedOperationException("One-way Pipe Connection");
    }

    /**
     * Create a single one-way pipe connection.  The provider's default executor will be used to
     * execute handler methods.
     *
     * @param sourceHandler the handler for the "source" side of the pipe
     * @param sinkHandler the handler for the "sink" side of the pipe
     *
     * @return the future connection
     */
    public IoFuture<Closeable> createOneWayPipeConnection(IoHandler<? super StreamSourceChannel> sourceHandler, IoHandler<? super StreamSinkChannel> sinkHandler) {
        throw new UnsupportedOperationException("One-way Pipe Connection");
    }

    /**
     * Create a TCP acceptor.
     *
     * @param executor the executor to use to execute the handlers
     *
     * @return a factory that can be used to configure a TCP acceptor
     */
    public ConfigurableFactory<TcpAcceptor> createTcpAcceptor(Executor executor) {
        throw new UnsupportedOperationException("TCP Acceptor");
    }

    /**
     * Create a TCP acceptor.  The provider's default executor will be used to
     * execute handler methods.
     *
     * @return a factory that can be used to configure a TCP acceptor
     */
    public ConfigurableFactory<TcpAcceptor> createTcpAcceptor() {
        throw new UnsupportedOperationException("TCP Acceptor");
    }

    /**
     * Wake up any blocking I/O operation being carried out on a given thread.  Custom implementors of {@link Thread}
     * may call this method from their implementation of {@link Thread#interrupt()} after the default implementation
     * to ensure that any thread waiting in a blocking operation is woken up in a timely manner.  Some implementations
     * may not implement this method, relying instead on the interruption mechanism built in to the JVM; as such this
     * method should not be relied upon as a guaranteed way to awaken a blocking thread independently of thread
     * interruption.
     *
     * @param targetThread the thread to awaken
     */
    public void awaken(Thread targetThread) {
        // nothing by default
    }

    /**
     * Close this XNIO provider.  Calling this method more than one time has no additional effect.
     */
    public abstract void close() throws IOException;
}
