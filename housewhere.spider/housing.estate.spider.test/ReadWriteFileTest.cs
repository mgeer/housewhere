using System;
using System.IO;
using System.Text;
using Microsoft.VisualStudio.TestTools.UnitTesting;

namespace housing.estate.spider.test
{
    [TestClass]
    public class ReadWriteFileTest
    {
        [TestMethod]
        public void it_should_write_and_read_utf8_text()
        {
            var output = @".\hello_china";
            var text = "你好中国";
            var bytes = Encoding.UTF8.GetBytes(text);
            using (var stream = new FileStream(output, FileMode.CreateNew, FileAccess.Write))
            {
                stream.Write(bytes, 0, bytes.Length);
                stream.Close();
            }

            using (var stream = new FileStream(output, FileMode.Open, FileAccess.Read))
            {
                using (var reader = new StreamReader(stream, Encoding.UTF8))
                {
                    var content = reader.ReadToEnd();
                    Assert.AreEqual(text, content);
                }
            }
        }
    }
}
