package com.locate.gate.server;

import javax.annotation.Resource;

import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.handler.codec.http.HttpChunkAggregator;
import org.jboss.netty.handler.codec.http.HttpContentCompressor;
import org.jboss.netty.handler.codec.http.HttpRequestDecoder;
import org.jboss.netty.handler.codec.http.HttpResponseEncoder;
import org.jboss.netty.handler.stream.ChunkedWriteHandler;
import org.springframework.stereotype.Service;

import com.locate.gate.coder.WebAdapterHandler;
import com.locate.gate.hanlder.HttpRequestHandler;

@Service
public class HttpServerPipelineFactory implements ChannelPipelineFactory {
	@Resource
	private HttpRequestHandler httpRequestHandler;
	@Resource
	private WebAdapterHandler webAdapterHandler;

	public ChannelPipeline getPipeline() throws Exception {
		// Create a default pipeline implementation.
		ChannelPipeline pipeline = Channels.pipeline();

		// Uncomment the following line if you want HTTPS
		// SSLEngine engine =
		// SecureChatSslContextFactory.getServerContext().createSSLEngine();
		// engine.setUseClientMode(false);
		// pipeline.addLast("ssl", new SslHandler(engine));

		pipeline.addLast("decoder", new HttpRequestDecoder());
		// Uncomment the following line if you don't want to handle HttpChunks.
		// pipeline.addLast("aggregator", new HttpChunkAggregator(1048576));
		pipeline.addLast("encoder", new HttpResponseEncoder());
		// Remove the following line if you don't want automatic content
		// compression.
		pipeline.addLast("aggregator", new HttpChunkAggregator(65536));
		pipeline.addLast("chunkedWriter", new ChunkedWriteHandler());
		pipeline.addLast("deflater", new HttpContentCompressor());
		pipeline.addLast("handler", httpRequestHandler);
//		pipeline.addLast("webAdpterHandler", webAdapterHandler);
		return pipeline;
	}
}
