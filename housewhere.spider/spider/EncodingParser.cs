using System;
using System.Text;

namespace X3.Spider
{
    public class EncodingParser
    {
        private readonly IWebResponse response;

        public EncodingParser(IWebResponse response)
        {
            this.response = response;
        }

        public Encoding GetEncoding()
        {
            var characterSet = response.CharacterSet;
            return !String.IsNullOrEmpty(characterSet) 
                ? GetEncoding(characterSet) : Encoding.Default;
        }

        private static Encoding GetEncoding(string contentEncoding)
        {
            return Encoding.GetEncoding(contentEncoding);
        }
    }
}