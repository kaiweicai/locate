package com.locate.gate.server;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.locate.gate.coder.WebAdapterHandler;
import com.locate.gate.hanlder.HttpRequestHandler;

@Service
public class HttpServerPipelineFactory {
	@Resource
	private HttpRequestHandler httpRequestHandler;
	@Resource
	private WebAdapterHandler webAdapterHandler;

//	public ChannelPipeline getPipeline() throws Exception {
//		// Create a default pipeline implementation.
//		ChannelPipeline pipeline = Channels.pipeline();
//
//		// Uncomment the following line if you want HTTPS
//		// SSLEngine engine =
//		// SecureChatSslContextFactory.getServerContext().createSSLEngine();
//		// engine.setUseClientMode(false);
//		// pipeline.addLast("ssl", new SslHandler(engine));
//
//		pipeline.addLast("decoder", new HttpRequestDecoder());
//		// Uncomment the following line if you don't want to handle HttpChunks.
//		// pipeline.addLast("aggregator", new HttpChunkAggregator(1048576));
//		pipeline.addLast("encoder", new HttpResponseEncoder());
//		// Remove the following line if you don't want automatic content
//		// compression.
//		pipeline.addLast("aggregator", new HttpChunkAggregator(65536));
//		pipeline.addLast("chunkedWriter", new ChunkedWriteHandler());
//		pipeline.addLast("deflater", new HttpContentCompressor());
//		pipeline.addLast("handler", httpRequestHandler);
////		pipeline.addLast("webAdpterHandler", webAdapterHandler);
//		return pipeline;
//	}
}
