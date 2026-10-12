package com.blog.markdown;

import org.commonmark.Extension;
import org.commonmark.ext.autolink.AutolinkExtension;
import org.commonmark.ext.gfm.strikethrough.StrikethroughExtension;
import org.commonmark.ext.gfm.tables.TablesExtension;
import org.commonmark.node.Link;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Turns Markdown into HTML that is safe to insert into a page unescaped:
 * raw HTML in the input is escaped, and links or images with unsafe
 * schemes such as {@code javascript:} lose their URL.
 */
@Component
public class MarkdownRenderer {

    private static final List<Extension> EXTENSIONS = List.of(
            TablesExtension.create(),
            StrikethroughExtension.create(),
            AutolinkExtension.create());

    private final Parser parser = Parser.builder()
            .extensions(EXTENSIONS)
            .build();

    private final HtmlRenderer renderer = HtmlRenderer.builder()
            .extensions(EXTENSIONS)
            .escapeHtml(true)
            .sanitizeUrls(true)
            // sanitizeUrls also marks every link rel="nofollow", which suits untrusted
            // content such as comments, but posts are written by the blog's own authors
            .attributeProviderFactory(context -> (node, tagName, attributes) -> {
                if (node instanceof Link) {
                    attributes.remove("rel");
                }
            })
            .build();

    public String toHtml(String markdown) {
        return renderer.render(parser.parse(markdown));
    }
}
