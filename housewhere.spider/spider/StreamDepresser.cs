using System;
using System.IO;
using System.IO.Compression;

namespace X3.Spider
{
    public class StreamDepresser
    {
        private readonly IWebResponse response;

        public StreamDepresser(IWebResponse response)
        {
            this.response = response;
        }

        public Stream Depress()
        {
            var responseStream = response.ResponseStream;
            return "gzip".Equals(response.ContentEncoding, StringComparison.OrdinalIgnoreCase) 
                ? new GZipStream(responseStream, CompressionMode.Decompress) 
                : responseStream;
        }
    }
}