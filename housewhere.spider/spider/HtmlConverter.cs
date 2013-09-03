using System.IO;
using System.Text;

namespace X3.Spider
{
    public class HtmlConverter
    {
        private readonly HtmlCharsetMatcher matcher = new HtmlCharsetMatcher();
        public string Convert(Stream stream, string backupEncoding)
        {
            var defaultEncoding = Encoding.UTF8;
            var bytes = GetBytes(stream);
            var html = ConvertToString(bytes, defaultEncoding);

            var charsetEncodingInHtml = matcher.Match(html);
            if (CharsetIsSpecifiedInHtml(charsetEncodingInHtml))
            {
                return EncodingEquals(charsetEncodingInHtml, defaultEncoding)
                    ? html
                    : ConvertToString(bytes, charsetEncodingInHtml);
            }

            if (!string.IsNullOrEmpty(backupEncoding))
            {
                return EncodingEquals(EncodingUtil.TryGetEncoding(backupEncoding), defaultEncoding) 
                    ? html 
                    : ConvertToString(bytes, Encoding.GetEncoding(backupEncoding));
            }
            return html;
        }

        private static byte[] GetBytes(Stream stream)
        {
            const int bufferLength = 32768;
            var buffer = new byte[bufferLength];
            using (var memoryStream = new MemoryStream())
            {
                while (true)
                {
                    var readLength = stream.Read(buffer, 0, buffer.Length);
                    if (readLength <= 0)
                        return memoryStream.ToArray();
                    memoryStream.Write(buffer, 0, readLength);
                }
            }
        }

        private bool EncodingEquals(Encoding charset, Encoding defaultEncoding)
        {
            return charset == defaultEncoding;
        }

        private static bool CharsetIsSpecifiedInHtml(Encoding encoding)
        {
            return encoding != null;
        }

        private static string ConvertToString(byte[] bytes, Encoding encoding)
        {
            return encoding.GetString(bytes);
        }
    }
}