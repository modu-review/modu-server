package com.modureview.service.utils;

import org.owasp.html.HtmlPolicyBuilder;
import org.owasp.html.PolicyFactory;

public class HtmlSanitizerPolicy {

  public static final PolicyFactory POLICY = new HtmlPolicyBuilder()
      .allowElements("p", "ul", "ol", "li", "br", "strong", "em", "blockquote", "code", "pre")
      .allowElements("a").allowAttributes("href").onElements("a")
      .allowUrlProtocols("http", "https", "mailto")
      .allowElements("img")
      .allowElements("png")
      .allowAttributes("src", "alt").onElements("img")
      .allowUrlProtocols("http", "https", "data")
      .toFactory();
}