using System.Text;
using Moq;
using X3.Spider;
using X3.Test.Core;
using Xunit;

namespace spider_test
{
    public class EncodingParserTest
    {
        [Fact]
        public void it_chooses_character_set_as_encoding()
        {
            var mock = GetMock("unicode");
            var encodingParser = new EncodingParser(mock.Object);
            encodingParser.GetEncoding().ShouldBe(Encoding.Unicode);
        }

        [Fact]
        public void it_uses_local_encoding_as_default()
        {
            var mock = GetMock("");
            var encodingParser = new EncodingParser(mock.Object);
            encodingParser.GetEncoding().ShouldBe(Encoding.Default);
        }


        private static Mock<IWebResponse> GetMock(string characterSet)
        {
            var mock = new Mock<IWebResponse>();
            mock.Setup(m => m.CharacterSet).Returns(characterSet);
            return mock;
        }
    }
}