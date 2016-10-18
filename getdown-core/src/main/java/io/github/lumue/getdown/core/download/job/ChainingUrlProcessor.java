package io.github.lumue.getdown.core.download.job;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * modifies a url by applying a chain of url processors
 *
 * Created by lm on 18.10.16.
 */
public class ChainingUrlProcessor implements UrlProcessor{
	private final List<UrlProcessor> processorChain;

	private ChainingUrlProcessor(List<UrlProcessor> processorChain) {
		this.processorChain = new ArrayList<>(processorChain);
	}

	private ChainingUrlProcessor(Builder builder) {
		processorChain = builder.processorChain;
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	@Override
	public String processUrl(String in) {
		String out=in;
		for (UrlProcessor urlProcessor:this.processorChain  ) {
			out=urlProcessor.processUrl(out);
		}
		return out;
	}

	public static final class Builder {
		private final List<UrlProcessor> processorChain=new LinkedList<>();

		private Builder() {
		}

		public Builder add(UrlProcessor val) {
			processorChain.add(val);
			return this;
		}

		public ChainingUrlProcessor build() {
			return new ChainingUrlProcessor(this);
		}
	}
}
