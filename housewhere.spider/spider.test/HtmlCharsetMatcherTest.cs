using System;
using System.Text;
using X3.Spider;
using X3.Test.Core;
using Xunit;

namespace spider_test
{
    public class HtmlCharsetMatcherTest
    {
        [Fact]
        public void it_matches_charset_with_format_text_html_charset()
        {
            GetCharsetEncodingInHtml("<head>" + Environment.NewLine + 
                                     "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />" + Environment.NewLine + 
                                     "<title>"
                ).ShouldBe(Encoding.UTF8);
        }

        private static Encoding GetCharsetEncodingInHtml(string html)
        {
            return new HtmlCharsetMatcher().Match(html);
        }
    }
}