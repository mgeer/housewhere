using System.IO;
using System.Net;

namespace X3.Spider
{
    public class WebResponseAdapter : IWebResponse
    {
        private readonly HttpWebResponse response;

        public WebResponseAdapter(HttpWebResponse response)
        {
            this.response = response;
        }

        public string ContentEncoding
        {
            get { return response.ContentEncoding; }
        }

        public string CharacterSet
        {
            get { return response.CharacterSet; }
        }

        public Stream ResponseStream
        {
            get { return response.GetResponseStream(); }
        }
    }
}