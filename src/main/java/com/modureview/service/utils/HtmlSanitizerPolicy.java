package com.modureview.service.utils;

import org.owasp.html.HtmlPolicyBuilder;
import org.owasp.html.PolicyFactory;

public class HtmlSanitizerPolicy {

  public static final PolicyFactory POLICY = new HtmlPolicyBuilder()
      .allowElements("p", "ul", "ol", "li", "br", "strong", "em", "blockquote", "code", "pre")
      .allowElements("a").allowAttributes("href").onElements("a")
      .allowUrlProtocols("http", "https", "mailto")
      .allowElements("img").allowAttributes("src", "alt").onElements("img")
      .allowUrlProtocols("http", "https", "data")
      .allowElements("div", "h1", "h2", "h3", "h4", "h5", "h6")
      .toFactory();

}