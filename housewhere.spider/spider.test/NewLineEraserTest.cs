using X3.Spider;
using Xunit;
using X3.Test.Core;

namespace spider_test
{
    public class NewLineEraserTest
    {
        [Fact]
        public void it_replace_slash_n_with_blank()
        {
            var newLineEraser = new NewLineEraser();
            newLineEraser.Filter("Hello\r\nWorld!").ShouldBe("Hello\r World!");
        }
    }
}