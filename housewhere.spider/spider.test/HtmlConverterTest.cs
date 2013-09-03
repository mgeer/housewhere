using System.IO;
using System.Text;
using X3.Spider;
using Xunit;
using X3.Test.Core;

namespace spider_test
{
    public class HtmlConverterTest
    {
        [Fact]
        public void it_uses_meta_charset_as_encoding_firstly_although_backup_encoding_is_given()
        {
            var html = GetHtml("gbk");
            var stream = GetStream(html, Encoding.GetEncoding("gbk"));
            AssertConversion(html, stream, "unicode");
        }

        [Fact]
        public void it_uses_default_encoding_if_charset_is_same_as_default_encoding()
        {
            var html = GetHtml("utf-8");
            var stream = GetStream(html, Encoding.UTF8);
            AssertConversion(html, stream, "unicode");
        }

        [Fact]
        public void it_uses_backup_encoding_if_charset_is_not_set()
        {
            var html = GetHtml("");
            var stream = GetStream(html, Encoding.Unicode);
            AssertConversion(html, stream, "unicode");
        }

        [Fact]
        public void it_uses_default_encoding_if_backup_encoding_is_same_as_default_encoding_when_charset_is_not_set()
        {
            var html = GetHtml("");
            var stream = GetStream(html, Encoding.UTF8);
            AssertConversion(html, stream, "utf-8");
        }

        [Fact]
        public void it_uses_utf_8_encoding_if_both_charset_and_backup_encoding_are_empty()
        {
            var html = GetHtml("");
            var stream = GetStream(html, Encoding.UTF8);
            AssertConversion(html, stream, "");
        }

        [Fact]
        public void it_uses_backup_encoding_if_meta_charset_is_not_valid()
        {
            const string html = "<meta charset=\"invalid-encoding\"";
            var stream = GetStream(html, Encoding.Unicode);
            AssertConversion(html, stream, "unicode");
        }

        [Fact]
        public void it_uses_utf_8_encoding_if_both_charset_and_backup_encoding_are_not_valid()
        {
            const string html = "<meta charset=\"invalid-encoding\"";
            var stream = GetStream(html, Encoding.UTF8);
            AssertConversion(html, stream, null);
        }

        private static void AssertConversion(string html, MemoryStream stream, string backupEncoding)
        {
            var convertedHtml = new HtmlConverter().Convert(stream, backupEncoding);
            convertedHtml.ShouldBe(html);
        }

        private static MemoryStream GetStream(string html, Encoding streamEncoding)
        {
            var stream = new MemoryStream(streamEncoding.GetBytes(html));
            return stream;
        }

        private static string GetHtml(string charset)
        {
            return
                string.Format(
                    "<!DOCTYPE html><html><head><meta charset=\"{0}\" /><meta name=\"keywords\" content=\"\" /><meta name=\"description\"" +
                    " content=\"\" /><title>品质团购每一天</title>", charset);
        }
    }
}