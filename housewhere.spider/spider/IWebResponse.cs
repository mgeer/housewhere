using System.IO;

namespace X3.Spider
{
    public interface IWebResponse
    {
        string ContentEncoding { get; }
        string CharacterSet { get; }
        Stream ResponseStream { get; }
    }
}