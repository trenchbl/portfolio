package com.blog.markdown;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MarkdownRendererTest {

    private final MarkdownRenderer renderer = new MarkdownRenderer();

    @Test
    void rendersParagraphsAndEmphasis() {
        assertThat(renderer.toHtml("First **bold** paragraph.\n\nSecond *one*."))
                .isEqualTo("<p>First <strong>bold</strong> paragraph.</p>\n<p>Second <em>one</em>.</p>\n");
    }

    @Test
    void rendersHeadingsListsAndCode() {
        String html = renderer.toHtml("""
                ## Heading

                - one
                - two

                ```java
                var x = 1;
                ```
                """);

        assertThat(html)
                .contains("<h2>Heading</h2>")
                .contains("<ul>\n<li>one</li>\n<li>two</li>\n</ul>")
                .contains("<pre><code class=\"language-java\">var x = 1;\n</code></pre>");
    }

    @Test
    void rendersGithubFlavouredExtensions() {
        String html = renderer.toHtml("""
                | A | B |
                |---|---|
                | 1 | 2 |

                ~~gone~~ and https://example.com
                """);

        assertThat(html)
                .contains("<table>")
                .contains("<td>1</td>")
                .contains("<del>gone</del>")
                .contains("<a href=\"https://example.com\">https://example.com</a>");
    }

    @Test
    void escapesRawHtml() {
        String html = renderer.toHtml("Hello <script>alert('x')</script> and <b onclick=\"x()\">bold</b>");

        assertThat(html)
                .doesNotContain("<script>")
                .doesNotContain("<b ")
                .contains("&lt;script&gt;");
    }

    @Test
    void escapesRawHtmlBlocks() {
        String html = renderer.toHtml("<div>\n<img src=x onerror=alert(1)>\n</div>");

        assertThat(html)
                .doesNotContain("<div>")
                .doesNotContain("<img")
                .contains("&lt;img");
    }

    @Test
    void removesJavascriptLinks() {
        String html = renderer.toHtml("[click me](javascript:alert(1)) and ![image](javascript:alert(2))");

        assertThat(html).doesNotContainIgnoringCase("javascript:");
    }

    @Test
    void keepsSafeLinks() {
        assertThat(renderer.toHtml("[Spring](https://spring.io) and [mail](mailto:me@example.com)"))
                .contains("<a href=\"https://spring.io\">Spring</a>")
                .contains("<a href=\"mailto:me@example.com\">mail</a>");
    }
}
