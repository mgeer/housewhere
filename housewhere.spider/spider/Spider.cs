using System.Net;

namespace X3.Spider
{
    public class Spider : ISpider
    {
        private static readonly NewLineEraser
            NewLineEraser = new NewLineEraser();
        private readonly HtmlConverter htmlConverter = new HtmlConverter();

        public string Grab(string url)
        {
            var webRequest = (HttpWebRequest)WebRequest.Create(url);
            using (var response = (HttpWebResponse)webRequest.GetResponse())
            {
                var text = GetStringFrom(response);
                var filter = NewLineEraser.Filter(text);
                return filter;
            }
        }

        private string GetStringFrom(HttpWebResponse response)
        {
            var webResponseAdapter = new WebResponseAdapter(response);
            var streamDepresser = new StreamDepresser(webResponseAdapter);
            using (var depressedStream = streamDepresser.Depress())
            {
                var pageContent = htmlConverter.Convert(depressedStream, webResponseAdapter.CharacterSet);
                return pageContent;
            }
        }
    }
}