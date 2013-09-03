using System.IO;
using System.IO.Compression;
using Moq;
using X3.Spider;
using X3.Test.Core;
using Xunit;

namespace spider_test
{
    public class StreamDepresserTest
    {
        [Fact]
        public void it_depress_if_content_encoding_is_set_as_gzip()
        {
            var mock = new Mock<IWebResponse>();
            mock.Setup(m => m.ContentEncoding).Returns("gzip");
            mock.Setup(m => m.ResponseStream).Returns(new MemoryStream());
            var responseStream = new StreamDepresser(mock.Object);
            var stream = responseStream.Depress();
            (stream is GZipStream).ShouldBe(true);
        }

        [Fact]
        public void it_returns_original_stream_if_content_encoding_is_not_set()
        {
            var mock = new Mock<IWebResponse>();
            mock.Setup(m => m.ResponseStream).Returns(new MemoryStream());
            var responseStream = new StreamDepresser(mock.Object);
            var stream = responseStream.Depress();
            (stream is MemoryStream).ShouldBe(true);
        }
    }
}